package com.dicedmelon.allplay.daemon.speakers

import de.kaizencode.tchaikovsky.AllPlay
import de.kaizencode.tchaikovsky.listener.SpeakerAnnouncedListener
import de.kaizencode.tchaikovsky.listener.SpeakerChangedListener
import de.kaizencode.tchaikovsky.speaker.PlayState
import de.kaizencode.tchaikovsky.speaker.Speaker
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SpeakersDaemon {
    private val logger: Logger = LoggerFactory.getLogger(SpeakersDaemon::class.java)
    private val speakerAnnouncedListener = SpeakerAnnouncedListener { speaker ->
        speakers.putIfAbsent(speaker.id, speaker)
        speaker.connect()
        speaker.addSpeakerChangedListener(createSpeakerChangedListener())
        speaker.addSpeakerConnectionListener { id, _ ->
            speakers.remove(id)
        }
    }
    private lateinit var allPlay: AllPlay
    private val speakers: MutableMap<String, Speaker> = mutableMapOf()

    private fun createSpeakerChangedListener(): SpeakerChangedListener {
        return object : SpeakerChangedListener {
            override fun onPlayStateChanged(playState: PlayState) {
                logger.debug("onPlayStateChanged($playState)")
            }

            override fun onVolumeControlChanged(changed: Boolean) {
                logger.debug("onVolumeControlChanged($changed)")
            }

            override fun onLoopModeChanged(loopMode: Speaker.LoopMode) {
                logger.debug("onLoopModeChanged($loopMode)")
            }

            override fun onVolumeChanged(volume: Int) {
                logger.debug("onVolumeChanged($volume)")
            }

            override fun onShuffleModeChanged(shuffleMode: Speaker.ShuffleMode) {
                logger.debug("onShuffleModeChanged($shuffleMode)")
            }

            override fun onZoneChanged(
                zoneId: String,
                timestamp: Int,
                slaves: MutableMap<String, Int>
            ) {
                logger.debug("onZoneChanged($zoneId, $timestamp, $slaves)")
            }

            override fun onInputChanged(input: String) {
                logger.debug("onInputChanged($input)")
            }

            override fun onMuteChanged(mute: Boolean) {
                logger.debug("onMuteChanged($mute)")
            }

            override fun onPlaylistChanged() {
                logger.debug("onPlaylistChanged()")
            }

        }
    }

    fun start(): SpeakersDaemon {
        allPlay = AllPlay("allplay-daemon").apply {
            addSpeakerAnnouncedListener(speakerAnnouncedListener)
            connect()
            discoverSpeakers()
        }
        return this
    }

    fun stop() {
        allPlay.removeSpeakerAnnouncedListener(speakerAnnouncedListener)
        allPlay.disconnect()
    }

    val isConnected: Boolean
        get() = allPlay.isConnected

    val availableSpeakers: Map<String, Speaker>
        get() = speakers
}

fun speakersDaemon(
    configure: SpeakersDaemon.() -> Unit = {}
): SpeakersDaemon {
    return SpeakersDaemon()
}