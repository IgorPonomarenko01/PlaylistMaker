package com.example.playlistmaker.domain.impl

import com.bumptech.glide.util.Executors
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository

class TrackInteractorImpl(private val repository: TracksRepository): TracksInteractor {

    override fun searchTracks(text: String, consumer: TracksInteractor.TracksConsumer) {
        val t = Thread {
            consumer.consume(repository.searchTracks(text))
        }
        t.start()
    }
}