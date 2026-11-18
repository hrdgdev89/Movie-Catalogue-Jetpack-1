package com.jetpack.moviecataloguejetpack.domain.usecase

import com.jetpack.moviecataloguejetpack.common.Resource
import com.jetpack.moviecataloguejetpack.domain.model.Movie
import com.jetpack.moviecataloguejetpack.domain.repository.IMovieRepository
import kotlinx.coroutines.flow.Flow

class SearchMoviesUseCase(
    private val repository: IMovieRepository
) {
    operator fun invoke(query: String): Flow<Resource<List<Movie>>> {
        return repository.searchMovies(query)
    }
}
