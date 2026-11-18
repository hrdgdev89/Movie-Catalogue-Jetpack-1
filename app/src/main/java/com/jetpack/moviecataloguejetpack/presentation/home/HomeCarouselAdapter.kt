package com.jetpack.moviecataloguejetpack.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jetpack.moviecataloguejetpack.databinding.ItemCarouselMovieBinding
import com.jetpack.moviecataloguejetpack.domain.model.Genre
import com.jetpack.moviecataloguejetpack.domain.model.Movie
import com.jetpack.moviecataloguejetpack.utils.Constants
import com.jetpack.moviecataloguejetpack.utils.DateFormatter
import com.jetpack.moviecataloguejetpack.utils.loadImage

class HomeCarouselAdapter(
    private val onItemClick: (Movie) -> Unit
) : ListAdapter<Movie, HomeCarouselAdapter.CarouselViewHolder>(DIFF_CALLBACK) {

    private var genreMap: Map<Int, String> = emptyMap()

    fun setGenres(genres: List<Genre>) {
        genreMap = genres.associate { it.id to it.name }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val binding = ItemCarouselMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CarouselViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CarouselViewHolder(
        private val binding: ItemCarouselMovieBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            val yearStr = DateFormatter.formatYear(movie.releaseDate)
            binding.tvTitle.text = if (yearStr.isNotEmpty()) "${movie.title} $yearStr" else movie.title

            // Format genres
            val genreNames = movie.genreIds.mapNotNull { genreMap[it] }
            binding.tvGenres.text = if (genreNames.isNotEmpty()) {
                genreNames.joinToString(", ")
            } else {
                "Featured Movie"
            }

            binding.ivPoster.loadImage(Constants.getPosterUrl(movie.posterPath))

            binding.root.setOnClickListener {
                onItemClick(movie)
            }
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
