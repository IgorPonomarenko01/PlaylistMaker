package com.example.playlistmaker.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.TracksInteractor

class SearchViewModelFactory(
    private val trackInteractor: TracksInteractor,
    private val historyInteractor: SearchHistoryInteractor
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchViewModel(trackInteractor, historyInteractor) as T
    }
}