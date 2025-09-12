package com.example.playlistmaker.search.domain

import kotlinx.coroutines.flow.Flow

interface TracksRepository {
   suspend fun searchTracks(text: String): Flow<List<Track>>
}