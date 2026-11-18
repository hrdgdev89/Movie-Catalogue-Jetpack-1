package com.jetpack.moviecataloguejetpack.utils

object Constants {
    const val BASE_URL = "https://api.themoviedb.org/3/"
    const val API_KEY = "e1c6110868d89666f3f7ddda01910da7"
    const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
    const val POSTER_URL_W185 = "${IMAGE_BASE_URL}w185"
    const val POSTER_URL_W500 = "${IMAGE_BASE_URL}w500"
    const val POSTER_URL_W92 = "${IMAGE_BASE_URL}w92"

    fun getPosterUrl(path: String?): String? {
        return if (!path.isNullOrEmpty()) {
            "${IMAGE_BASE_URL}w342$path"
        } else null
    }

    fun getBackdropUrl(path: String?): String? {
        return if (!path.isNullOrEmpty()) {
            "${IMAGE_BASE_URL}w780$path"
        } else null
    }
}
