package com.jetpack.moviecataloguejetpack.di

import com.jetpack.moviecataloguejetpack.data.repository.FavoriteRepositoryImpl
import com.jetpack.moviecataloguejetpack.data.repository.MovieRepositoryImpl
import com.jetpack.moviecataloguejetpack.domain.repository.IFavoriteRepository
import com.jetpack.moviecataloguejetpack.domain.repository.IMovieRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<IMovieRepository> {
        MovieRepositoryImpl(get())
    }

    single<IFavoriteRepository> {
        FavoriteRepositoryImpl(get())
    }
}
