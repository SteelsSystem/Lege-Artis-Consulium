package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        VariantEntity::class,
        QMaxSyncPacketEntity::class,
        ConsultingDocEntity::class,
        DocumentVersionEntity::class,
        ClientPortalEntity::class,
        ActivityLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LegeArtisDatabase : RoomDatabase() {
    abstract fun legeArtisDao(): LegeArtisDao

    companion object {
        @Volatile
        private var INSTANCE: LegeArtisDatabase? = null

        fun getInstance(context: Context): LegeArtisDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LegeArtisDatabase::class.java,
                    "lege_artis_qmax_offline.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
