package com.example.playlistmaker.player.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.AudioPlayerState
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.domain.PlayerStatus
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(private val track: Track,
                           private val trackTimeMillisDefault: String,
                           private val playerInteractor: PlayerInteractor
) : ViewModel() {

    companion object {
         const val REFRESH_CURRENT_POSITION = 300L
    }

    private val timeFormatter = SimpleDateFormat("mm:ss", Locale.getDefault())
    private val _playerState = MutableLiveData<AudioPlayerState>()
    val playerState: LiveData<AudioPlayerState> = _playerState
    private var timeJob: Job? = null

    init {
        _playerState.value = AudioPlayerState(
            track = track,
            currentPosition = trackTimeMillisDefault,
            playerStatus = PlayerStatus.DEFAULT
        )
        preparePlayer()
    }

    private fun preparePlayer() {
        playerInteractor.prepare(
            track = track,
            onPrepared = {
                _playerState.postValue(
                    _playerState.value?.copy(
                        playerStatus = PlayerStatus.PREPARED
                    )
                )
            },
            onCompletion = {
                timeJob?.cancel()
                _playerState.postValue(
                    _playerState.value?.copy(
                        playerStatus = PlayerStatus.PREPARED,
                        currentPosition = trackTimeMillisDefault
                    )
                )
            }
        )
    }

    private fun startPlayer() {
        playerInteractor.play()
        _playerState.value = _playerState.value?.copy(
            playerStatus = PlayerStatus.PLAYING
        )
        timeJob = viewModelScope.launch {
            while (isActive) {
                updateTimer()
                delay(REFRESH_CURRENT_POSITION)
            }
        }
    }

    fun pausePlayer() {
        playerInteractor.pause()
        _playerState.value = _playerState.value?.copy(
            playerStatus = PlayerStatus.PAUSED
        )
        timeJob?.cancel()
        updateTimer()
    }

    fun playbackControl() {
        when (_playerState.value?.playerStatus) {
            PlayerStatus.PLAYING -> {
                pausePlayer()
            }
            PlayerStatus.PREPARED, PlayerStatus.PAUSED -> {
                startPlayer()
            }
            PlayerStatus.DEFAULT -> {
                Log.w("AudioPlayer", "Player is not ready yet")
            }
            null -> {
                Log.e("AudioPlayer", "Player state is null")
                _playerState.value = AudioPlayerState(
                    track = track,
                    currentPosition = trackTimeMillisDefault,
                    playerStatus = PlayerStatus.DEFAULT
                )
            }
        }
    }

    private fun updateTimer() {
        val newPosition = timeFormatter.format(playerInteractor.getCurrentPosition())
        _playerState.postValue(_playerState.value?.copy(currentPosition = newPosition))
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.release()
    }
}