package com.dicedmelon.allplay.daemon

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Main {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(Main::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            logger.error(System.getProperty("java.library.path"))
            App().main(args)
        }
    }
}
