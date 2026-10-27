package com.example.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.ImageConfig
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.ImageSize
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.ThinkingLevel
import com.google.firebase.ai.type.Tool
import com.google.firebase.ai.type.generationConfig
import com.google.firebase.ai.type.thinkingConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

private const val TAG = "AiCopilotViewModel"

enum class AiExecutionMode(val label: String, val modelId: String, val subtitle: String) {
    HIGH_THINKING(
        label = "High Thinking (Argon)",
        modelId = "gemini-4-argon",
        subtitle = "gemini-4-argon • ThinkingLevel.HIGH • Complex Bio-Legal Reasoning"
    ),
    SEARCH_GROUNDED(
        label = "Google Search Grounded",
        modelId = "gemini-3.8-flash",
        subtitle = "gemini-3.8-flash • Google Search Tool • Live ClinVar & Regulatory Lookup"
    ),
    LOW_LATENCY(
        label = "Low-Latency Copilot",
        modelId = "gemini-3.1-flash-lite",
        subtitle = "gemini-3.1-flash-lite • Streaming Sub-Second Lab & QC Explanations"
    )
}

enum class SelectedImageResolution(val label: String, val sdkSize: ImageSize) {
    RES_1K("1K (1024px)", ImageSize.SIZE_1K),
    RES_2K("2K (2048px)", ImageSize.SIZE_2K),
    RES_4K("4K (4096px)", ImageSize.SIZE_4K)
}

sealed interface TextAiUiState {
    data object Idle : TextAiUiState
    data object Loading : TextAiUiState
    data class Streaming(val partialText: String) : TextAiUiState
    data class Success(val responseText: String, val modelUsed: String) : TextAiUiState
    data class Error(val message: String) : TextAiUiState
}

sealed interface ImageGenUiState {
    data object Idle : ImageGenUiState
    data object Loading : ImageGenUiState
    data class Success(val bitmap: Bitmap, val resolutionLabel: String) : ImageGenUiState
    data class Error(val message: String) : ImageGenUiState
}

class AiCopilotViewModel : ViewModel() {

    private val _selectedMode = MutableStateFlow(AiExecutionMode.HIGH_THINKING)
    val selectedMode: StateFlow<AiExecutionMode> = _selectedMode.asStateFlow()

    private val _textUiState = MutableStateFlow<TextAiUiState>(TextAiUiState.Idle)
    val textUiState: StateFlow<TextAiUiState> = _textUiState.asStateFlow()

    private val _selectedResolution = MutableStateFlow(SelectedImageResolution.RES_1K)
    val selectedResolution: StateFlow<SelectedImageResolution> = _selectedResolution.asStateFlow()

    private val _imageUiState = MutableStateFlow<ImageGenUiState>(ImageGenUiState.Idle)
    val imageUiState: StateFlow<ImageGenUiState> = _imageUiState.asStateFlow()

    // 1. High Thinking model: gemini-4-argon with ThinkingLevel.HIGH (no maxOutputTokens)
    private val highThinkingModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-4-argon",
            generationConfig = generationConfig {
                thinkingConfig = thinkingConfig {
                    thinkingLevel = ThinkingLevel.HIGH
                }
            }
        )
    }

    // 2. Google Search Grounded model: gemini-3.8-flash with Tool.googleSearch()
    private val searchGroundedModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            tools = listOf(Tool.googleSearch())
        )
    }

    // 3. Low-Latency model: gemini-3.1-flash-lite
    private val lowLatencyModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.1-flash-lite"
        )
    }

    fun selectExecutionMode(mode: AiExecutionMode) {
        _selectedMode.value = mode
    }

    fun selectImageResolution(resolution: SelectedImageResolution) {
        _selectedResolution.value = resolution
    }

    fun runAiQuery(prompt: String) {
        if (prompt.isBlank()) return
        val mode = _selectedMode.value

        viewModelScope.launch {
            _textUiState.value = TextAiUiState.Loading
            try {
                when (mode) {
                    AiExecutionMode.HIGH_THINKING -> {
                        val response = withTimeout(60_000L) {
                            highThinkingModel.generateContent(prompt)
                        }
                        val out = response.text
                        if (!out.isNullOrBlank()) {
                            _textUiState.value = TextAiUiState.Success(out, mode.subtitle)
                        } else {
                            _textUiState.value = TextAiUiState.Error("Model returned an empty response.")
                        }
                    }

                    AiExecutionMode.SEARCH_GROUNDED -> {
                        val response = withTimeout(60_000L) {
                            searchGroundedModel.generateContent(prompt)
                        }
                        val out = response.text
                        if (!out.isNullOrBlank()) {
                            _textUiState.value = TextAiUiState.Success(out, mode.subtitle)
                        } else {
                            _textUiState.value = TextAiUiState.Error("Search-grounded model returned an empty response.")
                        }
                    }

                    AiExecutionMode.LOW_LATENCY -> {
                        val accumulated = StringBuilder()
                        withTimeout(60_000L) {
                            lowLatencyModel.generateContentStream(prompt).collect { chunk ->
                                accumulated.append(chunk.text.orEmpty())
                                _textUiState.value = TextAiUiState.Streaming(accumulated.toString())
                            }
                        }
                        if (accumulated.isNotBlank()) {
                            _textUiState.value = TextAiUiState.Success(accumulated.toString(), mode.subtitle)
                        } else {
                            _textUiState.value = TextAiUiState.Error("Low-latency stream returned empty output.")
                        }
                    }
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "AI inference timed out", e)
                _textUiState.value = TextAiUiState.Error("AI request timed out after 60 seconds.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "AI inference failed (${mode.modelId})", e)
                _textUiState.value = TextAiUiState.Error(
                    e.localizedMessage ?: "AI generation failed."
                )
            }
        }
    }

    /**
     * Generates a studio molecular or report visual using gemini-3-pro-image-preview
     * and the user-selected 1K, 2K, or 4K ImageSize.
     */
    fun generateMolecularVisual(prompt: String) {
        if (prompt.isBlank()) return
        val res = _selectedResolution.value

        viewModelScope.launch {
            _imageUiState.value = ImageGenUiState.Loading
            try {
                val config = generationConfig {
                    responseModalities = listOf(ResponseModality.IMAGE)
                    imageConfig = ImageConfig.builder()
                        .setImageSize(res.sdkSize)
                        .build()
                }
                val imageModel = Firebase.ai.generativeModel(
                    modelName = "gemini-3-pro-image-preview",
                    generationConfig = config
                )

                val bitmap = withTimeout(60_000L) {
                    val response = imageModel.generateContent(prompt)
                    response.candidates.firstOrNull()
                        ?.content?.parts?.filterIsInstance<ImagePart>()
                        ?.firstOrNull()?.image
                }

                if (bitmap != null) {
                    _imageUiState.value = ImageGenUiState.Success(bitmap, res.label)
                } else {
                    _imageUiState.value = ImageGenUiState.Error("No image was returned by gemini-3-pro-image-preview.")
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Image generation timed out", e)
                _imageUiState.value = ImageGenUiState.Error("Image generation timed out after 60 seconds.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Image generation failed", e)
                _imageUiState.value = ImageGenUiState.Error(
                    e.localizedMessage ?: "Image generation failed."
                )
            }
        }
    }
}
