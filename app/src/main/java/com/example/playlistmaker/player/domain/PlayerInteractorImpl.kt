package com.example.playlistmaker.player.domain

import com.example.playlistmaker.search.domain.Track

class PlayerInteractorImpl(
    private val playerRepository: PlayerRepository
) : PlayerInteractor {

    override fun prepare(track: Track, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        playerRepository.prepare(track, onPrepared, onCompletion)
    }

    override fun play() = playerRepository.start()
    override fun pause() = playerRepository.pause()
    override fun release() = playerRepository.release()

    override fun getCurrentPosition(): Int = playerRepository.getCurrentPosition()
}