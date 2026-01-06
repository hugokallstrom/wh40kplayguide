package org.example.fixtures

import org.example.game.GameState

/**
 * Shared test utilities and fixtures for unit tests.
 */
object TestFixtures {

    // ========== GameState Factories ==========

    /**
     * Returns a fresh GameState with default values.
     */
    fun defaultGameState(): GameState = GameState()

    /**
     * Returns a GameState at the specified round with given active player.
     * Simulates a game in progress.
     */
    fun gameStateAtRound(round: Int, activePlayer: Int = 1): GameState = GameState(
        currentRound = round,
        activePlayerNumber = activePlayer,
        firstPlayerNumber = 1
    )

    /**
     * Returns a GameState ready for battle (round 1, player 1).
     */
    fun gameStateInBattle(): GameState = GameState(
        currentRound = 1,
        activePlayerNumber = 1,
        firstPlayerNumber = 1
    )

    /**
     * Returns a GameState near end of game (round 5, player 2).
     */
    fun gameStateAtRound5Player2(): GameState = GameState(
        currentRound = 5,
        activePlayerNumber = 2,
        firstPlayerNumber = 1
    )

    /**
     * Returns a GameState with specified attacker player number.
     */
    fun gameStateWithAttacker(playerNumber: Int): GameState = GameState(
        attackerPlayerNumber = playerNumber,
        activePlayerNumber = playerNumber
    )

    /**
     * Returns a GameState with player 2 going first.
     */
    fun gameStateWithPlayer2First(): GameState = GameState(
        firstPlayerNumber = 2,
        activePlayerNumber = 2,
        currentRound = 1
    )
}
