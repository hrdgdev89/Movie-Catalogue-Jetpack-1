package com.jetpack.moviecataloguejetpack.domain.usecase

import com.jetpack.moviecataloguejetpack.common.Resource
import com.jetpack.moviecataloguejetpack.domain.model.Video
import com.jetpack.moviecataloguejetpack.domain.repository.IMovieRepository
import kotlinx.coroutines.flow.Flow

class GetMovieTrailersUseCase(private val movieRepository: IMovieRepository) {
    operator fun invoke(movieId: Int): Flow<Resource<List<Video>>> {
        return movieRepository.getMovieTrailers(movieId)
    }
}
