package com.dicedmelon.allplay.daemon.routes

import com.dicedmelon.allplay.daemon.speakers.SpeakersDaemon
import com.dicedmelon.allplay.daemon.web.SpeakerInputRequestBody
import com.dicedmelon.allplay.daemon.web.SpeakerVolumeRequestBody
import com.dicedmelon.allplay.daemon.web.SpeakersResponse
import com.dicedmelon.allplay.daemon.web.toWebSpeaker
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun Route.speakers(daemon: SpeakersDaemon) {
    get("/speakers") {
        val speakers = daemon.availableSpeakers
        GlobalScope.launch {
            call.respond(
                SpeakersResponse(
                    speakers = speakers.values.map { speaker -> speaker.toWebSpeaker() }
                )
            )
        }.join()
    }
    get("/speakers/{id}") {
        val speaker = daemon.availableSpeakers[call.parameters["id"]]
        GlobalScope.launch {
            if (speaker == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respond(speaker.toWebSpeaker())
            }
        }.join()
    }
    post("/speakers/{id}/input") {
        val speaker = daemon.availableSpeakers[call.parameters["id"]]
        if (speaker == null) {
            call.respond(HttpStatusCode.NotFound)
        } else {
            val body = call.receive<SpeakerInputRequestBody>()
            speaker.speaker.input().setInput(body.input)
            call.respond(HttpStatusCode.OK)
        }
    }
    post("/speakers/{id}/volume") {
        val speaker = daemon.availableSpeakers[call.parameters["id"]]
        if (speaker == null) {
            call.respond(HttpStatusCode.NotFound)
        } else {
            val body = call.receive<SpeakerVolumeRequestBody>()
            body.level?.run { speaker.speaker.volume().volume = this }
            body.isMute?.run { speaker.speaker.volume().mute(this) }
            call.respond(HttpStatusCode.OK)
        }
    }
}
