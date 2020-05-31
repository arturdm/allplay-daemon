package com.dicedmelon.allplay.daemon.web

data class PlaylistItem(
    val artist: String,
    val title: String,
    val durationInMs: Long,
    val mediaType: String,
    val url: String,
    val thumbnailUrl: String
)
