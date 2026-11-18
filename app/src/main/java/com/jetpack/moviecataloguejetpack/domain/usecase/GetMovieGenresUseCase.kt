package com.jetpack.moviecataloguejetpack.domain.usecase

import com.jetpack.moviecataloguejetpack.common.Resource
import com.jetpack.moviecataloguejetpack.domain.model.Genre
import com.jetpack.moviecataloguejetpack.domain.repository.IMovieRepository
import kotlinx.coroutines.flow.Flow

class GetMovieGenresUseCase(private val movieRepository: IMovieRepository) {
    operator fun invoke(): Flow<Resource<List<Genre>>> {
        return movieRepository.getGenres()
    }
}
