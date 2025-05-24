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
import com.example.app.R
import com.example.app.databinding.FragmentLogin2Binding
import com.example.app.view.activity.MainActivity
import com.example.app.viewmodel.CartViewModel
import com.example.app.viewmodel.LoginViewModel
import com.example.app.rest.RetrofitClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.regex.Pattern

class Login2Fragment : Fragment() {
    private var _binding: FragmentLogin2Binding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var cartViewModel: CartViewModel
    private var generatedOTP: String? = null
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

                Toast.makeText(requireContext(), getString(R.string.enter_login ,tableNumber), Toast.LENGTH_LONG).show()

                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            } else {
                Toast.makeText(requireContext(), getString(R.string.error_login), Toast.LENGTH_LONG).show()
            }
        }

        binding.checkOTP.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            if (!isValidEmail(email)) {
                Toast.makeText(requireContext(), "Email không hợp lệ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val otp = (100000..999999).random().toString()
            generatedOTP = otp

            viewModel.sendOtpToEmail(email, otp) { success, message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        binding.signInButton.setOnClickListener {
            val phone = binding.TextPhone.text.toString().trim()
            val tableNumber = binding.editTextNumber.text.toString().trim()
            val enteredOtp = binding.OTP.text.toString().trim()

            when {
                phone.isEmpty() -> {
                    Toast.makeText(requireContext(), getString(R.string.error_phone), Toast.LENGTH_SHORT).show()
                }
                enteredOtp.isEmpty() -> {
                    Toast.makeText(requireContext(), getString(R.string.enter_otp), Toast.LENGTH_SHORT).show()
                }
                tableNumber.isEmpty() -> {
                    Toast.makeText(requireContext(),getString(R.string.enter_number), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    if (enteredOtp == generatedOTP) {
                        viewModel.validateLogin(phone)
                        Toast.makeText(requireContext(), getString(R.string.enter_login ,tableNumber), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.error_otp), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
        return pattern.matcher(email).matches()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}