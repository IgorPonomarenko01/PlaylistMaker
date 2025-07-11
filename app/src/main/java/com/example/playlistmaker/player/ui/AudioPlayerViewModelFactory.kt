package com.example.playlistmaker.player.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AudioPlayerViewModelFactory(
    private val url: String,
    private val trackTimeMillisDefault: String
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AudioPlayerViewModel::class.java)) {
            return AudioPlayerViewModel(url, trackTimeMillisDefault) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}