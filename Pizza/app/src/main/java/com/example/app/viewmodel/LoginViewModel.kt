package com.example.app.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.model.Users
import com.example.app.repository.UserRepository
import com.example.app.rest.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoginViewModel : ViewModel() {

    private val repository = UserRepository()

    // LiveData cho kết quả đăng nhập và lỗi
    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _otpState = MutableLiveData<OtpState>()
    val otpState: LiveData<OtpState> get() = _otpState
//    fun sendOtp(phone: String) {
//        _otpState.value = OtpState.Loading
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                Log.d(TAG, "Sending OTP to phone: $phone")
//                val response = RetrofitClient.apiService.sendOtp(OtpRequest(phone))
//                _otpState.postValue(
//                    if (response.success) OtpState.Success(response.message)
//                    else OtpState.Error(response.message)
//                )
//            } catch (e: HttpException) {
//                val errorBody = e.response()?.errorBody()?.string()
//                Log.e(TAG, "HTTP error: ${e.code()}, body: $errorBody")
//                _otpState.postValue(OtpState.Error("Lỗi server: ${e.code()} - $errorBody"))
//            } catch (e: IOException) {
//                Log.e(TAG, "Network error: ${e.message}")
//                _otpState.postValue(OtpState.Error("Lỗi mạng: Kiểm tra kết nối internet"))
//            } catch (e: Exception) {
//                Log.e(TAG, "Unexpected error: ${e.message}", e)
//                _otpState.postValue(OtpState.Error("Lỗi không xác định: ${e.message}"))
//            }
//        }
//    }
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
}
sealed class OtpState {
    object Loading : OtpState()
    data class Success(val message: String) : OtpState()
    data class Error(val message: String) : OtpState()
}