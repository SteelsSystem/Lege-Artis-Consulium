package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.QMaxSyncPacketEntity
import com.example.ui.theme.ArtifactAmberWarning
import com.example.ui.theme.BenignBioEmerald
import com.example.ui.theme.CerenkovCyanPrimary
import com.example.ui.theme.HelixIndigoSecondary
import com.example.viewmodel.LegeArtisViewModel
import kotlin.math.roundToInt

@Composable
fun QMaxCypherScreen(
    viewModel: LegeArtisViewModel,
    modifier: Modifier = Modifier
) {
    val plainInput by viewModel.cypherPlainInput.collectAsStateWithLifecycle()
    val saltHex by viewModel.cypherSaltHex.collectAsStateWithLifecycle()
    val toggledBits by viewModel.toggledBitIndices.collectAsStateWithLifecycle()
    val cypherResult by viewModel.cypherResult.collectAsStateWithLifecycle()
    val syncPackets by viewModel.syncPackets.collectAsStateWithLifecycle()
    val policy = viewModel.currentPolicy()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Q-MAX Cypher",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Local Secure Q-MAX Cypher & Binary Sync",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Salted SHA-256/512 • AES-GCM Block • Offline Room Persistence",
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
                                text = "CRC:${cypherResult.crc32Hex}",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                style = MaterialTheme.typography.labelLarge,
                                color = BenignBioEmerald
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = plainInput,
                        onValueChange = viewModel::updateCypherPlainInput,
                        label = { Text("Genomic / Dossier Payload Input") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("qmax_payload_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = viewModel::regenerateRandomSalt,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_regenerate_salt")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "New Salt")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Salt: $saltHex")
                        }

                        Button(
                            onClick = { viewModel.commitCurrentSyncPacket("NGS_VARIANT") },
                            enabled = policy.canExecuteQMaxSync,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_commit_sync_packet")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = "Save Sync Packet")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sync to Room DB")
                        }
                    }
                }
            }
        }

        // Interactive 8x16 Binary Bit-Matrix & Avalanche Inspector
        item {
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
                                imageVector = Icons.Default.Memory,
                                contentDescription = "Binary Matrix",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Interactive 128-Bit Q-MAX Matrix (8×16)",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Tap any bit cell to flip state & inspect SHA-256 avalanche entropy",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        if (toggledBits.isNotEmpty()) {
                            OutlinedButton(onClick = viewModel::clearToggledBits) {
                                Text("Reset (${toggledBits.size})")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 8 rows x 16 columns bit matrix
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF070B12))
                            .padding(10.dp)
                            .testTag("qmax_bit_matrix"),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (row in 0 until 8) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                for (col in 0 until 16) {
                                    val bitIdx = row * 16 + col
                                    val bitVal = cypherResult.binaryMatrix128.getOrElse(bitIdx) { 0 }
                                    val isFlipped = toggledBits.contains(bitIdx)
                                    val cellBg = when {
                                        isFlipped -> ArtifactAmberWarning
                                        bitVal == 1 -> CerenkovCyanPrimary.copy(alpha = 0.28f)
                                        else -> Color(0xFF151F2E)
                                    }
                                    val textColor = when {
                                        isFlipped -> Color.Black
                                        bitVal == 1 -> CerenkovCyanPrimary
                                        else -> Color(0xFF64748B)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(cellBg)
                                            .clickable { viewModel.toggleMatrixBit(bitIdx) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = bitVal.toString(),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = textColor,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Cryptographic Telemetry Readouts
                    HashReadoutRow(
                        label = "Avalanche Effect",
                        value = "${"%.1f".format(cypherResult.avalancheHammingPercent)}% Hamming Diffusion (${cypherResult.flippedBitCount}/256 bits shifted) • Resilience ${(cypherResult.tensionResilienceScore * 100).roundToInt()}%",
                        highlightColor = if (toggledBits.isEmpty()) BenignBioEmerald else ArtifactAmberWarning
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    HashReadoutRow(
                        label = "Salted SHA-256",
                        value = cypherResult.sha256Hex,
                        highlightColor = CerenkovCyanPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    HashReadoutRow(
                        label = "Q-MAX AES-GCM",
                        value = cypherResult.cipherPayloadHex,
                        highlightColor = HelixIndigoSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    HashReadoutRow(
                        label = "Sync Frame",
                        value = cypherResult.syncPacketFrame,
                        highlightColor = BenignBioEmerald
                    )
                }
            }
        }

        item {
            Text(
                text = "Offline Room Binary Sync Packets (${syncPackets.size})",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(syncPackets, key = { it.id }) { packet ->
            SyncPacketCard(packet = packet)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HashReadoutRow(
    label: String,
    value: String,
    highlightColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = highlightColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SyncPacketCard(packet: QMaxSyncPacketEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = "Verified Sync Packet",
                        tint = BenignBioEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = packet.plainSummary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Surface(
                    color = BenignBioEmerald.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = packet.syncStatus,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = BenignBioEmerald
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = packet.packetHeaderHex,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "SHA-256: ${packet.sha256ChainHash.take(36)}... • Salt: ${packet.saltHex}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "128-Bit Prefix: ${packet.binaryMatrixSnapshot.take(32)}...",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
