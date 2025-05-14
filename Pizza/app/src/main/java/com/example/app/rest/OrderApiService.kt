package com.example.app.rest

import com.example.app.model.Carts
import com.example.app.model.Orders
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface OrderApiService {
    @GET("orders_get.php")
    suspend fun getOrdersByPhone(@Query("phone_number") phoneNumber: String): Response<List<Orders>>

    @POST("orders_save.php")
    suspend fun saveOrder(@Body order: Orders): Response<Unit>
}