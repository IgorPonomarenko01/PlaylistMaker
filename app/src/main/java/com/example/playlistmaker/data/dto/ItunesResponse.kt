package com.example.playlistmaker.data.dto

class ItunesResponse (
    val resultCount: Int,
    val results: List<TrackDto>
): Response()