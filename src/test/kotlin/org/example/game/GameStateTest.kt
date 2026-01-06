package org.example.game

import org.example.fixtures.TestFixtures
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for GameState - core game state logic.
 */
class GameStateTest {

    // ========== Default State Tests ==========

    @Test
    fun `default state has correct battle size`() {
        val state = TestFixtures.defaultGameState()
        assertEquals(BattleSize.STRIKE_FORCE, state.battleSize)
    }

    @Test
    fun `default state starts at round 0`() {
        val state = TestFixtures.defaultGameState()
        assertEquals(0, state.currentRound)
    }

    // ========== startBattle Tests ==========

    @Test
    fun `startBattle sets round to 1`() {
        val state = TestFixtures.defaultGameState()
        state.startBattle()
        assertEquals(1, state.currentRound)
    }

    @Test
    fun `startBattle sets activePlayer to firstPlayer`() {
        val state = TestFixtures.defaultGameState()
        state.firstPlayerNumber = 1
        state.startBattle()
        assertEquals(1, state.activePlayerNumber)
    }

    @Test
    fun `startBattle with player 2 first`() {
        val state = TestFixtures.defaultGameState()
        state.firstPlayerNumber = 2
        state.startBattle()
        assertEquals(2, state.activePlayerNumber)
    }

    // ========== advanceToNextTurn Tests ==========

    @Test
    fun `advanceToNextTurn switches from player 1 to player 2`() {
        val state = TestFixtures.gameStateAtRound(1, activePlayer = 1)
        state.firstPlayerNumber = 1

        state.advanceToNextTurn()

        assertEquals(2, state.activePlayerNumber)
        assertEquals(1, state.currentRound) // Still round 1
    }

    @Test
    fun `advanceToNextTurn returns false within round`() {
        val state = TestFixtures.gameStateAtRound(1, activePlayer = 1)
        state.firstPlayerNumber = 1

        val newRoundStarted = state.advanceToNextTurn()

        assertFalse(newRoundStarted)
    }

    @Test
    fun `advanceToNextTurn increments round after player 2`() {
        val state = TestFixtures.gameStateAtRound(1, activePlayer = 2)
        state.firstPlayerNumber = 1

        state.advanceToNextTurn()

        assertEquals(2, state.currentRound)
        assertEquals(1, state.activePlayerNumber) // Back to first player
    }

    @Test
    fun `advanceToNextTurn returns true on new round`() {
        val state = TestFixtures.gameStateAtRound(1, activePlayer = 2)
        state.firstPlayerNumber = 1

        val newRoundStarted = state.advanceToNextTurn()

        assertTrue(newRoundStarted)
    }

    @Test
    fun `advanceToNextTurn respects custom first player`() {
        // Player 2 goes first
        val state = GameState(
            currentRound = 1,
            activePlayerNumber = 2,
            firstPlayerNumber = 2
        )

        // First player (2) finishes, switch to player 1
        state.advanceToNextTurn()
        assertEquals(1, state.activePlayerNumber)
        assertEquals(1, state.currentRound)

        // Player 1 finishes, advance to round 2, back to player 2
        state.advanceToNextTurn()
        assertEquals(2, state.activePlayerNumber)
        assertEquals(2, state.currentRound)
    }

    // ========== isGameOver Tests ==========

    @Test
    fun `isGameOver false for rounds 1-5`() {
        for (round in 1..5) {
            val state = TestFixtures.gameStateAtRound(round)
            assertFalse(state.isGameOver(), "Round $round should not be game over")
        }
    }

    @Test
    fun `isGameOver true after round 5`() {
        val state = TestFixtures.gameStateAtRound(6)
        assertTrue(state.isGameOver())
    }

    // ========== Player Role Tests ==========

    @Test
    fun `defenderPlayerNumber opposite of attacker`() {
        val state1 = TestFixtures.gameStateWithAttacker(1)
        assertEquals(2, state1.defenderPlayerNumber)

        val state2 = TestFixtures.gameStateWithAttacker(2)
        assertEquals(1, state2.defenderPlayerNumber)
    }

    @Test
    fun `isAttackerTurn true when attacker is active`() {
        val state = GameState(
            attackerPlayerNumber = 1,
            activePlayerNumber = 1
        )
        assertTrue(state.isAttackerTurn)

        state.activePlayerNumber = 2
        assertFalse(state.isAttackerTurn)
    }
}
