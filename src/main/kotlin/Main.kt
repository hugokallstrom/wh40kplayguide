package org.example

import org.example.cli.CliRunner
import org.example.web.WebServer
import java.io.File

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
    val projectRoot = System.getProperty("user.dir")

    // Check for web mode
    val webModeIndex = args.indexOfFirst { it == "--web" || it == "-w" }
    if (webModeIndex >= 0) {
        val port = args.getOrNull(webModeIndex + 1)?.toIntOrNull() ?: 8080
        startWebServer(projectRoot, port)
    } else {
        startCliMode(projectRoot, args)
    }
}

/**
 * Starts the web server mode.
 */
private fun startWebServer(projectRoot: String, port: Int) {
    val primaryMissionsPath = findFile(projectRoot, "primary_missions.txt")
        ?: "primary_missions.txt"

    val secondaryMissionsPath = findFile(projectRoot, "secondary_missions_attacker.txt")
        ?: "secondary_missions_attacker.txt"

    val server = WebServer(
        primaryMissionsPath = primaryMissionsPath,
        secondaryMissionsPath = secondaryMissionsPath,
        port = port
    )

    server.start()
}

/**
 * Starts the CLI mode.
 */
private fun startCliMode(projectRoot: String, args: Array<String>) {
    val primaryMissionsPath = args.getOrNull(0)
        ?: findFile(projectRoot, "primary_missions.txt")
        ?: "primary_missions.txt"

    val secondaryMissionsPath = args.getOrNull(1)
        ?: findFile(projectRoot, "secondary_missions_attacker.txt")
        ?: "secondary_missions_attacker.txt"

    // Verify files exist
    if (!File(primaryMissionsPath).exists()) {
        println("Warning: Primary missions file not found at: $primaryMissionsPath")
        println("The game will run but mission selection may not work correctly.")
        println()
    }

    if (!File(secondaryMissionsPath).exists()) {
        println("Warning: Secondary missions file not found at: $secondaryMissionsPath")
        println("Secondary mission selection may not work correctly.")
        println()
    }

    val runner = CliRunner(
        primaryMissionsPath = primaryMissionsPath,
        secondaryMissionsPath = secondaryMissionsPath
    )

    runner.run()
}

/**
 * Tries to find a file in common locations.
 */
private fun findFile(projectRoot: String, filename: String): String? {
    val candidates = listOf(
        "$projectRoot/$filename",
        "$projectRoot/src/main/resources/$filename",
        filename
    )

    return candidates.find { File(it).exists() }
}
