package com.example.playlistmaker.domain.impl

import com.bumptech.glide.util.Executors
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
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
