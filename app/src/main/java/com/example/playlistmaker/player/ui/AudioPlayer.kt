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

        setupUi()
        setupObservers()
        setupClickListeners()
    }

    private fun setupUi() {
        viewModel.trackData.observe(this) { track ->
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
        }
    }

    private fun setupObservers() {
        viewModel.playBtnsRes.observe(this) { resId ->
            binding.playBtn.setImageResource(resId)
        }

        viewModel.currentPosition.observe(this) { position ->
            binding.playTimeMillis.text = position

        }

        viewModel.playBtnClickable.observe(this) { isClickable ->
            binding.playBtn.isClickable = isClickable

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
            viewModel.pausePLayer()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus && viewModel.playerState.value == AudioPlayerViewModel.STATE_PLAYING) {
            viewModel.pausePLayer()
        }
    }
}