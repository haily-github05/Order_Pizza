package com.example.app.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app.R
import com.example.app.model.Orders
import java.text.SimpleDateFormat
import java.util.*

class OrderAdapter(private var orders: List<Orders>) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val orderIdText: TextView = view.findViewById(R.id.orderIdText)
        val totalAmountText: TextView = view.findViewById(R.id.totalAmountText)
        val timestampText: TextView = view.findViewById(R.id.timestampText)
        val itemsText: TextView = view.findViewById(R.id.itemsText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.orderIdText.text = "Mã đơn: ${order.order_id}"
        holder.totalAmountText.text = "Tổng tiền: ${"%,.0f".format(order.totalAmount)} VND"
        holder.timestampText.text = "Thời gian: ${formatDate(order.timestamp)}"

        val itemDetails = buildString {
            append("Món đã gọi:\n")
            order.items.forEach {
                append("- ${it.name} x${it.quantity} ( ${String.format("%,.0f", it.price)} VND")
                if (!it.note.isNullOrBlank()) {
                    append("; Ghi chú: ${it.note}")
                }
                append(")\n")
            }
        }

        holder.itemsText.text = itemDetails
    }


    override fun getItemCount(): Int = orders.size

    fun updateData(newOrders: List<Orders>) {
        orders = newOrders
        notifyDataSetChanged()
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
