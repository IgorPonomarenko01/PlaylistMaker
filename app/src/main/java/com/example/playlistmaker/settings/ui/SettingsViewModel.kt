package com.example.playlistmaker.settings.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.ThemeInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel: ViewModel() {
    private val themeInteractor = Creator.provideThemeInteractor()
    private lateinit var sharingInteractor: SharingInteractor

    fun initSharingInteractor(context: Context) {
        sharingInteractor = Creator.provideSharingInteractor(context.applicationContext)
    }
    fun switchTheme(checked: Boolean) {
        themeInteractor.switchTheme(checked)
    }
    fun getCurrentTheme(): Boolean {
       return themeInteractor.getCurrentTheme()
    }

    fun shareApp() = sharingInteractor.shareApp()
    fun openSupport() = sharingInteractor.openSupport()
    fun openTerms() = sharingInteractor.openTerms()
}