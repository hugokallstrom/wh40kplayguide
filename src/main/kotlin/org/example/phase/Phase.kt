package org.example.phase

import org.example.game.GameState
import org.example.guidance.GuidanceContent

/**
 * Represents a phase in the game that displays guidance and can transition to the next phase.
 */
sealed interface Phase {
    /**
     * The name of this phase for display purposes.
     */
    val phaseName: String

    /**
     * Returns the guidance text to display for this phase (used by CLI).
     */
    fun displayGuidance(state: GameState): String

    /**
     * Returns structured guidance content for this phase (used by web).
     * Default implementation wraps the plain text in a single Paragraph.
     * Override this method to provide rich HTML rendering.
     */
    fun displayStructuredGuidance(state: GameState): List<GuidanceContent> {
        return listOf(GuidanceContent.Paragraph(displayGuidance(state)))
    }

    /**
     * Returns the next phase after this one.
     * May modify the game state (e.g., advancing rounds).
     */
    fun nextPhase(state: GameState): Phase

    /**
     * Returns true if this phase requires user input (e.g., selecting a mission).
     */
    fun requiresInput(): Boolean = false

    /**
     * Processes user input for phases that require it.
     * @return the next phase after processing input, or null if input was invalid
     */
    fun processInput(input: String, state: GameState): Phase? = nextPhase(state)
}

/**
 * Marker interface for setup phases (pre-battle).
 */
sealed interface SetupPhaseMarker : Phase

/**
 * Marker interface for battle round phases.
 */
sealed interface BattlePhaseMarker : Phase
