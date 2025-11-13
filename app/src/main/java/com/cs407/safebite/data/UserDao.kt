package com.cs407.safebite.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    // Check if Firebase user already exists locally
    @Query("SELECT * FROM user WHERE userUID = :uid")
    suspend fun getByUID(uid: String): User?

    // Retrieve user details by database ID
    @Query("SELECT * FROM user WHERE userId = :id")
    suspend fun getById(id: Int): User

    // Add new user after Firebase authentication
    @Insert(entity = User::class)
    suspend fun insert(user: User)
}