package com.example.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.app.R
import com.example.app.model.Carts
import com.example.app.model.Products
import com.example.app.rest.RetrofitClient
import com.example.app.utils.formatCurrency
import com.example.app.viewmodel.CartViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MenuAdapter(
    private var products: List<Products>,
    private val cartViewModel: CartViewModel,
    private val onAddToCart: (Carts) -> Unit
) : RecyclerView.Adapter<MenuAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val nameTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
        val addToCartButton: View = itemView.findViewById(R.id.selectButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_products, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.nameTextView.text = product.name
        holder.descriptionTextView.text = product.description
        holder.priceTextView.text = formatCurrency(product.price)

        val context = holder.itemView.context
        val resourceId = context.resources.getIdentifier(
            product.image, "drawable", context.packageName
        )

        Glide.with(context)
            .load(if (resourceId != 0) resourceId else R.drawable.pizza)
            .placeholder(R.drawable.pizza)
            .error(R.drawable.pizza)
            .into(holder.imageView)

        holder.addToCartButton.setOnClickListener {
            val tableNumber = cartViewModel.getTableNumber()
            if (tableNumber.isEmpty()) {
                Toast.makeText(
                    context,
                    "Vui lòng chọn số bàn trước khi thêm vào giỏ hàng!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val cartItem = Carts(
                idProducts = product.idProducts,
                name = product.name,
                price = product.price,
                quantity = 1,
                tableNumber = tableNumber
            )

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.cartApi.addCartItem(cartItem)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            onAddToCart(cartItem)
                            Toast.makeText(
                                context,
                                "${product.name} đã thêm vào giỏ hàng!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Lỗi thêm giỏ: ${response.code()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Lỗi: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    fun updateData(newList: List<Products>) {
        this.products = newList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = products.size
}