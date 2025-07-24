package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.interactorModule
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.di.viewModelModule
import com.example.playlistmaker.settings.domain.ThemeInteractor
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.logger.AndroidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

const val SHARED_PREFS = "shared_prefs"

class App : Application() {
    private val themeInteractor: ThemeInteractor by inject()
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            logger(AndroidLogger(Level.DEBUG))
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }
        setupTheme()
    }
    private fun setupTheme() {
        themeInteractor.switchTheme(themeInteractor.getCurrentTheme())
    }
}