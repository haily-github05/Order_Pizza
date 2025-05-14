package com.example.app.model

data class Carts(
    val idProducts: String,
    val price: Double,
    val name: String,
    var quantity: Int,
    var note: String? = null,
    val tableNumber: String
)
