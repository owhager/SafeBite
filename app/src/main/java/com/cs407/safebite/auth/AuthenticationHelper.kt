package com.cs407.safebite.auth

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.cs407.safebite.R

/**
 * MILESTONE 2: Authentication Helper
 * Contains all business logic for Firebase authentication
 */

// ============================================
// Email Validation
// ============================================

enum class EmailResult {
    Valid,
    Empty,
    Invalid,
}

fun checkEmail(email: String): EmailResult {
    if (email.isEmpty()){
        return EmailResult.Empty
    }

    // 1. username of email should only contain "0-9, a-z, _, A-Z, ."
    // 2. there is one and only one "@" between username and server address
    // 3. there are multiple domain names with at least one top-level domain
    // 4. domain name "0-9, a-z, -, A-Z" (could not have "_" but "-" is valid)
    // 5. multiple domain separate with '.'
    // 6. top level domain should only contain letters and at lest 2 letters
    // this email check only valid for this course
    val pattern = Regex("^[\\w.]+@([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$")
    return if (pattern.matches(email)) {
        EmailResult.Valid
    } else {
        EmailResult.Invalid
    }

}

// ============================================
// Password Validation
// ============================================

enum class PasswordResult {
    Valid,
    Empty,
    Short,
    Invalid
}

fun checkPassword(password: String) : PasswordResult {
    // 1. password should contain at least one uppercase letter, lowercase letter, one digit
    // 2. minimum length: 5
    if (password.isEmpty()) {
        return PasswordResult.Empty
    }
    if (password.length < 5) {
        return PasswordResult.Short
    }
    if (Regex("\\d+").containsMatchIn(password) &&
        Regex("[a-z]+").containsMatchIn(password) &&
        Regex("[A-Z]+").containsMatchIn(password)
    ) {
        return PasswordResult.Valid
    } else {
        return PasswordResult.Invalid
    }


}

// ============================================
// Firebase Authentication Functions
// ============================================

/**
 * Sign in existing user with email and password
 * If sign-in fails, automatically attempts to create new account
 */
fun signIn(
    email: String,
    password: String,
    onResult: (success: Boolean, message: String?, user: FirebaseUser?) -> Unit
    //any other callback function or parameters if you want
) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if(task.isSuccessful) {
                val user = auth.currentUser
                onResult(true, null ,user)
                // Sign in success
                // TODO: Get current user from the response and propogate it
            }
            else {
                createAccount(email, password, onResult)
                // Sign in failed, try creating account
                // TODO: Call createAccount function
            }
        }
}

/**
 * Create new Firebase account with email and password
 */
fun createAccount(
    email: String,
    password: String,
    onResult: (Boolean, String?, FirebaseUser?) -> Unit
    //any other callback function or parameters if you want
) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    auth.createUserWithEmailAndPassword(email, password)
        .addOnSuccessListener { result ->
            val user = auth.currentUser
            onResult(true, null ,user)
            // TODO: Logic to propogate success response
        }
        .addOnFailureListener { exception ->

            onResult(false, null, null)
            //TODO error in creation of account
        }
}

/**
 * Update Firebase Auth displayName
 * Used in Milestone 3 for username collection
 */
fun updateName(name: String, onComplete: (Boolean, Exception?, FirebaseUser?) -> Unit) {
    val user = Firebase.auth.currentUser

    //TODO create a request object to update the display name and then call updateProfile() function
    val profileUpdates = userProfileChangeRequest {
        displayName = name
    }

    user!!.updateProfile(profileUpdates)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onComplete(true, null, user)
                Log.d(TAG, "User profile updated")
            }else {
                onComplete(false, task.exception, null)
            }
        }
}