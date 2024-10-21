package com.example.mawanele

data class User (
    val userid: String,
    val flatNumber: String,
    val phoneNumber: String,
    val email: String,
    val password: String,
    val profile_url: String? = null
)