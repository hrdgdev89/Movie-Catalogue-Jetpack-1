package com.jetpack.moviecataloguejetpack.data.repository

import com.jetpack.moviecataloguejetpack.data.mapper.DataMapper
import com.jetpack.moviecataloguejetpack.data.source.local.room.FavoriteMovieDao
import com.jetpack.moviecataloguejetpack.domain.model.Movie
import com.jetpack.moviecataloguejetpack.domain.model.MovieDetail
import com.jetpack.moviecataloguejetpack.domain.repository.IFavoriteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FavoriteRepositoryImpl(
    private val favoriteMovieDao: FavoriteMovieDao
) : IFavoriteRepository {

    override fun getFavoriteMovies(): Flow<List<Movie>> {
        return favoriteMovieDao.getFavoriteMovies()
            .map { entities -> DataMapper.mapEntitiesToDomain(entities) }
            .flowOn(Dispatchers.IO)
    }

    override fun isFavoriteMovie(movieId: Int): Flow<Boolean> {
        return favoriteMovieDao.isFavorite(movieId)
            .flowOn(Dispatchers.IO)
    }

    override suspend fun addFavoriteMovie(movie: MovieDetail) {
        withContext(Dispatchers.IO) {
            val entity = DataMapper.mapDetailDomainToEntity(movie)
            favoriteMovieDao.insertFavorite(entity)
        }
    }

    override suspend fun removeFavoriteMovie(movieId: Int) {
        withContext(Dispatchers.IO) {
            favoriteMovieDao.deleteFavoriteById(movieId)
        }
    }
}
