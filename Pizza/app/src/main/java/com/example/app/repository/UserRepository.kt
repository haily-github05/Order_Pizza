package com.example.app.repository

import com.example.app.model.Users
import com.example.app.rest.RetrofitClient
import retrofit2.Response

class UserRepository {

    suspend fun getUsers(): Response<List<Users>> {
        return RetrofitClient.apiService.getUsers()
    }

    suspend fun getUserByPhone(phone: String): Response<Users> {
        return RetrofitClient.apiService.getUserByPhone(phone)
    }

    suspend fun addUser(user: Users): Response<Users> {
        return RetrofitClient.apiService.addUser(user)
    }
}
