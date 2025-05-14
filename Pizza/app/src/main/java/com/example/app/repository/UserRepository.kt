package com.example.app.repository

import com.example.app.model.Users
import com.example.app.rest.RetrofitClient
import retrofit2.Response

class UserRepository {

    // Lấy danh sách người dùng
    suspend fun getUsers(): Response<List<Users>> {
        return RetrofitClient.apiService.getUsers()
    }

    // Lấy người dùng theo số điện thoại
    suspend fun getUserByPhone(phone: String): Response<Users> {
        return RetrofitClient.apiService.getUserByPhone(phone)
    }

    // Thêm người dùng mới
    suspend fun addUser(user: Users): Response<Users> {
        return RetrofitClient.apiService.addUser(user)
    }
}
