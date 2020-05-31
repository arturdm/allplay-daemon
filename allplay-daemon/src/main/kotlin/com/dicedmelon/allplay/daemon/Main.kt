package com.dicedmelon.allplay.daemon

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

class Main {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(Main::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            logger.error(System.getProperty("java.library.path"))
            tryLoadingNativeLibrary()
            App().main(args)
        }

        private fun tryLoadingNativeLibrary() {
            try {
                System.loadLibrary("alljoyn_java")
            } catch (e: UnsatisfiedLinkError) {
                loadLibrary()
            }
        }

        private fun loadLibrary() {
            val distDir = extractDistDirFromClasspath()
            val libraryPath = createLibraryPath(distDir)
            logger.info("Loading alljoyn_java from $libraryPath")
            System.load(libraryPath)
        }

        private fun extractDistDirFromClasspath(): String {
            val paths = System.getProperty("java.class.path").split(File.pathSeparator)
            return paths.first { it.contains("tchaikovsky.jar") }
                .substringBefore("/tchaikovsky.jar")
        }

        private fun createLibraryPath(distDir: String): String {
            val separator = File.separator
            val filename = createLibraryFilename()
            return "${distDir}${separator}lib${separator}${osName()}${separator}${osArch()}${separator}${filename}"
        }

        private fun osName(): String {
            return when (System.getProperty("os.name")) {
                "Mac OS X", "darwin" -> "darwin"
                "linux" -> "linux"
                else -> throw RuntimeException("Couldn't extract supported os.name")
            }
        }

        private fun osArch(): String {
            return when (System.getProperty("os.arch")) {
                "x86_64" -> "x86_64"
                "i386", "x386" -> "x386"
                else -> throw RuntimeException("Couldn't extract supported os.arch")
            }
        }

        private fun createLibraryFilename(): String {
            val libraryName = "alljoyn_java"
            return when (System.getProperty("os.name")) {
                "Mac OS X", "darwin" -> "lib${libraryName}.dylib"
                "linux" -> "lib${libraryName}.so"
                else -> throw RuntimeException("Couldn't create library filename with current os.name")
            }
        }
    }
}
