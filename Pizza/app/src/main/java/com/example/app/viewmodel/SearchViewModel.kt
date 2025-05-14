package com.example.app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.model.Products
import com.example.app.rest.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel : ViewModel() {

    private val _products = MutableLiveData<List<Products>>()
    val products: LiveData<List<Products>> get() = _products

    private val _suggestions = MutableLiveData<List<String>>()
    val suggestions: LiveData<List<String>> get() = _suggestions

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        // Lấy sản phẩm gợi ý mặc định khi khởi tạo
        fetchDefaultPizzaProducts()
    }

    fun searchProducts(query: String) {
        if (query.isBlank()) {
            // Khi query trống, hiển thị sản phẩm PIZZA mặc định
            fetchDefaultPizzaProducts()
            return
        }

        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val normalizedQuery = query.trim().lowercase()
                Log.d("SearchViewModel", "Gửi yêu cầu tìm kiếm với query: $normalizedQuery")
                val response = RetrofitClient.productApiService.searchProducts(normalizedQuery)
                Log.d("SearchViewModel", "API trả về: ${response.size} sản phẩm - $response")
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                    _products.value = response
                    _suggestions.value = response.map { it.name }
                    if (response.isEmpty()) {
                        _error.value = "Không tìm thấy sản phẩm"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                    Log.e("SearchViewModel", "Lỗi API: ${e.message}", e)
                    _error.value = "Lỗi: ${e.message}"
                    // Dữ liệu giả nếu API lỗi
                    _products.value = listOf(
                        Products("1", "Pizza Hải Sản", "Pizza với tôm, mực", 150000.0, "pizza", "PIZZA", 0),
                        Products("2", "Pizza Phô Mai", "Pizza phủ phô mai", 120000.0, "pizza", "PIZZA", 0)
                    )
                    _suggestions.value = _products.value?.map { it.name } ?: emptyList()
                }
            }
        }
    }

    private fun fetchDefaultPizzaProducts() {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("SearchViewModel", "Lấy sản phẩm gợi ý mặc định (type = PIZZA)")
                // Gọi API để lấy sản phẩm có type = PIZZA
                val response = RetrofitClient.productApiService.getProductsByType("PIZZA")
                Log.d("SearchViewModel", "API trả về gợi ý: ${response.size} sản phẩm - $response")
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                    _products.value = response.take(4) // Giới hạn tối đa 4 sản phẩm
                    _suggestions.value = response.map { it.name }
                    if (response.isEmpty()) {
                        // Dữ liệu giả nếu API rỗng
                        _products.value = listOf(
                            Products("1", "Pizza Hải Sản", "Pizza với tôm, mực", 150000.0, "pizza", "PIZZA", 0),
                            Products("2", "Pizza Phô Mai", "Pizza phủ phô mai", 120000.0, "pizza", "PIZZA", 0)
                        )
                        _suggestions.value = _products.value?.map { it.name } ?: emptyList()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                    Log.e("SearchViewModel", "Lỗi API gợi ý: ${e.message}", e)
                    _error.value = "Lỗi khi lấy gợi ý: ${e.message}"
                    // Dữ liệu giả nếu API lỗi
                    _products.value = listOf(
                        Products("1", "Pizza Hải Sản", "Pizza với tôm, mực", 150000.0, "pizza", "PIZZA", 0),
                        Products("2", "Pizza Phô Mai", "Pizza phủ phô mai", 120000.0, "pizza", "PIZZA", 0)
                    )
                    _suggestions.value = _products.value?.map { it.name } ?: emptyList()
                }
            }
        }
    }
}