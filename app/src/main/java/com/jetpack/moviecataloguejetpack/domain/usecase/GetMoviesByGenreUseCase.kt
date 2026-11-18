package com.jetpack.moviecataloguejetpack.domain.usecase

import androidx.paging.PagingData
import com.jetpack.moviecataloguejetpack.domain.model.Movie
import com.jetpack.moviecataloguejetpack.domain.repository.IMovieRepository
import kotlinx.coroutines.flow.Flow

class GetMoviesByGenreUseCase(private val movieRepository: IMovieRepository) {
    operator fun invoke(genreId: Int): Flow<PagingData<Movie>> {
        return movieRepository.getMoviesByGenre(genreId)
    }
}
