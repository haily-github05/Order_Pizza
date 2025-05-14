package com.example.app.rest

import com.example.app.model.Products
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductApiService {
    @GET("product_get.php")
    suspend fun getProducts(): List<Products>

    @GET("search.php")
    suspend fun searchProducts(@Query("query") query: String): List<Products>

    @GET("products_get_by_type.php")
    suspend fun getProductsByType(@Query("type") type: String): List<Products>
}
