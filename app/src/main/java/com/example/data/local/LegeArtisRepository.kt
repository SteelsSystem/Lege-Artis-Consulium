package com.example.data.local

import com.example.crypto.QMaxCypherEngine
import kotlinx.coroutines.flow.Flow

class LegeArtisRepository(private val dao: LegeArtisDao) {

    val variants: Flow<List<VariantEntity>> = dao.observeVariants()
    val syncPackets: Flow<List<QMaxSyncPacketEntity>> = dao.observeSyncPackets()
    val consultingDocs: Flow<List<ConsultingDocEntity>> = dao.observeConsultingDocs()
    val documentVersions: Flow<List<DocumentVersionEntity>> = dao.observeDocumentVersions()
    val clientPortals: Flow<List<ClientPortalEntity>> = dao.observeClientPortals()
    val activityLogs: Flow<List<ActivityLogEntity>> = dao.observeActivityLogs()

    suspend fun ensureSeeded() {
        if (dao.getVariantCount() > 0) return

        val initialVariants = listOf(
            createSeededVariant(
                gene = "BRCA1", chr = "chr17", pos = 43094464L,
                coding = "c.5266dupC", protein = "p.Gln1756Profs*74",
                ref = "G", alt = "GC", vaf = 0.48f, pVal = 1.2e-9, phred = 39, depth = 412,
                classification = "PATHOGENIC",
                notes = "ACMG PVS1+PS4+PM2: High-penetrance frameshift allele; mandatory clinical-legal disclosure under Lege Artis protocol."
            ),
            createSeededVariant(
                gene = "TP53", chr = "chr17", pos = 7674220L,
                coding = "c.743G>A", protein = "p.Arg248Gln",
                ref = "C", alt = "T", vaf = 0.34f, pVal = 4.5e-8, phred = 38, depth = 520,
                classification = "PATHOGENIC",
                notes = "Hotspot DNA-binding domain missense mutation; strong somatic & germline oncogenic driver."
            ),
            createSeededVariant(
                gene = "EGFR", chr = "chr7", pos = 55191822L,
                coding = "c.2573T>G", protein = "p.Leu858Arg",
                ref = "T", alt = "G", vaf = 0.29f, pVal = 2.8e-7, phred = 37, depth = 610,
                classification = "PATHOGENIC",
                notes = "Actionable kinase domain activating mutation responsive to targeted TKI therapy."
            ),
            createSeededVariant(
                gene = "BRAF", chr = "chr7", pos = 140753336L,
                coding = "c.1799T>A", protein = "p.Val600Glu",
                ref = "A", alt = "T", vaf = 0.41f, pVal = 8.1e-9, phred = 40, depth = 485,
                classification = "PATHOGENIC",
                notes = "Class I BRAF V600E pathogenic driver verified by bidirectional Sanger & Q-MAX hash."
            ),
            createSeededVariant(
                gene = "CFTR", chr = "chr7", pos = 117559590L,
                coding = "c.1521_1523delCTT", protein = "p.Phe508del",
                ref = "ATCT", alt = "A", vaf = 0.51f, pVal = 3.4e-8, phred = 36, depth = 340,
                classification = "LIKELY_PATHOGENIC",
                notes = "In-frame deletion affecting ATP-gated chloride channel folding."
            ),
            createSeededVariant(
                gene = "KRAS", chr = "chr12", pos = 25245350L,
                coding = "c.35G>A", protein = "p.Gly12Asp",
                ref = "C", alt = "T", vaf = 0.22f, pVal = 1.9e-6, phred = 35, depth = 390,
                classification = "PATHOGENIC",
                notes = "Codon 12 GTPase activating variant associated with anti-EGFR resistance."
            ),
            createSeededVariant(
                gene = "PIK3CA", chr = "chr3", pos = 179234297L,
                coding = "c.3140A>G", protein = "p.His1047Arg",
                ref = "A", alt = "G", vaf = 0.19f, pVal = 6.2e-5, phred = 33, depth = 275,
                classification = "LIKELY_PATHOGENIC",
                notes = "Catalytic subunit kinase domain gain-of-function variant."
            ),
            createSeededVariant(
                gene = "MLH1", chr = "chr3", pos = 37053568L,
                coding = "c.655A>G", protein = "p.Ile219Val",
                ref = "A", alt = "G", vaf = 0.49f, pVal = 0.018, phred = 34, depth = 290,
                classification = "BENIGN",
                notes = "Common polymorphic mismatch repair variant with intact functional assay."
            ),
            createSeededVariant(
                gene = "ALK", chr = "chr2", pos = 29220829L,
                coding = "c.3522C>A", protein = "p.Phe1174Leu",
                ref = "G", alt = "T", vaf = 0.14f, pVal = 0.00042, phred = 29, depth = 195,
                classification = "VUS",
                notes = "Variant of Uncertain Significance; requires longitudinal follow-up and structural docking review."
            ),
            createSeededVariant(
                gene = "APC", chr = "chr5", pos = 112839514L,
                coding = "c.3920T>A", protein = "p.Ile1307Lys",
                ref = "T", alt = "A", vaf = 0.46f, pVal = 0.0021, phred = 32, depth = 260,
                classification = "VUS",
                notes = "Low-penetrance hypermutable tract allele; borderline medico-legal disclosure threshold."
            ),
            createSeededVariant(
                gene = "PTEN", chr = "chr10", pos = 87933147L,
                coding = "c.388C>T", protein = "p.Arg130Ter",
                ref = "C", alt = "T", vaf = 0.04f, pVal = 0.084, phred = 14, depth = 48,
                classification = "SEQUENCING_ARTIFACT",
                notes = "CRITICAL QC WARNING: Homopolymer slip & Phred Q14 base-call noise; exclude from clinical reporting."
            ),
            createSeededVariant(
                gene = "ERBB2", chr = "chr17", pos = 39724728L,
                coding = "c.2329G>T", protein = "p.Val777Leu",
                ref = "G", alt = "T", vaf = 0.03f, pVal = 0.14, phred = 16, depth = 52,
                classification = "SEQUENCING_ARTIFACT",
                notes = "High strand-bias technical artifact near adapter dimer; flagged by Q-MAX QC filter."
            )
        )
        dao.insertVariants(initialVariants)

        val salt1 = QMaxCypherEngine.generateRandomSaltHex(6)
        val state1 = QMaxCypherEngine.computeCypherState(
            plainInput = "NGS-RUN-2026-BRCA1-c.5266dupC|Q39|VAF:0.48",
            saltHex = salt1,
            toggledBitIndices = emptySet()
        )
        dao.insertSyncPacket(
            QMaxSyncPacketEntity(
                packetHeaderHex = state1.syncPacketFrame,
                sourceType = "NGS_VARIANT",
                plainSummary = "BRCA1 c.5266dupC Pathogenic Call (Q39)",
                saltHex = salt1,
                sha256ChainHash = state1.sha256Hex,
                cipherPayloadHex = state1.cipherPayloadHex,
                binaryMatrixSnapshot = state1.binaryMatrix128.joinToString(""),
                tensionResilienceScore = state1.tensionResilienceScore,
                syncStatus = "T1_SUBMITTED"
            )
        )

        val salt2 = QMaxCypherEngine.generateRandomSaltHex(6)
        val state2 = QMaxCypherEngine.computeCypherState(
            plainInput = "DOSSIER-LAC-2026-09|T=1_PODANI|LEGE_ARTIS_94%",
            saltHex = salt2,
            toggledBitIndices = emptySet()
        )
        dao.insertSyncPacket(
            QMaxSyncPacketEntity(
                packetHeaderHex = state2.syncPacketFrame,
                sourceType = "LEGAL_DOSSIER",
                plainSummary = "Lege Artis Oncology Panel Dossier #LAC-2026-09",
                saltHex = salt2,
                sha256ChainHash = state2.sha256Hex,
                cipherPayloadHex = state2.cipherPayloadHex,
                binaryMatrixSnapshot = state2.binaryMatrix128.joinToString(""),
                tensionResilienceScore = state2.tensionResilienceScore,
                syncStatus = "VERIFIED_LOCAL"
            )
        )

        val doc1Id = dao.insertConsultingDoc(
            ConsultingDocEntity(
                title = "BRCA1/TP53 Hereditary Oncology Medico-Legal Opinion",
                caseReference = "LAC-2026-09A",
                patientSampleId = "SMP-NGS-8841",
                currentVersion = "v1.2",
                contentSummary = "Confirmed pathogenic BRCA1 c.5266dupC & TP53 p.Arg248Gln variants. Excluded PTEN Q14 sequencing artifact per Lege Artis diagnostic duty of care.",
                legalRiskScore = 94,
                submissionMode = "T=1 podání (Locked)",
                encryptionStatus = "Q-MAX AES-256-GCM + Salted SHA-256",
                allowedRolesCsv = "Lead Bioinformatician,Clinical Geneticist,Legal Counsel,Client Reviewer"
            )
        )

        val doc2Id = dao.insertConsultingDoc(
            ConsultingDocEntity(
                title = "EGFR/BRAF Targeted Therapy Liability & QC Audit",
                caseReference = "LAC-2026-14B",
                patientSampleId = "SMP-NGS-9104",
                currentVersion = "v1.0",
                contentSummary = "Evaluation of actionable EGFR L858R and BRAF V600E calls alongside Q-MAX binary sync attestation for institutional compliance.",
                legalRiskScore = 89,
                submissionMode = "W4 Calibration Ready",
                encryptionStatus = "Q-MAX AES-256-GCM + Salted SHA-256",
                allowedRolesCsv = "Lead Bioinformatician,Legal Counsel"
            )
        )

        dao.insertDocumentVersion(
            DocumentVersionEntity(
                documentId = doc1Id,
                versionTag = "v1.0",
                changelog = "Initial NGS variant ingest and raw ACMG/AMP classification.",
                sha256Hash = QMaxCypherEngine.sha256Hex("LAC-2026-09A|v1.0", "A100").take(32),
                authorRole = "Lead Bioinformatician"
            )
        )
        dao.insertDocumentVersion(
            DocumentVersionEntity(
                documentId = doc1Id,
                versionTag = "v1.1",
                changelog = "Filtered out PTEN Q14 homopolymer artifact; updated Lege Artis compliance calculation.",
                sha256Hash = QMaxCypherEngine.sha256Hex("LAC-2026-09A|v1.1", "B200").take(32),
                authorRole = "Clinical Geneticist"
            )
        )
        dao.insertDocumentVersion(
            DocumentVersionEntity(
                documentId = doc1Id,
                versionTag = "v1.2",
                changelog = "Finalized legal risk attestation under T=1 podání single-submission protocol.",
                sha256Hash = QMaxCypherEngine.sha256Hex("LAC-2026-09A|v1.2", "C300").take(32),
                authorRole = "Legal Counsel"
            )
        )
        dao.insertDocumentVersion(
            DocumentVersionEntity(
                documentId = doc2Id,
                versionTag = "v1.0",
                changelog = "Baseline W4 calibration dossier encrypted with Q-MAX binary block cypher.",
                sha256Hash = QMaxCypherEngine.sha256Hex("LAC-2026-14B|v1.0", "D400").take(32),
                authorRole = "Lead Bioinformatician"
            )
        )

        dao.insertClientPortal(
            ClientPortalEntity(
                portalTitle = "University Hospital Molecular Tumor Board Portal",
                clientOrganization = "FN Motol / Bio-Legal Review Board",
                documentCaseRef = "LAC-2026-09A",
                accessPassphrase = "QMAX-2026",
                qMaxTokenHash = state1.sha256Hex.take(24),
                encryptionCipher = "Q-MAX 128-Bit + AES-256-GCM",
                permissionLevel = "COMMENT_SIGN",
                isActive = true,
                expiresAtLabel = "Valid 30 Days (Offline Sync Locked)"
            )
        )
        dao.insertClientPortal(
            ClientPortalEntity(
                portalTitle = "External Regulatory & Insurance Counsel Vault",
                clientOrganization = "Consulium Lex & Partners",
                documentCaseRef = "LAC-2026-14B",
                accessPassphrase = "LEGE-77",
                qMaxTokenHash = state2.sha256Hex.take(24),
                encryptionCipher = "Q-MAX 128-Bit + Salted SHA-512",
                permissionLevel = "READ_ONLY",
                isActive = true,
                expiresAtLabel = "Single-Submission (T=1) Immutable"
            )
        )

        dao.insertActivityLog(
            ActivityLogEntity(
                actorRole = "Lead Bioinformatician",
                actionType = "QMAX_SYNC",
                details = "Initialized Offline Room Q-MAX Cypher Database with 12 NGS loci and 128-bit binary sync frames.",
                severity = "INFO"
            )
        )
        dao.insertActivityLog(
            ActivityLogEntity(
                actorRole = "Clinical Geneticist",
                actionType = "VARIANT_RECLASSIFIED",
                details = "Flagged PTEN c.388C>T (Phred Q14) as SEQUENCING_ARTIFACT to prevent false-positive reporting.",
                severity = "WARNING"
            )
        )
        dao.insertActivityLog(
            ActivityLogEntity(
                actorRole = "Legal Counsel",
                actionType = "VERSION_COMMITTED",
                details = "Signed Dossier LAC-2026-09A v1.2 under T=1 podání (Lege Artis score 94%).",
                severity = "CRITICAL"
            )
        )
    }

    private fun createSeededVariant(
        gene: String,
        chr: String,
        pos: Long,
        coding: String,
        protein: String,
        ref: String,
        alt: String,
        vaf: Float,
        pVal: Double,
        phred: Int,
        depth: Int,
        classification: String,
        notes: String
    ): VariantEntity {
        val sig = QMaxCypherEngine.sha256Hex("$gene|$chr:$pos|$coding|$vaf", "SEED").take(16)
        return VariantEntity(
            geneSymbol = gene,
            chromosome = chr,
            position = pos,
            hgvsCoding = coding,
            hgvsProtein = protein,
            refAllele = ref,
            altAllele = alt,
            alleleFrequency = vaf,
            pValue = pVal,
            phredQuality = phred,
            readDepth = depth,
            classification = classification,
            qMaxHashSignature = sig,
            clinicalNotes = notes
        )
    }

    suspend fun updateVariantClassification(
        variant: VariantEntity,
        newClassification: String,
        actorRole: String
    ) {
        val newSig = QMaxCypherEngine.sha256Hex(
            "${variant.geneSymbol}|${variant.hgvsCoding}|$newClassification|${System.currentTimeMillis()}",
            "UPD"
        ).take(16)
        dao.updateVariant(
            variant.copy(
                classification = newClassification,
                qMaxHashSignature = newSig,
                updatedAt = System.currentTimeMillis()
            )
        )
        dao.insertActivityLog(
            ActivityLogEntity(
                actorRole = actorRole,
                actionType = "VARIANT_RECLASSIFIED",
                details = "Reclassified ${variant.geneSymbol} (${variant.hgvsCoding}) to $newClassification [Q-MAX:$newSig]",
                severity = if (newClassification == "PATHOGENIC") "CRITICAL" else "INFO"
            )
        )
    }

    suspend fun saveSyncPacket(packet: QMaxSyncPacketEntity, actorRole: String) {
        dao.insertSyncPacket(packet)
        dao.insertActivityLog(
            ActivityLogEntity(
                actorRole = actorRole,
                actionType = "QMAX_SYNC",
                details = "Committed deterministic binary sync packet ${packet.packetHeaderHex.take(28)}... (Resilience ${(packet.tensionResilienceScore * 100).toInt()}%)",
                severity = "INFO"
            )
        )
    }

    suspend fun commitNewDocVersion(
        doc: ConsultingDocEntity,
        newVersionTag: String,
        changelog: String,
        newLegalScore: Int,
        actorRole: String
    ) {
        val hash = QMaxCypherEngine.sha256Hex("${doc.caseReference}|$newVersionTag|$changelog", "VER").take(32)
        dao.updateConsultingDoc(
            doc.copy(
                currentVersion = newVersionTag,
                contentSummary = changelog,
                legalRiskScore = newLegalScore,
                updatedAt = System.currentTimeMillis()
            )
        )
        dao.insertDocumentVersion(
            DocumentVersionEntity(
                documentId = doc.id,
                versionTag = newVersionTag,
                changelog = changelog,
                sha256Hash = hash,
                authorRole = actorRole
            )
        )
        dao.insertActivityLog(
            ActivityLogEntity(
                actorRole = actorRole,
                actionType = "VERSION_COMMITTED",
                details = "Committed ${doc.caseReference} $newVersionTag [SHA-256: ${hash.take(12)}...] by $actorRole",
                severity = "INFO"
            )
        )
    }

    suspend fun createConsultingDossier(
        title: String,
        caseRef: String,
        sampleId: String,
        summary: String,
        legalScore: Int,
        actorRole: String
    ) {
        val hash = QMaxCypherEngine.sha256Hex("$caseRef|v1.0|$summary", "NEW").take(32)
        val docId = dao.insertConsultingDoc(
            ConsultingDocEntity(
                title = title,
                caseReference = caseRef,
                patientSampleId = sampleId,
                currentVersion = "v1.0",
                contentSummary = summary,
                legalRiskScore = legalScore,
                submissionMode = "T=1 podání (Locked)",
                encryptionStatus = "Q-MAX AES-256-GCM + Salted SHA-256",
                allowedRolesCsv = "Lead Bioinformatician,Clinical Geneticist,Legal Counsel,Client Reviewer"
            )
        )
        dao.insertDocumentVersion(
            DocumentVersionEntity(
                documentId = docId,
                versionTag = "v1.0",
                changelog = summary,
                sha256Hash = hash,
                authorRole = actorRole
            )
        )
        dao.insertActivityLog(
            ActivityLogEntity(
                actorRole = actorRole,
                actionType = "VERSION_COMMITTED",
                details = "Created new dossier $caseRef v1.0 ($title) with Q-MAX hash ${hash.take(12)}...",
                severity = "INFO"
            )
        )
    }

    suspend fun toggleClientPortalActive(portal: ClientPortalEntity, actorRole: String) {
        val updated = portal.copy(isActive = !portal.isActive)
        dao.updateClientPortal(updated)
        dao.insertActivityLog(
            ActivityLogEntity(
                actorRole = actorRole,
                actionType = "PORTAL_ACCESS",
                details = "${if (updated.isActive) "Activated" else "Revoked"} encrypted client portal '${portal.portalTitle}' (${portal.documentCaseRef})",
                severity = if (updated.isActive) "INFO" else "WARNING"
            )
        )
    }

    suspend fun createClientPortal(
        title: String,
        organization: String,
        caseRef: String,
        passphrase: String,
        permissionLevel: String,
        actorRole: String
    ) {
        val salt = QMaxCypherEngine.generateRandomSaltHex(4)
        val tokenHash = QMaxCypherEngine.sha256Hex("$title|$organization|$caseRef|$passphrase", salt).take(24)
        dao.insertClientPortal(
            ClientPortalEntity(
                portalTitle = title,
                clientOrganization = organization,
                documentCaseRef = caseRef,
                accessPassphrase = passphrase,
                qMaxTokenHash = tokenHash,
                encryptionCipher = "Q-MAX 128-Bit + AES-256-GCM",
                permissionLevel = permissionLevel,
                isActive = true,
                expiresAtLabel = "T=1 Offline Sync Verified"
            )
        )
        dao.insertActivityLog(
            ActivityLogEntity(
                actorRole = actorRole,
                actionType = "PORTAL_ACCESS",
                details = "Provisioned encrypted portal '$title' for $organization [Token: ${tokenHash.take(10)}...]",
                severity = "INFO"
            )
        )
    }

    suspend fun logAction(actorRole: String, actionType: String, details: String, severity: String = "INFO") {
        dao.insertActivityLog(
            ActivityLogEntity(
                actorRole = actorRole,
                actionType = actionType,
                details = details,
                severity = severity
            )
        )
    }
}
