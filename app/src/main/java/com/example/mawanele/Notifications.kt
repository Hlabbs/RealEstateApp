package com.example.mawanele

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class Notifications : AppCompatActivity() {

    private lateinit var notificationsRecyclerView: RecyclerView
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var profilePicture: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        notificationsRecyclerView = findViewById(R.id.notificationsRecyclerView)
        notificationsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Assuming you have a method to get the notifications
        val notificationsList = getNotifications()
        notificationAdapter = NotificationAdapter(notificationsList)
        notificationsRecyclerView.adapter = notificationAdapter

        // Initialize the profile picture ImageView
        profilePicture = findViewById(R.id.profilePicture)

        // Bottom Navigation Bar - Assign click listeners to the nav buttons
        findViewById<LinearLayout>(R.id.navHome).setOnClickListener {
            // Navigate to Home activity
            startActivity(Intent(this@Notifications, HomeActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.navIncident).setOnClickListener {
            // Navigate to Incident activity
            startActivity(Intent(this@Notifications, Incident::class.java))
        }

        findViewById<LinearLayout>(R.id.navNotifications).setOnClickListener {
            // Current page is Notifications, no need to navigate
        }

        findViewById<LinearLayout>(R.id.navTickets).setOnClickListener {
            // Navigate to Ticket Activity
            startActivity(Intent(this@Notifications, Ticket::class.java))
        }

        findViewById<LinearLayout>(R.id.navSupport).setOnClickListener {
            // Navigate to SettingsSupport activity
            startActivity(Intent(this@Notifications, SettingsSupport::class.java))
        }

        // Find the settings button using its ID
        val settingsButton: ImageView = findViewById(R.id.settingsButton)

        // Set an OnClickListener to navigate to the SettingsAccount activity
        settingsButton.setOnClickListener {
            val intent = Intent(this@Notifications , SettingsActivity::class.java)
            startActivity(intent)
        }

        // Profile picture click action
        findViewById<ImageView>(R.id.profilePicture).setOnClickListener {
            // Navigate to ProfileActivity (assuming you have a ProfileActivity)
            val intent = Intent(this, SettingsAccount::class.java)
            startActivity(intent)
        }
    }

    private fun getNotifications(): List<NotificationModel> {
        // Replace this with your actual data source
        return listOf(
            NotificationModel("You're All Set!", "Your app is up to date with the latest version and features.", getCurrentTime()),
            NotificationModel("Welcome!", "Thank you for joining Mawanele. Let's get started on an amazing journey together!", getCurrentTime())
        )
    }

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault()) // Format time as HH:MM AM/PM
        return sdf.format(Date()) // Return current time
    }

    private fun openGallery() {
        // Implement the logic to open the gallery
    }
}
