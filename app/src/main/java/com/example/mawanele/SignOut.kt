package com.example.mawanele

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SignOut : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sign out logic, clearing any user session data
        signOutUser()

        // Redirect to SignIn activity after signing out
        val intent = Intent(this, SignOut::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Ensures user can't go back
        startActivity(intent)
        finish() // Close the SignOut activity
    }

    private fun signOutUser() {
        // Example: Clear shared preferences to sign out
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear() // Clear the stored user session data
        editor.apply()


         FirebaseAuth.getInstance().signOut()
    }
}
