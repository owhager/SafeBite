package com.cs407.safebite.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RecentScanDao {

    @Query("SELECT * FROM recent_scans WHERE userUID = :userUID ORDER BY timestamp DESC")
    suspend fun getAllForUser(userUID: String): List<RecentScan>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recentScan: RecentScan)

    @Query("DELETE FROM recent_scans WHERE userUID = :userUID AND barcode = :barcode")
    suspend fun deleteByBarcode(userUID: String, barcode: String)

    @Query("DELETE FROM recent_scans WHERE userUID = :userUID")
    suspend fun deleteAllForUser(userUID: String)
}
