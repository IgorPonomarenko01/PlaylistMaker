package com.example.playlistmaker.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibFavsFragment : Fragment() {

    private val favsViewModel: FavsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lib_favs, container, false)
    }

    companion object {
        fun newInstance() =
            LibFavsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}