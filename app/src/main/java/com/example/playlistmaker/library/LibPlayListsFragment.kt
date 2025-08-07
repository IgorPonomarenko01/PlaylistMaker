package com.example.playlistmaker.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibPlayListsFragment : Fragment() {

    private val playListViewModel: PlaylistViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lib_play_lists, container, false)
    }

    companion object {
        fun newInstance() =
            LibPlayListsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}