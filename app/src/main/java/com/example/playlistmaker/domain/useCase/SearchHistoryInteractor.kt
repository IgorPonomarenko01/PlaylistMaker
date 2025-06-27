package com.example.playlistmaker.domain.useCase

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.SearchHistoryRepository

interface SearchHistoryInteractor {
    fun addToHistory(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()
}