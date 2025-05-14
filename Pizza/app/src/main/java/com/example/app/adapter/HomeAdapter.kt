package com.example.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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

class HomeAdapter(
    private var products: List<Products>,
    private val cartViewModel: CartViewModel,
    private val onAddToCart: (Carts) -> Unit
) : RecyclerView.Adapter<HomeAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image)
        val titleTextView: TextView = itemView.findViewById(R.id.name)
        val descriptionTextView: TextView = itemView.findViewById(R.id.description)
        val priceTextView: TextView = itemView.findViewById(R.id.price)
        val selectButton: Button = itemView.findViewById(R.id.Button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        holder.titleTextView.text = product.name
        holder.descriptionTextView.text = product.description
        holder.priceTextView.text = formatCurrency(product.price)

        val resourceId = holder.itemView.context.resources.getIdentifier(
            product.image, "drawable", holder.itemView.context.packageName
        )

        Glide.with(holder.itemView.context)
            .load(if (resourceId != 0) resourceId else R.drawable.pizza)
            .placeholder(R.drawable.pizza)
            .error(R.drawable.pizza)
            .into(holder.imageView)

        holder.selectButton.setOnClickListener {
            val tableNumber = cartViewModel.getTableNumber()
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
                                holder.itemView.context,
                                "${product.name} đã thêm vào giỏ hàng!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                holder.itemView.context,
                                "Lỗi thêm giỏ: ${response.code()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            holder.itemView.context,
                            "Lỗi: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = products.size

    fun updateData(newProducts: List<Products>) {
        this.products = newProducts
        notifyDataSetChanged()
    }
}