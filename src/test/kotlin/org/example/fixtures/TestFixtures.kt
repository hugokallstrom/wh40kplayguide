package org.example.fixtures

import org.example.game.BattleSize
import org.example.game.GameState
import org.example.mission.Mission
import org.example.mission.MissionType
import org.example.phase.SetupPhase

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
        firstPlayerNumber = 1,
        primaryMission = samplePrimaryMission(),
        attackerSecondaryMission = sampleSecondaryMission(),
        defenderSecondaryMission = sampleSecondaryMission()
    )

    /**
     * Returns a GameState near end of game (round 5, player 2).
     */
    fun gameStateAtRound5Player2(): GameState = GameState(
        currentRound = 5,
        activePlayerNumber = 2,
        firstPlayerNumber = 1,
        primaryMission = samplePrimaryMission()
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

    // ========== Mission Factories ==========

    /**
     * Returns a sample primary mission.
     */
    fun samplePrimaryMission(): Mission = Mission(
        name = "Take and Hold",
        type = MissionType.PRIMARY,
        fullText = """
            Control objective markers to score VP.
            At the end of your Command phase (from the second battle round onwards),
            you score VP for each objective marker you control:
            - Control 1 objective: 1VP
            - Control 2+ objectives: 3VP
        """.trimIndent(),
        hasAction = false
    )

    /**
     * Returns a sample asymmetric primary mission.
     */
    fun sampleAsymmetricMission(): Mission = Mission(
        name = "Supply Drop",
        type = MissionType.PRIMARY_ASYMMETRIC,
        fullText = """
            ASYMMETRIC WAR
            The Attacker must secure supply drops.
            The Defender must hold the line.

            ATTACKER:
            Score 4VP each time you complete the Secure Supplies action.

            (ACTION) SECURE SUPPLIES
            One Infantry unit can start this action at the end of your Shooting phase
            if it is within range of an objective marker.

            DEFENDER:
            Score 4VP at the end of your turn if you control 2+ objective markers.
        """.trimIndent(),
        hasAction = true
    )

    /**
     * Returns a sample secondary mission.
     */
    fun sampleSecondaryMission(): Mission = Mission(
        name = "Engage on All Fronts",
        type = MissionType.SECONDARY,
        fullText = """
            At the end of your turn, you score VP for having units wholly within
            different table quarters:
            - 2 quarters: 2VP
            - 3 quarters: 3VP
            - 4 quarters: 4VP
        """.trimIndent(),
        hasAction = false
    )

    /**
     * Returns a sample fixed secondary mission.
     */
    fun sampleFixedSecondaryMission(): Mission = Mission(
        name = "Assassination",
        type = MissionType.SECONDARY_FIXED,
        fullText = """
            FIXED - cannot be changed during the battle.
            Each time an enemy CHARACTER model is destroyed, score 4VP.
            If the enemy WARLORD is destroyed, score 1 additional VP.
        """.trimIndent(),
        hasAction = false
    )

    /**
     * Returns a mission with an action.
     */
    fun missionWithAction(): Mission = Mission(
        name = "Cleanse",
        type = MissionType.PRIMARY,
        fullText = """
            At the end of your Command phase, score VP for objectives controlled.

            (ACTION) CLEANSE
            One unit can start this action at the end of your Shooting phase.
            The action is completed at the end of your turn.
        """.trimIndent(),
        hasAction = true
    )

    // ========== File Content Samples ==========

    /**
     * Returns sample primary mission file content for parsing tests.
     */
    fun samplePrimaryMissionFileContent(): String = """
PRIMARY MISSION
Take and Hold

Control objective markers to score VP.
At the end of your Command phase, score:
- 1 objective: 1VP
- 2+ objectives: 3VP

PRIMARY MISSION - ASYMMETRIC WAR
Supply Drop

ASYMMETRIC WAR
The Attacker secures supplies.

(ACTION) SECURE SUPPLIES
Infantry unit can perform this action.

PRIMARY MISSION
Purge the Foe

At the end of the battle round, score VP based on enemy units destroyed.
    """.trimIndent()

    /**
     * Returns sample secondary mission file content for parsing tests.
     */
    fun sampleSecondaryMissionFileContent(): String = """
SECONDARY MISSION
Engage on All Fronts

Score VP for having units in different table quarters.
- 2 quarters: 2VP
- 3 quarters: 3VP
- 4 quarters: 4VP

==========

FIXED - SECONDARY MISSION
Assassination

Each time an enemy CHARACTER is destroyed, score 4VP.

==========

SECONDARY MISSION
Behind Enemy Lines

Score VP for having units in the enemy deployment zone.
    """.trimIndent()

    /**
     * Returns empty mission content for edge case testing.
     */
    fun emptyMissionContent(): String = ""

    /**
     * Returns malformed mission content for error handling tests.
     */
    fun malformedMissionContent(): String = """
Some random text without proper headers.
This should not parse into any missions.
    """.trimIndent()

    // ========== Setup Helpers ==========

    /**
     * Sets up available missions on SetupPhase objects for testing.
     * Call this before testing phases that require mission lists.
     */
    fun setupAvailableMissions() {
        val primaryMissions = listOf(
            samplePrimaryMission(),
            sampleAsymmetricMission()
        )
        val secondaryMissions = listOf(
            sampleSecondaryMission(),
            sampleFixedSecondaryMission()
        )

        SetupPhase.ReadMissionObjectives.availableMissions = primaryMissions
        SetupPhase.SelectAttackerSecondary.availableMissions = secondaryMissions
        SetupPhase.SelectDefenderSecondary.availableMissions = secondaryMissions
    }

    /**
     * Clears available missions after tests.
     */
    fun clearAvailableMissions() {
        SetupPhase.ReadMissionObjectives.availableMissions = emptyList()
        SetupPhase.SelectAttackerSecondary.availableMissions = emptyList()
        SetupPhase.SelectDefenderSecondary.availableMissions = emptyList()
    }
}
