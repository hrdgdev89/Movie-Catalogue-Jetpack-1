package com.jetpack.moviecataloguejetpack.presentation.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpack.moviecataloguejetpack.domain.model.Movie
import com.jetpack.moviecataloguejetpack.domain.usecase.GetFavoriteMoviesUseCase
import com.jetpack.moviecataloguejetpack.domain.usecase.RemoveFavoriteMovieUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoriteViewModel(
    getFavoriteMoviesUseCase: GetFavoriteMoviesUseCase,
    private val removeFavoriteMovieUseCase: RemoveFavoriteMovieUseCase
) : ViewModel() {

    val favoriteMovies: StateFlow<List<Movie>> = getFavoriteMoviesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    fun removeFavorite(movieId: Int) {
        viewModelScope.launch {
            removeFavoriteMovieUseCase(movieId)
        }
    }
}
