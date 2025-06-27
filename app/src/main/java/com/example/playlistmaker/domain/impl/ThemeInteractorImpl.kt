package com.example.playlistmaker.domain.impl

import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.repository.ThemeRepository
import com.example.playlistmaker.domain.useCase.ThemeInteractor

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