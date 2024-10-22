package com.example.mawanele

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class   SettingsAccount : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var profileImageView: ImageView
    private val REQUEST_CODE_IMAGE_CAPTURE = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_account)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users")

        profileImageView = findViewById(R.id.profileImageView) // Reference to the profile ImageView

        val userId = auth.currentUser?.uid

        if (userId != null) {
            // Retrieve user data
            database.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val profileUrl = snapshot.child("profile_url").getValue(String::class.java)
                        val flatNumber = snapshot.child("flatNumber").getValue(String::class.java)
                        val phoneNumber = snapshot.child("phoneNumber").getValue(String::class.java)

                        findViewById<EditText>(R.id.editFlatNumber).setText(flatNumber)
                        findViewById<EditText>(R.id.editPhoneNumber).setText(phoneNumber)

                        // Load profile picture (if profile_url is not null)
                        profileUrl?.let {
                            Glide.with(this@SettingsAccount).load(profileUrl).into(profileImageView)
                        }
                    } else {
                        Toast.makeText(this@SettingsAccount, "User data not found.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@SettingsAccount, "Error retrieving data.", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // Save button click listener
        findViewById<Button>(R.id.saveButton).setOnClickListener {
            val newFlatNumber = findViewById<EditText>(R.id.editFlatNumber).text.toString()
            val newPhoneNumber = findViewById<EditText>(R.id.editPhoneNumber).text.toString()

            if (userId != null) {
                // Prepare updates
                val updates = hashMapOf<String, Any>(
                    "flatNumber" to newFlatNumber,
                    "phoneNumber" to newPhoneNumber
                )

                // Update user data in Realtime Database
                database.child(userId).updateChildren(updates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show()

                            // Navigate back to the home page after saving
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                            finish() // Optionally finish the current activity to remove it from the back stack
                        } else {
                            Toast.makeText(this, "Failed to update profile.", Toast.LENGTH_SHORT).show()
                        }
                    }
                /*
                // Update user data in Realtime Database
                database.child(userId).updateChildren(updates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to update profile.", Toast.LENGTH_SHORT).show()
                        }
                    }

                 */
            }
        }

        // Change Profile Picture button click listener (Camera capture only)
        findViewById<Button>(R.id.changeProfilePicButton).setOnClickListener {
            // Open camera to capture picture
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_CODE_IMAGE_CAPTURE)
        }
    }

    // Handle image capture result from camera
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val userId = auth.currentUser?.uid

        if (requestCode == REQUEST_CODE_IMAGE_CAPTURE && resultCode == RESULT_OK && userId != null) {
            val capturedImage = data?.extras?.get("data") as Bitmap

            // Immediately display the captured image in the ImageView
            profileImageView.setImageBitmap(capturedImage)

            // Upload the image to Firebase
            uploadCapturedImageToFirebase(capturedImage, userId)
        }
    }

    // Upload captured image from camera to Firebase Storage and update the profile_url in Realtime Database
    private fun uploadCapturedImageToFirebase(capturedImage: Bitmap, userId: String) {
        val storageRef = FirebaseStorage.getInstance().reference.child("profile_pictures/$userId.jpg")

        // Convert the Bitmap to a byte array for upload
        val outputStream = ByteArrayOutputStream()
        capturedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val imageData = outputStream.toByteArray()

        // Upload image to Firebase Storage
        storageRef.putBytes(imageData).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                val profileUrl = uri.toString()

                // Ensure that profile_url is never null
                val updates = hashMapOf<String, Any>(
                    "profile_url" to profileUrl
                )

                // Update the profile_url in the database
                database.child(userId).updateChildren(updates).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Profile picture updated.", Toast.LENGTH_SHORT).show()

                        // Load the image from Firebase Storage in case there were any issues
                        Glide.with(this).load(profileUrl).into(profileImageView)
                    } else {
                        Toast.makeText(this, "Failed to update profile picture.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Image upload failed.", Toast.LENGTH_SHORT).show()
        }
    }
}