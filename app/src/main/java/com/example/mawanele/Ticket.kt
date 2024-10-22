package com.example.mawanele

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.*

class Ticket : AppCompatActivity() {

    private lateinit var etSubject: EditText
    private lateinit var etDescription: EditText
    private lateinit var etDate: EditText // Date EditText
    private lateinit var btnSubmit: Button
    private lateinit var btnUpload: Button
    private lateinit var profilePicture: ImageView // Profile picture ImageView
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private var imageUri: Uri? = null
    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        database = FirebaseDatabase.getInstance().getReference("tickets")
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference.child("ticket_images")

        initViews()

        btnUpload.setOnClickListener {
            openCamera()
        }

        btnSubmit.setOnClickListener {
            submitTicket()
        }

        etDate.setOnClickListener {
            showDatePickerDialog()
        }

        // Bottom Navigation Bar - Assign click listeners to the nav buttons
        findViewById<LinearLayout>(R.id.navHome).setOnClickListener {
            // Current page is Home, no need to navigate
        }

        findViewById<LinearLayout>(R.id.navIncident).setOnClickListener {
            // Navigate to Incident activity
            startActivity(Intent(this@Ticket, Incident::class.java))
        }

        findViewById<LinearLayout>(R.id.navNotifications).setOnClickListener {
            // Navigate to Notifications activity
            startActivity(Intent(this@Ticket, Notifications::class.java))
        }

        findViewById<LinearLayout>(R.id.navTickets).setOnClickListener {
            // Navigate to Ticket Activity
            startActivity(Intent(this@Ticket, Ticket::class.java))
        }

        findViewById<LinearLayout>(R.id.navSupport).setOnClickListener {
            // Navigate to SettingsSupport activity
            startActivity(Intent(this@Ticket, SettingsSupport::class.java))
        }

        // Find the settings button using its ID
        val settingsButton: ImageView = findViewById(R.id.settingsButton)

        // Set an OnClickListener to navigate to the SettingsAccount activity
        settingsButton.setOnClickListener {
            val intent = Intent(this@Ticket, SettingsActivity::class.java)
            startActivity(intent)
        }

        // Profile picture click action
        findViewById<ImageView>(R.id.profilePicture).setOnClickListener {
            val intent = Intent(this, SettingsAccount::class.java)
            startActivity(intent)
        }
    }

    private fun initViews() {
        etSubject = findViewById(R.id.etSubject)
        etDescription = findViewById(R.id.etDescription)
        etDate = findViewById(R.id.etDate) // Initialize Date EditText
        btnSubmit = findViewById(R.id.btnSubmit)
        btnUpload = findViewById(R.id.btnUpload)
        profilePicture = findViewById(R.id.profilePicture) // Initialize ImageView
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    private fun submitTicket() {
        val subject = etSubject.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val uid = auth.currentUser?.uid // This can be null

        // Ensure ticketId is generated
        val ticketId = database.push().key ?: return

        // If there is an image, upload it to Firebase Storage
        if (imageUri != null) {
            uploadImageToFirebase(ticketId, subject, description, uid)
        } else {
            // Handle case where there's no image
            val ticket = TicketModel(ticketId, uid ?: "", subject, description, "", etDate.text.toString())
            saveTicketToDatabase(ticket)
        }
    }

    private fun uploadImageToFirebase(ticketId: String, subject: String, description: String, uid: String?) {
        val fileReference = storageReference.child("$ticketId.jpg")

        fileReference.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                fileReference.downloadUrl.addOnSuccessListener { downloadUri ->
                    val ticket = TicketModel(ticketId, uid ?: "", subject, description, downloadUri.toString(), etDate.text.toString())
                    saveTicketToDatabase(ticket)
                }
            }
            .addOnFailureListener { err ->
                Toast.makeText(this, "Image upload failed: ${err.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveTicketToDatabase(ticket: TicketModel) {
        database.child(ticket.ticketId).setValue(ticket)
            .addOnCompleteListener {
                Toast.makeText(this, "Ticket submitted successfully!", Toast.LENGTH_SHORT).show()
                clearForm()
            }
            .addOnFailureListener { err ->
                Toast.makeText(this, "Error: ${err.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            val capturedImage = data?.extras?.get("data") as Bitmap
           // profilePicture.setImageBitmap(capturedImage) // Display captured image in ImageView
            imageUri = getImageUri(capturedImage) // Convert Bitmap to Uri if needed
        }
    }

    private fun getImageUri(bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "TicketImage", null)
        return Uri.parse(path)
    }

    private fun clearForm() {
        etSubject.text.clear()
        etDescription.text.clear()
        etDate.text.clear() // Clear the date EditText
        profilePicture.setImageResource(R.drawable.ic_account) // Reset to a default image if needed
        imageUri = null
        selectedDate = ""
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            etDate.setText(selectedDate) // Display selected date in EditText
        }, year, month, day)

        datePickerDialog.show()
    }

    companion object {
        private const val CAMERA_REQUEST_CODE = 1000
    }
}
