package com.cs407.safebite.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.cs407.safebite.data.AllergenDatabase
import com.cs407.safebite.data.User
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
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    // Firebase auth instance
    private val auth: FirebaseAuth = Firebase.auth

    // Public read-only state
    val userState = _userState.asStateFlow()

    fun setLoading(value: Boolean) {
        _isLoading.value = value
    }

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
            setLoading(false)
        }
    }

    // Function to update user state
    fun setUser(state: UserState) {
        _userState.update { state }
    }

    fun insertUserLocally(uid: String, context: Context, onComplete: (Int) -> Unit = {}) {
        viewModelScope.launch {
            val userDao = AllergenDatabase.getDatabase(context).userDao()
            var localUser = userDao.getByUID(uid)
            if (localUser == null) {
                userDao.insert(User(userUID = uid))  // ← this returns the real ID
                localUser = userDao.getByUID(uid)
            }
            // NOW UPDATE THE STATE WITH THE REAL ID
            if (localUser != null) {
                _userState.update {
                    it.copy(
                        id = localUser.userId,
                        uid = uid,
                        name = auth.currentUser?.displayName ?: it.name
                    )
                }
                onComplete(localUser.userId)
            }

        }
    }
    // Delete user locally
    fun deleteAccount(context: Context, navController: NavHostController) {
        viewModelScope.launch {
            val user = auth.currentUser

            if (user != null) {
                val uid = user.uid

                // 1. Delete locally first
                val userDao = AllergenDatabase.getDatabase(context).userDao()
                val existingUser = userDao.getByUID(uid)
                if (existingUser != null) {
                    val deleteDao = AllergenDatabase.getDatabase(context).deleteDao()
                    deleteDao.delete(existingUser.userId)
                }

                // 2. Delete from Firebase
                user.delete()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // 3. Reset local user state
                            setUser(UserState())

                            // 4. Navigate back to login
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        } else {
                            // Optional: handle error
                        }
                    }
            }
        }
    }

    // Logout
    fun logout(navController: NavHostController) {
        setLoading(true)

        // Sign out from Firebase
        auth.signOut()

        // Reset local state
        setUser(UserState())

        // Navigate back to login
        navController.navigate("login") {
            popUpTo(0) { inclusive = true }
        }

        setLoading(false)
    }



}