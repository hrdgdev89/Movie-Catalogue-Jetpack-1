package com.jetpack.moviecataloguejetpack.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.jetpack.moviecataloguejetpack.common.Resource
import com.jetpack.moviecataloguejetpack.data.mapper.DataMapper
import com.jetpack.moviecataloguejetpack.data.repository.paging.MovieByGenrePagingSource
import com.jetpack.moviecataloguejetpack.data.repository.paging.MovieReviewsPagingSource
import com.jetpack.moviecataloguejetpack.data.source.remote.api.MovieApiService
import com.jetpack.moviecataloguejetpack.domain.model.Genre
import com.jetpack.moviecataloguejetpack.domain.model.Movie
import com.jetpack.moviecataloguejetpack.domain.model.MovieDetail
import com.jetpack.moviecataloguejetpack.domain.model.Review
import com.jetpack.moviecataloguejetpack.domain.model.Video
import com.jetpack.moviecataloguejetpack.domain.repository.IMovieRepository
import com.jetpack.moviecataloguejetpack.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MovieRepositoryImpl(
    private val apiService: MovieApiService
) : IMovieRepository {

    override fun getGenres(): Flow<Resource<List<Genre>>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getMovieGenres(Constants.API_KEY)
            val genres = DataMapper.mapGenreResponsesToDomain(response.genres)
            emit(Resource.Success(genres))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan saat memuat genre", e))
        }
    }.flowOn(Dispatchers.IO)

    override fun getMoviesByGenre(genreId: Int): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MovieByGenrePagingSource(apiService, genreId) }
        ).flow
    }

    override fun getMovieDetail(movieId: Int): Flow<Resource<MovieDetail>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getMovieDetail(movieId, Constants.API_KEY)
            val detail = DataMapper.mapMovieDetailResponseToDomain(response)
            emit(Resource.Success(detail))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan saat memuat detail film", e))
        }
    }.flowOn(Dispatchers.IO)

    override fun getMovieReviews(movieId: Int): Flow<PagingData<Review>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MovieReviewsPagingSource(apiService, movieId) }
        ).flow
    }

    override fun getMovieTrailers(movieId: Int): Flow<Resource<List<Video>>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getMovieVideos(movieId, Constants.API_KEY)
            val allVideos = DataMapper.mapVideoResponsesToDomain(response.results)
            // Filter YouTube trailer videos
            val trailers = allVideos.filter { video ->
                video.site.equals("YouTube", ignoreCase = true) &&
                        (video.type.equals("Trailer", ignoreCase = true) || video.type.equals("Teaser", ignoreCase = true))
            }
            emit(Resource.Success(trailers))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan saat memuat trailer film", e))
        }
    }.flowOn(Dispatchers.IO)
}
