package com.example.playlistmaker.settings.domain

interface ThemeInteractor {
    fun getCurrentTheme(): Boolean
    fun switchTheme(isDark: Boolean)
    fun applyTheme(isDark: Boolean)
}