package com.example.app.view.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.app.R
import com.example.app.view.activity.MainActivity
import com.example.app.viewmodel.CartViewModel

class Login1Fragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login1, container, false)

        val cartViewModel = ViewModelProvider(requireActivity()).get(CartViewModel::class.java)

        val signInButton = view.findViewById<Button>(R.id.signInButton)
        val editTextNumber = view.findViewById<EditText>(R.id.editTextNumber)

        val sharedPref = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        sharedPref.edit()
            .putString("login_type", "guest")
            .apply()

        signInButton.setOnClickListener {
            val tableNumber = editTextNumber.text.toString().trim()

            if (tableNumber.isNotEmpty()) {
                sharedPref.edit()
                    .putString("TABLE_NUMBER", tableNumber)
                    .putString("USER_ROLE", "guest")
                    .apply()

                cartViewModel.setTableNumber(tableNumber)

                Toast.makeText(requireContext(), getString(R.string.enter_login ,tableNumber), Toast.LENGTH_LONG).show()

                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            } else {
                Toast.makeText(requireContext(), getString(R.string.enter_number), Toast.LENGTH_LONG).show()
            }
        }

        return view
    }
}