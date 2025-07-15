package com.example.playlistmaker.player.domain

import com.example.playlistmaker.search.domain.Track

interface PlayerInteractor {
    fun prepare(track: Track, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun play()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
}