package com.example.mawanele

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

class RegisterActivity : AppCompatActivity() {

    private lateinit var etFlatNumber: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhoneNumber: EditText
    private lateinit var etPassword: EditText
    private lateinit var cbShowPassword: CheckBox
    private lateinit var btnCreateAccount: Button
    private lateinit var tvSelectedCountryCode: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    private var selectedCountryCode: String = "+1" // Default country code

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        // Reference to Realtime Database
        mDatabase = FirebaseDatabase.getInstance().getReference("users")

        // Initialize views
        etFlatNumber = findViewById(R.id.etFlatNumber)
        etEmail = findViewById(R.id.etEmail)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        etPassword = findViewById(R.id.etPassword)
        cbShowPassword = findViewById(R.id.cbShowPassword)
        btnCreateAccount = findViewById(R.id.btnCreateAccount)
        tvSelectedCountryCode = findViewById(R.id.tvSelectedCountryCode)

        // Set up the show password functionality
        cbShowPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Show the password
                etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                // Hide the password
                etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            // Move the cursor to the end of the text after toggling visibility
            etPassword.setSelection(etPassword.text.length)
        }

        // Default country code setup
        tvSelectedCountryCode.text = selectedCountryCode

        // Set up the button click listener
        btnCreateAccount.setOnClickListener {
            registerUser()
        }

        // Already have an account - navigate to login
        val tvAlreadyHaveAccount: TextView = findViewById(R.id.tvAlreadyHaveAccount)
        tvAlreadyHaveAccount.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            finish()
        }

        // Set up click listener for country code picker
        tvSelectedCountryCode.setOnClickListener {
            showCountryCodePicker()
        }
    }

    private fun showCountryCodePicker() {
        // Create a list of country codes
        val countryCodes = arrayOf("+1", "+27", "+44", "+91", "+61") // Add more as needed

        // Create and show a dialog to select country code
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Country Code")
        builder.setItems(countryCodes) { _, which ->
            // Update the selected country code based on user choice
            selectedCountryCode = countryCodes[which]
            tvSelectedCountryCode.text = selectedCountryCode
        }
        builder.show()
    }

    private fun registerUser() {
        val flatNumber = etFlatNumber.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phoneNumber = etPhoneNumber.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Input validation
        if (TextUtils.isEmpty(flatNumber)) {
            etFlatNumber.error = "Flat Number is required."
            return
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.error = "Email is required."
            return
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            etPhoneNumber.error = "Phone Number is required."
            return
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.error = "Password is required."
            return
        }

        // Combine the selected country code with the phone number for storage
        val fullPhoneNumber =
            "$selectedCountryCode $phoneNumber" // Full phone number with country code


        // Proceed with Firebase authentication
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Get the unique user ID after successful registration
                    val userId = mAuth.currentUser?.uid ?: return@addOnCompleteListener

                    // Create a User object with the user's information
                    val user = User(userId, flatNumber, fullPhoneNumber, email, password)

                    // Save user details to Firebase Realtime Database
                    mDatabase.child(userId).setValue(user)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT)
                                .show()
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                this,
                                "Failed to save user details: ${it.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    Toast.makeText(
                        this,
                        "Registration failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    // Move getCurrentTime() function outside of registerUser()
    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault()) // Format time as HH:MM AM/PM
        return sdf.format(Date()) // Return current time
    }
}

