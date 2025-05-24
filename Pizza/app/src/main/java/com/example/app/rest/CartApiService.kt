package com.example.app.rest

import androidx.room.FtsOptions
import com.example.app.model.Carts
import com.example.app.model.Orders
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CartApiService {
    @POST("cart_add.php")
    suspend fun addCartItem(@Body cartItem: Carts): Response<Map<String, Any>>

    @GET("cart_get.php")
    suspend fun getCartItems(@Query("tableNumber") tableNumber: String): Response<List<Carts>>

    @FormUrlEncoded
    @PUT("cart_update.php")
    suspend fun updateCartItem(@FieldMap cartItem: Map<String, String>): Response<Void>

    @FormUrlEncoded
    @PUT("cart_update_note.php")
    suspend fun updateCartNote(
        @Field("idProducts") idProducts: String,
        @Field("table_number") tableNumber: String,
        @Field("note") note: String?
    ): Response<Void>

    @POST("cart_delete.php")
    suspend fun deleteCartItem(@Body cartItem: Map<String, String>): Response<Void>

    @POST("cart_clear.php")
    fun clearCart(@Body body: Map<String, String>): Call<Void>

}