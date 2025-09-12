package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.example.playlistmaker.player.domain.AudioPlayerState
import com.example.playlistmaker.player.domain.PlayerStatus
import com.example.playlistmaker.utils.Utils
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AudioPlayerFragment : Fragment() {

    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!

    private val args: AudioPlayerFragmentArgs by navArgs()

    private val viewModel: AudioPlayerViewModel by viewModel {
        parametersOf(args.track, getString(R.string.trackTimeMillisDefault))
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListeners()
        binding.playerToolBar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun renderPlayerState(state: AudioPlayerState) {

        with(binding) {
            Glide.with(requireContext())
                .load(state.track.getCoverArtWork())
                .transform(RoundedCorners(Utils.dpToPx(8f, requireContext())))
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
        viewModel.playerState.observe(viewLifecycleOwner) { state ->
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
        viewModel.pausePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
