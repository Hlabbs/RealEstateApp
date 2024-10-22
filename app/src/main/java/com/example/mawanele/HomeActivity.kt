package com.example.mawanele

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class HomeActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Get the current user
        val user = FirebaseAuth.getInstance().currentUser

        // Find the welcomeText TextView
        val welcomeText = findViewById<TextView>(R.id.welcomeText)

        // Check if the user is not null and update the welcome message with the user's email
        if (user != null) {
            val email = user.email
            welcomeText.text = "Welcome, $email"
        } else {
            welcomeText.text = "Welcome to the Home Page"
        }


        // Bottom Navigation Bar - Assign click listeners to the nav buttons
        findViewById<LinearLayout>(R.id.navHome).setOnClickListener {
            // Current page is Home, no need to navigate
        }
        // Bottom Navigation Bar - Assign click listeners to the nav buttons
        findViewById<LinearLayout>(R.id.navHome).setOnClickListener {
            // Current page is Home, no need to navigate
        }

        findViewById<LinearLayout>(R.id.navIncident).setOnClickListener {
            // Navigate to Incident activity
            startActivity(Intent(this@HomeActivity, Incident::class.java))
        }

        findViewById<LinearLayout>(R.id.navNotifications).setOnClickListener {
            // Navigate to SettingsNotification activity
            startActivity(Intent(this@HomeActivity, Notifications::class.java))
        }

        findViewById<LinearLayout>(R.id.navTickets).setOnClickListener {

            // Navigate to Ticket Activity
            startActivity(Intent(this@HomeActivity, Ticket::class.java))

            // Placeholder: handle ticket navigation
        }

        findViewById<LinearLayout>(R.id.navSupport).setOnClickListener {
            // Navigate to SettingsSupport activity
            startActivity(Intent(this@HomeActivity, SettingsSupport::class.java))
        }

        // Find the settings button using its ID
        val settingsButton: ImageView = findViewById(R.id.settingsButton)

        // Set an OnClickListener to navigate to the SettingsAccount activity
        settingsButton.setOnClickListener {
            val intent = Intent(this@HomeActivity , SettingsActivity::class.java)
            startActivity(intent)
        }

        // Profile picture click action
        findViewById<ImageView>(R.id.profilePicture).setOnClickListener {
            // Navigate to ProfileActivity (assuming you have a ProfileActivity)
            val intent = Intent(this, SettingsAccount::class.java)
            startActivity(intent)
        }


    }
}
