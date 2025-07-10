package com.example.playlistmaker.search.domain

interface TracksRepository {
    fun searchTracks(text: String): List<Track>
}