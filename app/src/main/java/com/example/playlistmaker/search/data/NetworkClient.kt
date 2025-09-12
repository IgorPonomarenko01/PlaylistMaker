package com.example.playlistmaker.search.data

interface NetworkClient {
   suspend fun doRequest(dto: Any): Response
}