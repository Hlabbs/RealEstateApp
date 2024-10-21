package com.example.mawanele

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import android.util.Log
import android.widget.CheckBox

class LoginActivity : AppCompatActivity() {

    private lateinit var etPhoneNumber: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvNoAccount: TextView
    private lateinit var mDatabase: DatabaseReference
    private lateinit var tvSelectedCountryCode: TextView
    private lateinit var cbShowPassword: CheckBox  // Add this line

    private var selectedCountryCode: String = "+1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Realtime Database
        mDatabase = FirebaseDatabase.getInstance().getReference("users")

        // Find views
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvNoAccount = findViewById(R.id.tvNoAccount)
        tvSelectedCountryCode = findViewById(R.id.tvSelectedCountryCode)
        cbShowPassword = findViewById(R.id.cbShowPassword)  // Initialize the CheckBox

        // Default Country Code Setup
        tvSelectedCountryCode.text = selectedCountryCode
        tvSelectedCountryCode.setOnClickListener { showCountryCodePicker() }
        // Toggle password visibility when checkbox is checked
        cbShowPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            // Move the cursor to the end of the EditText
            etPassword.setSelection(etPassword.text.length)
        }

        btnLogin.setOnClickListener {
            val phoneNumber = etPhoneNumber.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (TextUtils.isEmpty(phoneNumber)) {
                etPhoneNumber.error = "Please enter a valid phone number"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                etPassword.error = "Please enter a valid password"
                return@setOnClickListener
            }

            val fullPhoneNumber = "$selectedCountryCode $phoneNumber"
            loginUser(fullPhoneNumber, password)
        }

        tvNoAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun showCountryCodePicker() {
        val countryCodes = arrayOf("+1", "+27", "+44", "+91", "+61")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Country Code")
        builder.setItems(countryCodes) { _, which ->
            selectedCountryCode = countryCodes[which]
            tvSelectedCountryCode.text = selectedCountryCode
        }
        builder.show()
    }

    private fun loginUser(phoneNumber: String, password: String) {
        mDatabase.orderByChild("phoneNumber").equalTo(phoneNumber)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Validate password here, if stored in the database
                        val userSnapshot = dataSnapshot.children.first()
                        val storedPassword =
                            userSnapshot.child("password").getValue(String::class.java)

                        if (storedPassword == password) {
                            Toast.makeText(
                                this@LoginActivity,
                                "Login successful",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "Invalid password",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Login failed. User not found.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("LoginActivity", "Database error: ${databaseError.message}")
                    Toast.makeText(this@LoginActivity, "Login failed. Try again.", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }
}
