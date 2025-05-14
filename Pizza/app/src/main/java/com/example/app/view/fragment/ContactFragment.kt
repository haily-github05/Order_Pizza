package com.example.app.view.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.app.databinding.FragmentContactBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.app.R

class ContactFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!

    private val phoneNumber = "0865529572"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnCall.setOnClickListener {
            val message = binding.tvMessage.text.toString().trim()
            if (message.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập nội dung yêu cầu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(requireContext(), "Đang liên hệ nhân viên...", Toast.LENGTH_SHORT).show()
            checkPermissionAndSendSMS(message)
        }
    }

    private fun checkPermissionAndSendSMS(message: String) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.SEND_SMS),
                101
            )
        } else {
            sendSMS(message)
        }
    }

    private fun sendSMS(message: String) {
        val tableNumber = arguments?.getString("TABLE_NUMBER") ?: ""
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, "Khách BÀN $tableNumber yêu cầu: $message", null, null)

            Toast.makeText(requireContext(), "Đã gửi tin nhắn đến nhân viên!", Toast.LENGTH_SHORT).show()
            dismiss()

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Gửi thất bại: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val message = binding.tvMessage.text.toString().trim()
                sendSMS(message)
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
