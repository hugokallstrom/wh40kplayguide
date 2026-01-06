package org.example.web.templates

import kotlinx.html.*
import org.example.web.SessionGameData

/**
 * Renders the game status sidebar.
 */
fun FlowContent.renderGameStatus(gameData: SessionGameData) {
    val state = gameData.state

    article {
        header {
            h3 { +"Game Status" }
        }

        // Battle Size
        div(classes = "status-item") {
            span(classes = "status-label") { +"Battle Size" }
            span { +state.battleSize.name.replace("_", " ") }
        }

        div(classes = "status-item") {
            span(classes = "status-label") { +"Points" }
            span { +"${state.battleSize.points} pts" }
        }

        // Round info (only during battle)
        if (state.currentRound > 0) {
            hr { }

            div(classes = "status-item") {
                span(classes = "status-label") { +"Round" }
                span { +"${state.currentRound} / 5" }
            }

            div(classes = "status-item") {
                span(classes = "status-label") { +"Active Player" }
                span { +"Player ${state.activePlayerNumber}" }
            }

            div(classes = "status-item") {
                span(classes = "status-label") { +"Role" }
                span { +if (state.isAttackerTurn) "ATTACKER" else "DEFENDER" }
            }
        }

        // Player roles (after attacker is determined)
        if (state.currentRound > 0 || state.attackerPlayerNumber != 1) {
            hr { }

            div(classes = "status-item") {
                span(classes = "status-label") { +"Attacker" }
                span { +"Player ${state.attackerPlayerNumber}" }
            }

            div(classes = "status-item") {
                span(classes = "status-label") { +"Defender" }
                span { +"Player ${state.defenderPlayerNumber}" }
            }

            if (state.currentRound > 0) {
                div(classes = "status-item") {
                    span(classes = "status-label") { +"First Player" }
                    span { +"Player ${state.firstPlayerNumber}" }
                }
            }
        }
    }
}

/**
 * Renders the game status as a partial (for HTMX updates).
 */
fun HTML.renderGameStatusPartial(gameData: SessionGameData) {
    body {
        renderGameStatus(gameData)
    }
}
