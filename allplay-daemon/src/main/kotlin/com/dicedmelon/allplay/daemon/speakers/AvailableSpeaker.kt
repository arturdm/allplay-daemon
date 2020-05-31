package com.dicedmelon.allplay.daemon.speakers

import de.kaizencode.tchaikovsky.speaker.Speaker

data class AvailableSpeaker(
    val speaker: Speaker,
    var retryCount: Int = 0
)
