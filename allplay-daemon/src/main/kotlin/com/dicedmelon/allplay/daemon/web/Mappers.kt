package com.dicedmelon.allplay.daemon.web

import com.dicedmelon.allplay.daemon.speakers.AvailableSpeaker

fun AvailableSpeaker.toWebSpeaker(): Speaker {
    val speakerToReturn = Speaker(
        id = speaker.id,
        name = speaker.name,
        isConnected = speaker.isConnected
    )
    return if (speaker.isConnected) {
        val input = speaker.input()
        val volume = speaker.volume()
        speakerToReturn.copy(
            input = Input(
                activeInput = input.activeInput,
                availableInputs = input.inputList
            ),
            playlist = Playlist(
                items = speaker.playlist.playlistItems.map {
                    PlaylistItem(
                        artist = it.artist,
                        title = it.title,
                        durationInMs = it.durationInMs,
                        mediaType = it.mediaType,
                        url = it.url,
                        thumbnailUrl = it.thumbnailUrl
                    )
                }
            ),
            volume = Volume(
                isMute = volume.isMute,
                level = volume.volume
            )
        )
    } else {
        speakerToReturn
    }
}
