package com.example.app.repository

import com.example.app.model.Reviews

data class ReviewResponseList(
    val status: String,  // Trạng thái thành công hoặc lỗi
    val reviews: List<Reviews>  // Danh sách các đánh giá
)