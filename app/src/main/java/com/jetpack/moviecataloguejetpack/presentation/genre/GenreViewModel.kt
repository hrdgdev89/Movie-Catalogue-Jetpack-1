package com.jetpack.moviecataloguejetpack.presentation.genre

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpack.moviecataloguejetpack.common.Resource
import com.jetpack.moviecataloguejetpack.domain.model.Genre
import com.jetpack.moviecataloguejetpack.domain.usecase.GetMovieGenresUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GenreViewModel(
    private val getMovieGenresUseCase: GetMovieGenresUseCase
) : ViewModel() {

    private val _genresState = MutableStateFlow<Resource<List<Genre>>>(Resource.Loading)
    val genresState: StateFlow<Resource<List<Genre>>> = _genresState.asStateFlow()

    init {
        loadGenres()
    }

    fun loadGenres() {
        viewModelScope.launch {
            getMovieGenresUseCase().collect { resource ->
                _genresState.value = resource
            }
        }
    }
}
