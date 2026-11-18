package com.jetpack.moviecataloguejetpack.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jetpack.moviecataloguejetpack.common.Resource
import com.jetpack.moviecataloguejetpack.domain.model.MovieDetail
import com.jetpack.moviecataloguejetpack.domain.model.Review
import com.jetpack.moviecataloguejetpack.domain.model.Video
import com.jetpack.moviecataloguejetpack.domain.usecase.AddFavoriteMovieUseCase
import com.jetpack.moviecataloguejetpack.domain.usecase.GetMovieDetailUseCase
import com.jetpack.moviecataloguejetpack.domain.usecase.GetMovieReviewsUseCase
import com.jetpack.moviecataloguejetpack.domain.usecase.GetMovieTrailersUseCase
import com.jetpack.moviecataloguejetpack.domain.usecase.IsFavoriteMovieUseCase
import com.jetpack.moviecataloguejetpack.domain.usecase.RemoveFavoriteMovieUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MovieDetailViewModel(
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val getMovieTrailersUseCase: GetMovieTrailersUseCase,
    private val getMovieReviewsUseCase: GetMovieReviewsUseCase,
    private val isFavoriteMovieUseCase: IsFavoriteMovieUseCase,
    private val addFavoriteMovieUseCase: AddFavoriteMovieUseCase,
    private val removeFavoriteMovieUseCase: RemoveFavoriteMovieUseCase
) : ViewModel() {

    private val _detailState = MutableStateFlow<Resource<MovieDetail>>(Resource.Loading)
    val detailState: StateFlow<Resource<MovieDetail>> = _detailState.asStateFlow()

    private val _trailersState = MutableStateFlow<Resource<List<Video>>>(Resource.Loading)
    val trailersState: StateFlow<Resource<List<Video>>> = _trailersState.asStateFlow()

    private var currentMovieId: Int? = null
    private var reviewsFlow: Flow<PagingData<Review>>? = null

    fun loadMovie(movieId: Int) {
        if (currentMovieId == movieId && _detailState.value is Resource.Success) return
        currentMovieId = movieId

        viewModelScope.launch {
            getMovieDetailUseCase(movieId).collect { resource ->
                _detailState.value = resource
            }
        }

        viewModelScope.launch {
            getMovieTrailersUseCase(movieId).collect { resource ->
                _trailersState.value = resource
            }
        }
    }

    fun getReviews(movieId: Int): Flow<PagingData<Review>> {
        val lastResult = reviewsFlow
        if (currentMovieId == movieId && lastResult != null) {
            return lastResult
        }
        val newResult = getMovieReviewsUseCase(movieId).cachedIn(viewModelScope)
        reviewsFlow = newResult
        return newResult
    }

    fun isFavorite(movieId: Int): StateFlow<Boolean> {
        return isFavoriteMovieUseCase(movieId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false
            )
    }

    fun toggleFavorite(detail: MovieDetail, isCurrentlyFavorite: Boolean) {
        viewModelScope.launch {
            if (isCurrentlyFavorite) {
                removeFavoriteMovieUseCase(detail.id)
            } else {
                addFavoriteMovieUseCase(detail)
            }
        }
    }
}
