package com.jetpack.moviecataloguejetpack.di

import androidx.room.Room
import com.jetpack.moviecataloguejetpack.data.source.local.room.MovieDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            MovieDatabase::class.java,
            "movie_catalogue.db"
        ).fallbackToDestructiveMigration().build()
    }

    single {
        get<MovieDatabase>().favoriteMovieDao()
    }
}
