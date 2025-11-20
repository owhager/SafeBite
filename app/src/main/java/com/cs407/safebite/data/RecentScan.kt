package com.cs407.safebite.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recent_scans",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["userUID"],
        childColumns = ["userUID"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("userUID")]
)
data class RecentScan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userUID: String,
    val barcode: String,
    val foodName: String,
    val brandName: String,
    val timestamp: Long = System.currentTimeMillis()
)
