package com.example.playlistmaker.search.domain

sealed class SearchState {
    object Loading : SearchState()
    object Empty : SearchState()
    data class Content(val tracks: List<Track>) : SearchState()
    data class Error(val message: String) : SearchState()
}