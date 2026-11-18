package com.jetpack.moviecataloguejetpack.domain.repository

import androidx.paging.PagingData
import com.jetpack.moviecataloguejetpack.common.Resource
import com.jetpack.moviecataloguejetpack.domain.model.Genre
import com.jetpack.moviecataloguejetpack.domain.model.Movie
import com.jetpack.moviecataloguejetpack.domain.model.MovieDetail
import com.jetpack.moviecataloguejetpack.domain.model.Review
import com.jetpack.moviecataloguejetpack.domain.model.Video
import kotlinx.coroutines.flow.Flow

interface IMovieRepository {
    fun getGenres(): Flow<Resource<List<Genre>>>
    fun getMoviesByGenre(genreId: Int): Flow<PagingData<Movie>>
    fun getMovieDetail(movieId: Int): Flow<Resource<MovieDetail>>
    fun getMovieReviews(movieId: Int): Flow<PagingData<Review>>
    fun getMovieTrailers(movieId: Int): Flow<Resource<List<Video>>>
    fun getTopRatedMovies(): Flow<Resource<List<Movie>>>
    fun searchMovies(query: String): Flow<Resource<List<Movie>>>
}
