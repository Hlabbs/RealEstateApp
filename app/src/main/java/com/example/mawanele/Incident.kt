package com.example.mawanele

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class Incident : AppCompatActivity() {
    // Profile picture ImageView
    private lateinit var profilePicture: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incident)

        // Initialize the profile picture ImageView
        profilePicture = findViewById(R.id.profilePicture)

        // Bottom Navigation Bar - Assign click listeners to the nav buttons
        findViewById<LinearLayout>(R.id.navHome).setOnClickListener {
            // Navigate to Home activity
            startActivity(Intent(this@Incident, HomeActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.navIncident).setOnClickListener {
            // Current page is Incident, no need to navigate
        }

        findViewById<LinearLayout>(R.id.navNotifications).setOnClickListener {
            // Navigate to Notifications activity
            startActivity(Intent(this@Incident, Notifications::class.java))
        }

        findViewById<LinearLayout>(R.id.navTickets).setOnClickListener {
            // Navigate to Ticket Activity
            startActivity(Intent(this@Incident, Ticket::class.java))
        }

        findViewById<LinearLayout>(R.id.navSupport).setOnClickListener {
            // Navigate to SettingsSupport activity
            startActivity(Intent(this@Incident, SettingsSupport::class.java))
        }

        // Find the settings button using its ID
        val settingsButton: ImageView = findViewById(R.id.settingsButton)

        // Set an OnClickListener to navigate to the SettingsAccount activity
        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsAccount::class.java)
            startActivity(intent)
        }

        // Trigger opening gallery
        profilePicture.setOnClickListener {
            openGallery()
        }
    }

    // Function to open gallery (placeholder)
    private fun openGallery() {
        // Add your gallery-opening logic here
    }
}
