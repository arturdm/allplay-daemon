package com.dicedmelon.allplay.daemon.web

data class Speaker(
    val id: String,
    val name: String,
    val isConnected: Boolean,
    val input: Input? = null,
    val playlist: Playlist? = null,
    val volume: Volume? = null
)
