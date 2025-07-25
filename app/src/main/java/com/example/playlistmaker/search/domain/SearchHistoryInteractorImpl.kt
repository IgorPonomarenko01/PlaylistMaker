package com.example.playlistmaker.search.domain

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override fun addToHistory(track: Track) {
        repository.addTrack(track)
    }

    override fun getHistory(): List<Track> {
        return repository.getHistory()
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
    override fun registerHistoryListener(listener: (List<Track>) -> Unit) {
        repository.registerListener(listener)
    }

    override fun unregisterHistoryListener(listener: (List<Track>) -> Unit) {
        repository.unregisterListener(listener)
    }
}