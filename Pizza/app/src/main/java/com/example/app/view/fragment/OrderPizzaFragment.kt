package com.example.app.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app.databinding.FragmentOrderPizzaBinding
import com.example.app.model.Orders
import com.example.app.rest.RetrofitClient
import com.example.app.view.adapter.OrderAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.app.AlertDialog
import android.app.AlertDialog.*
import com.example.app.R

class OrderPizzaFragment : Fragment() {

    private var _binding: FragmentOrderPizzaBinding? = null
    private val binding get() = _binding!!
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderPizzaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo RecyclerView và Adapter
        orderAdapter = OrderAdapter(emptyList())
        binding.RecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderAdapter
        }

        val sharedPref = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val phoneNumber = sharedPref.getString("phone_number", null)

        val loginType = sharedPref.getString("login_type", "guest")
        if (loginType == "guest" || phoneNumber == null) {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.required)
                .setMessage(R.string.required_phone)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    val homeFragment = HomeFragment()
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.content_frame, homeFragment)
                        .commit()
                }
                .setCancelable(false)
                .show()
            return
        }
        if (phoneNumber == null) {
            Toast.makeText(requireContext(), getString(R.string.phone_not_found), Toast.LENGTH_LONG).show()
            return
        }

        fetchOrders(phoneNumber)
    }


    private fun fetchOrders(phoneNumber: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Gọi API với số điện thoại của người dùng
                val response = RetrofitClient.orderApi.getOrdersByPhone(phoneNumber)
                withContext(Dispatchers.Main) {
                    if (isAdded) {
                        if (response.isSuccessful) {
                            val orders = response.body() ?: emptyList()
                            if (orders.isEmpty()) {
                                Toast.makeText(requireContext(), "Bạn chưa có đơn hàng nào.", Toast.LENGTH_SHORT).show()
                            }
                            orderAdapter.updateData(orders)
                        } else {
                            Toast.makeText(requireContext(), "Lỗi tải đơn hàng: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    if (isAdded) {
                        Toast.makeText(requireContext(), "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}