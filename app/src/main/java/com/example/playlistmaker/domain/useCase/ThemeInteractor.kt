package com.example.playlistmaker.domain.useCase

interface ThemeInteractor {
    fun getCurrentTheme(): Boolean
    fun switchTheme(isDark: Boolean)
    fun applyTheme(isDark: Boolean)
}