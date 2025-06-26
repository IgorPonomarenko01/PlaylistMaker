package com.example.playlistmaker

import com.example.playlistmaker.data.dto.TracksRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.TrackInteractorImpl

object Creator {
    private fun getTracksRepository() : TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }
    fun provideTracksInteractor(): TracksInteractor {
        return TrackInteractorImpl(getTracksRepository())
    }
}