package com.example.playlistmaker.player.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.search.domain.Track

class AudioPlayerViewModelFactory(
    private val track: Track,
    private val trackTimeMillisDefault: String
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AudioPlayerViewModel::class.java)) {
            return AudioPlayerViewModel(track, trackTimeMillisDefault) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}