package com.example.playlistmaker.player.ui

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.R
import com.example.playlistmaker.player.domain.AudioPlayerState
import com.example.playlistmaker.player.domain.PlayerStatus
import com.example.playlistmaker.search.domain.Track
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(private val track: Track,
    private val trackTimeMillisDefault: String) : ViewModel() {

    companion object {
         const val REFRESH_CURRENT_POSITION = 300L
    }

    private val mediaPlayer = MediaPlayer()
    private val timeFormatter = SimpleDateFormat("mm:ss", Locale.getDefault())
    private val handler = Handler(Looper.getMainLooper())
    private val playTimerRunnable = object : Runnable {
        override fun run() {
            updateTimer()
            handler.postDelayed(this, REFRESH_CURRENT_POSITION)
        }
    }

    private val _playerState = MutableLiveData<AudioPlayerState>()
    val playerState: LiveData<AudioPlayerState> = _playerState

    init {
        _playerState.value = AudioPlayerState(
            track = track,
            currentPosition = trackTimeMillisDefault,
            playerStatus = PlayerStatus.DEFAULT
        )
        preparePlayer()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            _playerState.postValue(
                _playerState.value?.copy(
                    playerStatus = PlayerStatus.PREPARED
                )
            )
        }
        mediaPlayer.setOnCompletionListener {
            handler.removeCallbacks(playTimerRunnable)
            _playerState.postValue(
                _playerState.value?.copy(
                    playerStatus = PlayerStatus.PREPARED,
                    currentPosition = trackTimeMillisDefault
                )
            )
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        _playerState.value = _playerState.value?.copy(
            playerStatus = PlayerStatus.PLAYING
        )
        handler.post(playTimerRunnable)
    }

    fun pausePlayer() {
        mediaPlayer.pause()
        _playerState.value = _playerState.value?.copy(
            playerStatus = PlayerStatus.PAUSED
        )
        Log.d("DB_VM pausePlayer", "do pausePlayer ${_playerState.value?.playerStatus}")
        handler.removeCallbacks(playTimerRunnable)
        updateTimer()
    }

    fun playbackControl() {
        Log.d("DB_VM playbackControl", "playbackControl with status ${_playerState.value?.playerStatus}")
        when (_playerState.value?.playerStatus) {
            PlayerStatus.PLAYING -> {
                Log.d("DB_VM playbackControl", "do pausePlayer")
                pausePlayer()
            }
            PlayerStatus.PREPARED, PlayerStatus.PAUSED -> {
                Log.d("DB_VM playbackControl", "do startPlayer")
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
        val newPosition = timeFormatter.format(mediaPlayer.currentPosition)
        _playerState.postValue(_playerState.value?.copy(currentPosition = newPosition))
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(playTimerRunnable)
        mediaPlayer.release()
    }

}