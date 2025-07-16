package com.example.playlistmaker.search.domain

interface SearchHistoryRepository {
    fun addTrack(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()
}