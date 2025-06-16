package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayer : AppCompatActivity() {

    private lateinit var play: ImageView
    private var mediaPlayer = MediaPlayer()
    private lateinit var url: String
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var playTimeMillis: TextView
    private val playTimerRunnable = object : Runnable {
        override fun run() {
            updateTimer()
            handler.postDelayed(this, REFRESH_CURRENT_POSITION)
        }
    }

    private val timeFormatter = SimpleDateFormat("mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        val navBack = findViewById<MaterialToolbar>(R.id.tool_bar)

        navBack.setNavigationOnClickListener {
            finish()
        }

        val track = intent.getSerializableExtra(Constants.TRACK_KEY) as Track
        url = track.previewUrl

        val trackImage = findViewById<ImageView>(R.id.trackImage)
        val trackName = findViewById<TextView>(R.id.trackName)
        val artistName = findViewById<TextView>(R.id.artistName)
        val trackTime = findViewById<TextView>(R.id.trackTime)
        val collectionName = findViewById<TextView>(R.id.collectionName)
        val collectionNameText = findViewById<TextView>(R.id.collectionNameText)
        val releaseDate = findViewById<TextView>(R.id.releaseDate)
        val primaryGenreName = findViewById<TextView>(R.id.primaryGenreName)
        val country = findViewById<TextView>(R.id.country)
        playTimeMillis = findViewById(R.id.playTimeMillis)
        play = findViewById(R.id.playBtn)

        preparePlayer()

        Glide.with(this)
            .load(track.getCoverArtWork())
            .transform(RoundedCorners(Utils.dpToPx(8f, this)))
            .placeholder(R.drawable.placeholder)
            .into(trackImage)
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = track.trackTime
        if (track.collectionName.isNullOrEmpty()) {
            collectionNameText.isVisible = false
            collectionName.isVisible = false
        } else {
            collectionName.text = track.collectionName
        }
        releaseDate.text = track.releaseYear
        primaryGenreName.text = track.primaryGenreName
        country.text = track.country

        play.setOnClickListener {
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
            play.isClickable = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            handler.removeCallbacks(playTimerRunnable)
            play.setImageResource(R.drawable.play)
            playTimeMillis.text = getString(R.string.trackTimeMillisDefault)
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        play.setImageResource(R.drawable.pause)
        playerState = STATE_PLAYING
        handler.post(playTimerRunnable)
    }

    private fun pausePLayer() {
        mediaPlayer.pause()
        play.setImageResource(R.drawable.play)
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
        playTimeMillis.text = timeFormatter.format(currentPosition)
    }
}