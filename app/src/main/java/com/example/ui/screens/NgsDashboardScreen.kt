package com.example.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.VariantEntity
import com.example.ui.theme.ArtifactAmberWarning
import com.example.ui.theme.BaseAdenineGreen
import com.example.ui.theme.BaseCytosineBlue
import com.example.ui.theme.BaseGuanineAmber
import com.example.ui.theme.BaseThymineRed
import com.example.ui.theme.BenignBioEmerald
import com.example.ui.theme.CerenkovCyanPrimary
import com.example.ui.theme.HelixIndigoSecondary
import com.example.ui.theme.LikelyPathogenicCoral
import com.example.ui.theme.PathogenicCrimson
import com.example.ui.theme.VusAmethyst
import com.example.viewmodel.LegeArtisViewModel
import kotlin.math.cos
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun NgsDashboardScreen(
    viewModel: LegeArtisViewModel,
    modifier: Modifier = Modifier
) {
    val allVariants by viewModel.allVariants.collectAsStateWithLifecycle()
    val negLog10P by viewModel.negLog10PThreshold.collectAsStateWithLifecycle()
    val minVaf by viewModel.minVafThreshold.collectAsStateWithLifecycle()
    val minPhred by viewModel.minPhredQuality.collectAsStateWithLifecycle()
    val selectedClass by viewModel.selectedClassFilter.collectAsStateWithLifecycle()
    val excludeArtifacts by viewModel.excludeArtifacts.collectAsStateWithLifecycle()
    val selectedVariantId by viewModel.selectedVariantId.collectAsStateWithLifecycle()
    val scrubPos by viewModel.chromatogramScrubNormalized.collectAsStateWithLifecycle()
    val informedConsent by viewModel.informedConsentVerified.collectAsStateWithLifecycle()
    val dualSignature by viewModel.dualSignatureVerified.collectAsStateWithLifecycle()
    val singleSubmission by viewModel.singleSubmissionLocked.collectAsStateWithLifecycle()
    val activeRole by viewModel.activeRole.collectAsStateWithLifecycle()

    val filteredVariants by remember(
        allVariants,
        negLog10P,
        minVaf,
        minPhred,
        selectedClass,
        excludeArtifacts
    ) {
        derivedStateOf {
            allVariants.filter { v ->
                val vNegLog10P = (-log10(v.pValue.coerceAtLeast(1e-12))).toFloat()
                val passesP = vNegLog10P >= negLog10P
                val passesVaf = v.alleleFrequency >= minVaf
                val passesPhred = v.phredQuality >= minPhred
                val passesClass = selectedClass == "ALL" || v.classification == selectedClass
                val passesArtifact = !excludeArtifacts || v.classification != "SEQUENCING_ARTIFACT"
                passesP && passesVaf && passesPhred && passesClass && passesArtifact
            }
        }
    }

    val selectedVariant = remember(allVariants, selectedVariantId) {
        allVariants.find { it.id == selectedVariantId } ?: allVariants.firstOrNull()
    }

    val legeArtisScore = remember(
        filteredVariants,
        informedConsent,
        dualSignature,
        singleSubmission
    ) {
        viewModel.computeLegeArtisScore(filteredVariants)
    }

    val policy = viewModel.currentPolicy()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            QcAndLegeArtisBanner(
                totalVariants = allVariants.size,
                retainedVariants = filteredVariants.size,
                legeArtisScore = legeArtisScore,
                informedConsent = informedConsent,
                dualSignature = dualSignature,
                singleSubmission = singleSubmission,
                onToggleConsent = viewModel::toggleInformedConsent,
                onToggleDualSig = viewModel::toggleDualSignature,
                onToggleSingleSub = viewModel::toggleSingleSubmissionLocked
            )
        }

        item {
            MoleculeAndChromatogramSection(
                selectedVariant = selectedVariant,
                allVariants = allVariants,
                scrubNormalized = scrubPos,
                onScrubChange = viewModel::updateChromatogramScrub
            )
        }

        item {
            VolcanoCurtainAndFiltersCard(
                allVariants = allVariants,
                filteredCount = filteredVariants.size,
                negLog10PThreshold = negLog10P,
                minVafThreshold = minVaf,
                minPhredQuality = minPhred,
                selectedClass = selectedClass,
                excludeArtifacts = excludeArtifacts,
                onNegLog10PChange = viewModel::updateNegLog10PThreshold,
                onMinVafChange = viewModel::updateMinVafThreshold,
                onMinPhredChange = { viewModel.updateMinPhredQuality(it.roundToInt()) },
                onSelectClass = viewModel::updateClassFilter,
                onToggleExcludeArtifacts = viewModel::toggleExcludeArtifacts,
                onTapVariantDot = viewModel::selectVariant
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "NGS Variant Calls (${filteredVariants.size}/${allVariants.size})",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Role: $activeRole",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        items(filteredVariants, key = { it.id }) { variant ->
            VariantRowCard(
                variant = variant,
                isSelected = variant.id == selectedVariant?.id,
                canReclassify = policy.canReclassifyVariants,
                onSelect = { viewModel.selectVariant(variant) },
                onReclassify = { newClass -> viewModel.reclassifyVariant(variant, newClass) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun QcAndLegeArtisBanner(
    totalVariants: Int,
    retainedVariants: Int,
    legeArtisScore: Int,
    informedConsent: Boolean,
    dualSignature: Boolean,
    singleSubmission: Boolean,
    onToggleConsent: () -> Unit,
    onToggleDualSig: () -> Unit,
    onToggleSingleSub: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("qc_lege_artis_card"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
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
                        imageVector = Icons.Default.Gavel,
                        contentDescription = "Lege Artis Compliance",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "QC Telemetry & Lege Artis Index",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Q30: 94.8% • Coverage: 385x • Ts/Tv: 2.14 • Retained: $retainedVariants/$totalVariants",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Surface(
                    color = if (legeArtisScore >= 85) BenignBioEmerald.copy(alpha = 0.2f)
                    else ArtifactAmberWarning.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "$legeArtisScore% LEGE ARTIS",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = if (legeArtisScore >= 85) BenignBioEmerald else ArtifactAmberWarning
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = informedConsent,
                    onClick = onToggleConsent,
                    label = { Text("GDPR & Informed Consent") },
                    modifier = Modifier.testTag("chip_informed_consent")
                )
                FilterChip(
                    selected = dualSignature,
                    onClick = onToggleDualSig,
                    label = { Text("Dual Bio-Legal Sign-Off") },
                    modifier = Modifier.testTag("chip_dual_signature")
                )
                FilterChip(
                    selected = singleSubmission,
                    onClick = onToggleSingleSub,
                    label = { Text("T=1 podání Lock") },
                    modifier = Modifier.testTag("chip_single_submission")
                )
            }
        }
    }
}

@Composable
private fun MoleculeAndChromatogramSection(
    selectedVariant: VariantEntity?,
    allVariants: List<VariantEntity>,
    scrubNormalized: Float,
    onScrubChange: (Float) -> Unit
) {
    var userYaw by remember { mutableFloatStateOf(0.4f) }
    var userPitch by remember { mutableFloatStateOf(0.2f) }

    // Micro-interaction #2: Spring-animated target angle when selectedVariant changes
    val targetYawOffset by animateFloatAsState(
        targetValue = ((selectedVariant?.position ?: 0L) % 360L) * 0.017f,
        animationSpec = spring(
            dampingRatio = 0.65f,
            stiffness = Spring.StiffnessLow
        ),
        label = "helix_yaw_spring"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "locus_pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_radius"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "3D Molecular Structure & Locus Sync",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = selectedVariant?.let {
                            "Active Locus: ${it.geneSymbol} ${it.hgvsCoding} (${it.hgvsProtein}) • Drag to rotate 3D helix"
                        } ?: "Select a variant row to focus 3D locus",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (selectedVariant != null) {
                    ClassificationBadge(selectedVariant.classification)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3D Molecular Double Helix Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF070B12))
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            userYaw += dragAmount.x * 0.012f
                            userPitch = (userPitch + dragAmount.y * 0.008f).coerceIn(-0.8f, 0.8f)
                        }
                    }
                    .testTag("molecule_3d_canvas")
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val centerY = h / 2f
                    val numRungs = 18
                    val effectiveYaw = userYaw + targetYawOffset
                    val highlightRung = ((scrubNormalized * (numRungs - 1)).roundToInt())
                        .coerceIn(0, numRungs - 1)

                    for (i in 0 until numRungs) {
                        val t = i.toFloat() / (numRungs - 1)
                        val x = w * 0.1f + t * (w * 0.8f)
                        val angle = effectiveYaw + i * 0.52f
                        val radius = h * 0.34f
                        val y1 = centerY + sin(angle) * radius * cos(userPitch)
                        val z1 = cos(angle)
                        val y2 = centerY - sin(angle) * radius * cos(userPitch)
                        val z2 = -cos(angle)

                        val isSelectedLocus = (i == highlightRung)
                        val rungColor = when {
                            isSelectedLocus && selectedVariant?.classification == "PATHOGENIC" -> PathogenicCrimson
                            isSelectedLocus && selectedVariant?.classification == "SEQUENCING_ARTIFACT" -> ArtifactAmberWarning
                            i % 4 == 0 -> BaseAdenineGreen
                            i % 4 == 1 -> BaseThymineRed
                            i % 4 == 2 -> BaseCytosineBlue
                            else -> BaseGuanineAmber
                        }

                        // Hydrogen bond rung
                        drawLine(
                            color = rungColor.copy(alpha = if (isSelectedLocus) 0.95f else 0.45f),
                            start = Offset(x, y1),
                            end = Offset(x, y2),
                            strokeWidth = if (isSelectedLocus) 5.5f else 2.5f,
                            cap = StrokeCap.Round
                        )

                        // Strand 1 node
                        val node1Radius = if (z1 > 0) 7f else 4.5f
                        drawCircle(
                            color = CerenkovCyanPrimary.copy(alpha = if (z1 > 0) 0.95f else 0.45f),
                            radius = node1Radius,
                            center = Offset(x, y1)
                        )

                        // Strand 2 node
                        val node2Radius = if (z2 > 0) 7f else 4.5f
                        drawCircle(
                            color = HelixIndigoSecondary.copy(alpha = if (z2 > 0) 0.95f else 0.45f),
                            radius = node2Radius,
                            center = Offset(x, y2)
                        )

                        // Glowing pulse on active mutated locus
                        if (isSelectedLocus) {
                            val locusColor = if (selectedVariant?.classification == "SEQUENCING_ARTIFACT")
                                ArtifactAmberWarning else PathogenicCrimson
                            drawCircle(
                                color = locusColor.copy(alpha = 0.28f),
                                radius = pulseRadius * 1.6f,
                                center = Offset(x, (y1 + y2) / 2f)
                            )
                            drawCircle(
                                color = locusColor,
                                radius = 7.5f,
                                center = Offset(x, (y1 + y2) / 2f)
                            )
                        }
                    }
                }

                Text(
                    text = "Codon #${(scrubNormalized * 600).roundToInt()} • Q-MAX 3D Projection",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Micro-Interaction #1: Base-Call Chromatogram & Phred Magnification Loupe
            val localPhred = selectedVariant?.phredQuality ?: 36
            val isLowQualityZone = scrubNormalized in 0.72f..0.88f || localPhred < 20
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Chromatogram Trace (Scrub to Inspect Peak & Phred)",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (isLowQualityZone) "⚠️ Phred Q${minOf(localPhred, 16)} Artifact Zone"
                    else "Phred Q$localPhred (High Confidence)",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isLowQualityZone) ArtifactAmberWarning else BenignBioEmerald
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF070B12))
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            change.consume()
                            val norm = (change.position.x / size.width.coerceAtLeast(1)).coerceIn(0.02f, 0.98f)
                            onScrubChange(norm)
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val norm = (offset.x / size.width.coerceAtLeast(1)).coerceIn(0.02f, 0.98f)
                            onScrubChange(norm)
                        }
                    }
                    .testTag("chromatogram_canvas")
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Shade low-Phred artifact warning zone on right side (0.72..0.88)
                    drawRect(
                        color = ArtifactAmberWarning.copy(alpha = 0.12f),
                        topLeft = Offset(w * 0.72f, 0f),
                        size = Size(w * 0.16f, h)
                    )

                    val channels = listOf(
                        Pair(BaseAdenineGreen, 0.0f),
                        Pair(BaseThymineRed, 1.3f),
                        Pair(BaseCytosineBlue, 2.6f),
                        Pair(BaseGuanineAmber, 3.9f)
                    )

                    channels.forEach { (color, phase) ->
                        val path = Path()
                        val steps = 80
                        for (s in 0..steps) {
                            val nx = s.toFloat() / steps
                            val x = nx * w
                            // Magnify amplitude near scrubNormalized (Loupe effect)
                            val dist = kotlin.math.abs(nx - scrubNormalized)
                            val loupeBoost = if (dist < 0.12f) 1.35f else 0.85f
                            val wave = (sin(nx * 24f + phase) * 0.5f + 0.5f).pow(3)
                            val y = h - 16f - (wave * (h * 0.62f) * loupeBoost).coerceAtMost(h - 24f)
                            if (s == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }
                        drawPath(
                            path = path,
                            color = color,
                            style = Stroke(width = 3.2f, cap = StrokeCap.Round)
                        )
                    }

                    // Scrub cursor & magnification loupe circle
                    val cursorX = scrubNormalized * w
                    drawLine(
                        color = Color.White,
                        start = Offset(cursorX, 0f),
                        end = Offset(cursorX, h),
                        strokeWidth = 2.5f
                    )
                    drawCircle(
                        color = CerenkovCyanPrimary.copy(alpha = 0.2f),
                        radius = 34f,
                        center = Offset(cursorX, h * 0.45f)
                    )
                    drawCircle(
                        color = CerenkovCyanPrimary,
                        radius = 34f,
                        center = Offset(cursorX, h * 0.45f),
                        style = Stroke(width = 2f)
                    )
                }

                val calledBase = when (((scrubNormalized * 20).roundToInt()) % 4) {
                    0 -> "A (Adenine • 98.2%)"
                    1 -> "T (Thymine • 96.7%)"
                    2 -> "C (Cytosine • 99.1%)"
                    else -> "G (Guanine • 97.4%)"
                }
                Text(
                    text = "Loupe Base Call: $calledBase | Ref>${selectedVariant?.refAllele ?: "G"} Alt>${selectedVariant?.altAllele ?: "A"}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun VolcanoCurtainAndFiltersCard(
    allVariants: List<VariantEntity>,
    filteredCount: Int,
    negLog10PThreshold: Float,
    minVafThreshold: Float,
    minPhredQuality: Int,
    selectedClass: String,
    excludeArtifacts: Boolean,
    onNegLog10PChange: (Float) -> Unit,
    onMinVafChange: (Float) -> Unit,
    onMinPhredChange: (Float) -> Unit,
    onSelectClass: (String) -> Unit,
    onToggleExcludeArtifacts: () -> Unit,
    onTapVariantDot: (VariantEntity) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                        imageVector = Icons.Default.FilterAlt,
                        contentDescription = "Mutation Filter",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mutation Filter & Live Threshold Curtain",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Text(
                    text = "$filteredCount Active Loci",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Micro-Interaction #3: Volcano Plot with Live Threshold Curtain
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF070B12))
                    .pointerInput(allVariants) {
                        detectTapGestures { offset ->
                            val w = size.width.coerceAtLeast(1)
                            val h = size.height.coerceAtLeast(1)
                            val tapped = allVariants.minByOrNull { v ->
                                val vx = (v.alleleFrequency / 0.6f).coerceIn(0.05f, 0.95f) * w
                                val vNegLogP = (-log10(v.pValue.coerceAtLeast(1e-10))).toFloat()
                                val vy = h - ((vNegLogP / 10f).coerceIn(0.05f, 0.95f) * h)
                                (vx - offset.x) * (vx - offset.x) + (vy - offset.y) * (vy - offset.y)
                            }
                            if (tapped != null) onTapVariantDot(tapped)
                        }
                    }
                    .testTag("volcano_curtain_canvas")
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    val vafCurtainX = (minVafThreshold / 0.6f).coerceIn(0f, 1f) * w
                    val pCurtainY = h - ((negLog10PThreshold / 10f).coerceIn(0f, 1f) * h)

                    // Shaded excluded curtain regions
                    drawRect(
                        color = Color(0x44EF4444),
                        topLeft = Offset(0f, pCurtainY),
                        size = Size(w, h - pCurtainY)
                    )
                    drawRect(
                        color = Color(0x33F59E0B),
                        topLeft = Offset(0f, 0f),
                        size = Size(vafCurtainX, pCurtainY)
                    )

                    // Laser curtain threshold lines
                    drawLine(
                        color = CerenkovCyanPrimary,
                        start = Offset(0f, pCurtainY),
                        end = Offset(w, pCurtainY),
                        strokeWidth = 2.5f
                    )
                    drawLine(
                        color = HelixIndigoSecondary,
                        start = Offset(vafCurtainX, 0f),
                        end = Offset(vafCurtainX, h),
                        strokeWidth = 2.5f
                    )

                    // Plot each variant locus
                    allVariants.forEach { v ->
                        val vx = (v.alleleFrequency / 0.6f).coerceIn(0.06f, 0.94f) * w
                        val vNegLogP = (-log10(v.pValue.coerceAtLeast(1e-10))).toFloat()
                        val vy = h - ((vNegLogP / 10f).coerceIn(0.08f, 0.92f) * h)
                        val retained = vNegLogP >= negLog10PThreshold && v.alleleFrequency >= minVafThreshold
                        val dotColor = when (v.classification) {
                            "PATHOGENIC" -> PathogenicCrimson
                            "LIKELY_PATHOGENIC" -> LikelyPathogenicCoral
                            "SEQUENCING_ARTIFACT" -> ArtifactAmberWarning
                            "VUS" -> VusAmethyst
                            else -> BenignBioEmerald
                        }
                        drawCircle(
                            color = dotColor.copy(alpha = if (retained) 1f else 0.25f),
                            radius = if (retained) 8.5f else 5f,
                            center = Offset(vx, vy)
                        )
                    }
                }

                Text(
                    text = "Curtain: -log10(p) ≥ ${"%.1f".format(negLog10PThreshold)} | VAF ≥ ${(minVafThreshold * 100).roundToInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Significance Threshold -log10(p): ${"%.1f".format(negLog10PThreshold)} (p ≤ ${"%.1e".format(10.0.pow(-negLog10PThreshold.toDouble()))})",
                style = MaterialTheme.typography.labelLarge
            )
            Slider(
                value = negLog10PThreshold,
                onValueChange = onNegLog10PChange,
                valueRange = 0.5f..8.5f,
                modifier = Modifier.testTag("slider_pvalue_curtain")
            )

            Text(
                text = "Minimum Allele Frequency (VAF): ${(minVafThreshold * 100).roundToInt()}% • Min Phred: Q$minPhredQuality",
                style = MaterialTheme.typography.labelLarge
            )
            Slider(
                value = minVafThreshold,
                onValueChange = onMinVafChange,
                valueRange = 0.01f..0.50f,
                modifier = Modifier.testTag("slider_vaf_curtain")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Exclude Sequencing Artifacts (Phred < Q20)",
                    style = MaterialTheme.typography.bodyMedium
                )
                Switch(
                    checked = excludeArtifacts,
                    onCheckedChange = { onToggleExcludeArtifacts() },
                    modifier = Modifier.testTag("switch_exclude_artifacts")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val classes = listOf("ALL", "PATHOGENIC", "LIKELY_PATHOGENIC", "VUS", "BENIGN", "SEQUENCING_ARTIFACT")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                classes.forEach { cls ->
                    FilterChip(
                        selected = selectedClass == cls,
                        onClick = { onSelectClass(cls) },
                        label = { Text(cls.replace("_", " ")) }
                    )
                }
            }
        }
    }
}

@Composable
private fun VariantRowCard(
    variant: VariantEntity,
    isSelected: Boolean,
    canReclassify: Boolean,
    onSelect: () -> Unit,
    onReclassify: (String) -> Unit
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable { onSelect() }
            .testTag("variant_card_${variant.geneSymbol}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(classificationColor(variant.classification))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${variant.geneSymbol} • ${variant.hgvsCoding}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                ClassificationBadge(variant.classification)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${variant.chromosome}:${variant.position} (${variant.hgvsProtein}) • Ref:${variant.refAllele}→Alt:${variant.altAllele}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "VAF: ${(variant.alleleFrequency * 100).roundToInt()}% | p=${"%.2e".format(variant.pValue)} | Phred: Q${variant.phredQuality} | Depth: ${variant.readDepth}x | Q-MAX: ${variant.qMaxHashSignature}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = variant.clinicalNotes,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = if (isSelected) 4 else 2,
                overflow = TextOverflow.Ellipsis
            )

            if (isSelected && canReclassify) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("PATHOGENIC", "LIKELY_PATHOGENIC", "VUS", "BENIGN", "SEQUENCING_ARTIFACT").forEach { target ->
                        if (target != variant.classification) {
                            AssistChip(
                                onClick = { onReclassify(target) },
                                label = { Text("Mark ${target.replace("_", " ")}") }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClassificationBadge(classification: String) {
    val color = classificationColor(classification)
    val icon = when (classification) {
        "PATHOGENIC", "LIKELY_PATHOGENIC" -> Icons.Default.Error
        "SEQUENCING_ARTIFACT" -> Icons.Default.Warning
        "BENIGN" -> Icons.Default.CheckCircle
        else -> Icons.Default.Science
    }
    Surface(
        color = color.copy(alpha = 0.18f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = classification,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = classification.replace("_", " "),
                style = MaterialTheme.typography.labelMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun classificationColor(classification: String): Color = when (classification) {
    "PATHOGENIC" -> PathogenicCrimson
    "LIKELY_PATHOGENIC" -> LikelyPathogenicCoral
    "SEQUENCING_ARTIFACT" -> ArtifactAmberWarning
    "VUS" -> VusAmethyst
    else -> BenignBioEmerald
}
