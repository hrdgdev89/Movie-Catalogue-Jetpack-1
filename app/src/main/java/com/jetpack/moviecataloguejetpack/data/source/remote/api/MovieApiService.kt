package com.jetpack.moviecataloguejetpack.data.source.remote.api

import com.jetpack.moviecataloguejetpack.data.source.remote.response.GenreListResponse
import com.jetpack.moviecataloguejetpack.data.source.remote.response.MovieDetailResponse
import com.jetpack.moviecataloguejetpack.data.source.remote.response.MovieListResponse
import com.jetpack.moviecataloguejetpack.data.source.remote.response.MovieReviewsResponse
import com.jetpack.moviecataloguejetpack.data.source.remote.response.MovieVideosResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {

    @GET("genre/movie/list")
    suspend fun getMovieGenres(
        @Query("api_key") apiKey: String
    ): GenreListResponse

    @GET("discover/movie")
    suspend fun getMoviesByGenre(
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int,
        @Query("api_key") apiKey: String
    ): MovieListResponse

    @GET("movie/{movieId}")
    suspend fun getMovieDetail(
        @Path("movieId") movieId: Int,
        @Query("api_key") apiKey: String
    ): MovieDetailResponse

    @GET("movie/{movieId}/reviews")
    suspend fun getMovieReviews(
        @Path("movieId") movieId: Int,
        @Query("page") page: Int,
        @Query("api_key") apiKey: String
    ): MovieReviewsResponse

    @GET("movie/{movieId}/videos")
    suspend fun getMovieVideos(
        @Path("movieId") movieId: Int,
        @Query("api_key") apiKey: String
    ): MovieVideosResponse
}
