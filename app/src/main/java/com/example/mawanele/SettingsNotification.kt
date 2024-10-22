package com.example.mawanele

import android.content.Context
import android.os.Bundle
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsNotification : AppCompatActivity() {

    private lateinit var notificationsSwitch: Switch
    private val PREFS_NAME = "notification_prefs"
    private val NOTIFICATION_KEY = "notifications_enabled"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_notifications)

        notificationsSwitch = findViewById(R.id.notificationsSwitchon)

        // Initialize the switch state based on the saved preference
        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isNotificationsEnabled = sharedPrefs.getBoolean(NOTIFICATION_KEY, false)
        notificationsSwitch.isChecked = isNotificationsEnabled

        // Set the listener for the switch toggle
        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            toggleNotifications(isChecked)
        }
    }

    private fun toggleNotifications(isEnabled: Boolean) {
        // Save the state of the switch in SharedPreferences
        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putBoolean(NOTIFICATION_KEY, isEnabled)
        editor.apply()

        // Show feedback to the user
        val message = if (isEnabled) {
            "Notifications enabled"
        } else {
            "Notifications disabled"
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

        // Here you can add the actual logic to enable/disable notifications if needed
        // e.g., subscribe to/unsubscribe from notification topics, enable/disable services, etc.
    }
}
