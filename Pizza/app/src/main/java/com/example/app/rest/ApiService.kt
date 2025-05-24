package com.example.app.rest

import com.example.app.model.OtpResponse
import com.example.app.model.OtpSendRequest
import com.example.app.model.Users
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers

interface ApiService {
    @GET("user_get.php")
    suspend fun getUsers(): Response<List<Users>>

    @GET("user_check.php")
    suspend fun getUserByPhone(@Query("sdt") phone: String): Response<Users>

    @POST("user_add.php")
    suspend fun addUser(@Body user: Users): Response<Users>

    @POST("user_update_points.php")
    suspend fun updateUserPoints(@Body body: Map<String, Any>): Response<Void>

    @POST("cart_clear.php")
    fun clearCart(): retrofit2.Call<Void>


//    @FormUrlEncoded
//    @POST("send_otp")
//    suspend fun sendOtp(
//        @Field("phone") phone: String,
//        @Field("message") message: String
//    ): Response<SmsResponse>
    @POST("send_email_otp.php")
    suspend fun sendOtp(@Body request: OtpSendRequest): Response<OtpResponse>
}


