package com.example.playlistmaker.search.domain

import okio.IOException

class TrackInteractorImpl(private val repository: TracksRepository): TracksInteractor {

    override fun searchTracks(text: String, consumer: TracksInteractor.TracksConsumer) {
        Thread {
            try {
                val tracks = repository.searchTracks(text)
                if (tracks.isEmpty()) {
                    consumer.consume(emptyList())
                } else {
                    consumer.consume(tracks)
                }
            } catch (e: IOException) {
                consumer.onError(e.message ?: "Network error")
            } catch (e: Exception) {
                consumer.onError("Unknown error occurred")
            }
        }.start()
    }
}
