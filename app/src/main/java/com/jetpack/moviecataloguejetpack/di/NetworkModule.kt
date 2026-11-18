package com.jetpack.moviecataloguejetpack.di

import com.jetpack.moviecataloguejetpack.data.source.remote.api.MovieApiService
import com.jetpack.moviecataloguejetpack.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    single<OkHttpClient> {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single<Retrofit> {
        val converter: Converter.Factory = GsonConverterFactory.create()
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(converter)
            .client(get<OkHttpClient>())
            .build()
    }

    single<MovieApiService> {
        get<Retrofit>().create(MovieApiService::class.java)
    }
}
