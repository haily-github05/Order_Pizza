package com.example.app.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.app.R
import com.example.app.model.Carts
import com.example.app.rest.RetrofitClient
import com.example.app.utils.formatCurrency
import com.example.app.viewmodel.CartViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartAdapter(
    private val onIncrease: (Carts) -> Unit,
    private val onDecrease: (Carts) -> Unit,
    private val onDelete: (Carts) -> Unit,
    private val onCartUpdated: (List<Carts>) -> Unit,
    private val cartViewModel: CartViewModel
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    private var cartItems: List<Carts> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        holder.itemName.text = item.name
        holder.itemPrice.text = formatCurrency(item.price)
        holder.itemQuantity.text = item.quantity.toString()

        holder.increaseButton.setOnClickListener {
            val updatedQuantity = item.quantity + 1
            updateCartItemQuantity(item, updatedQuantity, holder)
        }

        holder.decreaseButton.setOnClickListener {
            val currentQuantity = item.quantity
            if (currentQuantity > 1) {
                val updatedQuantity = currentQuantity - 1
                updateCartItemQuantity(item, updatedQuantity, holder)
            } else {
                deleteCartItem(item, holder)
            }
        }

        holder.deleteButton.setOnClickListener {
            deleteCartItem(item, holder)
        }
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateData(newCartItems: List<Carts>) {
        cartItems = newCartItems
        notifyDataSetChanged()
        onCartUpdated(cartItems)
    }

    fun getCartItems(): List<Carts> = cartItems

    private fun updateCartItemQuantity(item: Carts, updatedQuantity: Int, holder: CartViewHolder) {
        val tableNumber = cartViewModel.getTableNumber()
        if (tableNumber.isEmpty()) {
            showToastOnce(holder.itemView.context, "Số bàn không hợp lệ")
            return
        }
        val cartUpdate = mapOf(
            "idProducts" to item.idProducts,
            "quantity" to updatedQuantity.toString(),
            "table_number" to tableNumber
        )
        Log.d("CartAdapter", "Sending update request: $cartUpdate")
        val oldQuantity = item.quantity
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.cartApi.updateCartItem(cartUpdate)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        item.quantity = updatedQuantity
                        holder.itemQuantity.text = updatedQuantity.toString()
                        cartViewModel.updateCart(item.copy())
                        if (updatedQuantity > oldQuantity) {
                            onIncrease(item)
                        } else {
                            onDecrease(item)
                        }
                        onCartUpdated(cartItems)
                    } else {
                        val errorBody = response.errorBody()?.string()
                        showToastOnce(holder.itemView.context, "Lỗi cập nhật số lượng: ${response.code()} - $errorBody")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToastOnce(holder.itemView.context, "Lỗi mạng: ${e.message}")
                }
            }
        }
    }

    private fun deleteCartItem(item: Carts, holder: CartViewHolder) {
        val tableNumber = cartViewModel.getTableNumber()
        if (tableNumber.isEmpty()) {
            showToastOnce(holder.itemView.context, "Số bàn không hợp lệ")
            return
        }
        val deleteMap = mapOf(
            "idProducts" to item.idProducts,
            "table_number" to tableNumber
        )
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.cartApi.deleteCartItem(deleteMap)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        cartViewModel.deleteProduct(item)
                        item.quantity = 0
                        holder.itemQuantity.text = "0"
                        onDelete(item)
                        onCartUpdated(cartItems)
                        showToastOnce(holder.itemView.context, "Đã xóa ${item.name}")
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("CartAdapter", "Lỗi xóa: ${response.code()} - $errorBody")
                        showToastOnce(holder.itemView.context, "Lỗi xóa: ${response.code()} - $errorBody")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("CartAdapter", "Lỗi mạng: ${e.message}")
                    showToastOnce(holder.itemView.context, "Lỗi mạng: ${e.message}")
                }
            }
        }
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.itemName)
        val itemPrice: TextView = itemView.findViewById(R.id.itemPrice)
        val itemQuantity: TextView = itemView.findViewById(R.id.itemQuantity)
        val increaseButton: ImageButton = itemView.findViewById(R.id.increaseButton)
        val decreaseButton: ImageButton = itemView.findViewById(R.id.decreaseButton)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    }

    private var lastToastTime = 0L

    private fun showToastOnce(context: Context, message: String) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastToastTime > 1000) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            lastToastTime = currentTime
        }
    }
}