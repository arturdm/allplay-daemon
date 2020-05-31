package com.dicedmelon.allplay.daemon

import com.dicedmelon.allplay.daemon.speakers.speakersDaemon
import com.dicedmelon.allplay.daemon.web.*
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.text.DateFormat

class App : CliktCommand() {
    private val logger: Logger = LoggerFactory.getLogger(App::class.java)
    private val port: Int by option(help = "port").int().default(8765)

    override fun run() {
        val daemon = speakersDaemon {

        }.start()

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

            routing {
                get("/status") {
                    call.respond(Status(isConnected = daemon.isConnected))
                }
                get("/speakers") {
                    val speakers = daemon.availableSpeakers
                    GlobalScope.launch {
                        call.respond(
                            SpeakersResponse(
                                speakers = speakers.values.map {
                                    val input = it.input()
                                    val volume = it.volume()
                                    Speaker(
                                        id = it.id,
                                        name = it.name,
                                        input = Input(
                                            activeInput = safeTry { input.activeInput }.orEmpty(),
                                            availableInputs = safeTry { input.inputList }.orEmpty()
                                        ),
                                        volume = Volume(
                                            isMute = safeTry { volume.isMute },
                                            volume = safeTry { volume.volume }
                                        ),
                                        isConnected = it.isConnected
                                    )
                                }
                            )
                        )
                    }.join()
                }
                post("/speakers/{id}/input") {
                    val speaker = daemon.availableSpeakers[call.parameters["id"]]
                    if (speaker == null) {
                        call.respond(HttpStatusCode.NotFound)
                    } else {
                        val body = call.receive<SpeakerInputRequestBody>()
                        speaker.input().setInput(body.input)
                        call.respond(HttpStatusCode.OK)
                    }
                }
            }
        }.start(wait = true)
    }

    private fun <T> safeTry(function: () -> T): T? {
        return try {
            function()
        } catch (e: Exception) {
            logger.warn("Could not retrieve value", e)
            return null
        }
    }
}
