package com.cs407.safebite.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
//import com.cs407.safebite.NoteScreen
import com.cs407.safebite.data.UserState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    // Private mutable state (only ViewModel can modify)
    private val _userState = MutableStateFlow(UserState())

    // Firebase auth instance
    private val auth: FirebaseAuth = Firebase.auth

    // Public read-only state
    val userState = _userState.asStateFlow()

    init {
        // Listen for authentication state changes
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                // User logged out, reset state
                setUser(UserState())
            }
            else {
                setUser(UserState(name = user.displayName ?: "", uid = user.uid))
            }
        }
    }

    // Function to update user state
    fun setUser(state: UserState) {
        _userState.update { state }
    }

    // Insert user locally
//    fun insertUserLocally(uid: String, context: Context) {
//        viewModelScope.launch {
//            val userDao = NoteDatabase.getDatabase(context).userDao()
//            val existingUser = userDao.getByUID(uid)
//            if (existingUser == null) {
//                userDao.insert(User(userUID = uid))
//            }
//        }
//    }

    // Delete user locally
//    fun deleteAccount(context: Context, navController: NavHostController) {
//        viewModelScope.launch {
//            val user = auth.currentUser
//
//            if (user != null) {
//                val uid = user.uid
//
//                // 1. Delete locally first
//                val userDao = NoteDatabase.getDatabase(context).userDao()
//                val existingUser = userDao.getByUID(uid)
//                if (existingUser != null) {
//                    val deleteDao = NoteDatabase.getDatabase(context).deleteDao()
//                    deleteDao.delete(existingUser.userId)
//                }
//
//                // 2. Delete from Firebase
//                user.delete()
//                    .addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            // 3. Reset local user state
//                            setUser(UserState())
//
//                            // 4. Navigate back to login
//                            navController.navigate(NoteScreen.Login.name) {
//                                popUpTo(0) { inclusive = true }
//                            }
//                        } else {
//                            // Optional: handle error
//                        }
//                    }
//            }
//        }
//    }

    // Logout
    fun logout(navController: NavHostController) {
        // Sign out from Firebase
        auth.signOut()

        // Reset local state
        setUser(UserState())

        // Navigate back to login
        navController.navigate("Login") {
            popUpTo(0) { inclusive = true }
        }
    }



}