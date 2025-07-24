package com.example.playlistmaker.di

import com.example.playlistmaker.R
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.domain.PlayerInteractorImpl
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.SearchHistoryInteractorImpl
import com.example.playlistmaker.search.domain.TrackInteractorImpl
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.settings.domain.ThemeInteractor
import com.example.playlistmaker.settings.domain.ThemeInteractorImpl
import com.example.playlistmaker.sharing.domain.EmailData
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val interactorModule = module {

    single<PlayerInteractor> { PlayerInteractorImpl(playerRepository = get()) }

    single<TracksInteractor> { TrackInteractorImpl(repository = get()) }

    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(repository = get())
    }
    single<ThemeInteractor> {
        ThemeInteractorImpl(repository = get())
    }
    single<SharingInteractor> {
        val context = androidContext()
        SharingInteractorImpl(
            externalNavigator = get(),
            shareAppLink = context.getString(R.string.practicumLink),
            termsLink = context.getString(R.string.agreementLink),
            supportEmailData = EmailData(
                email = context.getString(R.string.supportEmail),
                subject = context.getString(R.string.supportEmailSubj),
                text = context.getString(R.string.supportEmailText)
            )
        )
    }
}