package com.example.playlistmaker.search.domain

interface SearchHistoryInteractor {
    fun addToHistory(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()
    fun registerHistoryListener(listener: (List<Track>) -> Unit)
    fun unregisterHistoryListener(listener: (List<Track>) -> Unit)
}