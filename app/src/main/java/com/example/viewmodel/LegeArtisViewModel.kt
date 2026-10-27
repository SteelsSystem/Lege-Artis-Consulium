package com.example.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.crypto.MetasimCalibrationReport
import com.example.crypto.QMaxCypherEngine
import com.example.crypto.QMaxCypherResult
import com.example.crypto.SemanticSatireOutput
import com.example.data.local.ActivityLogEntity
import com.example.data.local.ClientPortalEntity
import com.example.data.local.ConsultingDocEntity
import com.example.data.local.DocumentVersionEntity
import com.example.data.local.LegeArtisDatabase
import com.example.data.local.LegeArtisRepository
import com.example.data.local.QMaxSyncPacketEntity
import com.example.data.local.VariantEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.roundToInt

data class RolePermissionPolicy(
    val roleName: String,
    val canReclassifyVariants: Boolean,
    val canExecuteQMaxSync: Boolean,
    val canCommitDocVersions: Boolean,
    val canManageClientPortals: Boolean
)

class LegeArtisViewModel(
    private val repository: LegeArtisRepository
) : ViewModel() {

    val allVariants: StateFlow<List<VariantEntity>> = repository.variants
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val syncPackets: StateFlow<List<QMaxSyncPacketEntity>> = repository.syncPackets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val consultingDocs: StateFlow<List<ConsultingDocEntity>> = repository.consultingDocs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val documentVersions: StateFlow<List<DocumentVersionEntity>> = repository.documentVersions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val clientPortals: StateFlow<List<ClientPortalEntity>> = repository.clientPortals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activityLogs: StateFlow<List<ActivityLogEntity>> = repository.activityLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Theme & Role State
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _activeRole = MutableStateFlow("Lead Bioinformatician")
    val activeRole: StateFlow<String> = _activeRole.asStateFlow()

    private val _rolePolicies = MutableStateFlow(
        listOf(
            RolePermissionPolicy("Lead Bioinformatician", true, true, true, true),
            RolePermissionPolicy("Clinical Geneticist", true, true, true, false),
            RolePermissionPolicy("Legal Counsel", false, true, true, true),
            RolePermissionPolicy("Client Reviewer", false, false, false, false)
        )
    )
    val rolePolicies: StateFlow<List<RolePermissionPolicy>> = _rolePolicies.asStateFlow()

    // NGS Filter & Micro-Interaction State
    private val _negLog10PThreshold = MutableStateFlow(2.5f) // -log10(p) >= 2.5 -> p <= 0.00316
    val negLog10PThreshold: StateFlow<Float> = _negLog10PThreshold.asStateFlow()

    private val _minVafThreshold = MutableStateFlow(0.10f)
    val minVafThreshold: StateFlow<Float> = _minVafThreshold.asStateFlow()

    private val _minPhredQuality = MutableStateFlow(20)
    val minPhredQuality: StateFlow<Int> = _minPhredQuality.asStateFlow()

    private val _selectedClassFilter = MutableStateFlow("ALL")
    val selectedClassFilter: StateFlow<String> = _selectedClassFilter.asStateFlow()

    private val _excludeArtifacts = MutableStateFlow(true)
    val excludeArtifacts: StateFlow<Boolean> = _excludeArtifacts.asStateFlow()

    private val _selectedVariantId = MutableStateFlow<Long?>(null)
    val selectedVariantId: StateFlow<Long?> = _selectedVariantId.asStateFlow()

    private val _chromatogramScrubNormalized = MutableStateFlow(0.5f)
    val chromatogramScrubNormalized: StateFlow<Float> = _chromatogramScrubNormalized.asStateFlow()

    private val _helixPulseSequence = MutableStateFlow(0)
    val helixPulseSequence: StateFlow<Int> = _helixPulseSequence.asStateFlow()

    // Lege Artis Legal Calculator toggles
    private val _informedConsentVerified = MutableStateFlow(true)
    val informedConsentVerified: StateFlow<Boolean> = _informedConsentVerified.asStateFlow()

    private val _dualSignatureVerified = MutableStateFlow(true)
    val dualSignatureVerified: StateFlow<Boolean> = _dualSignatureVerified.asStateFlow()

    private val _singleSubmissionLocked = MutableStateFlow(true)
    val singleSubmissionLocked: StateFlow<Boolean> = _singleSubmissionLocked.asStateFlow()

    // Q-MAX Cypher & Binary Sync Interactive State
    private val _cypherPlainInput = MutableStateFlow("NGS-RUN-2026|BRCA1:c.5266dupC|Q39|T=1_PODANI")
    val cypherPlainInput: StateFlow<String> = _cypherPlainInput.asStateFlow()

    private val _cypherSaltHex = MutableStateFlow(QMaxCypherEngine.generateRandomSaltHex(8))
    val cypherSaltHex: StateFlow<String> = _cypherSaltHex.asStateFlow()

    private val _toggledBitIndices = MutableStateFlow<Set<Int>>(emptySet())
    val toggledBitIndices: StateFlow<Set<Int>> = _toggledBitIndices.asStateFlow()

    private val _tensionLoad = MutableStateFlow(0.38f)
    val tensionLoad: StateFlow<Float> = _tensionLoad.asStateFlow()

    private val _reinforcementRate = MutableStateFlow(0.91f)
    val reinforcementRate: StateFlow<Float> = _reinforcementRate.asStateFlow()

    private val _cypherResult = MutableStateFlow(
        QMaxCypherEngine.computeCypherState(
            plainInput = _cypherPlainInput.value,
            saltHex = _cypherSaltHex.value,
            toggledBitIndices = emptySet(),
            tensionLoad = 0.38f,
            reinforcementRate = 0.91f
        )
    )
    val cypherResult: StateFlow<QMaxCypherResult> = _cypherResult.asStateFlow()

    // Metasimulator & Semantic Satire State
    private val _metasimReport = MutableStateFlow(
        QMaxCypherEngine.runMetasimCalibration(
            variantGene = "BRCA1",
            hgvs = "c.5266dupC",
            pValue = 1.2e-9,
            phredQuality = 39,
            tensionLoad = 0.38f,
            reinforcementRate = 0.91f
        )
    )
    val metasimReport: StateFlow<MetasimCalibrationReport> = _metasimReport.asStateFlow()

    private val _satireOutput = MutableStateFlow(
        QMaxCypherEngine.generateSemanticSatire(
            topic = "Klinická validace somatických a germinálních mutací",
            targetGene = "BRCA1 (c.5266dupC)",
            phredScore = 39,
            legeArtisScore = 94
        )
    )
    val satireOutput: StateFlow<SemanticSatireOutput> = _satireOutput.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
        }
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun setActiveRole(role: String) {
        _activeRole.value = role
        viewModelScope.launch {
            repository.logAction(
                actorRole = role,
                actionType = "RBAC_UPDATED",
                details = "Switched active operator session to role: $role"
            )
        }
    }

    fun currentPolicy(): RolePermissionPolicy {
        return _rolePolicies.value.find { it.roleName == _activeRole.value }
            ?: _rolePolicies.value.first()
    }

    fun toggleRolePermission(roleName: String, permissionField: String) {
        _rolePolicies.value = _rolePolicies.value.map { policy ->
            if (policy.roleName != roleName) policy
            else when (permissionField) {
                "VARIANT" -> policy.copy(canReclassifyVariants = !policy.canReclassifyVariants)
                "QMAX" -> policy.copy(canExecuteQMaxSync = !policy.canExecuteQMaxSync)
                "VERSION" -> policy.copy(canCommitDocVersions = !policy.canCommitDocVersions)
                "PORTAL" -> policy.copy(canManageClientPortals = !policy.canManageClientPortals)
                else -> policy
            }
        }
        viewModelScope.launch {
            repository.logAction(
                actorRole = _activeRole.value,
                actionType = "RBAC_UPDATED",
                details = "Updated $permissionField permission flag for role '$roleName'",
                severity = "WARNING"
            )
        }
    }

    // NGS Filter Setters
    fun updateNegLog10PThreshold(value: Float) {
        _negLog10PThreshold.value = value
    }

    fun updateMinVafThreshold(value: Float) {
        _minVafThreshold.value = value
    }

    fun updateMinPhredQuality(value: Int) {
        _minPhredQuality.value = value
    }

    fun updateClassFilter(classification: String) {
        _selectedClassFilter.value = classification
    }

    fun toggleExcludeArtifacts() {
        _excludeArtifacts.value = !_excludeArtifacts.value
    }

    fun selectVariant(variant: VariantEntity) {
        _selectedVariantId.value = variant.id
        _helixPulseSequence.value += 1
        // Map genomic position to normalized chromatogram peak focus
        val normalized = ((variant.position % 1000L).toFloat() / 1000f).coerceIn(0.15f, 0.85f)
        _chromatogramScrubNormalized.value = normalized
        // Sync selected variant into Q-MAX cypher input
        _cypherPlainInput.value = "NGS|${variant.geneSymbol}:${variant.hgvsCoding}|VAF:${variant.alleleFrequency}|Q${variant.phredQuality}"
        recomputeCypher()
    }

    fun updateChromatogramScrub(normalizedX: Float) {
        _chromatogramScrubNormalized.value = normalizedX.coerceIn(0.02f, 0.98f)
    }

    fun toggleInformedConsent() {
        _informedConsentVerified.value = !_informedConsentVerified.value
    }

    fun toggleDualSignature() {
        _dualSignatureVerified.value = !_dualSignatureVerified.value
    }

    fun toggleSingleSubmissionLocked() {
        _singleSubmissionLocked.value = !_singleSubmissionLocked.value
    }

    fun computeLegeArtisScore(filteredVariants: List<VariantEntity>): Int {
        if (filteredVariants.isEmpty()) return 75
        val avgPhred = filteredVariants.map { it.phredQuality }.average().toFloat()
        val artifactPenalty = filteredVariants.count { it.classification == "SEQUENCING_ARTIFACT" } * 12
        val consentBonus = if (_informedConsentVerified.value) 12 else -15
        val dualBonus = if (_dualSignatureVerified.value) 10 else -8
        val t1Bonus = if (_singleSubmissionLocked.value) 8 else 0
        val raw = (avgPhred * 1.75f).roundToInt() + consentBonus + dualBonus + t1Bonus - artifactPenalty
        return raw.coerceIn(18, 99)
    }

    fun reclassifyVariant(variant: VariantEntity, newClassification: String) {
        if (!currentPolicy().canReclassifyVariants) return
        viewModelScope.launch {
            repository.updateVariantClassification(variant, newClassification, _activeRole.value)
        }
    }

    // Q-MAX Cypher & Binary Bit-Matrix Actions
    fun updateCypherPlainInput(input: String) {
        _cypherPlainInput.value = input
        recomputeCypher()
    }

    fun regenerateRandomSalt() {
        _cypherSaltHex.value = QMaxCypherEngine.generateRandomSaltHex(8)
        _toggledBitIndices.value = emptySet()
        recomputeCypher()
    }

    fun toggleMatrixBit(bitIndex: Int) {
        val current = _toggledBitIndices.value.toMutableSet()
        if (current.contains(bitIndex)) {
            current.remove(bitIndex)
        } else {
            current.add(bitIndex)
        }
        _toggledBitIndices.value = current
        recomputeCypher()
    }

    fun clearToggledBits() {
        _toggledBitIndices.value = emptySet()
        recomputeCypher()
    }

    fun updateTensionAndReinforcement(tension: Float, reinforcement: Float) {
        _tensionLoad.value = tension.coerceIn(0.05f, 0.95f)
        _reinforcementRate.value = reinforcement.coerceIn(0.10f, 0.99f)
        recomputeCypher()
    }

    private fun recomputeCypher() {
        _cypherResult.value = QMaxCypherEngine.computeCypherState(
            plainInput = _cypherPlainInput.value,
            saltHex = _cypherSaltHex.value,
            toggledBitIndices = _toggledBitIndices.value,
            tensionLoad = _tensionLoad.value,
            reinforcementRate = _reinforcementRate.value
        )
    }

    fun commitCurrentSyncPacket(sourceType: String = "NGS_VARIANT") {
        if (!currentPolicy().canExecuteQMaxSync) return
        val result = _cypherResult.value
        viewModelScope.launch {
            repository.saveSyncPacket(
                packet = QMaxSyncPacketEntity(
                    packetHeaderHex = result.syncPacketFrame,
                    sourceType = sourceType,
                    plainSummary = result.plainInput.take(64),
                    saltHex = result.saltHex,
                    sha256ChainHash = result.sha256Hex,
                    cipherPayloadHex = result.cipherPayloadHex,
                    binaryMatrixSnapshot = result.binaryMatrix128.joinToString(""),
                    tensionResilienceScore = result.tensionResilienceScore,
                    syncStatus = if (_singleSubmissionLocked.value) "T1_SUBMITTED" else "VERIFIED_LOCAL"
                ),
                actorRole = _activeRole.value
            )
        }
    }

    // Metasimulator & Stin v kolonce Actions
    fun runMetasimulationForSelectedVariant() {
        val selected = allVariants.value.find { it.id == _selectedVariantId.value }
            ?: allVariants.value.firstOrNull()
        val gene = selected?.geneSymbol ?: "BRCA1"
        val hgvs = selected?.hgvsCoding ?: "c.5266dupC"
        val pVal = selected?.pValue ?: 1.2e-9
        val phred = selected?.phredQuality ?: 39

        val report = QMaxCypherEngine.runMetasimCalibration(
            variantGene = gene,
            hgvs = hgvs,
            pValue = pVal,
            phredQuality = phred,
            tensionLoad = _tensionLoad.value,
            reinforcementRate = _reinforcementRate.value
        )
        _metasimReport.value = report

        viewModelScope.launch {
            repository.logAction(
                actorRole = _activeRole.value,
                actionType = "METASIM_RUN",
                details = "Executed W4 MAP-Processing [${report.schemeId}] for $gene $hgvs -> Token ${report.singleSubmissionToken}",
                severity = "INFO"
            )
        }
    }

    fun generateSemanticAuditSatire(customTopic: String) {
        val selected = allVariants.value.find { it.id == _selectedVariantId.value }
            ?: allVariants.value.firstOrNull()
        val gene = selected?.let { "${it.geneSymbol} (${it.hgvsCoding})" } ?: "BRCA1 (c.5266dupC)"
        val phred = selected?.phredQuality ?: 39
        val score = computeLegeArtisScore(allVariants.value)

        _satireOutput.value = QMaxCypherEngine.generateSemanticSatire(
            topic = customTopic.ifBlank { "Klinická interpretace NGS a limit T=1 podání" },
            targetGene = gene,
            phredScore = phred,
            legeArtisScore = score
        )
    }

    // Consulting Portal & Version Tracking Actions
    fun commitNewDocumentVersion(doc: ConsultingDocEntity, changelog: String) {
        if (!currentPolicy().canCommitDocVersions) return
        val parts = doc.currentVersion.removePrefix("v").split(".")
        val major = parts.getOrNull(0)?.toIntOrNull() ?: 1
        val minor = (parts.getOrNull(1)?.toIntOrNull() ?: 0) + 1
        val nextVersion = "v$major.$minor"
        val currentScore = computeLegeArtisScore(allVariants.value)

        viewModelScope.launch {
            repository.commitNewDocVersion(
                doc = doc,
                newVersionTag = nextVersion,
                changelog = changelog,
                newLegalScore = currentScore,
                actorRole = _activeRole.value
            )
        }
    }

    fun createNewConsultingDossier(title: String, caseRef: String, sampleId: String, summary: String) {
        if (!currentPolicy().canCommitDocVersions) return
        val score = computeLegeArtisScore(allVariants.value)
        viewModelScope.launch {
            repository.createConsultingDossier(
                title = title,
                caseRef = caseRef,
                sampleId = sampleId,
                summary = summary,
                legalScore = score,
                actorRole = _activeRole.value
            )
        }
    }

    fun togglePortalStatus(portal: ClientPortalEntity) {
        if (!currentPolicy().canManageClientPortals) return
        viewModelScope.launch {
            repository.toggleClientPortalActive(portal, _activeRole.value)
        }
    }

    fun createEncryptedClientPortal(
        title: String,
        org: String,
        caseRef: String,
        passphrase: String,
        permissionLevel: String
    ) {
        if (!currentPolicy().canManageClientPortals) return
        viewModelScope.launch {
            repository.createClientPortal(
                title = title,
                organization = org,
                caseRef = caseRef,
                passphrase = passphrase,
                permissionLevel = permissionLevel,
                actorRole = _activeRole.value
            )
        }
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val db = LegeArtisDatabase.getInstance(context)
                val repo = LegeArtisRepository(db.legeArtisDao())
                LegeArtisViewModel(repo)
            }
        }
    }
}
