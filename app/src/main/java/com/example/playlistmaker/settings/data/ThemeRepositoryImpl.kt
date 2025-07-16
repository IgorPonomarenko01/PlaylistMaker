package com.example.playlistmaker.settings.data

import android.content.SharedPreferences
import com.example.playlistmaker.settings.domain.ThemeRepository

class ThemeRepositoryImpl(
    private val sharedPrefs: SharedPreferences
) : ThemeRepository {

    companion object {
        const val THEME_PREFS_KEY = "theme_prefs_key"
    }

    override fun getCurrentTheme(): Boolean {
        return sharedPrefs.getBoolean(THEME_PREFS_KEY, false)
    }

    override fun saveTheme(isDark: Boolean) {
        sharedPrefs.edit().putBoolean(THEME_PREFS_KEY, isDark).apply()
    }
}