package com.example.playlistmaker.search.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import okio.IOException

class TrackInteractorImpl(private val repository: TracksRepository): TracksInteractor {

    override suspend fun searchTracks(text: String): Flow<List<Track>> =
        repository.searchTracks(text)
    }

