package com.jetpack.moviecataloguejetpack.presentation.detail

import android.text.TextUtils
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jetpack.moviecataloguejetpack.R
import com.jetpack.moviecataloguejetpack.databinding.ItemReviewBinding
import com.jetpack.moviecataloguejetpack.domain.model.Review
import com.jetpack.moviecataloguejetpack.utils.DateFormatter
import java.util.Locale

class ReviewPagedAdapter : PagingDataAdapter<Review, ReviewPagedAdapter.ReviewViewHolder>(DIFF_CALLBACK) {

    private val expandedReviewIds = mutableSetOf<String>()

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
            holder.bind(review, expandedReviewIds)
        }
    }

    class ReviewViewHolder(
        private val binding: ItemReviewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(review: Review, expandedIds: MutableSet<String>) {
            binding.tvAuthor.text = review.author
            binding.tvAuthorInitial.text = review.author.firstOrNull()?.uppercaseChar()?.toString() ?: "U"
            binding.tvDate.text = DateFormatter.formatDate(review.createdAt)
            binding.tvContent.text = review.content

            // Display ratings and stars
            val rating = review.rating
            if (rating != null && rating > 0) {
                binding.layoutRating.isVisible = true
                binding.tvRating.text = String.format(Locale.getDefault(), "%.1f / 10", rating)

                val stars = (rating / 2.0).coerceIn(0.0, 5.0)
                binding.star1.setImageResource(if (stars >= 0.8) R.drawable.baseline_star_24 else R.drawable.baseline_star_border_24)
                binding.star2.setImageResource(if (stars >= 1.8) R.drawable.baseline_star_24 else R.drawable.baseline_star_border_24)
                binding.star3.setImageResource(if (stars >= 2.8) R.drawable.baseline_star_24 else R.drawable.baseline_star_border_24)
                binding.star4.setImageResource(if (stars >= 3.8) R.drawable.baseline_star_24 else R.drawable.baseline_star_border_24)
                binding.star5.setImageResource(if (stars >= 4.8) R.drawable.baseline_star_24 else R.drawable.baseline_star_border_24)
            } else {
                binding.layoutRating.isVisible = false
            }

            // Expand / Collapse long review text
            val isLongContent = review.content.length > 150
            if (isLongContent) {
                binding.btnToggleExpand.isVisible = true
                val isExpanded = expandedIds.contains(review.id)
                applyExpandedState(isExpanded)

                val toggleAction = {
                    val currentlyExpanded = expandedIds.contains(review.id)
                    val nextState = !currentlyExpanded
                    if (nextState) {
                        expandedIds.add(review.id)
                    } else {
                        expandedIds.remove(review.id)
                    }
                    val parentGroup = binding.root.parent as? ViewGroup ?: (binding.root as ViewGroup)
                    TransitionManager.beginDelayedTransition(parentGroup)
                    applyExpandedState(nextState)
                }

                binding.btnToggleExpand.setOnClickListener { toggleAction() }
                binding.cardReview.setOnClickListener { toggleAction() }
            } else {
                binding.btnToggleExpand.isVisible = false
                binding.tvContent.maxLines = Int.MAX_VALUE
                binding.tvContent.ellipsize = null
                binding.cardReview.setOnClickListener(null)
            }
        }

        private fun applyExpandedState(isExpanded: Boolean) {
            if (isExpanded) {
                binding.tvContent.maxLines = Int.MAX_VALUE
                binding.tvContent.ellipsize = null
                binding.tvToggleExpand.text = "Lebih sedikit"
                binding.ivToggleExpand.rotation = 270f
            } else {
                binding.tvContent.maxLines = 3
                binding.tvContent.ellipsize = TextUtils.TruncateAt.END
                binding.tvToggleExpand.text = "Selengkapnya"
                binding.ivToggleExpand.rotation = 90f
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
