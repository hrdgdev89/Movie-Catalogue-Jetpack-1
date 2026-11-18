package com.jetpack.moviecataloguejetpack.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jetpack.moviecataloguejetpack.common.Resource
import com.jetpack.moviecataloguejetpack.domain.model.Genre
import com.jetpack.moviecataloguejetpack.domain.model.Movie
import com.jetpack.moviecataloguejetpack.domain.usecase.GetMovieGenresUseCase
import com.jetpack.moviecataloguejetpack.domain.usecase.GetMoviesByGenreUseCase
import com.jetpack.moviecataloguejetpack.domain.usecase.GetTopRatedMoviesUseCase
import com.jetpack.moviecataloguejetpack.domain.usecase.SearchMoviesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getTopRatedMoviesUseCase: GetTopRatedMoviesUseCase,
    private val getMovieGenresUseCase: GetMovieGenresUseCase,
    private val getMoviesByGenreUseCase: GetMoviesByGenreUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase
) : ViewModel() {

    private val _carouselMoviesState = MutableStateFlow<Resource<List<Movie>>>(Resource.Loading)
    val carouselMoviesState: StateFlow<Resource<List<Movie>>> = _carouselMoviesState.asStateFlow()

    private val _genresState = MutableStateFlow<Resource<List<Genre>>>(Resource.Loading)
    val genresState: StateFlow<Resource<List<Genre>>> = _genresState.asStateFlow()

    private val _selectedGenre = MutableStateFlow<Genre?>(null)
    val selectedGenre: StateFlow<Genre?> = _selectedGenre.asStateFlow()

    private val _searchState = MutableStateFlow<Resource<List<Movie>>?>(null)
    val searchState: StateFlow<Resource<List<Movie>>?> = _searchState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadInitialData()
        observeSearch()
    }

    fun loadInitialData() {
        loadCarouselMovies()
        loadGenres()
    }

    fun loadCarouselMovies() {
        viewModelScope.launch {
            getTopRatedMoviesUseCase().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _carouselMoviesState.value = Resource.Success(resource.data.take(5))
                    }
                    is Resource.Error -> {
                        _carouselMoviesState.value = resource
                    }
                    is Resource.Loading -> {
                        _carouselMoviesState.value = Resource.Loading
                    }
                }
            }
        }
    }

    fun loadGenres() {
        viewModelScope.launch {
            getMovieGenresUseCase().collect { resource ->
                _genresState.value = resource
                if (resource is Resource.Success && _selectedGenre.value == null && resource.data.isNotEmpty()) {
                    _selectedGenre.value = resource.data.first()
                }
            }
        }
    }

    fun selectGenre(genre: Genre) {
        _selectedGenre.value = genre
    }

    fun getMoviesByGenre(genreId: Int): Flow<PagingData<Movie>> {
        return getMoviesByGenreUseCase(genreId).cachedIn(viewModelScope)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.trim().isEmpty()) {
            _searchState.value = null
        }
    }

    @OptIn(kotlinx.coroutines.FlowPreview::class)
    private fun observeSearch() {
        viewModelScope.launch {
            _searchQuery
                .debounce(400)
                .distinctUntilChanged()
                .filter { it.trim().isNotEmpty() }
                .collectLatest { query ->
                    searchMoviesUseCase(query.trim()).collect { resource ->
                        _searchState.value = resource
                    }
                }
        }
    }
}
