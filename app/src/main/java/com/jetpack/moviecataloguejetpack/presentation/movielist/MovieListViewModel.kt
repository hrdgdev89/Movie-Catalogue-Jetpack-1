package com.jetpack.moviecataloguejetpack.presentation.movielist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jetpack.moviecataloguejetpack.domain.model.Movie
import com.jetpack.moviecataloguejetpack.domain.usecase.GetMoviesByGenreUseCase
import kotlinx.coroutines.flow.Flow

class MovieListViewModel(
    private val getMoviesByGenreUseCase: GetMoviesByGenreUseCase
) : ViewModel() {

    private var currentGenreId: Int? = null
    private var currentMovieFlow: Flow<PagingData<Movie>>? = null

    fun getMoviesByGenre(genreId: Int): Flow<PagingData<Movie>> {
        val lastResult = currentMovieFlow
        if (genreId == currentGenreId && lastResult != null) {
            return lastResult
        }
        currentGenreId = genreId
        val newResult = getMoviesByGenreUseCase(genreId).cachedIn(viewModelScope)
        currentMovieFlow = newResult
        return newResult
    }
}
