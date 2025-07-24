package com.example.playlistmaker.di

import com.example.playlistmaker.player.ui.AudioPlayerViewModel
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import com.example.playlistmaker.search.domain.Track
val viewModelModule = module {
    viewModel { (track: Track, defaultTime: String) ->
        AudioPlayerViewModel(
            track = track,
            trackTimeMillisDefault = defaultTime,
            playerInteractor = get()
        )
    }
    viewModel {
        SearchViewModel(
            trackInteractor = get(),
            historyInteractor = get()
        )
    }
    viewModel {
        SettingsViewModel(
            themeInteractor = get(),
            sharingInteractor = get()
        )
    }
}