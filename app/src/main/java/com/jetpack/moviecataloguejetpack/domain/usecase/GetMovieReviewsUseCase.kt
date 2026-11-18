package com.jetpack.moviecataloguejetpack.domain.usecase

import androidx.paging.PagingData
import com.jetpack.moviecataloguejetpack.domain.model.Review
import com.jetpack.moviecataloguejetpack.domain.repository.IMovieRepository
import kotlinx.coroutines.flow.Flow

class GetMovieReviewsUseCase(private val movieRepository: IMovieRepository) {
    operator fun invoke(movieId: Int): Flow<PagingData<Review>> {
        return movieRepository.getMovieReviews(movieId)
    }
}
