package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LegeArtisDao {
    @Query("SELECT * FROM genomic_variants ORDER BY pValue ASC")
    fun observeVariants(): Flow<List<VariantEntity>>

    @Query("SELECT COUNT(*) FROM genomic_variants")
    suspend fun getVariantCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVariants(variants: List<VariantEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVariant(variant: VariantEntity): Long

    @Update
    suspend fun updateVariant(variant: VariantEntity)

    @Query("SELECT * FROM qmax_sync_packets ORDER BY createdAt DESC")
    fun observeSyncPackets(): Flow<List<QMaxSyncPacketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncPacket(packet: QMaxSyncPacketEntity): Long

    @Query("DELETE FROM qmax_sync_packets WHERE id = :id")
    suspend fun deleteSyncPacket(id: Long)

    @Query("SELECT * FROM consulting_docs ORDER BY updatedAt DESC")
    fun observeConsultingDocs(): Flow<List<ConsultingDocEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsultingDoc(doc: ConsultingDocEntity): Long

    @Update
    suspend fun updateConsultingDoc(doc: ConsultingDocEntity)

    @Query("SELECT * FROM document_versions ORDER BY createdAt DESC")
    fun observeDocumentVersions(): Flow<List<DocumentVersionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocumentVersion(version: DocumentVersionEntity): Long

    @Query("SELECT * FROM client_portals ORDER BY createdAt DESC")
    fun observeClientPortals(): Flow<List<ClientPortalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClientPortal(portal: ClientPortalEntity): Long

    @Update
    suspend fun updateClientPortal(portal: ClientPortalEntity)

    @Query("SELECT * FROM activity_logs ORDER BY createdAt DESC LIMIT 100")
    fun observeActivityLogs(): Flow<List<ActivityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityLog(log: ActivityLogEntity): Long
}
