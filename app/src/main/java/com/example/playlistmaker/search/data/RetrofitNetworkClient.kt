package com.example.playlistmaker.search.data

import okio.IOException

class RetrofitNetworkClient(private val iTunesService: ItunesApi) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        return try {
            if (dto is ItunesRequest) {
                val resp = iTunesService.search(dto.text).execute()
                val body = resp.body() ?: Response()

                 body.apply { resultCode = resp.code() }
            } else {
                 Response().apply { resultCode = 400 }
            }
        } catch (e: IOException) {
            Response().apply {
                resultCode = -1
            }
        }
    }
}