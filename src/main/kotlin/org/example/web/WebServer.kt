package org.example.web

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.sessions.*
import org.example.game.GameState
import org.example.phase.Phase
import org.example.phase.SetupPhase
import java.util.concurrent.ConcurrentHashMap

/**
 * Session data for each web user.
 */
data class GameSession(val sessionId: String)

/**
 * Holds the game state for a session.
 */
data class SessionGameData(
    var state: GameState = GameState(),
    var currentPhase: Phase = SetupPhase.MusterArmies,
    var version: Int = 0,
    val snapshots: MutableMap<Int, Pair<GameState, Phase>> = mutableMapOf()
) {
    /**
     * Saves current state and phase as a snapshot at the current version.
     */
    fun saveSnapshot() {
        snapshots[version] = state.copy() to currentPhase
    }

    /**
     * Restores state and phase from a previous snapshot.
     * Returns true if the snapshot was found and restored.
     */
    fun restoreSnapshot(v: Int): Boolean {
        return snapshots[v]?.let { (savedState, savedPhase) ->
            state = savedState.copy()
            currentPhase = savedPhase
            version = v
            true
        } ?: false
    }
}

/**
 * Web server for the Warhammer 40K Game Guide.
 */
open class WebServer(
    private val port: Int = System.getenv("PORT")?.toIntOrNull() ?: 8080
) {
    private val sessionGames = ConcurrentHashMap<String, SessionGameData>()

    fun start() {
        println("Starting web server on http://localhost:$port")

        embeddedServer(Netty, port = port) {
            configureServer()
        }.start(wait = true)
    }

    private fun Application.configureServer() {
        install(Sessions) {
            cookie<GameSession>("GAME_SESSION") {
                cookie.path = "/"
                cookie.maxAgeInSeconds = 3600 * 24 // 24 hours
            }
        }

        configureRoutes(this@WebServer)
    }

    /**
     * Gets or creates game data for a session.
     */
    fun getOrCreateGameData(sessionId: String): SessionGameData {
        return sessionGames.computeIfAbsent(sessionId) { SessionGameData() }
    }

    /**
     * Resets the game data for a session.
     */
    fun resetGame(sessionId: String): SessionGameData {
        val newData = SessionGameData()
        sessionGames[sessionId] = newData
        return newData
    }
}
