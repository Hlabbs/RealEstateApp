package com.example.mawanele


import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UserSettingsService {

    // Reference to the "users" node in Firebase Realtime Database
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")

    // Function to update user settings in Firebase
    fun updateUserSettings(userId: String, updatedSettings: UserSettings,
                           onSuccess: () -> Unit,
                           onFailure: (String) -> Unit) {
        // Reference to the specific user's settings
        val userSettingsReference = database.child(userId).child("settings")

        // Set the updated settings for the user
        userSettingsReference.setValue(updatedSettings)
            .addOnSuccessListener {
                // Call onSuccess callback
                onSuccess()
            }
            .addOnFailureListener { exception ->
                // Call onFailure callback with the error message
                onFailure(exception.message ?: "Unknown error occurred")
            }
    }
}
