package com.example.playlistmaker.player

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Constants
import com.example.playlistmaker.R
import com.example.playlistmaker.Utils
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.search.domain.Track
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayer : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private var mediaPlayer = MediaPlayer()
    private lateinit var url: String
    private val handler = Handler(Looper.getMainLooper())
    private val playTimerRunnable = object : Runnable {
        override fun run() {
            updateTimer()
            handler.postDelayed(this, REFRESH_CURRENT_POSITION)
        }
    }

    private val timeFormatter = SimpleDateFormat("mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playerToolBar.setNavigationOnClickListener {
            finish()
        }

        val track = intent.getSerializableExtra(Constants.TRACK_KEY) as Track
        url = track.previewUrl

        preparePlayer()

        Glide.with(this)
            .load(track.getCoverArtWork())
            .transform(RoundedCorners(Utils.dpToPx(8f, this)))
            .placeholder(R.drawable.placeholder)
            .into(binding.trackImage)
        binding.apply {
            trackName.text = track.trackName
            artistName.text = track.artistName
            trackTime.text = track.trackTime
        }
        if (track.collectionName.isNullOrEmpty()) {
            binding.collectionNameText.isVisible = false
            binding.collectionName.isVisible = false
        } else {
            binding.collectionName.text = track.collectionName
        }
        binding.apply {
            releaseDate.text = track.releaseYear
            primaryGenreName.text = track.primaryGenreName
            country.text = track.country
        }
        binding.playBtn.setOnClickListener {
            playbackControl()
        }
    }

    override fun onPause() {
        super.onPause()
        if (isFinishing || !isChangingConfigurations) {
            pausePLayer()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus && playerState == STATE_PLAYING) {
            pausePLayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(playTimerRunnable)
        mediaPlayer.release()
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val REFRESH_CURRENT_POSITION = 300L
    }

    private var playerState = STATE_DEFAULT

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            binding.playBtn.isClickable = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            handler.removeCallbacks(playTimerRunnable)
            binding.playBtn.setImageResource(R.drawable.play)
            binding.playTimeMillis.text = getString(R.string.trackTimeMillisDefault)
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        binding.playBtn.setImageResource(R.drawable.pause)
        playerState = STATE_PLAYING
        handler.post(playTimerRunnable)
    }

    private fun pausePLayer() {
        mediaPlayer.pause()
        binding.playBtn.setImageResource(R.drawable.play)
        playerState = STATE_PAUSED
        handler.removeCallbacks(playTimerRunnable)
        updateTimer()
    }

    private fun playbackControl() {
        when (playerState) {
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
        binding.playTimeMillis.text = timeFormatter.format(currentPosition)
    }
}