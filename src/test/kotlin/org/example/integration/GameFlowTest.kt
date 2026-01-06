package org.example.integration

import org.example.fixtures.TestFixtures
import org.example.game.GameState
import org.example.phase.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Integration tests for complete game flows.
 */
class GameFlowTest {

    // ========== Setup Flow Tests ==========

    @Test
    fun `setup phases complete in correct order`() {
        val state = TestFixtures.defaultGameState()
        var phase: Phase = SetupPhase.MusterArmies

        // MusterArmies -> DrawPrimaryMission
        phase = phase.processInput("2", state)!! // Strike Force
        assertIs<SetupPhase.DrawPrimaryMission>(phase)

        // DrawPrimaryMission -> CreateBattlefield
        phase = phase.nextPhase(state)
        assertIs<SetupPhase.CreateBattlefield>(phase)

        // CreateBattlefield -> DetermineAttacker
        phase = phase.nextPhase(state)
        assertIs<SetupPhase.DetermineAttacker>(phase)

        // DetermineAttacker -> DrawAttackerSecondary
        phase = phase.processInput("1", state)!!
        assertIs<SetupPhase.DrawAttackerSecondary>(phase)

        // DrawAttackerSecondary -> DrawDefenderSecondary (requires input: Fixed=1, Tactical=2)
        phase = phase.processInput("1", state)!!
        assertIs<SetupPhase.DrawDefenderSecondary>(phase)

        // DrawDefenderSecondary -> DeclareBattleFormations (requires input: Fixed=1, Tactical=2)
        phase = phase.processInput("2", state)!!
        assertIs<SetupPhase.DeclareBattleFormations>(phase)

        // DeclareBattleFormations -> DeployArmies
        phase = phase.nextPhase(state)
        assertIs<SetupPhase.DeployArmies>(phase)

        // DeployArmies -> PreBattleRules
        phase = phase.nextPhase(state)
        assertIs<SetupPhase.PreBattleRules>(phase)

        // PreBattleRules -> DetermineFirstTurn
        phase = phase.nextPhase(state)
        assertIs<SetupPhase.DetermineFirstTurn>(phase)

        // DetermineFirstTurn -> CommandPhase (battle starts!)
        phase = phase.processInput("1", state)!!
        assertEquals(CommandPhase, phase)

        // Verify battle has started
        assertEquals(1, state.currentRound)
    }

    // ========== Battle Round Tests ==========

    @Test
    fun `battle round completes full cycle`() {
        val state = TestFixtures.gameStateInBattle()
        var phase: Phase = CommandPhase

        // Round 1: No VP scoring
        // CommandPhase -> MovementPhase
        phase = phase.nextPhase(state)
        assertEquals(MovementPhase, phase)

        // MovementPhase -> ShootingPhase
        phase = phase.nextPhase(state)
        assertEquals(ShootingPhase, phase)

        // ShootingPhase -> ChargePhase
        phase = phase.nextPhase(state)
        assertEquals(ChargePhase, phase)

        // ChargePhase -> FightPhase
        phase = phase.nextPhase(state)
        assertEquals(FightPhase, phase)

        // FightPhase -> EndOfTurnPhase
        phase = phase.nextPhase(state)
        assertEquals(EndOfTurnPhase, phase)
    }

    @Test
    fun `two-player round advances correctly`() {
        val state = GameState(
            currentRound = 1,
            activePlayerNumber = 1,
            firstPlayerNumber = 1
        )

        // Player 1's turn ends
        val afterPlayer1 = EndOfTurnPhase.nextPhase(state)
        assertEquals(CommandPhase, afterPlayer1)
        assertEquals(2, state.activePlayerNumber)
        assertEquals(1, state.currentRound) // Still round 1

        // Player 2's turn ends
        val afterPlayer2 = EndOfTurnPhase.nextPhase(state)
        assertIs<EndOfRoundPhase>(afterPlayer2)
        assertEquals(2, state.currentRound) // Now round 2
        assertEquals(1, state.activePlayerNumber) // Back to player 1
    }

    @Test
    fun `five rounds complete game`() {
        val state = GameState(
            currentRound = 1,
            activePlayerNumber = 1,
            firstPlayerNumber = 1
        )

        // Simulate 5 complete rounds
        for (round in 1..5) {
            assertEquals(round, state.currentRound)

            // Player 1's turn
            state.advanceToNextTurn()

            // Player 2's turn
            state.advanceToNextTurn()
        }

        // After 5 rounds, game should be over
        assertTrue(state.isGameOver())
        assertEquals(6, state.currentRound)
    }

    @Test
    fun `game state consistent after multiple rounds`() {
        val state = GameState(
            currentRound = 1,
            activePlayerNumber = 1,
            firstPlayerNumber = 1,
            attackerPlayerNumber = 1
        )

        // Simulate 3 complete rounds
        for (round in 1..3) {
            // Player 1's turn
            val firstNewRound = state.advanceToNextTurn()
            assertEquals(false, firstNewRound)

            // Player 2's turn
            val secondNewRound = state.advanceToNextTurn()
            assertEquals(true, secondNewRound)
        }

        // After 3 rounds
        assertEquals(4, state.currentRound)
        assertEquals(1, state.activePlayerNumber) // Back to first player
        assertEquals(1, state.firstPlayerNumber) // Unchanged
        assertEquals(1, state.attackerPlayerNumber) // Unchanged
    }
}
