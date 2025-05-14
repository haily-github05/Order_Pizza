package com.example.app.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app.databinding.FragmentReviewBinding
import com.example.app.rest.RetrofitClient
import com.example.app.adapter.ReviewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReviewFragment : Fragment() {

    private var _binding: FragmentReviewBinding? = null
    private val binding get() = _binding!!
    private lateinit var reviewAdapter: ReviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo RecyclerView và Adapter
        reviewAdapter = ReviewAdapter(emptyList())
        binding.recyclerViewReviews.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = reviewAdapter
        }

        // Gọi API lấy đánh giá
        fetchReviews()
    }

    private fun fetchReviews() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Gọi API để lấy danh sách đánh giá
                val response = RetrofitClient.apiService.getReviews()

                // Chuyển về Main Thread để cập nhật giao diện
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        // Truy cập vào trường "reviews" trong đối tượng response
                        val reviews = response.body()?.reviews ?: emptyList()

                        // Cập nhật Adapter
                        reviewAdapter.updateData(reviews)

                        if (reviews.isEmpty()) {
                            Toast.makeText(requireContext(), "Không có đánh giá nào.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Lỗi tải đánh giá: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
