package com.jetpack.moviecataloguejetpack.data.mapper

import com.jetpack.moviecataloguejetpack.data.source.local.entity.FavoriteMovieEntity
import com.jetpack.moviecataloguejetpack.data.source.remote.response.GenreResponse
import com.jetpack.moviecataloguejetpack.data.source.remote.response.MovieDetailResponse
import com.jetpack.moviecataloguejetpack.data.source.remote.response.MovieItemResponse
import com.jetpack.moviecataloguejetpack.data.source.remote.response.ReviewItemResponse
import com.jetpack.moviecataloguejetpack.data.source.remote.response.VideoItemResponse
import com.jetpack.moviecataloguejetpack.domain.model.Genre
import com.jetpack.moviecataloguejetpack.domain.model.Movie
import com.jetpack.moviecataloguejetpack.domain.model.MovieDetail
import com.jetpack.moviecataloguejetpack.domain.model.Review
import com.jetpack.moviecataloguejetpack.domain.model.Video

object DataMapper {

    fun mapGenreResponsesToDomain(responses: List<GenreResponse>?): List<Genre> {
        return responses?.mapNotNull { response ->
            val id = response.id ?: return@mapNotNull null
            val name = response.name ?: return@mapNotNull null
            Genre(id = id, name = name)
        } ?: emptyList()
    }

    fun mapMovieResponsesToDomain(responses: List<MovieItemResponse>?): List<Movie> {
        return responses?.mapNotNull { response ->
            val id = response.id ?: return@mapNotNull null
            val title = response.title ?: ""
            Movie(
                id = id,
                title = title,
                overview = response.overview.orEmpty(),
                posterPath = response.posterPath,
                backdropPath = response.backdropPath,
                releaseDate = response.releaseDate,
                voteAverage = response.voteAverage ?: 0.0
            )
        } ?: emptyList()
    }

    fun mapMovieDetailResponseToDomain(response: MovieDetailResponse): MovieDetail {
        return MovieDetail(
            id = response.id ?: 0,
            title = response.title.orEmpty(),
            overview = response.overview.orEmpty(),
            posterPath = response.posterPath,
            backdropPath = response.backdropPath,
            releaseDate = response.releaseDate,
            voteAverage = response.voteAverage ?: 0.0,
            runtime = response.runtime,
            genres = mapGenreResponsesToDomain(response.genres),
            tagline = response.tagline
        )
    }

    fun mapReviewResponsesToDomain(responses: List<ReviewItemResponse>?): List<Review> {
        return responses?.mapNotNull { response ->
            val id = response.id ?: return@mapNotNull null
            Review(
                id = id,
                author = response.author.orEmpty(),
                content = response.content.orEmpty(),
                createdAt = response.createdAt,
                rating = response.authorDetails?.rating,
                avatarPath = response.authorDetails?.avatarPath
            )
        } ?: emptyList()
    }

    fun mapVideoResponsesToDomain(responses: List<VideoItemResponse>?): List<Video> {
        return responses?.mapNotNull { response ->
            val id = response.id ?: return@mapNotNull null
            val key = response.key ?: return@mapNotNull null
            Video(
                id = id,
                key = key,
                name = response.name.orEmpty(),
                site = response.site.orEmpty(),
                type = response.type.orEmpty()
            )
        } ?: emptyList()
    }

    fun mapEntityToDomain(entity: FavoriteMovieEntity): Movie {
        return Movie(
            id = entity.id,
            title = entity.title,
            overview = entity.overview,
            posterPath = entity.posterPath,
            backdropPath = entity.backdropPath,
            releaseDate = entity.releaseDate,
            voteAverage = entity.voteAverage
        )
    }

    fun mapEntitiesToDomain(entities: List<FavoriteMovieEntity>): List<Movie> {
        return entities.map { mapEntityToDomain(it) }
    }

    fun mapDetailDomainToEntity(detail: MovieDetail): FavoriteMovieEntity {
        return FavoriteMovieEntity(
            id = detail.id,
            title = detail.title,
            overview = detail.overview,
            posterPath = detail.posterPath,
            backdropPath = detail.backdropPath,
            releaseDate = detail.releaseDate,
            voteAverage = detail.voteAverage
        )
    }
}
