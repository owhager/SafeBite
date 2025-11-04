package com.cs407.safebite.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(
        value = ["userUID"],
        unique = true
    )]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,

    val userUID: String = ""
)