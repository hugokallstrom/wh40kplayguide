package org.example.web

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.example.phase.BattlePhaseMarker
import org.example.phase.EndGamePhase
import org.example.phase.SetupPhase
import org.example.web.templates.*
import java.util.UUID

/**
 * Configures all HTTP routes for the web application.
 */
fun Application.configureRoutes(server: WebServer) {
    routing {
        // Home page - shows start screen or current phase
        get("/") {
            val session = call.getOrCreateSession()
            val gameData = server.getOrCreateGameData(session.sessionId)

            call.respondHtml {
                renderLayout("Warhammer 40K Game Guide") {
                    renderHomePage(gameData)
                }
            }
        }

        // Start a new game
        post("/start") {
            val session = call.getOrCreateSession()
            val gameData = server.resetGame(session.sessionId)

            call.respondRedirect("/phase?v=${gameData.version}")
        }

        // Current phase view
        get("/phase") {
            val session = call.getOrCreateSession()
            val gameData = server.getOrCreateGameData(session.sessionId)

            // Check if browser navigated back/forward to a different version
            val requestedVersion = call.parameters["v"]?.toIntOrNull()
            if (requestedVersion != null && requestedVersion != gameData.version) {
                gameData.restoreSnapshot(requestedVersion)
            }

            call.respondHtml {
                renderLayout("Warhammer 40K Game Guide") {
                    renderPhaseView(gameData)
                }
            }
        }

        // HTMX partial - just the phase content
        get("/phase/content") {
            val session = call.getOrCreateSession()
            val gameData = server.getOrCreateGameData(session.sessionId)

            call.respondHtml {
                renderPhaseContent(gameData)
            }
        }

        // Advance to next phase (for non-input phases)
        post("/phase/advance") {
            val session = call.getOrCreateSession()
            val gameData = server.getOrCreateGameData(session.sessionId)
            val params = call.receiveParameters()

            // Restore to the version the user was viewing (handles browser back/forward)
            val submittedVersion = params["version"]?.toIntOrNull()
            if (submittedVersion != null && submittedVersion != gameData.version) {
                gameData.restoreSnapshot(submittedVersion)
            }

            if (!gameData.currentPhase.requiresInput()) {
                gameData.saveSnapshot()
                gameData.currentPhase = gameData.currentPhase.nextPhase(gameData.state)
                gameData.version++
                gameData.saveSnapshot()
            }

            // Always redirect with version (for browser history)
            val redirectUrl = "/phase?v=${gameData.version}"
            val isHtmx = call.request.headers["HX-Request"] == "true"
            if (isHtmx) {
                call.response.headers.append("HX-Redirect", redirectUrl)
                call.respondText("")
            } else {
                call.respondRedirect(redirectUrl)
            }
        }

        // Process selection (for input phases)
        post("/phase/select") {
            val session = call.getOrCreateSession()
            val gameData = server.getOrCreateGameData(session.sessionId)
            val params = call.receiveParameters()
            val choice = params["choice"] ?: ""

            // Restore to the version the user was viewing (handles browser back/forward)
            val submittedVersion = params["version"]?.toIntOrNull()
            if (submittedVersion != null && submittedVersion != gameData.version) {
                gameData.restoreSnapshot(submittedVersion)
            }

            if (gameData.currentPhase.requiresInput()) {
                val nextPhase = gameData.currentPhase.processInput(choice, gameData.state)
                if (nextPhase != null) {
                    gameData.saveSnapshot()
                    gameData.currentPhase = nextPhase
                    gameData.version++
                    gameData.saveSnapshot()
                }
            }

            // Always redirect with version (for browser history)
            val redirectUrl = "/phase?v=${gameData.version}"
            val isHtmx = call.request.headers["HX-Request"] == "true"
            if (isHtmx) {
                call.response.headers.append("HX-Redirect", redirectUrl)
                call.respondText("")
            } else {
                call.respondRedirect(redirectUrl)
            }
        }

        // Game status partial (for HTMX sidebar updates)
        get("/status") {
            val session = call.getOrCreateSession()
            val gameData = server.getOrCreateGameData(session.sessionId)

            call.respondHtml {
                renderGameStatusPartial(gameData)
            }
        }

        // Reset game
        post("/reset") {
            val session = call.getOrCreateSession()
            val gameData = server.resetGame(session.sessionId)

            // Always redirect with version (for browser history)
            val redirectUrl = "/phase?v=${gameData.version}"
            val isHtmx = call.request.headers["HX-Request"] == "true"
            if (isHtmx) {
                call.response.headers.append("HX-Redirect", redirectUrl)
                call.respondText("")
            } else {
                call.respondRedirect(redirectUrl)
            }
        }
    }
}

/**
 * Gets or creates a session for the current call.
 */
private fun ApplicationCall.getOrCreateSession(): GameSession {
    return sessions.get<GameSession>() ?: run {
        val newSession = GameSession(UUID.randomUUID().toString())
        sessions.set(newSession)
        newSession
    }
}
