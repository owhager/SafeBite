package com.cs407.safebite.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [RecentScan::class],
    version = 2,
    exportSchema = false
)
abstract class RecentScanDatabase : RoomDatabase() {

    abstract fun recentScanDao(): RecentScanDao

    companion object {
        @Volatile
        private var INSTANCE: RecentScanDatabase? = null

        fun getDatabase(context: Context): RecentScanDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecentScanDatabase::class.java,
                    "recents_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
