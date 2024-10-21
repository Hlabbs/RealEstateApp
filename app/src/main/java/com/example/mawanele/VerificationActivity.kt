package com.example.mawanele

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import android.view.View

class VerificationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var verificationCodeInput: EditText
    private lateinit var verifyButton: Button
    private lateinit var resendCodeText: TextView
    private lateinit var incorrectCodeText: TextView

    private lateinit var verificationId: String
    private lateinit var phoneNumber: String // Add this line to hold the phone number

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)

        auth = FirebaseAuth.getInstance()

        verificationCodeInput = findViewById(R.id.etVerificationCode)
        verifyButton = findViewById(R.id.btnVerify)
        resendCodeText = findViewById(R.id.tvResendCode)
        incorrectCodeText = findViewById(R.id.tvIncorrectCode)

        // Get the verification ID and phone number from the intent
        verificationId = intent.getStringExtra("verificationId") ?: ""
        phoneNumber = intent.getStringExtra("phoneNumber") ?: "" // Add this line

        verifyButton.setOnClickListener {
            val code = verificationCodeInput.text.toString()
            if (TextUtils.isEmpty(code)) {
                Toast.makeText(this, "Please enter the verification code", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            verifyCode(code)
        }

        resendCodeText.setOnClickListener {
            // Logic to resend code
            sendVerificationCode(phoneNumber) // Use the phone number to resend
        }
    }

    private fun verifyCode(code: String) {
        val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, code)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Verification Successful", Toast.LENGTH_SHORT).show()
                    // Navigate to the next activity
                    // startActivity(Intent(this, NextActivity::class.java))
                    finish()
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        incorrectCodeText.visibility = View.VISIBLE
                    }
                }
            }
    }

    // Include the resend code logic as a function here
    private fun sendVerificationCode(phoneNumber: String) {
        // Similar to your previous method in LoginActivity
        // Add your verification code sending logic here
    }
}

