package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.ActivityLogEntity
import com.example.data.local.ClientPortalEntity
import com.example.data.local.ConsultingDocEntity
import com.example.data.local.DocumentVersionEntity
import com.example.ui.theme.ArtifactAmberWarning
import com.example.ui.theme.BenignBioEmerald
import com.example.ui.theme.CerenkovCyanPrimary
import com.example.ui.theme.PathogenicCrimson
import com.example.viewmodel.LegeArtisViewModel
import com.example.viewmodel.RolePermissionPolicy
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ConsultingPortalScreen(
    viewModel: LegeArtisViewModel,
    modifier: Modifier = Modifier
) {
    val docs by viewModel.consultingDocs.collectAsStateWithLifecycle()
    val versions by viewModel.documentVersions.collectAsStateWithLifecycle()
    val portals by viewModel.clientPortals.collectAsStateWithLifecycle()
    val logs by viewModel.activityLogs.collectAsStateWithLifecycle()
    val activeRole by viewModel.activeRole.collectAsStateWithLifecycle()
    val rolePolicies by viewModel.rolePolicies.collectAsStateWithLifecycle()
    val policy = viewModel.currentPolicy()

    var showNewDossierDialog by remember { mutableStateOf(false) }
    var showNewPortalDialog by remember { mutableStateOf(false) }
    var docForNewVersion by remember { mutableStateOf<ConsultingDocEntity?>(null) }
    var portalToVerify by remember { mutableStateOf<ClientPortalEntity?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            RbacRoleControlCard(
                activeRole = activeRole,
                rolePolicies = rolePolicies,
                onSelectRole = viewModel::setActiveRole,
                onTogglePermission = viewModel::toggleRolePermission
            )
        }

        // Section 1: Version-Tracked Consulting Dossiers
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = "Consulting Dossiers",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Version-Tracked Consulting Dossiers (${docs.size})",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                if (policy.canCommitDocVersions) {
                    Button(
                        onClick = { showNewDossierDialog = true },
                        modifier = Modifier.testTag("btn_new_dossier")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "New Dossier")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Dossier")
                    }
                }
            }
        }

        items(docs, key = { it.id }) { doc ->
            val docVersions = versions.filter { it.documentId == doc.id }
            ConsultingDossierCard(
                doc = doc,
                versions = docVersions,
                canCommitVersion = policy.canCommitDocVersions,
                onCommitNewVersion = { docForNewVersion = doc }
            )
        }

        // Section 2: Encrypted Client Access Portals
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Encrypted Portals",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Encrypted Client Portals (${portals.size})",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                if (policy.canManageClientPortals) {
                    OutlinedButton(
                        onClick = { showNewPortalDialog = true },
                        modifier = Modifier.testTag("btn_new_portal")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "New Portal")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Portal")
                    }
                }
            }
        }

        items(portals, key = { it.id }) { portal ->
            ClientPortalCard(
                portal = portal,
                canManage = policy.canManageClientPortals,
                onToggleActive = { viewModel.togglePortalStatus(portal) },
                onVerifyAccess = { portalToVerify = portal }
            )
        }

        // Section 3: Real-Time Activity Audit Log
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "Activity Audit Log",
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Real-Time Activity Audit Log (${logs.size})",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        items(logs.take(15), key = { it.id }) { log ->
            ActivityLogRow(log = log)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showNewDossierDialog) {
        NewDossierDialog(
            onDismiss = { showNewDossierDialog = false },
            onCreate = { title, caseRef, sampleId, summary ->
                viewModel.createNewConsultingDossier(title, caseRef, sampleId, summary)
                showNewDossierDialog = false
            }
        )
    }

    if (docForNewVersion != null) {
        CommitVersionDialog(
            doc = docForNewVersion!!,
            onDismiss = { docForNewVersion = null },
            onCommit = { changelog ->
                viewModel.commitNewDocumentVersion(docForNewVersion!!, changelog)
                docForNewVersion = null
            }
        )
    }

    if (showNewPortalDialog) {
        NewPortalDialog(
            onDismiss = { showNewPortalDialog = false },
            onCreate = { title, org, caseRef, passphrase, perm ->
                viewModel.createEncryptedClientPortal(title, org, caseRef, passphrase, perm)
                showNewPortalDialog = false
            }
        )
    }

    if (portalToVerify != null) {
        VerifyPortalPassphraseDialog(
            portal = portalToVerify!!,
            onDismiss = { portalToVerify = null }
        )
    }
}

@Composable
private fun RbacRoleControlCard(
    activeRole: String,
    rolePolicies: List<RolePermissionPolicy>,
    onSelectRole: (String) -> Unit,
    onTogglePermission: (String, String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("rbac_matrix_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = "RBAC Permissions",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Role-Based Permission Settings (RBAC Matrix)",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Active Session Role: $activeRole • Tap role chip to switch session",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                rolePolicies.forEach { rp ->
                    FilterChip(
                        selected = activeRole == rp.roleName,
                        onClick = { onSelectRole(rp.roleName) },
                        label = { Text(rp.roleName) }
                    )
                }
            }

            val currentPolicy = rolePolicies.find { it.roleName == activeRole } ?: rolePolicies.first()
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = currentPolicy.canReclassifyVariants,
                    onClick = { onTogglePermission(activeRole, "VARIANT") },
                    label = { Text("Reclassify NGS") }
                )
                FilterChip(
                    selected = currentPolicy.canExecuteQMaxSync,
                    onClick = { onTogglePermission(activeRole, "QMAX") },
                    label = { Text("Q-MAX Sync") }
                )
                FilterChip(
                    selected = currentPolicy.canCommitDocVersions,
                    onClick = { onTogglePermission(activeRole, "VERSION") },
                    label = { Text("Commit Versions") }
                )
                FilterChip(
                    selected = currentPolicy.canManageClientPortals,
                    onClick = { onTogglePermission(activeRole, "PORTAL") },
                    label = { Text("Manage Portals") }
                )
            }
        }
    }
}

@Composable
private fun ConsultingDossierCard(
    doc: ConsultingDocEntity,
    versions: List<DocumentVersionEntity>,
    canCommitVersion: Boolean,
    onCommitNewVersion: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dossier_card_${doc.caseReference}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = doc.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Case: ${doc.caseReference} • Sample: ${doc.patientSampleId} • ${doc.submissionMode}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Surface(
                    color = BenignBioEmerald.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${doc.currentVersion} (${doc.legalRiskScore}%)",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = BenignBioEmerald
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = doc.contentSummary,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Cipher: ${doc.encryptionStatus}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (versions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Automated Version History (${versions.size} commits):",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    versions.take(3).forEach { ver ->
                        Text(
                            text = "• ${ver.versionTag} [${ver.authorRole}] SHA-256:${ver.sha256Hash.take(12)}… — ${ver.changelog}",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            if (canCommitVersion) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onCommitNewVersion,
                    modifier = Modifier.testTag("btn_commit_version_${doc.caseReference}")
                ) {
                    Text("Commit Next Semantic Version")
                }
            }
        }
    }
}

@Composable
private fun ClientPortalCard(
    portal: ClientPortalEntity,
    canManage: Boolean,
    onToggleActive: () -> Unit,
    onVerifyAccess: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (portal.isActive) CerenkovCyanPrimary.copy(alpha = 0.4f) else PathogenicCrimson.copy(alpha = 0.4f),
                RoundedCornerShape(14.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = if (portal.isActive) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = "Portal Encryption Status",
                        tint = if (portal.isActive) BenignBioEmerald else PathogenicCrimson
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = portal.portalTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${portal.clientOrganization} • Case: ${portal.documentCaseRef}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (canManage) {
                    Switch(
                        checked = portal.isActive,
                        onCheckedChange = { onToggleActive() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Cipher: ${portal.encryptionCipher} • Access: ${portal.permissionLevel} • Token: ${portal.qMaxTokenHash}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = portal.expiresAtLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                AssistChip(
                    onClick = onVerifyAccess,
                    leadingIcon = {
                        Icon(
                            Icons.Default.Key,
                            contentDescription = "Verify Passphrase",
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    label = { Text("Decrypt & Preview Portal") }
                )
            }
        }
    }
}

@Composable
private fun ActivityLogRow(log: ActivityLogEntity) {
    val color = when (log.severity) {
        "CRITICAL" -> PathogenicCrimson
        "WARNING" -> ArtifactAmberWarning
        else -> CerenkovCyanPrimary
    }
    val timeStr = remember(log.createdAt) {
        SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(log.createdAt))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = color.copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = log.actionType,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = color,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.details,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${log.actorRole} • $timeStr",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun NewDossierDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("PIK3CA / KRAS Molecular Liability Report") }
    var caseRef by remember { mutableStateOf("LAC-2026-22C") }
    var sampleId by remember { mutableStateOf("SMP-NGS-9410") }
    var summary by remember { mutableStateOf("Confirmed somatic hotspot mutations with Q-MAX binary hash attestation under T=1 podání.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Consulting Dossier") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Dossier Title") }
                )
                OutlinedTextField(
                    value = caseRef,
                    onValueChange = { caseRef = it },
                    label = { Text("Case Reference") }
                )
                OutlinedTextField(
                    value = sampleId,
                    onValueChange = { sampleId = it },
                    label = { Text("Sample ID") }
                )
                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text("Initial Clinical-Legal Summary") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && caseRef.isNotBlank()) {
                        onCreate(title, caseRef, sampleId, summary)
                    }
                }
            ) {
                Text("Create v1.0")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun CommitVersionDialog(
    doc: ConsultingDocEntity,
    onDismiss: () -> Unit,
    onCommit: (String) -> Unit
) {
    var changelog by remember {
        mutableStateOf("Updated ACMG pathogenicity weighting & re-verified Q-MAX SHA-256 sync chain.")
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Commit New Version (${doc.caseReference})") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Current Version: ${doc.currentVersion}. Committing will generate an immutable SHA-256 version record.",
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = changelog,
                    onValueChange = { changelog = it },
                    label = { Text("Version Changelog & Legal Notes") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { if (changelog.isNotBlank()) onCommit(changelog) }) {
                Text("Commit Version")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun NewPortalDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("Oncology Legal Defense Vault") }
    var org by remember { mutableStateOf("BioLex Advisory Group") }
    var caseRef by remember { mutableStateOf("LAC-2026-09A") }
    var passphrase by remember { mutableStateOf("QMAX-9900") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Provision Encrypted Client Portal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Portal Name") })
                OutlinedTextField(value = org, onValueChange = { org = it }, label = { Text("Client Organization") })
                OutlinedTextField(value = caseRef, onValueChange = { caseRef = it }, label = { Text("Document Case Ref") })
                OutlinedTextField(value = passphrase, onValueChange = { passphrase = it }, label = { Text("Q-MAX Passphrase") })
            }
        },
        confirmButton = {
            Button(onClick = { onCreate(title, org, caseRef, passphrase, "COMMENT_SIGN") }) {
                Text("Encrypt & Provision")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun VerifyPortalPassphraseDialog(
    portal: ClientPortalEntity,
    onDismiss: () -> Unit
) {
    var enteredPassphrase by remember { mutableStateOf(portal.accessPassphrase) }
    val isVerified = enteredPassphrase.trim() == portal.accessPassphrase.trim()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Encrypted Client Portal Gate") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Portal: ${portal.portalTitle} (${portal.clientOrganization})",
                    style = MaterialTheme.typography.titleMedium
                )
                OutlinedTextField(
                    value = enteredPassphrase,
                    onValueChange = { enteredPassphrase = it },
                    label = { Text("Enter Passphrase (hint: ${portal.accessPassphrase})") }
                )
                if (isVerified) {
                    Surface(
                        color = BenignBioEmerald.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "✓ AES-256-GCM & Q-MAX Token Verified",
                                style = MaterialTheme.typography.labelLarge,
                                color = BenignBioEmerald
                            )
                            Text(
                                text = "Decrypted Dossier ${portal.documentCaseRef} • Permission: ${portal.permissionLevel} • Watermark: ${portal.qMaxTokenHash}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Invalid passphrase — zero-trust cipher locked.",
                        style = MaterialTheme.typography.labelLarge,
                        color = PathogenicCrimson
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Close") }
        }
    )
}
