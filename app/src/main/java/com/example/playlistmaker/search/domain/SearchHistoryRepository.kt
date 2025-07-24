package com.example.playlistmaker.search.domain

interface SearchHistoryRepository {
    fun addTrack(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()
    fun registerListener(listener: (List<Track>) -> Unit)
    fun unregisterListener(listener: (List<Track>) -> Unit)
}