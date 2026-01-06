package org.example.web.templates

import kotlinx.html.*
import org.example.game.BattleSize
import org.example.guidance.HtmlRenderer
import org.example.phase.*
import org.example.web.SessionGameData

/**
 * Renders the home page with start game option.
 */
fun MAIN.renderHomePage(gameData: SessionGameData) {
    section(classes = "hero-section") {
        h1 { +"Warhammer 40,000" }
        h2 { +"Game Guide" }
        p { +"A step-by-step guide through the phases of battle" }

        form {
            method = FormMethod.post
            action = "/start"

            button {
                type = ButtonType.submit
                +"Start New Game"
            }
        }

        // If there's an existing game in progress, show continue option
        if (gameData.currentPhase != SetupPhase.MusterArmies) {
            p {
                a(href = "/phase") { +"Continue Current Game" }
            }
        }
    }
}

/**
 * Renders the main phase view with sidebar.
 */
fun MAIN.renderPhaseView(gameData: SessionGameData) {
    div(classes = "game-layout") {
        // Main content area
        div {
            id = "phase-content"
            renderPhaseContentInner(gameData)
        }

        // Status sidebar
        aside(classes = "status-sidebar") {
            id = "status-sidebar"
            renderGameStatus(gameData)
        }
    }
}

/**
 * Renders just the phase content (for HTMX partial updates).
 */
fun HTML.renderPhaseContent(gameData: SessionGameData) {
    body {
        renderPhaseContentInner(gameData)
    }
}

/**
 * Inner function to render phase content.
 */
fun FlowContent.renderPhaseContentInner(gameData: SessionGameData) {
    val phase = gameData.currentPhase
    val state = gameData.state
    val version = gameData.version

    article(classes = "phase-content") {
        // Phase header
        header(classes = "phase-header") {
            div {
                h2 { +phase.phaseName }

                // Show turn info for battle phases
                if (phase is BattlePhaseMarker && phase !is EndGamePhase) {
                    p(classes = "turn-info") {
                        +state.currentTurnDisplay()
                    }
                }
            }

            // Loading indicator for HTMX
            span(classes = "htmx-indicator") {
                span(classes = "loading")
            }
        }

        // Phase guidance - use structured content for rich HTML
        div(classes = "guidance-content") {
            HtmlRenderer.render(this, phase.displayStructuredGuidance(state))
        }

        // Action area
        footer {
            if (phase.requiresInput()) {
                renderInputPhase(phase, state, version)
            } else if (phase is EndGamePhase) {
                renderEndGame(version)
            } else {
                renderContinueButton(version)
            }
        }
    }
}

/**
 * Renders input choices for phases that require selection.
 * @param version The current game version for browser history support
 */
private fun FlowContent.renderInputPhase(phase: Phase, state: org.example.game.GameState, version: Int) {
    when (phase) {
        is SetupPhase.MusterArmies -> {
            form(classes = "choice-group") {
                attributes["hx-post"] = "/phase/select"
                attributes["hx-target"] = "#phase-content"
                attributes["hx-swap"] = "innerHTML"
                attributes["hx-indicator"] = ".htmx-indicator"

                hiddenInput {
                    name = "version"
                    value = version.toString()
                }

                BattleSize.entries.forEachIndexed { index, size ->
                    button(classes = "choice-button secondary outline") {
                        type = ButtonType.submit
                        name = "choice"
                        value = (index + 1).toString()
                        +"${index + 1}. ${size.name.replace("_", " ")} (${size.points} pts)"
                    }
                }
            }
        }

        is SetupPhase.DetermineAttacker -> {
            form(classes = "choice-group") {
                attributes["hx-post"] = "/phase/select"
                attributes["hx-target"] = "#phase-content"
                attributes["hx-swap"] = "innerHTML"
                attributes["hx-indicator"] = ".htmx-indicator"

                hiddenInput {
                    name = "version"
                    value = version.toString()
                }

                button(classes = "choice-button secondary outline") {
                    type = ButtonType.submit
                    name = "choice"
                    value = "1"
                    +"Player 1 is the Attacker"
                }
                button(classes = "choice-button secondary outline") {
                    type = ButtonType.submit
                    name = "choice"
                    value = "2"
                    +"Player 2 is the Attacker"
                }
            }
        }

        is SetupPhase.DetermineFirstTurn -> {
            form(classes = "choice-group") {
                attributes["hx-post"] = "/phase/select"
                attributes["hx-target"] = "#phase-content"
                attributes["hx-swap"] = "innerHTML"
                attributes["hx-indicator"] = ".htmx-indicator"

                hiddenInput {
                    name = "version"
                    value = version.toString()
                }

                button(classes = "choice-button secondary outline") {
                    type = ButtonType.submit
                    name = "choice"
                    value = "1"
                    +"Player 1 goes first"
                }
                button(classes = "choice-button secondary outline") {
                    type = ButtonType.submit
                    name = "choice"
                    value = "2"
                    +"Player 2 goes first"
                }
            }
        }

        else -> {
            // Generic fallback for any other input phase
            renderContinueButton(version)
        }
    }
}

/**
 * Renders the continue button for non-input phases.
 * @param version The current game version for browser history support
 */
private fun FlowContent.renderContinueButton(version: Int) {
    div(classes = "action-buttons") {
        button {
            attributes["hx-post"] = "/phase/advance"
            attributes["hx-target"] = "#phase-content"
            attributes["hx-swap"] = "innerHTML"
            attributes["hx-indicator"] = ".htmx-indicator"
            attributes["hx-vals"] = """{"version": "$version"}"""
            +"Continue"
        }

        button(classes = "secondary outline") {
            attributes["hx-post"] = "/reset"
            attributes["hx-target"] = "#phase-content"
            attributes["hx-swap"] = "innerHTML"
            attributes["hx-confirm"] = "Are you sure you want to restart the game?"
            attributes["hx-vals"] = """{"version": "$version"}"""
            +"Restart Game"
        }
    }
}

/**
 * Renders end game options.
 * @param version The current game version for browser history support
 */
private fun FlowContent.renderEndGame(version: Int) {
    div(classes = "action-buttons") {
        a(href = "/") {
            button { +"Return Home" }
        }

        button(classes = "secondary") {
            attributes["hx-post"] = "/reset"
            attributes["hx-target"] = "#phase-content"
            attributes["hx-swap"] = "innerHTML"
            attributes["hx-vals"] = """{"version": "$version"}"""
            +"Play Again"
        }
    }
}
