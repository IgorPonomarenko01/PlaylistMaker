package com.example.playlistmaker.settings.domain

interface ThemeRepository {
    fun getCurrentTheme(): Boolean
    fun saveTheme(isDark: Boolean)
}