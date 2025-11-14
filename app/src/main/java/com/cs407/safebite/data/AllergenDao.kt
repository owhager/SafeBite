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
    @Query("SELECT * FROM allergens WHERE userId = :userId")
    suspend fun getAllForUser(userId: Int): List<Allergen>

    @Query("DELETE FROM allergens WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: Int)
}