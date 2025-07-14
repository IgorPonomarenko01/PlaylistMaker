package com.example.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.SearchState
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.TracksInteractor

class SearchViewModel(
    private val trackInteractor: TracksInteractor,
    private val historyInteractor: SearchHistoryInteractor
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true

    private val _searchState = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState> = _searchState

    private val _historyState = MutableLiveData<List<Track>>()
    val historyState: LiveData<List<Track>> = _historyState

    private var inputText: String = ""
    private val searchRunnable = Runnable {
        searchTracks(inputText)
    }
    fun searchDebounce(text: String) {
        inputText = text
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }
    fun clearHistory() {
        historyInteractor.clearHistory()
        getHistory()
    }
    fun addToHistory(track: Track) {
        historyInteractor.addToHistory(track)
        getHistory()
    }

    fun getHistory() {
        val history = historyInteractor.getHistory()
        _historyState.value = history
    }

    fun searchTracks(text: String) {
        if (text.isEmpty()) return

        _searchState.value = SearchState.Loading
        trackInteractor.searchTracks(text, object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>) {
                _searchState.postValue(
                    if (foundTracks.isEmpty()) SearchState.Empty
                    else SearchState.Content(foundTracks)
                )
            }

            override fun onError(error: String) {
                _searchState.postValue(SearchState.Error(error))
            }
        })
    }

    fun clickDebounce(): Boolean {
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
            return true
        }
        return false
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(null)
    }

}