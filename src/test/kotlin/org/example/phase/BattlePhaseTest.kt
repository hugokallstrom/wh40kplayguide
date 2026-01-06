package org.example.phase

import org.example.fixtures.TestFixtures
import org.example.game.GameState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Unit tests for battle phase transitions.
 */
class BattlePhaseTest {

    // ========== CommandPhase Tests ==========

    @Test
    fun `CommandPhase does not require input`() {
        assertFalse(CommandPhase.requiresInput())
    }

    @Test
    fun `CommandPhase transitions to MovementPhase in round 1`() {
        val state = TestFixtures.gameStateAtRound(1)
        val nextPhase = CommandPhase.nextPhase(state)

        assertEquals(MovementPhase, nextPhase)
    }

    @Test
    fun `CommandPhase transitions to MovementPhase in round 2`() {
        val state = TestFixtures.gameStateAtRound(2)
        val nextPhase = CommandPhase.nextPhase(state)

        assertEquals(MovementPhase, nextPhase)
    }

    @Test
    fun `CommandPhase transitions to MovementPhase in round 5`() {
        val state = TestFixtures.gameStateAtRound(5)
        val nextPhase = CommandPhase.nextPhase(state)

        assertEquals(MovementPhase, nextPhase)
    }

    // ========== Phase Sequence Tests ==========

    @Test
    fun `MovementPhase transitions to ShootingPhase`() {
        val state = TestFixtures.gameStateInBattle()
        val nextPhase = MovementPhase.nextPhase(state)

        assertEquals(ShootingPhase, nextPhase)
    }

    @Test
    fun `ShootingPhase transitions to ChargePhase`() {
        val state = TestFixtures.gameStateInBattle()
        val nextPhase = ShootingPhase.nextPhase(state)

        assertEquals(ChargePhase, nextPhase)
    }

    @Test
    fun `ChargePhase transitions to FightPhase`() {
        val state = TestFixtures.gameStateInBattle()
        val nextPhase = ChargePhase.nextPhase(state)

        assertEquals(FightPhase, nextPhase)
    }

    @Test
    fun `FightPhase transitions to EndOfTurnPhase`() {
        val state = TestFixtures.gameStateInBattle()
        val nextPhase = FightPhase.nextPhase(state)

        assertEquals(EndOfTurnPhase, nextPhase)
    }

    // ========== EndOfTurnPhase Tests ==========

    @Test
    fun `EndOfTurnPhase transitions to CommandPhase if player 1`() {
        // Player 1's turn ends, player 2 goes next (still same round)
        val state = GameState(
            currentRound = 1,
            activePlayerNumber = 1,
            firstPlayerNumber = 1
        )
        val nextPhase = EndOfTurnPhase.nextPhase(state)

        assertEquals(CommandPhase, nextPhase)
        assertEquals(2, state.activePlayerNumber) // State was mutated
    }

    @Test
    fun `EndOfTurnPhase transitions to EndOfRoundPhase if player 2`() {
        // Player 2's turn ends, round ends
        val state = GameState(
            currentRound = 1,
            activePlayerNumber = 2,
            firstPlayerNumber = 1
        )
        val nextPhase = EndOfTurnPhase.nextPhase(state)

        assertIs<EndOfRoundPhase>(nextPhase)
        assertEquals(1, (nextPhase as EndOfRoundPhase).completedRound)
    }

    // ========== EndOfRoundPhase Tests ==========

    @Test
    fun `EndOfRoundPhase transitions to CommandPhase`() {
        val state = GameState(
            currentRound = 2,
            activePlayerNumber = 1,
            firstPlayerNumber = 1
        )
        val endOfRound = EndOfRoundPhase(1)
        val nextPhase = endOfRound.nextPhase(state)

        assertEquals(CommandPhase, nextPhase)
    }

    @Test
    fun `EndOfRoundPhase transitions to EndGamePhase after round 5`() {
        val state = GameState(
            currentRound = 6, // After round 5, game is over
            activePlayerNumber = 1,
            firstPlayerNumber = 1
        )
        val endOfRound = EndOfRoundPhase(5)
        val nextPhase = endOfRound.nextPhase(state)

        assertEquals(EndGamePhase, nextPhase)
    }

    // ========== EndGamePhase Tests ==========

    @Test
    fun `EndGamePhase is terminal`() {
        val state = TestFixtures.gameStateAtRound(6)
        val nextPhase = EndGamePhase.nextPhase(state)

        assertEquals(EndGamePhase, nextPhase)
    }
}
