package com.dicedmelon.allplay.daemon

import com.dicedmelon.allplay.daemon.routes.speakers
import com.dicedmelon.allplay.daemon.routes.status
import com.dicedmelon.allplay.daemon.speakers.speakersDaemon
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.routing.Routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.text.DateFormat

class App : CliktCommand() {
    private val port: Int by option(help = "port").int().default(8765)

    override fun run() {
        val daemon = speakersDaemon().start()

        embeddedServer(Netty, port) {
            install(DefaultHeaders)
            install(Compression)
            install(CallLogging)
            install(ContentNegotiation) {
                gson {
                    setDateFormat(DateFormat.LONG)
                    setPrettyPrinting()
                }
            }
            install(Routing) {
                status(daemon)
                speakers(daemon)
            }
        }.start(wait = true)
    }
}