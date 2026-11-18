package com.jetpack.moviecataloguejetpack.data.source.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jetpack.moviecataloguejetpack.data.source.local.entity.FavoriteMovieEntity

@Database(
    entities = [FavoriteMovieEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun favoriteMovieDao(): FavoriteMovieDao
}
