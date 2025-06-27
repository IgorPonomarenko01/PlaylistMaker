package com.example.playlistmaker.data.dto

import android.util.Log
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track
import java.io.IOException

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(text: String): List<Track> {
        val response = networkClient.doRequest(ItunesRequest(text))
        return when (response.resultCode) {
            200 -> {
                (response as ItunesResponse).results.map {
                    Track(
                        it.trackName,
                        it.artistName,
                        it.trackTimeMillis,
                        it.artworkUrl100,
                        it.trackId,
                        it.collectionName ?: "",
                        it.releaseDate,
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl
                    )
                }
            }
            -1 -> throw IOException("No internet connection")
            else -> emptyList()
        }
    }
}