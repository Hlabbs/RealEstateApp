package com.example.mawanele

import android.content.Intent
import android.media.audiofx.EnvironmentalReverb.Settings
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Initialize buttons
        val notificationsButton = findViewById<LinearLayout>(R.id.settingsNotifications)
        val accountButton = findViewById<LinearLayout>(R.id.settingsAccount)
        val supportButton = findViewById<LinearLayout>(R.id.settingsSupport)
        val signOutButton = findViewById<LinearLayout>(R.id.settingsSignOut)

        // Set click listeners for each button
        notificationsButton.setOnClickListener {
            openNotificationsPage()
        }

        accountButton.setOnClickListener {
            openAccountPage()
        }

        supportButton.setOnClickListener {
            openSupportPage()
        }

        signOutButton.setOnClickListener {
            signOutUser()
        }

        // Bottom Navigation Bar - Assign click listeners to the nav buttons
        findViewById<LinearLayout>(R.id.navHome).setOnClickListener {
            // Navigate to Home activity
            startActivity(Intent(this@SettingsActivity, HomeActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.navIncident).setOnClickListener {
            // Navigate to Incident activity
            startActivity(Intent(this@SettingsActivity, Incident::class.java))
        }

        findViewById<LinearLayout>(R.id.navNotifications).setOnClickListener {
            // Navigate to Notifications activity
            startActivity(Intent(this@SettingsActivity, SettingsNotification::class.java))
        }

        findViewById<LinearLayout>(R.id.navTickets).setOnClickListener {
            // Navigate to Ticket activity
            startActivity(Intent(this@SettingsActivity, Ticket::class.java))
        }

        findViewById<LinearLayout>(R.id.navSupport).setOnClickListener {
            // Navigate to SettingsSupport activity
            startActivity(Intent(this@SettingsActivity, SettingsSupport::class.java))
        }
    }

    // Navigate to Notifications Page
    private fun openNotificationsPage() {
        val intent = Intent(this, SettingsNotification::class.java)
        startActivity(intent)
    }

    // Navigate to Account Page
    private fun openAccountPage() {
        val intent = Intent(this, SettingsAccount::class.java)
        startActivity(intent)
    }

    // Navigate to Support Page
    private fun openSupportPage() {
        val intent = Intent(this, SettingsSupport::class.java)
        startActivity(intent)
    }

    // Sign Out user and navigate to SignIn page (or perform sign out logic)
    private fun signOutUser() {
        //Alert Dialog to Confirm sign out action
      val builder = AlertDialog.Builder (this)
        builder.setTitle("Sign Out")
        builder.setMessage("Are you sure you want to sign out?")

        // Set up the buttons
        builder.setPositiveButton("Yes") { _, _ ->
            // sign-out logic
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            // If the user cancels, just dismiss the dialog
            dialog.dismiss()
        }

        // Display the dialog
        builder.show()
    }
}
