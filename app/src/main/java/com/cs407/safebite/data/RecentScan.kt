package com.cs407.safebite.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_scans")
data class RecentScan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userUID: String,
    val barcode: String,
    val foodName: String,
    val brandName: String,
    val timestamp: Long = System.currentTimeMillis()
)
