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
        parentColumns = ["userId"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("userId")]
)
data class Allergen(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,                 // FK → User.userId
    val name: String,
    val isChecked: Boolean = false
)