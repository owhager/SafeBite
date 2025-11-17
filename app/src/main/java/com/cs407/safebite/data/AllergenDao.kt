package com.cs407.safebite.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

// data/AllergenDao.kt
@Dao
interface AllergenDao {
    @Insert suspend fun insert(allergen: Allergen): Long
    @Update
    suspend fun update(allergen: Allergen)
    @Query("SELECT * FROM allergens WHERE userUID = :userUID")
    suspend fun getAllForUser(userUID: String): List<Allergen>

    @Query("DELETE FROM allergens WHERE userUID = :userUID")
    suspend fun deleteAllForUser(userUID: String)

    @Query("DELETE FROM allergens WHERE userUID = :userUID AND name = :name")
    suspend fun delete(userUID: String, name: String)  // ← ADD THIS
}