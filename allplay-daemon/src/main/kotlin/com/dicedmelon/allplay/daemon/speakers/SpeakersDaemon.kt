package com.dicedmelon.allplay.daemon.speakers

import de.kaizencode.tchaikovsky.AllPlay
import de.kaizencode.tchaikovsky.listener.SpeakerAnnouncedListener
import de.kaizencode.tchaikovsky.speaker.Speaker
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SpeakersDaemon {
    private val logger: Logger = LoggerFactory.getLogger(SpeakersDaemon::class.java)
    private val speakerAnnouncedListener = SpeakerAnnouncedListener { speaker ->
        speakers.putIfAbsent(speaker.id, AvailableSpeaker(speaker = speaker))
        speaker.addSpeakerChangedListener(LoggingSpeakerChangedListener())
    }
    private val speakers: MutableMap<String, AvailableSpeaker> = mutableMapOf()
    private lateinit var allPlay: AllPlay
    private lateinit var ticker: ReceiveChannel<Unit>

    fun start(): SpeakersDaemon {
        allPlay = AllPlay("allplay-daemon").apply {
            addSpeakerAnnouncedListener(speakerAnnouncedListener)
            connect()
            discoverSpeakers()
        }
        ticker = ticker(1_000)
        GlobalScope.launch {
            ticker.receiveAsFlow().collect {
                updateSpeakers()
            }
        }
        return this
    }

    private fun updateSpeakers() {
        speakers.forEach { (id, it) ->
            val speaker = it.speaker
            if (!speaker.isConnected) {
                try {
                    speaker.connect()
                } catch (e: Exception) {
                    logger.error("Failed to connect to speaker, rediscovering speakers", e)
                    speakers.remove(id)
                    allPlay.discoverSpeakers()
                }
            }
        }
    }

    val isConnected: Boolean
        get() = allPlay.isConnected

    val availableSpeakers: Map<String, AvailableSpeaker>
        get() = speakers
}

fun speakersDaemon(): SpeakersDaemon {
    return SpeakersDaemon()
}
