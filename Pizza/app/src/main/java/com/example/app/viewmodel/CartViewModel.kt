package com.example.app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.app.model.Carts
import com.example.app.rest.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartViewModel : ViewModel() {

    private val _cartItems = MutableLiveData<List<Carts>>(emptyList())
    val cartItems: LiveData<List<Carts>> = _cartItems

    private val _totalPrice = MutableLiveData<Double>(0.0)
    val totalPrice: LiveData<Double> = _totalPrice
    private var tableNumber: String = ""

    fun setTableNumber(number: String) {
        tableNumber = number
    }

    fun getTableNumber(): String = tableNumber
    fun setCartItems(items: List<Carts>) {
        _cartItems.value = items
        calculateTotal()
    }

    fun addProduct(carts: Carts) {
        val currentItems = _cartItems.value?.toMutableList() ?: mutableListOf()
        val existingProduct = currentItems.find { it.idProducts == carts.idProducts }
        if (existingProduct != null) {
            existingProduct.quantity += 1
        } else {
            carts.quantity = 1
            currentItems.add(carts.copy())
        }
        _cartItems.value = currentItems
        calculateTotal()
    }

    fun updateCart(item: Carts) {
        val currentList = _cartItems.value?.toMutableList() ?: mutableListOf()
        val index = currentList.indexOfFirst { it.idProducts == item.idProducts }
        if (index != -1) {
            currentList[index] = item
            _cartItems.value = currentList
            calculateTotal()
            Log.d("CartViewModel", "Updated Cart Item: $item")
        }

    }

    fun deleteProduct(item: Carts) {
        val currentList = _cartItems.value?.toMutableList() ?: return
        currentList.removeAll { it.idProducts == item.idProducts }
        _cartItems.value = currentList
        calculateTotal()
        Log.d("CartViewModel", "Deleted Item: ${item.idProducts}")
    }

    fun removeProduct(carts: Carts) {
        val currentItems = _cartItems.value?.toMutableList() ?: return
        val existingProduct = currentItems.find { it.idProducts == carts.idProducts }
        if (existingProduct != null && existingProduct.quantity > 1) {
            existingProduct.quantity -= 1
        } else {
            currentItems.remove(existingProduct)
        }
        _cartItems.value = currentItems
        calculateTotal()
    }

    private fun calculateTotal() {
        val total = _cartItems.value?.sumOf { it.price * it.quantity } ?: 0.0
        _totalPrice.value = total
        Log.d("CartViewModel", "Total Price: $total")
    }

    fun clearCart() {
        _cartItems.value = listOf()
    }

}
