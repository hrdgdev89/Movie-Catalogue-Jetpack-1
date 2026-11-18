package com.jetpack.moviecataloguejetpack.domain.usecase

import com.jetpack.moviecataloguejetpack.domain.model.Movie
import com.jetpack.moviecataloguejetpack.domain.model.MovieDetail
import com.jetpack.moviecataloguejetpack.domain.repository.IFavoriteRepository
import kotlinx.coroutines.flow.Flow

class GetFavoriteMoviesUseCase(private val favoriteRepository: IFavoriteRepository) {
    operator fun invoke(): Flow<List<Movie>> {
        return favoriteRepository.getFavoriteMovies()
    }
}

class IsFavoriteMovieUseCase(private val favoriteRepository: IFavoriteRepository) {
    operator fun invoke(movieId: Int): Flow<Boolean> {
        return favoriteRepository.isFavoriteMovie(movieId)
    }
}

class AddFavoriteMovieUseCase(private val favoriteRepository: IFavoriteRepository) {
    suspend operator fun invoke(movie: MovieDetail) {
        favoriteRepository.addFavoriteMovie(movie)
    }
}

class RemoveFavoriteMovieUseCase(private val favoriteRepository: IFavoriteRepository) {
    suspend operator fun invoke(movieId: Int) {
        favoriteRepository.removeFavoriteMovie(movieId)
    }
}
