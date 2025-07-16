package com.example.playlistmaker.player.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Constants
import com.example.playlistmaker.R
import com.example.playlistmaker.Utils
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.player.domain.AudioPlayerState
import com.example.playlistmaker.player.domain.PlayerStatus
import com.example.playlistmaker.search.domain.Track

class AudioPlayer : AppCompatActivity() {

    private lateinit var viewModel: AudioPlayerViewModel
    private lateinit var binding: ActivityAudioPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playerToolBar.setNavigationOnClickListener {
            finish()
        }

        val trackTimeMillisDefault = getString(R.string.trackTimeMillisDefault)
        val track = intent.getSerializableExtra(Constants.TRACK_KEY) as Track

        viewModel = ViewModelProvider(
            this,
            AudioPlayerViewModelFactory(track, trackTimeMillisDefault)
        ).get(
            AudioPlayerViewModel::class.java
        )

        setupObservers()
        setupClickListeners()
    }


    private fun renderPlayerState(state: AudioPlayerState) {

        with(binding) {
            Glide.with(this@AudioPlayer)
                .load(state.track.getCoverArtWork())
                .transform(RoundedCorners(Utils.dpToPx(8f, this@AudioPlayer)))
                .placeholder(R.drawable.placeholder)
                .into(trackImage)

            trackName.text = state.track.trackName
            artistName.text = state.track.artistName
            trackTime.text = state.track.trackTime

            if (state.track.collectionName.isNullOrEmpty()) {
                collectionNameText.isVisible = false
                collectionName.isVisible = false
            } else {
                collectionName.text = state.track.collectionName
            }

            releaseDate.text = state.track.releaseYear
            primaryGenreName.text = state.track.primaryGenreName
            country.text = state.track.country

            playTimeMillis.text = state.currentPosition
            playBtn.setImageResource(
                when (state.playerStatus) {
                    PlayerStatus.PLAYING -> R.drawable.pause
                    else -> R.drawable.play
                }
            )
            playBtn.isClickable = state.playerStatus != PlayerStatus.DEFAULT
        }
    }

    private fun setupObservers() {
        viewModel.playerState.observe(this) { state ->
            renderPlayerState(state)
        }
    }

    private fun setupClickListeners() {
        binding.playBtn.setOnClickListener {
            viewModel.playbackControl()
        }
    }

    override fun onPause() {
        super.onPause()
        if (isFinishing || !isChangingConfigurations) {
            viewModel.pausePlayer()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus && viewModel.playerState.value?.playerStatus == PlayerStatus.PLAYING) {
            viewModel.pausePlayer()
        }
    }
}