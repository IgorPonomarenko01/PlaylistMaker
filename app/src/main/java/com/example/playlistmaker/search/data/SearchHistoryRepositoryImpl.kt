package com.example.playlistmaker.search.data

import android.content.SharedPreferences
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.SearchHistoryRepository
import com.google.gson.Gson

class SearchHistoryRepositoryImpl(
    private val prefs: SharedPreferences,
    private val gson: Gson
) : SearchHistoryRepository {

    companion object {
         const val MAX_HISTORY_SIZE = 10
         const val KEY_HISTORY = "track_history"
    }

    override fun addTrack(track: Track) {
        val current = getHistory()
            .filterNot { it.trackId == track.trackId }
            .toMutableList()
            .apply { add(0, track) }
            .take(MAX_HISTORY_SIZE)

        prefs.edit()
            .putString(KEY_HISTORY, gson.toJson(current))
            .apply()
    }

    override fun getHistory(): List<Track> {
        val json = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        return Gson().fromJson(json, Array<Track>::class.java).toList()

    }

    override fun clearHistory() {
        prefs.edit().remove(KEY_HISTORY).apply()
    }
}