package com.example.app.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.app.R
import com.example.app.model.Feedback
import com.example.app.rest.RetrofitClient
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class FeedbackFragment : BottomSheetDialogFragment() {

    private lateinit var ratingBar: RatingBar
    private lateinit var feedbackEditText: EditText
    private lateinit var submitButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feedback, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ratingBar = view.findViewById(R.id.ratingBar)
        feedbackEditText = view.findViewById(R.id.feedbackEditText)
        submitButton = view.findViewById(R.id.submitRatingButton)

        submitButton.setOnClickListener {
            submitFeedback()
        }
    }

    private fun submitFeedback() {
        val sharedPref = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val loginType = sharedPref.getString("login_type", "guest") ?: "guest"
        val phoneNumber = if (loginType != "guest") {
            sharedPref.getString("phone_number", "")
        } else {
            ""
        }

        if (phoneNumber.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập để đánh giá", Toast.LENGTH_SHORT).show()
            return
        }

        val rating = ratingBar.rating.toInt()
        val feedbackText = feedbackEditText.text.toString().trim()

        if (rating == 0) {
            Toast.makeText(requireContext(), "Vui lòng chọn số sao đánh giá", Toast.LENGTH_SHORT).show()
            return
        }

        val feedback = Feedback(
            phone_number = phoneNumber,
            rating = rating,
            feedback_text = feedbackText.ifEmpty { null },
            created_at = null
        )

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response: Response<Void> = RetrofitClient.feedbackApi.submitFeedback(feedback)
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Đánh giá đã được gửi", Toast.LENGTH_SHORT).show()
                    ratingBar.rating = 0f
                    feedbackEditText.text.clear()
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "Lỗi khi gửi đánh giá", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}