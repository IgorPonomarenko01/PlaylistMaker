package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.TracksRepository
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override suspend fun searchTracks(text: String): Flow<List<Track>> = flow {
        val response = networkClient.doRequest(ItunesRequest(text))
         when (response.resultCode) {
            200 -> {
               val tracks = (response as ItunesResponse).results.map {
                    Track(
                        it.trackName,
                        it.artistName,
                        it.trackTimeMillis,
                        it.artworkUrl100,
                        it.trackId,
                        it.collectionName ?: "",
                        it.releaseDate,
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl
                    )
                }
                emit(tracks)
            }
            -1 -> throw IOException("No internet connection")
            else -> emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)
}