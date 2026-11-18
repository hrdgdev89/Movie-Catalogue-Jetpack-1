package com.jetpack.moviecataloguejetpack.di

import com.jetpack.moviecataloguejetpack.presentation.account.AccountViewModel
import com.jetpack.moviecataloguejetpack.presentation.detail.MovieDetailViewModel
import com.jetpack.moviecataloguejetpack.presentation.favorite.FavoriteViewModel
import com.jetpack.moviecataloguejetpack.presentation.genre.GenreViewModel
import com.jetpack.moviecataloguejetpack.presentation.home.HomeViewModel
import com.jetpack.moviecataloguejetpack.presentation.movielist.MovieListViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModel { AccountViewModel(androidApplication()) }
    viewModel { GenreViewModel(get()) }
    viewModel { MovieListViewModel(get()) }
    viewModel { MovieDetailViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { FavoriteViewModel(get(), get()) }
}
