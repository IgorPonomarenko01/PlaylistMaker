package com.example.playlistmaker.settings.domain

import androidx.appcompat.app.AppCompatDelegate

class ThemeInteractorImpl(
    private val repository: ThemeRepository
) : ThemeInteractor {

    override fun getCurrentTheme(): Boolean {
        return repository.getCurrentTheme()
    }

    override fun switchTheme(isDark: Boolean) {
        repository.saveTheme(isDark)
        applyTheme(isDark)
    }

    override fun applyTheme(isDark: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}