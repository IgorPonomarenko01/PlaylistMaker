package com.example.playlistmaker.data.dto

import com.example.playlistmaker.domain.models.Track

class ItunesResponse (
    val resultCount: Int,
    val results: List<TrackDto> //скорректировать на TrackDto
): Response()