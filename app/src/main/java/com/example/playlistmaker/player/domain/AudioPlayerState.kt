package com.example.playlistmaker.player.domain

import com.example.playlistmaker.search.domain.Track

data class AudioPlayerState(
    val track: Track,
    val currentPosition: String,
    val playerStatus: PlayerStatus
)

enum class PlayerStatus {
    DEFAULT, PREPARED, PLAYING, PAUSED
}