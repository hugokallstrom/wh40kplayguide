package org.example.phase

import org.example.fixtures.TestFixtures
import org.example.game.BattleSize
import org.example.game.GameState
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for SetupPhase transitions and input processing.
 */
class SetupPhaseTest {

    @BeforeTest
    fun setUp() {
        TestFixtures.setupAvailableMissions()
    }

    @AfterTest
    fun tearDown() {
        TestFixtures.clearAvailableMissions()
    }

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

    // ========== ReadMissionObjectives Tests ==========

    @Test
    fun `ReadMissionObjectives selects mission by number`() {
        val state = TestFixtures.defaultGameState()

        val nextPhase = SetupPhase.ReadMissionObjectives.processInput("1", state)

        assertNotNull(state.primaryMission)
        assertEquals("Take and Hold", state.primaryMission?.name)
        assertTrue(nextPhase is SetupPhase.DisplayMissionDetails)
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

    // ========== DetermineFirstTurn Tests ==========

    @Test
    fun `DetermineFirstTurn sets first player`() {
        val state = TestFixtures.defaultGameState()

        val nextPhase = SetupPhase.DetermineFirstTurn.processInput("2", state)

        assertEquals(2, state.firstPlayerNumber)
        assertEquals(CommandPhase, nextPhase)
    }

    @Test
    fun `PreBattleRules calls startBattle via DetermineFirstTurn`() {
        val state = TestFixtures.defaultGameState()
        assertEquals(0, state.currentRound) // Pre-battle

        // DetermineFirstTurn calls startBattle
        SetupPhase.DetermineFirstTurn.processInput("1", state)

        assertEquals(1, state.currentRound)
        assertEquals(1, state.activePlayerNumber)
    }
}
