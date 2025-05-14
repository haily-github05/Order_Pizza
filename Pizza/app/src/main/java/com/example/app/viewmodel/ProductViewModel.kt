package com.example.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.model.Products
import com.example.app.rest.RetrofitClient
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {
    private val _products = MutableLiveData<List<Products>>()
    val products: LiveData<List<Products>> get() = _products
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchProducts() {
        viewModelScope.launch {
            try {
                val productList = RetrofitClient.productApiService.getProducts()
                _products.postValue(productList)
            } catch (e: Exception) {
                _error.postValue("Failed to fetch products: ${e.message}")
            }
        }
    }

}