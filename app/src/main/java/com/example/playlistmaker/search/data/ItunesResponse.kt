package com.example.playlistmaker.search.data

class ItunesResponse (
    val resultCount: Int,
    val results: List<TrackDto>
): Response()