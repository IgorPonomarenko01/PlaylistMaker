package com.example.playlistmaker.search.domain

interface TracksInteractor {
    fun searchTracks(text: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume (foundTracks: List<Track>)

        fun onError(error: String) = Unit
    }
}