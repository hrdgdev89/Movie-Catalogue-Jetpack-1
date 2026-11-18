package com.jetpack.moviecataloguejetpack.di

import com.jetpack.moviecataloguejetpack.domain.usecase.AddFavoriteMovieUseCase
import com.jetpack.moviecataloguejetpack.domain.usecase.GetFavoriteMoviesUseCase
import com.jetpack.moviecataloguejetpack.domain.usecase.GetMovieDetailUseCase
import com.jetpack.moviecataloguejetpack.domain.usecase.GetMovieGenresUseCase
import com.jetpack.moviecataloguejetpack.domain.usecase.GetMovieReviewsUseCase
import com.jetpack.moviecataloguejetpack.domain.usecase.GetMovieTrailersUseCase
import com.jetpack.moviecataloguejetpack.domain.usecase.GetMoviesByGenreUseCase
import com.jetpack.moviecataloguejetpack.domain.usecase.IsFavoriteMovieUseCase
import com.jetpack.moviecataloguejetpack.domain.usecase.RemoveFavoriteMovieUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetMovieGenresUseCase(get()) }
    factory { GetMoviesByGenreUseCase(get()) }
    factory { GetMovieDetailUseCase(get()) }
    factory { GetMovieReviewsUseCase(get()) }
    factory { GetMovieTrailersUseCase(get()) }
    factory { GetFavoriteMoviesUseCase(get()) }
    factory { IsFavoriteMovieUseCase(get()) }
    factory { AddFavoriteMovieUseCase(get()) }
    factory { RemoveFavoriteMovieUseCase(get()) }
}
