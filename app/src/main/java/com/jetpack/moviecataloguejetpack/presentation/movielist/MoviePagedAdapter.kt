package com.jetpack.moviecataloguejetpack.presentation.movielist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jetpack.moviecataloguejetpack.R
import com.jetpack.moviecataloguejetpack.databinding.ItemMovieBinding
import com.jetpack.moviecataloguejetpack.domain.model.Movie
import com.jetpack.moviecataloguejetpack.utils.Constants
import com.jetpack.moviecataloguejetpack.utils.DateFormatter
import com.jetpack.moviecataloguejetpack.utils.loadImage
import java.util.Locale

class MoviePagedAdapter(
    private val onMovieClick: (Movie) -> Unit
) : PagingDataAdapter<Movie, MoviePagedAdapter.MovieViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        if (movie != null) {
            holder.bind(movie)
        }
    }

    inner class MovieViewHolder(
        private val binding: ItemMovieBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) onMovieClick(item)
                }
            }
        }

        fun bind(movie: Movie) {
            val yearStr = DateFormatter.formatYear(movie.releaseDate)
            binding.tvTitle.text = if (yearStr.isNotEmpty()) "${movie.title} $yearStr" else movie.title
            binding.tvRating.text = String.format(Locale.getDefault(), "%.1f", movie.voteAverage)
            binding.tvReleaseDate.text = DateFormatter.formatDate(movie.releaseDate)
            binding.tvOverview.text = movie.overview.ifEmpty { "Deskripsi tidak tersedia." }
            binding.tvGenres.text = "Movie"

            val stars = (movie.voteAverage / 2.0).coerceIn(1.0, 5.0)
            binding.star1.setImageResource(if (stars >= 1.0) R.drawable.baseline_star_24 else R.drawable.baseline_star_border_24)
            binding.star2.setImageResource(if (stars >= 2.0) R.drawable.baseline_star_24 else R.drawable.baseline_star_border_24)
            binding.star3.setImageResource(if (stars >= 3.0) R.drawable.baseline_star_24 else R.drawable.baseline_star_border_24)
            binding.star4.setImageResource(if (stars >= 4.0) R.drawable.baseline_star_24 else R.drawable.baseline_star_border_24)
            binding.star5.setImageResource(if (stars >= 4.7) R.drawable.baseline_star_24 else R.drawable.baseline_star_border_24)

            binding.ivPoster.loadImage(Constants.getPosterUrl(movie.posterPath))
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem == newItem
            }
        }
    }
}
