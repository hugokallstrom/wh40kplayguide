package org.example

import org.example.cli.CliRunner
import org.example.web.WebServer

/**
 * Warhammer 40,000 Game Guide
 *
 * A tool to guide players through the phases of a Warhammer 40K battle.
 *
 * Usage:
 *   ./gradlew run                    - Start CLI mode
 *   ./gradlew run --args="--web"     - Start web server on port 8080
 *   ./gradlew run --args="--web 3000" - Start web server on port 3000
 */
fun main(args: Array<String>) {
    // Check for web mode
    val webModeIndex = args.indexOfFirst { it == "--web" || it == "-w" }
    if (webModeIndex >= 0) {
        val port = args.getOrNull(webModeIndex + 1)?.toIntOrNull() ?: 8080
        startWebServer(port)
    } else {
        startCliMode()
    }
}

/**
 * Starts the web server mode.
 */
private fun startWebServer(port: Int) {
    val server = WebServer(port = port)
    server.start()
}

/**
 * Starts the CLI mode.
 */
private fun startCliMode() {
    val runner = CliRunner()
    runner.run()
}
