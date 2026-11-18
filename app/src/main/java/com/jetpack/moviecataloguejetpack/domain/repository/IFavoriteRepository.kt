package com.jetpack.moviecataloguejetpack.domain.repository

import com.jetpack.moviecataloguejetpack.domain.model.Movie
import com.jetpack.moviecataloguejetpack.domain.model.MovieDetail
import kotlinx.coroutines.flow.Flow

interface IFavoriteRepository {
    fun getFavoriteMovies(): Flow<List<Movie>>
    fun isFavoriteMovie(movieId: Int): Flow<Boolean>
    suspend fun addFavoriteMovie(movie: MovieDetail)
    suspend fun removeFavoriteMovie(movieId: Int)
}
