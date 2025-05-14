package com.example.app.model

data class Products(
    val idProducts: String,
    val name: String,
    val description: String,
    val price: Double,
    val image: String,
    val type: String,
    var quantity: Int? = 0
)
