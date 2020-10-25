package com.dicedmelon.allplay.daemon.web

data class Status(
    val isConnected: Boolean,
    val version: String,
    val revision: String
)
