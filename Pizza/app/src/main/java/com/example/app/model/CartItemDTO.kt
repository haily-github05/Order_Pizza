package com.example.app.model

data class CartItemDTO(
    val idProducts: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val note: String?
)
