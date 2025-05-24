package com.example.app.model

data class Feedback(
    val phone_number: String,
    val rating: Int,
    val feedback_text: String?,
    val created_at: String?
)