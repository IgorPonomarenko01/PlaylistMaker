package com.example.playlistmaker.search.domain

interface SearchHistoryInteractor {
    fun addToHistory(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()
}