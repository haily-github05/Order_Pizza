package com.example.app.rest

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
//    private const val BASE_URL = "http://10.0.2.2:8080/myapi/"
    private const val BASE_URL = "http://192.168.1.122:8080/myapi/"
    // Cấu hình OkHttpClient chung
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    // Hàm tạo Retrofit client chung
    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // API Service chung
    val apiService: ApiService by lazy {
        getRetrofit().create(ApiService::class.java)
    }

    val productApiService: ProductApiService by lazy {
        getRetrofit().create(ProductApiService::class.java)
    }

    val cartApi: CartApiService by lazy {
        getRetrofit().create(CartApiService::class.java)
    }

    val orderApi: OrderApiService by lazy {
        getRetrofit().create(OrderApiService::class.java)
    }
//    private val gson = GsonBuilder()
//        .setLenient()
//        .create()
}
