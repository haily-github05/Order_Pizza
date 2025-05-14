package com.example.app.repository

import com.google.gson.annotations.SerializedName

data class ReviewResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("rating") val rating: Int?,
    @SerializedName("comment") val feedback: String?,
    @SerializedName("image_product") val imageUrl: String?
) {
    fun isSuccessful(): Boolean = success
}

