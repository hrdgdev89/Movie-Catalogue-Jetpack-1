package com.jetpack.moviecataloguejetpack.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jetpack.moviecataloguejetpack.R
import com.jetpack.moviecataloguejetpack.databinding.ItemMovieBinding
import com.jetpack.moviecataloguejetpack.domain.model.Genre
import com.jetpack.moviecataloguejetpack.domain.model.Movie
import com.jetpack.moviecataloguejetpack.utils.Constants
import com.jetpack.moviecataloguejetpack.utils.DateFormatter
import com.jetpack.moviecataloguejetpack.utils.loadImage
import java.util.Locale

class HomeMovieAdapter(
    private val onMovieClick: (Movie) -> Unit
) : ListAdapter<Movie, HomeMovieAdapter.MovieViewHolder>(DIFF_CALLBACK) {

    private var genreMap: Map<Int, String> = emptyMap()
    private var defaultGenreName: String = ""

    fun setGenres(genres: List<Genre>) {
        genreMap = genres.associate { it.id to it.name }
        notifyDataSetChanged()
    }

    fun setDefaultGenreName(name: String) {
        defaultGenreName = name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MovieViewHolder(
        private val binding: ItemMovieBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onMovieClick(getItem(position))
                }
            }
        }

        fun bind(movie: Movie) {
            val yearStr = DateFormatter.formatYear(movie.releaseDate)
            binding.tvTitle.text = if (yearStr.isNotEmpty()) "${movie.title} $yearStr" else movie.title

            val mappedGenres = movie.genreIds.mapNotNull { genreMap[it] }
            binding.tvGenres.text = when {
                mappedGenres.isNotEmpty() -> mappedGenres.joinToString(", ")
                defaultGenreName.isNotEmpty() -> defaultGenreName
                else -> "Movie"
            }

            binding.tvRating.text = String.format(Locale.getDefault(), "%.1f", movie.voteAverage)

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
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
                oldItem == newItem
        }
    }
}
