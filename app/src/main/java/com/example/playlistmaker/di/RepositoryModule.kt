package com.example.playlistmaker.di

import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.PlayerRepository
import com.example.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.SearchHistoryRepository
import com.example.playlistmaker.search.domain.TracksRepository
import com.example.playlistmaker.settings.data.ThemeRepositoryImpl
import com.example.playlistmaker.settings.domain.ThemeRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<PlayerRepository> { PlayerRepositoryImpl() }

    single<TracksRepository> {
        TracksRepositoryImpl(networkClient = get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(
            prefs = get(),
            gson = get()
        )
    }

    single<ThemeRepository> {
        ThemeRepositoryImpl(sharedPrefs = get())
    }

}