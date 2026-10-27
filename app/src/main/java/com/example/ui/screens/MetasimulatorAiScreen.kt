package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.TheaterComedy
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.ArtifactAmberWarning
import com.example.ui.theme.BenignBioEmerald
import com.example.ui.theme.CerenkovCyanPrimary
import com.example.ui.theme.HelixIndigoSecondary
import com.example.ui.theme.PathogenicCrimson
import com.example.viewmodel.AiCopilotViewModel
import com.example.viewmodel.AiExecutionMode
import com.example.viewmodel.ImageGenUiState
import com.example.viewmodel.LegeArtisViewModel
import com.example.viewmodel.SelectedImageResolution
import com.example.viewmodel.TextAiUiState
import kotlin.math.roundToInt

@Composable
fun MetasimulatorAiScreen(
    legeArtisViewModel: LegeArtisViewModel,
    aiViewModel: AiCopilotViewModel,
    modifier: Modifier = Modifier
) {
    val tensionLoad by legeArtisViewModel.tensionLoad.collectAsStateWithLifecycle()
    val reinforcementRate by legeArtisViewModel.reinforcementRate.collectAsStateWithLifecycle()
    val metasimReport by legeArtisViewModel.metasimReport.collectAsStateWithLifecycle()
    val satireOutput by legeArtisViewModel.satireOutput.collectAsStateWithLifecycle()

    val selectedMode by aiViewModel.selectedMode.collectAsStateWithLifecycle()
    val textUiState by aiViewModel.textUiState.collectAsStateWithLifecycle()
    val selectedResolution by aiViewModel.selectedResolution.collectAsStateWithLifecycle()
    val imageUiState by aiViewModel.imageUiState.collectAsStateWithLifecycle()

    var aiPrompt by remember {
        mutableStateOf(
            "Analyze the clinical pathogenicity and Lege Artis medico-legal disclosure obligations for BRCA1 c.5266dupC (p.Gln1756Profs*74) at Phred Q39 vs. excluding low-quality PTEN Q14 sequencing artifacts under single-submission (T=1 podání)."
        )
    }
    var imagePrompt by remember {
        mutableStateOf(
            "3D scientific molecular visualization of BRCA1 RING and BRCT protein domains interacting with a glowing cyan DNA double helix, dark laboratory telemetry style"
        )
    }
    var satireTopic by remember {
        mutableStateOf("Klinická validace mutace BRCA1 vs. úřední kolonka T=1 podání")
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Section 1: W4 Framework Calibration & Task A-B-C MAP-Processing Metasimulator
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("metasimulator_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Hub,
                                contentDescription = "Metasimulator",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "W4 Calibration & Tension RL Metasimulator",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Divide Reasoning • Task A-B-C MAP-Processing • L=EN | P=W | T=1 podání",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Surface(
                            color = BenignBioEmerald.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "${(metasimReport.resilienceScore * 100).roundToInt()}% RESILIENT",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                style = MaterialTheme.typography.labelLarge,
                                color = BenignBioEmerald
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Adversarial Sequencing Tension Load: ${(tensionLoad * 100).roundToInt()}%",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Slider(
                        value = tensionLoad,
                        onValueChange = { legeArtisViewModel.updateTensionAndReinforcement(it, reinforcementRate) },
                        valueRange = 0.05f..0.95f,
                        modifier = Modifier.testTag("slider_tension_load")
                    )

                    Text(
                        text = "RL Security Reinforcement Rate: ${(reinforcementRate * 100).roundToInt()}%",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Slider(
                        value = reinforcementRate,
                        onValueChange = { legeArtisViewModel.updateTensionAndReinforcement(tensionLoad, it) },
                        valueRange = 0.10f..0.99f,
                        modifier = Modifier.testTag("slider_reinforcement_rate")
                    )

                    Button(
                        onClick = legeArtisViewModel::runMetasimulationForSelectedVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_run_metasimulator")
                    ) {
                        Text("Execute Task A → B → C MAP Calibration (${metasimReport.schemeId})")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    PipelineStepBox(title = metasimReport.taskALookupSummary, accent = CerenkovCyanPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    PipelineStepBox(title = metasimReport.taskBResearchCypher, accent = HelixIndigoSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    PipelineStepBox(title = metasimReport.taskCDefineMapOutput, accent = BenignBioEmerald)

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Divide Reasoning Verification Steps (${metasimReport.singleSubmissionToken}):",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    metasimReport.divideReasoningSteps.forEach { step ->
                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Section 2: Multi-Model Gemini Clinical & Legal Co-Pilot
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_copilot_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Gemini AI Studio",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Multi-Model Gemini Clinical & Legal Studio",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = selectedMode.subtitle,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AiExecutionMode.entries.forEach { mode ->
                            val icon = when (mode) {
                                AiExecutionMode.HIGH_THINKING -> Icons.Default.Psychology
                                AiExecutionMode.SEARCH_GROUNDED -> Icons.Default.Public
                                AiExecutionMode.LOW_LATENCY -> Icons.Default.Bolt
                            }
                            FilterChip(
                                selected = selectedMode == mode,
                                onClick = { aiViewModel.selectExecutionMode(mode) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = mode.label,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                label = { Text(mode.label) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = aiPrompt,
                        onValueChange = { aiPrompt = it },
                        label = { Text("Clinical Interpretation / Regulatory / Bio-Legal Query") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_query_input"),
                        minLines = 3
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { aiViewModel.runAiQuery(aiPrompt) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_run_ai_query")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Execute AI")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Run ${selectedMode.label}")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    when (val state = textUiState) {
                        is TextAiUiState.Idle -> {
                            Text(
                                text = "Select High Thinking (gemini-4-argon), Google Search Grounding (gemini-3.8-flash), or Low-Latency (gemini-3.1-flash-lite) and run a query.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        is TextAiUiState.Loading -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Synthesizing response via ${selectedMode.modelId}...",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        is TextAiUiState.Streaming -> {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = state.partialText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                        is TextAiUiState.Success -> {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = state.modelUsed,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = state.responseText,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                        is TextAiUiState.Error -> {
                            Surface(
                                color = PathogenicCrimson.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "AI Error: ${state.message}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = PathogenicCrimson,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Studio Molecular & Report Image Generator (1K, 2K, 4K)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("image_gen_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Image Generator",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Molecular & Report Visual Generator (1K / 2K / 4K)",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Model: gemini-3-pro-image-preview • Select output resolution",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SelectedImageResolution.entries.forEach { res ->
                            FilterChip(
                                selected = selectedResolution == res,
                                onClick = { aiViewModel.selectImageResolution(res) },
                                label = { Text(res.label) },
                                modifier = Modifier.testTag("chip_res_${res.name}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = imagePrompt,
                        onValueChange = { imagePrompt = it },
                        label = { Text("Molecular / Dossier Illustration Prompt") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("image_prompt_input"),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { aiViewModel.generateMolecularVisual(imagePrompt) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_generate_molecular_image")
                    ) {
                        Text("Generate ${selectedResolution.label} Visual")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        when (val imgState = imageUiState) {
                            is ImageGenUiState.Idle -> {
                                Text(
                                    text = "Choose 1K, 2K, or 4K resolution and generate a high-fidelity molecular or report visual.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            is ImageGenUiState.Loading -> {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator()
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Synthesizing ${selectedResolution.label} image with gemini-3-pro-image-preview (30–60s)...",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            is ImageGenUiState.Success -> {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Image(
                                        bitmap = imgState.bitmap.asImageBitmap(),
                                        contentDescription = "Generated Molecular Visual (${imgState.resolutionLabel})",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(220.dp)
                                            .clip(RoundedCornerShape(12.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Rendered at ${imgState.resolutionLabel}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = BenignBioEmerald
                                    )
                                }
                            }
                            is ImageGenUiState.Error -> {
                                Text(
                                    text = "Error: ${imgState.message}",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 4: Stín v kolonce — Semantic Institutional Satire & Bio-Bureaucracy Audit
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("semantic_satire_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TheaterComedy,
                            contentDescription = "Stín v kolonce",
                            tint = ArtifactAmberWarning
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Stín v Kolonce — Generátor Sémantické Satiry",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Bio-Legal Institutional Contradiction & Form-Box Audit (A–E)",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = satireTopic,
                            onValueChange = { satireTopic = it },
                            label = { Text("Téma / Rozpor v kolonce") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedButton(
                            onClick = { legeArtisViewModel.generateSemanticAuditSatire(satireTopic) },
                            modifier = Modifier.testTag("btn_generate_satire")
                        ) {
                            Text("Přegenerovat")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    listOf(
                        satireOutput.sectionATitleAndCore,
                        satireOutput.sectionBCreativeText,
                        satireOutput.sectionCPunchlineExplanation,
                        satireOutput.sectionDSemanticDeconstruction,
                        satireOutput.sectionEEthicalQuestion
                    ).forEach { block ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = block,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PipelineStepBox(
    title: String,
    accent: androidx.compose.ui.graphics.Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(accent.copy(alpha = 0.12f))
            .padding(10.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = accent,
            fontWeight = FontWeight.SemiBold
        )
    }
}
