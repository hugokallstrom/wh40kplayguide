package org.example.phase

import org.example.game.GameState

/**
 * Represents a phase in the game that displays guidance and can transition to the next phase.
 */
sealed interface Phase {
    /**
     * The name of this phase for display purposes.
     */
    val phaseName: String

    /**
     * Returns the guidance text to display for this phase.
     */
    fun displayGuidance(state: GameState): String

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
