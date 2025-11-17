package com.cs407.safebite.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// data/Allergen.kt
@Entity(
    tableName = "allergens",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["userUID"],
        childColumns = ["userUID"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("userUID")]
)
data class Allergen(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userUID: String,                 // FK → User.userUID
    val name: String,
    val isChecked: Boolean = false
)