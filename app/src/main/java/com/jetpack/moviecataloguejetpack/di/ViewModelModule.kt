package com.jetpack.moviecataloguejetpack.di

import com.jetpack.moviecataloguejetpack.presentation.detail.MovieDetailViewModel
import com.jetpack.moviecataloguejetpack.presentation.favorite.FavoriteViewModel
import com.jetpack.moviecataloguejetpack.presentation.genre.GenreViewModel
import com.jetpack.moviecataloguejetpack.presentation.movielist.MovieListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { GenreViewModel(get()) }
    viewModel { MovieListViewModel(get()) }
    viewModel { MovieDetailViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { FavoriteViewModel(get(), get()) }
}
