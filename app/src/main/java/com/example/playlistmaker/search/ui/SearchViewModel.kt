package com.example.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.SearchState
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.TracksInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SearchViewModel(
    private val trackInteractor: TracksInteractor,
    private val historyInteractor: SearchHistoryInteractor
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var searchJob: Job? = null
    private var clickDebounceJob: Job? = null
    private var isClickAllowed = true
    private val _searchState = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState> = _searchState

    private val _historyState = MutableLiveData<List<Track>>()
    val historyState: LiveData<List<Track>> = _historyState

    private var inputText: String = ""
    private val historyListener: (List<Track>) -> Unit = { history ->
        _historyState.postValue(history)
    }

    init {
        historyInteractor.registerHistoryListener(historyListener)
    }

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            clickDebounceJob?.cancel()
            clickDebounceJob = viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }
    fun searchDebounce(text: String) {
        inputText = text
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchTracks(inputText)
        }
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
        viewModelScope.launch {
            trackInteractor.searchTracks(text)
                .catch { e ->
                    _searchState.postValue(SearchState.Error(e.message ?: "Network error"))
                }
                .collect { foundTracks ->
                    _searchState.postValue(
                        if (foundTracks.isEmpty()) SearchState.Empty
                        else SearchState.Content(foundTracks)
                    )
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        historyInteractor.unregisterHistoryListener(historyListener)
    }

}