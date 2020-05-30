package com.dicedmelon.allplay.daemon.web

data class Speaker(
    val id: String,
    val name: String,
    val input: Input,
    val volume: Any,
    val isConnected: Boolean
)