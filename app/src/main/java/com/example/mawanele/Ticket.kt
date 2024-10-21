package com.example.mawanele

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth // Import FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class Ticket : AppCompatActivity() {
    // Formally known as TicketIncident

    private lateinit var etEmail: EditText
    private lateinit var etSubject: EditText
    private lateinit var etDate: EditText
    private lateinit var etDescription: EditText
    private lateinit var btnSubmit: Button
    private lateinit var database: DatabaseReference

    // Firebase Authentication instance
    private lateinit var auth: FirebaseAuth

    // Bottom Navigation Views
    private lateinit var navHome: LinearLayout
    private lateinit var navIncident: LinearLayout
    private lateinit var navNotifications: LinearLayout
    private lateinit var navTickets: LinearLayout
    private lateinit var navSupport: LinearLayout
    private lateinit var settingsButton: ImageView
    private lateinit var profilePicture: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().getReference("tickets")
        auth = FirebaseAuth.getInstance()

        // Initialize views
        initViews()

        // Date picker for the Date of Incident field
        etDate.setOnClickListener {
            showDatePickerDialog()
        }

        // Handle form submission
        btnSubmit.setOnClickListener {
            submitTicket()
        }

        // Set up bottom navigation
        setupBottomNavigation()
    }

    private fun initViews() {
        etEmail = findViewById(R.id.etEmail)
        etSubject = findViewById(R.id.etSubject)
        etDate = findViewById(R.id.etDate)
        etDescription = findViewById(R.id.etDescription)
        btnSubmit = findViewById(R.id.btnSubmit)

        // Initialize Bottom Navigation Views
        navHome = findViewById(R.id.navHome)
        navIncident = findViewById(R.id.navIncident)
        navNotifications = findViewById(R.id.navNotifications)
        navTickets = findViewById(R.id.navTickets)
        navSupport = findViewById(R.id.navSupport)
        settingsButton = findViewById(R.id.settingsButton)
        profilePicture = findViewById(R.id.profilePicture)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "${selectedDay}/${selectedMonth + 1}/$selectedYear"
                etDate.setText(formattedDate)
            }, year, month, day
        )

        datePickerDialog.show()
    }

    private fun submitTicket() {
        val email = etEmail.text.toString().trim()
        val subject = etSubject.text.toString().trim()
        val date = etDate.text.toString().trim()
        val description = etDescription.text.toString().trim()

        // Get the current user's ID
        val userId = auth.currentUser?.uid // Make sure the user is logged in

        if (email.isEmpty() || subject.isEmpty() || date.isEmpty() || description.isEmpty() || userId == null) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val ticketId = database.push().key ?: return
        val ticket = TicketModel(
            ticketId = ticketId,
            userid = userId, // Include userId in the ticket
            email = email,
            subject = subject,
            date = date,
            description = description
        )

        database.child(ticketId).setValue(ticket).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Ticket submitted successfully", Toast.LENGTH_SHORT).show()
                clearForm()
            } else {
                Toast.makeText(this, "Failed to submit ticket", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clearForm() {
        etEmail.setText("")
        etSubject.setText("")
        etDate.setText("")
        etDescription.setText("")
    }

    private fun setupBottomNavigation() {
        navHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish() // Optionally finish current activity
        }

        navIncident.setOnClickListener {
            startActivity(Intent(this, Ticket::class.java)) // Current Activity
            finish()
        }

        navNotifications.setOnClickListener {
            startActivity(Intent(this, Notifications::class.java))
            finish()
        }

        navTickets.setOnClickListener {
            startActivity(Intent(this, Ticket::class.java)) // Current Activity
            finish()
        }

        navSupport.setOnClickListener {
            startActivity(Intent(this, SettingsSupport::class.java))
            finish()
        }

        // Set an OnClickListener to navigate to the SettingsAccount activity
        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // Profile picture click action
        profilePicture.setOnClickListener {
            val intent = Intent(this, SettingsAccount::class.java)
            startActivity(intent)
        }
    }
}
