package com.example.app.rest

import com.example.app.model.SmsResponse
import com.example.app.model.Users
import com.example.app.repository.ReviewResponse
import com.example.app.repository.ReviewResponseList
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded

interface ApiService {
    @GET("user_get.php")
    suspend fun getUsers(): Response<List<Users>>

    @GET("user_check.php")
    suspend fun getUserByPhone(@Query("sdt") phone: String): Response<Users>

    @POST("user_add.php")
    suspend fun addUser(@Body user: Users): Response<Users>

    @POST("cart_clear.php")
    fun clearCart(): retrofit2.Call<Void>


    @FormUrlEncoded
    @POST("send_otp")
    suspend fun sendOtp(
        @Field("phone") phone: String,
        @Field("message") message: String
    ): Response<SmsResponse>
//    @POST("sendOtp")
//    suspend fun sendOtp(
//        @Query("phone") phone: String,
//        @Query("message") message: String
//    ): Response<Unit>

    //reviews
    @FormUrlEncoded
    @POST("reviews_save.php")
    suspend fun saveReview(
        @Field("image_product") image_product: String?,
        @Field("phone_number") phone_number: String,
        @Field("rating") rating: Int,
        @Field("comment") comment: String
    ): Response<ReviewResponse>

    @GET("reviews_get.php")
    suspend fun getReviews(): Response<ReviewResponseList>
}


