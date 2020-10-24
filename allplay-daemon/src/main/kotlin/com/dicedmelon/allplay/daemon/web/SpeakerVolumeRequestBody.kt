package com.dicedmelon.allplay.daemon.web

data class SpeakerVolumeRequestBody(
    val isMute: Boolean?,
    val level: Int?
)
