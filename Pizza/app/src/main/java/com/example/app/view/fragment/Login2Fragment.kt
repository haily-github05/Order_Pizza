package com.example.app.view.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.app.databinding.FragmentLogin2Binding
import com.example.app.view.activity.MainActivity
import com.example.app.viewmodel.CartViewModel
import com.example.app.viewmodel.LoginViewModel
import com.example.app.rest.RetrofitClient
import kotlinx.coroutines.launch

class Login2Fragment : Fragment() {
    private var _binding: FragmentLogin2Binding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var cartViewModel: CartViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogin2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartViewModel = ViewModelProvider(requireActivity()).get(CartViewModel::class.java)

        val sharedPref = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        sharedPref.edit()
            .putString("login_type", "registered")
            .apply()

        viewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
        }

        viewModel.loginResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                val phone = binding.TextPhone.text.toString().trim()
                val tableNumber = binding.editTextNumber.text.toString().trim()

                sharedPref.edit()
                    .putString("phone_number", phone)
                    .putString("TABLE_NUMBER", tableNumber)
                    .apply()

                cartViewModel.setTableNumber(tableNumber)

                Toast.makeText(requireContext(), "Đăng nhập thành công! Bàn số: $tableNumber", Toast.LENGTH_SHORT).show()

                // Navigate to MainActivity
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            } else {
                Toast.makeText(requireContext(), "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.checkOTP.setOnClickListener {
            val phoneNumber = binding.TextPhone.text.toString().trim()
            if (phoneNumber.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val otp = (100000..999999).random().toString()
                    val message = "Mã OTP của bạn là: $otp"
                    val response = RetrofitClient.apiService.sendOtp(phoneNumber, message)

                    if (response.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "OTP đã gửi tới $phoneNumber",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.checkOTP.apply {
                            isEnabled = false
                            text = "Đã gửi mã"
                        }
                    } else {
                        Toast.makeText(requireContext(), "Gửi OTP thất bại", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.signInButton.setOnClickListener {
            val phone = binding.TextPhone.text.toString().trim()
            val otp = binding.OTP.text.toString().trim()
            val tableNumber = binding.editTextNumber.text.toString().trim()

            when {
                phone.isEmpty() -> {
                    Toast.makeText(requireContext(), "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show()
                }
                otp.isEmpty() -> {
                    Toast.makeText(requireContext(), "Vui lòng nhập mã OTP", Toast.LENGTH_SHORT).show()
                }
                tableNumber.isEmpty() -> {
                    Toast.makeText(requireContext(), "Vui lòng nhập số bàn", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    if (otp == "123456") {
                        viewModel.validateLogin(phone)
                    } else {
                        Toast.makeText(requireContext(), "Mã OTP không đúng", Toast.LENGTH_SHORT).show()
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