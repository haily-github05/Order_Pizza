package com.example.app.view.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.app.R
import com.example.app.rest.RetrofitClient
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class FeedbackFragment : BottomSheetDialogFragment() {

    private lateinit var userImageView: ImageView
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICK = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feedback, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)
        val feedbackEditText = view.findViewById<EditText>(R.id.feedbackEditText)
        val submitButton = view.findViewById<Button>(R.id.submitRatingButton)
        val selectImageButton = view.findViewById<Button>(R.id.selectImageButton)
        userImageView = view.findViewById(R.id.userImageView)

        selectImageButton.setOnClickListener {
            showImagePickerDialog()
        }

        submitButton.setOnClickListener {
            val rating = ratingBar.rating
            val feedback = feedbackEditText.text.toString().trim()

            if (rating == 0f || feedback.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập đánh giá và chọn số sao!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Gửi đánh giá và ảnh (nếu có)
            submitReview(rating, feedback)
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Chụp ảnh", "Chọn từ thư viện")
        AlertDialog.Builder(requireContext())
            .setTitle("Thêm ảnh")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                        }
                    }
                    1 -> {
                        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(pickPhoto, REQUEST_IMAGE_PICK)
                    }
                }
            }
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val photo = data.extras?.get("data") as? Bitmap
                    photo?.let {
                        userImageView.setImageBitmap(it)
                    }
                }
                REQUEST_IMAGE_PICK -> {
                    val selectedImage: Uri? = data.data
                    selectedImage?.let {
                        userImageView.setImageURI(it)
                    }
                }
            }
        }
    }

    private fun submitReview(rating: Float, feedback: String) {
        // Kiểm tra nếu userImageView có Drawable, nếu không thì trả về null
        val imageUri = (userImageView.drawable as? BitmapDrawable)?.bitmap

// Chuyển ảnh thành base64 nếu có, nếu không có trả về chuỗi rỗng
        val imageToSend = if (imageUri != null) {
            convertBitmapToBase64(imageUri)
        } else {
            ""  // Trả về chuỗi rỗng nếu không có ảnh
        }

        // Lấy số điện thoại người dùng từ SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val loginType = sharedPref.getString("login_type", "guest") ?: "guest"
        val phoneNumber = if (loginType != "guest") {
            sharedPref.getString("phone_number", "")
        } else {
            ""  // Trường hợp không phải guest
        }
        if (phoneNumber.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập để đánh giá", Toast.LENGTH_SHORT)
                .show()
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.saveReview(
                    phone_number = phoneNumber,
                    rating = rating.toInt(),
                    comment = feedback,
                    image_product = imageToSend
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val reviewResponse = response.body()
                        if (reviewResponse?.isSuccessful() == true) {
                            Toast.makeText(
                                requireContext(),
                                "Đánh giá đã được lưu.",
                                Toast.LENGTH_SHORT
                            ).show()
                            dismiss()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                " ${reviewResponse?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Lỗi: Không thể kết nối tới server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Lỗi kết nối: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}
