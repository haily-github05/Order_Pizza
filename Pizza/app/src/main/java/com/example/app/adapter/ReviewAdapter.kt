package com.example.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app.R
import com.example.app.model.Feedback
import java.text.SimpleDateFormat
import java.util.Locale

class ReviewAdapter(
    private var reviews: List<Feedback>
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val reviewComment: TextView = itemView.findViewById(R.id.reviewComment)
        val reviewPhone: TextView = itemView.findViewById(R.id.reviewPhone)
        val reviewTime: TextView = itemView.findViewById(R.id.reviewTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reviews, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]

        holder.ratingBar.rating = review.rating.toFloat()
        holder.reviewComment.text = review.feedback_text ?: "Không có bình luận"
        holder.reviewPhone.text = review.phone_number.replaceRange(3, 7, "****")

        // Định dạng thời gian
        review.created_at?.let { createdAt ->
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val date = inputFormat.parse(createdAt)
                holder.reviewTime.text = holder.itemView.context.getString(
                    R.string.review_time_placeholder,
                    outputFormat.format(date)
                )
            } catch (e: Exception) {
                holder.reviewTime.text = holder.itemView.context.getString(
                    R.string.review_time_placeholder,
                    createdAt
                )
            }
        } ?: run {
            holder.reviewTime.text = holder.itemView.context.getString(
                R.string.review_time_placeholder,
                "Không xác định"
            )
        }
    }

    override fun getItemCount(): Int = reviews.size

    fun updateData(newReviews: List<Feedback>) {
        reviews = newReviews
        notifyDataSetChanged()
    }
}