package com.jetpack.moviecataloguejetpack.presentation.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jetpack.moviecataloguejetpack.R
import com.jetpack.moviecataloguejetpack.databinding.ItemHomeGenrePillBinding
import com.jetpack.moviecataloguejetpack.domain.model.Genre

class HomeGenreAdapter(
    private val onGenreClick: (Genre) -> Unit
) : ListAdapter<Genre, HomeGenreAdapter.GenreViewHolder>(DIFF_CALLBACK) {

    private var selectedGenreId: Int? = null

    fun setSelectedGenreId(genreId: Int?) {
        val previousSelectedId = selectedGenreId
        selectedGenreId = genreId

        val prevIndex = currentList.indexOfFirst { it.id == previousSelectedId }
        val newIndex = currentList.indexOfFirst { it.id == genreId }

        if (prevIndex != -1) notifyItemChanged(prevIndex)
        if (newIndex != -1) notifyItemChanged(newIndex)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val binding = ItemHomeGenrePillBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GenreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GenreViewHolder(
        private val binding: ItemHomeGenrePillBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(genre: Genre) {
            binding.tvGenreName.text = genre.name
            val isSelected = genre.id == selectedGenreId

            if (isSelected) {
                binding.layoutPill.setBackgroundResource(R.drawable.bg_chip_selected)
                binding.tvGenreName.setTextColor(Color.WHITE)
            } else {
                binding.layoutPill.setBackgroundResource(R.drawable.bg_chip_unselected)
                binding.tvGenreName.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.text_color_secondary)
                )
            }

            binding.root.setOnClickListener {
                if (selectedGenreId != genre.id) {
                    setSelectedGenreId(genre.id)
                    onGenreClick(genre)
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Genre>() {
            override fun areItemsTheSame(oldItem: Genre, newItem: Genre): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Genre, newItem: Genre): Boolean =
                oldItem == newItem
        }
    }
}
