package com.jetpack.moviecataloguejetpack.domain.usecase

import com.jetpack.moviecataloguejetpack.common.Resource
import com.jetpack.moviecataloguejetpack.domain.model.MovieDetail
import com.jetpack.moviecataloguejetpack.domain.repository.IMovieRepository
import kotlinx.coroutines.flow.Flow

class GetMovieDetailUseCase(private val movieRepository: IMovieRepository) {
    operator fun invoke(movieId: Int): Flow<Resource<MovieDetail>> {
        return movieRepository.getMovieDetail(movieId)
    }
}
