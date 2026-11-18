package com.jetpack.moviecataloguejetpack

import android.app.Application
import com.jetpack.moviecataloguejetpack.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MovieApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.INFO)
            androidContext(this@MovieApplication)
            modules(appModules)
        }
    }
}
