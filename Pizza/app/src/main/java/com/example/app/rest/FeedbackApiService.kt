package com.example.app.rest

import com.example.app.model.Feedback
import ReviewResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FeedbackApiService {
    @POST("feedbacks_save.php")
    suspend fun submitFeedback(@Body feedback: Feedback): Response<Void>
    @GET("feedbacks_get.php")
    suspend fun getReviews(): Response<ReviewResponse>
}