package com.example.app.view.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.app.R
import com.example.app.adapter.CartAdapter
import com.example.app.databinding.FragmentPaymentBinding
import com.example.app.model.CartItemDTO
import com.example.app.model.Carts
import com.example.app.model.Orders
import com.example.app.rest.RetrofitClient
import com.example.app.utils.formatCurrency
import com.example.app.viewmodel.CartViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PaymentFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!
    private lateinit var cartViewModel: CartViewModel
    private val phoneNumber = "0865529572"
    private lateinit var cartAdapter: CartAdapter
    companion object {
        private const val ARG_TOTAL_AMOUNT = "total_amount"

        fun newInstance(totalAmount: Double, cartViewModel: CartViewModel): PaymentFragment {
            val fragment = PaymentFragment()
            fragment.cartViewModel = cartViewModel
            val args = Bundle().apply {
                putDouble(ARG_TOTAL_AMOUNT, totalAmount)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Lấy tổng tiền từ arguments
        val totalAmount = arguments?.getDouble(ARG_TOTAL_AMOUNT) ?: 0.0
        binding.totalAmount.text = "Tổng Tiền: ${formatCurrency(totalAmount)}"

        binding.Button.setOnClickListener {
            val selectedId = binding.paymentOptions.checkedRadioButtonId

            if (selectedId == -1) {
                Toast.makeText(requireContext(), "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedId == R.id.Option1) {
                Toast.makeText(requireContext(), "Đang liên hệ nhân viên...", Toast.LENGTH_SHORT).show()
                sendSMS1()
            } else if (selectedId == R.id.Option2) {
                Toast.makeText(requireContext(), "Đang liên hệ nhân viên...", Toast.LENGTH_SHORT).show()
                sendSMS2()
            } else if (selectedId == R.id.Option3) {
                Toast.makeText(requireContext(), "Vui lòng mở ứng dụng thanh toán.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Phương thức thanh toán không hợp lệ", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun sendSMS1() {
        val sharedPref = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val tableNumber = sharedPref.getString("TABLE_NUMBER", "") ?: ""
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.SEND_SMS), 101)
            return
        }
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, "Khách BÀN $tableNumber yêu cầu thanh toán bằng tiền mặt.", null, null)
            moveCartToOrderHistory()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Gửi thất bại: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun sendSMS2() {
        val sharedPref = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val tableNumber = sharedPref.getString("TABLE_NUMBER", "") ?: ""
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.SEND_SMS), 101)
            return
        }
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, "Khách BÀN $tableNumber yêu cầu thanh toán bằng thẻ.", null, null)
            moveCartToOrderHistory()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Gửi thất bại: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun moveCartToOrderHistory() {
        val cartItems = cartViewModel.cartItems.value ?: emptyList()
        if (cartItems.isEmpty()) return

        val totalAmount = cartItems.sumOf { it.price * it.quantity }

        val sharedPref = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val loginType = sharedPref.getString("login_type", "guest")
        val phoneNumber = if (loginType != "guest") sharedPref.getString("phone_number", null) else null

        val order = Orders(
            order_id = System.currentTimeMillis().toString(),
            items = cartItems.map {
                CartItemDTO(
                    idProducts = it.idProducts,
                    price = it.price,
                    name = it.name,
                    note = it.note,
                    quantity = it.quantity
                )
            },
            totalAmount = totalAmount,
            timestamp = System.currentTimeMillis(),
            phoneNumber = phoneNumber
        )

        // Lưu đơn hàng vào API
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.orderApi.saveOrder(order)
                withContext(Dispatchers.Main) {
                    if (isAdded) {
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "Đã thanh toán" )
                            dismiss()
                            clearCart()
                        } else {
                            Toast.makeText(requireContext(), "Lỗi lưu đơn hàng: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    if (isAdded) { // Kiểm tra fragment còn gắn vào activity
                        Toast.makeText(requireContext(), "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun clearCart() {
        val sharedPref = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val tableNumber = sharedPref.getString("TABLE_NUMBER", "") ?: ""

        if (tableNumber.isEmpty()) {
            Toast.makeText(requireContext(), "Số bàn không hợp lệ", Toast.LENGTH_SHORT).show()
            return
        }

        val body = mapOf("table_number" to tableNumber)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.cartApi.clearCart(body).execute()
                withContext(Dispatchers.Main) {
                    if (isAdded) {
                        if (response.isSuccessful) {
                            cartViewModel.setCartItems(emptyList())
                            cartAdapter.updateData(emptyList())

                        } else {
                            Toast.makeText(requireContext(), "Lỗi xóa giỏ hàng: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    if (isAdded && context != null) {
                        Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSMS1()
                sendSMS2()
            } else {
                Toast.makeText(requireContext(), "Bạn cần cấp quyền để gửi SMS", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}