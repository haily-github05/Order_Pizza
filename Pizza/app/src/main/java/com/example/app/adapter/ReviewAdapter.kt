
package com.example.app.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app.R
import com.example.app.model.Reviews
import com.bumptech.glide.Glide

class ReviewAdapter(private var reviews: List<Reviews>) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    // ViewHolder chứa các thành phần cần hiển thị
    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val reviewComment: TextView = itemView.findViewById(R.id.reviewComment)
        val reviewPhone: TextView = itemView.findViewById(R.id.reviewPhone)
        val productImage: ImageView = itemView.findViewById(R.id.placeholder_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reviews, parent, false)
        return ReviewViewHolder(itemView)
    }

//    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
//        val currentReview = reviews[position]
//
//        // Cập nhật dữ liệu cho các thành phần trong view
//        holder.ratingBar.rating = currentReview.rating.toFloat()
//        holder.reviewComment.text = currentReview.comment
//        holder.reviewPhone.text = currentReview.phone_number
//
//        // Kiểm tra nếu có ảnh base64
//        if (!currentReview.image_product.isNullOrEmpty()) {
//            val imageBitmap = currentReview.image_product?.decodeBase64ToBitmap() // Safe call
//            holder.productImage.setImageBitmap(imageBitmap)
//        } else {
//            // Nếu không có ảnh, đặt ImageView thành hình ảnh trống
//            holder.productImage.setImageDrawable(null) // Đặt ImageView thành null để trống
//        }
//    }
override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
    val currentReview = reviews[position]

    // Cập nhật dữ liệu cho các thành phần trong view
    holder.ratingBar.rating = currentReview.rating.toFloat()
    holder.reviewComment.text = currentReview.comment

    // Ẩn 7 số đầu của số điện thoại
    holder.reviewPhone.text = "******" + currentReview.phone_number.takeLast(3)

    // Kiểm tra nếu có hình ảnh thì hiển thị, nếu không thì ẩn
    if (currentReview.image_product.isNullOrEmpty()) {
        holder.productImage.visibility = View.GONE
    } else {
        holder.productImage.visibility = View.VISIBLE
        Glide.with(holder.itemView.context)
            .load(currentReview.image_product)
            .into(holder.productImage)
    }
}


    override fun getItemCount(): Int {
        return reviews.size
    }
    // Sửa hàm updateData để thay đổi giá trị của reviews
    fun updateData(newReviews: List<Reviews>) {
        reviews = newReviews
        notifyDataSetChanged()
    }
}

// Mở rộng String để chuyển Base64 thành Bitmap
fun String.decodeBase64ToBitmap(): Bitmap? {
    return try {
        val decodedString = Base64.decode(this, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    } catch (e: IllegalArgumentException) {
        null
    }

}
