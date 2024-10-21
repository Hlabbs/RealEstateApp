package com.example.mawanele

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SettingsSupport : AppCompatActivity() {

    // Firebase database reference
    private lateinit var database: DatabaseReference

    // FirebaseAuth instance
    private lateinit var auth: FirebaseAuth

    // View components
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var messageEditText: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_support)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Sign in anonymously if no user is signed in
        if (auth.currentUser == null) {
            auth.signInAnonymously().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // User signed in anonymously
                } else {
                    Toast.makeText(this, "Failed to sign in", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Initialize Firebase Database reference pointing to "settings_support" node
        database = FirebaseDatabase.getInstance().getReference("settings_support")

        // Link the XML elements with their Kotlin counterparts
        nameEditText = findViewById(R.id.editName)
        emailEditText = findViewById(R.id.editEmail)
        messageEditText = findViewById(R.id.editMessage)
        submitButton = findViewById(R.id.submitButton)

        // Set a click listener on the submit button
        submitButton.setOnClickListener {
            submitSupportRequest()
        }

        // Bottom Navigation Bar - Assign click listeners to the nav buttons
        findViewById<LinearLayout>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this@SettingsSupport, HomeActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.navIncident).setOnClickListener {
            startActivity(Intent(this@SettingsSupport, Incident::class.java))
        }

        findViewById<LinearLayout>(R.id.navNotifications).setOnClickListener {
            startActivity(Intent(this@SettingsSupport, Notifications::class.java))
        }

        findViewById<LinearLayout>(R.id.navTickets).setOnClickListener {
            startActivity(Intent(this@SettingsSupport, Ticket::class.java))
        }

        findViewById<LinearLayout>(R.id.navSupport).setOnClickListener {
            // Current page is Support, no need to navigate
        }
    }

    private fun submitSupportRequest() {
        // Retrieve user inputs
        val name = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val message = messageEditText.text.toString().trim()

        // Validate the inputs
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(message)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Generate a unique ID for the support request
        val supportId = database.push().key

        // Get the current user ID from Firebase Authentication, or use "anonymous" if not logged in
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: "anonymous"

        if (supportId != null) {
            // Create a SupportRequest object with userId, supportId, name, email, and message
            val supportRequest = SupportRequest(userId, supportId, name, email, message)

            // Store the data in Firebase under the "settings_support" node
            database.child(supportId).setValue(supportRequest).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Support request submitted successfully", Toast.LENGTH_LONG).show()

                    // Clear input fields
                    nameEditText.text.clear()
                    emailEditText.text.clear()
                    messageEditText.text.clear()
                } else {
                    Toast.makeText(this, "Failed to submit support request", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

// Data model for support request
data class SupportRequest(
    val userid: String,    // User who submitted the request
    val supportId: String, // Unique ID for the support request
    val name: String,
    val email: String,
    val message: String
)
