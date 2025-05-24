package com.example.app.view.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app.R
import com.example.app.adapter.CartAdapter
import com.example.app.databinding.FragmentCartBinding
import com.example.app.model.Carts
import com.example.app.rest.RetrofitClient
import com.example.app.utils.formatCurrency
import com.example.app.viewmodel.CartViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var cartAdapter: CartAdapter
    private lateinit var cartViewModel: CartViewModel
    private var carts: List<Carts> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val tableNumber = sharedPref.getString("TABLE_NUMBER", "") ?: ""
        binding.tableNumberText.text = getString(R.string.table, tableNumber)

        cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)
        cartViewModel.setTableNumber(tableNumber)

        cartAdapter = CartAdapter(
            onIncrease = { updateTotalAmount(cartViewModel.cartItems.value ?: emptyList()) },
            onDecrease = { updateTotalAmount(cartViewModel.cartItems.value ?: emptyList()) },
            onDelete = { updateTotalAmount(cartViewModel.cartItems.value ?: emptyList()) },
            onCartUpdated = { updateTotalAmount(it) },
            cartViewModel = cartViewModel
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cartAdapter
        }

        cartViewModel.cartItems.observe(viewLifecycleOwner) { cartItems ->
            cartAdapter.updateData(cartItems ?: emptyList())
            updateTotalAmount(cartItems ?: emptyList())
        }

        loadCartItems()

        binding.checkoutButton.setOnClickListener {
            if (carts.isNotEmpty()) {
                proceedToPayment()
            } else {
                Toast.makeText(context, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun proceedToPayment() {
        val cartItems = cartViewModel.cartItems.value ?: emptyList()
        if (cartItems.isNotEmpty()) {
            val totalAmount = cartItems.sumOf { it.price * it.quantity }
            val bottomSheet = PaymentFragment.newInstance(totalAmount, cartViewModel)
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        } else {
            Toast.makeText(context, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadCartItems() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sharedPref = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                val tableNumber = sharedPref.getString("TABLE_NUMBER", "") ?: ""
                val response = RetrofitClient.cartApi.getCartItems(tableNumber)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val cartItems = response.body() ?: emptyList()
                        Log.d("CartFragment", "API Response: $cartItems")
                        carts = cartItems
                        cartViewModel.setCartItems(cartItems)
                        cartAdapter.updateData(cartItems)
                        updateTotalAmount(cartItems)
                    } else {
                        Toast.makeText(context, "Lỗi tải giỏ hàng: ${response.code()}", Toast.LENGTH_SHORT).show()
                        Log.e("CartFragment", "Load cart failed: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("CartFragment", "Load cart error: ${e.message}", e)
                }
            }
        }
    }

    private fun updateTotalAmount(cartItems: List<Carts>) {
        val total = cartItems.sumOf { it.price * it.quantity }
        binding.totalAmount.text = "Tổng Tiền: ${formatCurrency(total)}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}