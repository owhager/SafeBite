package com.cs407.safebite.data

/**
 * Represents a user's state within the app.
 *
 * @param id Auto-generated Room database ID (used in Milestone 3)
 * @param name The user’s display name (currently hardcoded as "User1")
 * @param uid The Firebase Authentication UID that uniquely identifies the user
 */
data class UserState(
    val id: Int = 0,      // Room database ID (will be used in Milestone 3)
    val name: String = "", // User's display name (hardcoded as "User1" for now)
    val uid: String = ""   // Firebase UID
)