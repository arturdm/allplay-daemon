package com.dicedmelon.allplay.daemon.routes

import com.dicedmelon.allplay.daemon.App
import com.dicedmelon.allplay.daemon.speakers.SpeakersDaemon
import com.dicedmelon.allplay.daemon.web.Status
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.status(daemon: SpeakersDaemon) {
    get("/status") {
        call.respond(
            Status(
                isConnected = daemon.isConnected,
                version = App::javaClass.javaClass.`package`.specificationVersion ?: "undefined",
                revision = App::javaClass.javaClass.`package`.implementationVersion ?: "undefined"
            )
        )
    }
}
