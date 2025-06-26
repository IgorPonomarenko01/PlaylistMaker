package com.example.playlistmaker.domain.models

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

data class Track(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val trackId: String,
    val collectionName: String?,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
): Serializable {
    val trackTime: String
        get() = formatTime(trackTimeMillis)
    val releaseYear: String
        get() = getReleaseYear(releaseDate)

    private fun formatTime(millis: Long): String {
        val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        return dateFormat.format(millis)
    }

    fun getCoverArtWork() = artworkUrl100.replaceAfterLast('/', "512x512.jpg")

    fun getReleaseYear(releaseDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val date = inputFormat.parse(releaseDate)
            val outPutFormat = SimpleDateFormat("yyyy", Locale.getDefault())
            outPutFormat.format(date)
        } catch (e: Exception) {
            releaseDate.take(4)
        }
    }
}
