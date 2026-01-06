package org.example.phase

import org.example.fixtures.TestFixtures
import org.example.game.BattleSize
import org.example.game.SecondaryMissionType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for SetupPhase transitions and input processing.
 */
class SetupPhaseTest {

    // ========== MusterArmies Tests ==========

    @Test
    fun `MusterArmies requires input`() {
        assertTrue(SetupPhase.MusterArmies.requiresInput())
    }

    @Test
    fun `MusterArmies sets INCURSION for input 1`() {
        val state = TestFixtures.defaultGameState()
        val nextPhase = SetupPhase.MusterArmies.processInput("1", state)

        assertEquals(BattleSize.INCURSION, state.battleSize)
        assertNotNull(nextPhase)
    }

    @Test
    fun `MusterArmies sets STRIKE_FORCE for input 2`() {
        val state = TestFixtures.defaultGameState()
        val nextPhase = SetupPhase.MusterArmies.processInput("2", state)

        assertEquals(BattleSize.STRIKE_FORCE, state.battleSize)
        assertNotNull(nextPhase)
    }

    @Test
    fun `MusterArmies sets ONSLAUGHT for input 3`() {
        val state = TestFixtures.defaultGameState()
        val nextPhase = SetupPhase.MusterArmies.processInput("3", state)

        assertEquals(BattleSize.ONSLAUGHT, state.battleSize)
        assertNotNull(nextPhase)
    }

    @Test
    fun `MusterArmies returns null for invalid input`() {
        val state = TestFixtures.defaultGameState()

        assertNull(SetupPhase.MusterArmies.processInput("4", state))
        assertNull(SetupPhase.MusterArmies.processInput("invalid", state))
        assertNull(SetupPhase.MusterArmies.processInput("", state))
    }

    @Test
    fun `MusterArmies advances to DrawPrimaryMission`() {
        val state = TestFixtures.defaultGameState()
        val nextPhase = SetupPhase.MusterArmies.processInput("2", state)
        assertIs<SetupPhase.DrawPrimaryMission>(nextPhase)
    }

    // ========== DrawPrimaryMission Tests ==========

    @Test
    fun `DrawPrimaryMission does not require input`() {
        assertTrue(!SetupPhase.DrawPrimaryMission.requiresInput())
    }

    @Test
    fun `DrawPrimaryMission advances to CreateBattlefield`() {
        val state = TestFixtures.defaultGameState()
        val nextPhase = SetupPhase.DrawPrimaryMission.nextPhase(state)
        assertIs<SetupPhase.CreateBattlefield>(nextPhase)
    }

    // ========== DetermineAttacker Tests ==========

    @Test
    fun `DetermineAttacker sets attacker to player 1`() {
        val state = TestFixtures.defaultGameState()

        SetupPhase.DetermineAttacker.processInput("1", state)

        assertEquals(1, state.attackerPlayerNumber)
    }

    @Test
    fun `DetermineAttacker sets attacker to player 2`() {
        val state = TestFixtures.defaultGameState()

        SetupPhase.DetermineAttacker.processInput("2", state)

        assertEquals(2, state.attackerPlayerNumber)
    }

    @Test
    fun `DetermineAttacker advances to DrawAttackerSecondary`() {
        val state = TestFixtures.defaultGameState()
        val nextPhase = SetupPhase.DetermineAttacker.processInput("1", state)
        assertIs<SetupPhase.DrawAttackerSecondary>(nextPhase)
    }

    // ========== DrawAttackerSecondary Tests ==========

    @Test
    fun `DrawAttackerSecondary requires input`() {
        assertTrue(SetupPhase.DrawAttackerSecondary.requiresInput())
    }

    @Test
    fun `DrawAttackerSecondary sets FIXED for input 1`() {
        val state = TestFixtures.defaultGameState()
        state.attackerPlayerNumber = 1

        val nextPhase = SetupPhase.DrawAttackerSecondary.processInput("1", state)

        assertEquals(SecondaryMissionType.FIXED, state.getSecondaryTypeForPlayer(1))
        assertNotNull(nextPhase)
    }

    @Test
    fun `DrawAttackerSecondary sets TACTICAL for input 2`() {
        val state = TestFixtures.defaultGameState()
        state.attackerPlayerNumber = 1

        val nextPhase = SetupPhase.DrawAttackerSecondary.processInput("2", state)

        assertEquals(SecondaryMissionType.TACTICAL, state.getSecondaryTypeForPlayer(1))
        assertNotNull(nextPhase)
    }

    @Test
    fun `DrawAttackerSecondary returns null for invalid input`() {
        val state = TestFixtures.defaultGameState()

        assertNull(SetupPhase.DrawAttackerSecondary.processInput("3", state))
        assertNull(SetupPhase.DrawAttackerSecondary.processInput("invalid", state))
    }

    @Test
    fun `DrawAttackerSecondary advances to DrawDefenderSecondary`() {
        val state = TestFixtures.defaultGameState()
        val nextPhase = SetupPhase.DrawAttackerSecondary.processInput("1", state)
        assertIs<SetupPhase.DrawDefenderSecondary>(nextPhase)
    }

    // ========== DrawDefenderSecondary Tests ==========

    @Test
    fun `DrawDefenderSecondary requires input`() {
        assertTrue(SetupPhase.DrawDefenderSecondary.requiresInput())
    }

    @Test
    fun `DrawDefenderSecondary sets FIXED for input 1`() {
        val state = TestFixtures.defaultGameState()
        state.attackerPlayerNumber = 1 // So defender is player 2

        val nextPhase = SetupPhase.DrawDefenderSecondary.processInput("1", state)

        assertEquals(SecondaryMissionType.FIXED, state.getSecondaryTypeForPlayer(2))
        assertNotNull(nextPhase)
    }

    @Test
    fun `DrawDefenderSecondary sets TACTICAL for input 2`() {
        val state = TestFixtures.defaultGameState()
        state.attackerPlayerNumber = 1 // So defender is player 2

        val nextPhase = SetupPhase.DrawDefenderSecondary.processInput("2", state)

        assertEquals(SecondaryMissionType.TACTICAL, state.getSecondaryTypeForPlayer(2))
        assertNotNull(nextPhase)
    }

    @Test
    fun `DrawDefenderSecondary returns null for invalid input`() {
        val state = TestFixtures.defaultGameState()

        assertNull(SetupPhase.DrawDefenderSecondary.processInput("3", state))
        assertNull(SetupPhase.DrawDefenderSecondary.processInput("invalid", state))
    }

    @Test
    fun `DrawDefenderSecondary advances to DeclareBattleFormations`() {
        val state = TestFixtures.defaultGameState()
        val nextPhase = SetupPhase.DrawDefenderSecondary.processInput("1", state)
        assertIs<SetupPhase.DeclareBattleFormations>(nextPhase)
    }

    // ========== DetermineFirstTurn Tests ==========

    @Test
    fun `DetermineFirstTurn sets first player`() {
        val state = TestFixtures.defaultGameState()

        val nextPhase = SetupPhase.DetermineFirstTurn.processInput("2", state)

        assertEquals(2, state.firstPlayerNumber)
        assertEquals(CommandPhase, nextPhase)
    }

    @Test
    fun `DetermineFirstTurn calls startBattle`() {
        val state = TestFixtures.defaultGameState()
        assertEquals(0, state.currentRound) // Pre-battle

        // DetermineFirstTurn calls startBattle
        SetupPhase.DetermineFirstTurn.processInput("1", state)

        assertEquals(1, state.currentRound)
        assertEquals(1, state.activePlayerNumber)
    }
}
