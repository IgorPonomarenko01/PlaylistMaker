package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.ItunesRequest
import com.example.playlistmaker.data.dto.Response
import okio.IOException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val iTunesbaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesbaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ItunesApi::class.java)

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