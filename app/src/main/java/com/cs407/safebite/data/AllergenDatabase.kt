package com.cs407.safebite.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cs407.safebite.R

@Database(
    entities = [User::class, Allergen::class, RecentScan::class],
    version = 3
)
abstract class AllergenDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun deleteDao(): DeleteDao
    abstract fun allergenDao(): AllergenDao
    abstract fun recentScanDao(): RecentScanDao

    companion object {
        // Singleton prevents multiple instances of database
        // opening at the same time
        @Volatile
        private var INSTANCE: AllergenDatabase? = null

        fun getDatabase(context: Context): AllergenDatabase {
            // If INSTANCE is not null, return it,
            // otherwise create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AllergenDatabase::class.java,
                    "safebite_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}