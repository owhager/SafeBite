package com.cs407.safebite.data

import androidx.room.Entity

@Entity(
    tableName = "recent_scans",
    primaryKeys = ["userUID", "barcode"]
)
data class RecentScan(
    val userUID: String,
    val barcode: String,
    val foodName: String,
    val brandName: String,
    val timestamp: Long = System.currentTimeMillis()
)
