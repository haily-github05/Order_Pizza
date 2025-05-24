package com.example.app.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.model.OtpSendRequest
import com.example.app.model.Users
import com.example.app.repository.UserRepository
import com.example.app.rest.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class LoginViewModel : ViewModel() {

    private val repository = UserRepository()

    // LiveData cho kết quả đăng nhập và lỗi
    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    // Hàm để kiểm tra số điện thoại và thêm người dùng nếu cần
    fun validateLogin(phone: String) {
        viewModelScope.launch {
            try {
                // Kiểm tra xem số điện thoại có trong bảng không
                val response = repository.getUserByPhone(phone)
                if (response.isSuccessful && response.body() != null) {
                    // Nếu có, thực hiện đăng nhập
                    _loginResult.postValue(true)
                } else {
                    // Nếu không có, thêm người dùng mới
                    addNewUser(phone)
                }
            } catch (e: Exception) {
                _error.postValue("Lỗi kết nối: ${e.message}")
            }
        }
    }

    // Thêm người dùng mới
    private fun addNewUser(phone: String) {
        viewModelScope.launch {
            try {
                val newUser = Users(sdt = phone, points = 0)
                val response = repository.addUser(newUser)
                if (response.isSuccessful) {
                    _loginResult.postValue(true)  // Đăng nhập thành công
                } else {
                    _error.postValue("Thêm người dùng thất bại")
                }
            } catch (e: Exception) {
                _error.postValue("Lỗi kết nối: ${e.message}")
            }
        }
    }

    // Lấy danh sách người dùng (tùy chọn)
    fun fetchUsers() {
        viewModelScope.launch {
            try {
                val response = repository.getUsers()
                if (response.isSuccessful) {
                    // Xử lý dữ liệu nếu cần
                } else {
                    _error.postValue("Lỗi khi lấy dữ liệu người dùng")
                }
            } catch (e: Exception) {
                _error.postValue("Lỗi kết nối: ${e.message}")
            }
        }
    }
    fun sendOtpToEmail(email: String, otp: String, callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.sendOtp(OtpSendRequest(email, otp))
                if (response.isSuccessful && response.body()?.success == true) {
                    callback(true, response.body()?.message ?: "Gửi thành công")
                } else {
                    callback(false, response.body()?.message ?: "Gửi thất bại")
                }
            } catch (e: Exception) {
                callback(false, "Lỗi: ${e.message}")
            }
        }
    }
}