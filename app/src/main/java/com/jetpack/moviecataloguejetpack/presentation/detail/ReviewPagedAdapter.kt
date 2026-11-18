package com.jetpack.moviecataloguejetpack.presentation.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jetpack.moviecataloguejetpack.databinding.ItemReviewBinding
import com.jetpack.moviecataloguejetpack.domain.model.Review
import com.jetpack.moviecataloguejetpack.utils.DateFormatter
import java.util.Locale

class ReviewPagedAdapter : PagingDataAdapter<Review, ReviewPagedAdapter.ReviewViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = getItem(position)
        if (review != null) {
            holder.bind(review)
        }
    }

    class ReviewViewHolder(
        private val binding: ItemReviewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(review: Review) {
            binding.tvAuthor.text = review.author
            binding.tvAuthorInitial.text = review.author.firstOrNull()?.uppercaseChar()?.toString() ?: "U"
            binding.tvDate.text = DateFormatter.formatDate(review.createdAt)
            binding.tvContent.text = review.content

            if (review.rating != null && review.rating > 0) {
                binding.tvRating.isVisible = true
                binding.tvRating.text = String.format(Locale.getDefault(), "★ %.1f", review.rating)
            } else {
                binding.tvRating.isVisible = false
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Review>() {
            override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
                return oldItem == newItem
            }
        }
    }
}
