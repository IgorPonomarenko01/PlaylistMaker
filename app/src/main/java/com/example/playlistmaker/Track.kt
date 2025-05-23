package com.example.playlistmaker

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

data class Track(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val trackId: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String
): Serializable {
    val trackTime: String
        get() = formatTime(trackTimeMillis)

    private fun formatTime(millis: Long): String {
        val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        return dateFormat.format(millis)
    }

    fun getCoverArtWork() = artworkUrl100.replaceAfterLast('/', "512x512.jpg")
}
