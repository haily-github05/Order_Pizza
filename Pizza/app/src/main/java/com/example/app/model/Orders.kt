package com.example.app.model

data class Orders(
    val order_id: String,
    val items: List<CartItemDTO>,
    val totalAmount: Double,
    val timestamp: Long,
    val phoneNumber: String? = null
)
