package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.useCase.ThemeInteractor

const val SHARED_PREFS = "shared_prefs"

class App : Application() {

    var darkTheme = false
    lateinit var sharedPrefs: SharedPreferences
    lateinit var themeInteractor: ThemeInteractor
    override fun onCreate() {
        super.onCreate()
        instance = this
        initDependencies()
        setupTheme()
    }

    private fun initDependencies() {
        sharedPrefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        themeInteractor = Creator.provideThemeInteractor()
    }

    private fun setupTheme() {
        themeInteractor.switchTheme(themeInteractor.getCurrentTheme())
    }

    companion object {
        private lateinit var instance: App

        fun getSharedPreferences(): SharedPreferences {
            return instance.sharedPrefs
        }
    }
}