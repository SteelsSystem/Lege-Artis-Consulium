package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "genomic_variants")
data class VariantEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val geneSymbol: String,
    val chromosome: String,
    val position: Long,
    val hgvsCoding: String,
    val hgvsProtein: String,
    val refAllele: String,
    val altAllele: String,
    val alleleFrequency: Float, // VAF 0.0 - 1.0
    val pValue: Double,
    val phredQuality: Int,      // Q-score (e.g., 14 to 42)
    val readDepth: Int,         // Coverage depth (e.g., 45x to 620x)
    val classification: String, // PATHOGENIC, LIKELY_PATHOGENIC, VUS, BENIGN, SEQUENCING_ARTIFACT
    val qMaxHashSignature: String,
    val clinicalNotes: String,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "qmax_sync_packets")
data class QMaxSyncPacketEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packetHeaderHex: String,
    val sourceType: String, // NGS_VARIANT, LEGAL_DOSSIER, METASIM_CALIBRATION
    val plainSummary: String,
    val saltHex: String,
    val sha256ChainHash: String,
    val cipherPayloadHex: String,
    val binaryMatrixSnapshot: String, // 128 chars of '0'/'1'
    val tensionResilienceScore: Float,
    val syncStatus: String, // VERIFIED_LOCAL, SYNC_LOCKED, T1_SUBMITTED
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "consulting_docs")
data class ConsultingDocEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val caseReference: String,
    val patientSampleId: String,
    val currentVersion: String,
    val contentSummary: String,
    val legalRiskScore: Int, // 0 - 100 Lege Artis compliance index
    val submissionMode: String, // e.g., "T=1 podání (Locked)" or "Draft Revision"
    val encryptionStatus: String,
    val allowedRolesCsv: String,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "document_versions")
data class DocumentVersionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val documentId: Long,
    val versionTag: String,
    val changelog: String,
    val sha256Hash: String,
    val authorRole: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "client_portals")
data class ClientPortalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val portalTitle: String,
    val clientOrganization: String,
    val documentCaseRef: String,
    val accessPassphrase: String,
    val qMaxTokenHash: String,
    val encryptionCipher: String,
    val permissionLevel: String, // READ_ONLY, COMMENT_SIGN, FULL_AUDIT
    val isActive: Boolean,
    val expiresAtLabel: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val actorRole: String,
    val actionType: String, // VARIANT_RECLASSIFIED, QMAX_SYNC, METASIM_RUN, VERSION_COMMITTED, PORTAL_ACCESS, RBAC_UPDATED
    val details: String,
    val severity: String, // INFO, WARNING, CRITICAL
    val createdAt: Long = System.currentTimeMillis()
)
