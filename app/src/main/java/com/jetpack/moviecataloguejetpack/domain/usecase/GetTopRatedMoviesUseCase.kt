package com.jetpack.moviecataloguejetpack.domain.usecase

import com.jetpack.moviecataloguejetpack.common.Resource
import com.jetpack.moviecataloguejetpack.domain.model.Movie
import com.jetpack.moviecataloguejetpack.domain.repository.IMovieRepository
import kotlinx.coroutines.flow.Flow

class GetTopRatedMoviesUseCase(
    private val repository: IMovieRepository
) {
    operator fun invoke(): Flow<Resource<List<Movie>>> {
        return repository.getTopRatedMovies()
    }
}
