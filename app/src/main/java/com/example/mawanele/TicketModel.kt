package com.example.mawanele

data class TicketModel(
    val ticketId: String = "",         // Unique identifier for the ticket
    val userid: String = "",           // User ID of the person submitting the ticket
    val subject: String = "",           // Subject of the ticket
    val description: String = "",       // Description of the issue
    val imageUrl: String = "" ,          // URL of the uploaded image (if applicable)
    val date: String = "" , // Add this line to include date
    val email: String // Add email property
)


