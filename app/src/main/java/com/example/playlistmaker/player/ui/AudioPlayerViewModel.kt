package com.example.playlistmaker.player.ui

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.R
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(private val url: String,
    private val trackTimeMillisDefault: String) : ViewModel() {

    companion object {
         const val STATE_DEFAULT = 0
         const val STATE_PREPARED = 1
         const val STATE_PLAYING = 2
         const val STATE_PAUSED = 3
         const val REFRESH_CURRENT_POSITION = 300L
    }

    private val mediaPlayer = MediaPlayer()
    private val timeFormatter = SimpleDateFormat("mm:ss", Locale.getDefault())
    private val _playerState = MutableLiveData<Int>(STATE_DEFAULT)
    val playerState: LiveData<Int> = _playerState

    private val _currentPosition = MutableLiveData<String>()
    val currentPosition: LiveData<String> = _currentPosition

    private val _playBtnClickable = MutableLiveData<Boolean>()
    val playBtnClickable: LiveData<Boolean> = _playBtnClickable

    private val _playBtnsRes = MutableLiveData<Int>()
    val playBtnsRes: LiveData<Int> = _playBtnsRes

    private val handler = Handler(Looper.getMainLooper())
    private val playTimerRunnable = object : Runnable {
        override fun run() {
            updateTimer()
            handler.postDelayed(this, REFRESH_CURRENT_POSITION)
        }
    }

    init {
        preparePlayer()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            _playBtnClickable.value = true
            _playerState.value = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            handler.removeCallbacks(playTimerRunnable)
            _playBtnsRes.value = R.drawable.play
            _currentPosition.value = trackTimeMillisDefault
            _playerState.value = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        _playBtnsRes.value = R.drawable.pause
        _playerState.value = STATE_PLAYING
        handler.post(playTimerRunnable)
    }

    fun pausePLayer() {
        mediaPlayer.pause()
        _playBtnsRes.value = R.drawable.play
        _playerState.value = STATE_PAUSED
        handler.removeCallbacks(playTimerRunnable)
        updateTimer()
    }

    fun playbackControl() {
        when (_playerState.value) {
            STATE_PLAYING -> {
                pausePLayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun updateTimer() {
        val currentPosition = mediaPlayer.currentPosition
        _currentPosition.value = timeFormatter.format(currentPosition)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(playTimerRunnable)
        mediaPlayer.release()
    }

}