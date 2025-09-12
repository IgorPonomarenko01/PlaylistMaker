package com.example.playlistmaker.search.domain

import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    suspend fun searchTracks(text: String): Flow<List<Track>>
}