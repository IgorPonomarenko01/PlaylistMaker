package com.example.playlistmaker.data.dto

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.api.TracksRepository

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(text: String): List<Track> {
        val response = networkClient.doRequest(ItunesRequest(text))
        if (response.resultCode == 200) {
            return (response as ItunesResponse).results.map {
                Track(
                    it.trackName,
                    it.artistName,
                    it.trackTimeMillis,
                    it.artworkUrl100,
                    it.trackId,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl
                )
            }
        } else {
            return emptyList()
        }
    }
}