package org.example.phase

import org.example.game.GameState

/**
 * Command Phase - Both players gain CP, Battle-shock tests
 */
data object CommandPhase : BattlePhaseMarker {
    override val phaseName = "COMMAND PHASE"

    override fun displayGuidance(state: GameState): String = buildString {
        appendLine("1. COMMAND")
        appendLine("   Both players gain 1 CP (track on your roster)")
        appendLine("   Resolve any other Command phase rules")
        appendLine()
        appendLine("2. BATTLE-SHOCK TESTS")
        appendLine("   Test each unit that is Below Half-strength")
        appendLine("   Roll 2D6: pass if >= unit's best Leadership")
        appendLine()
        appendLine("   Failed units are Battle-shocked until your next Command phase:")
        appendLine("     - OC becomes 0")
        appendLine("     - Cannot be affected by your Stratagems")
        appendLine("     - Must take Desperate Escape tests if Falling Back")
    }

    override fun nextPhase(state: GameState): Phase {
        // VP scoring only from round 2 onwards
        return if (state.currentRound >= 2) VPScoringPhase else MovementPhase
    }
}

/**
 * VP Scoring Phase - Check mission objectives (from round 2)
 */
data object VPScoringPhase : BattlePhaseMarker {
    override val phaseName = "VP SCORING"

    override fun displayGuidance(state: GameState): String = buildString {
        appendLine("Check VP scoring for your selected missions!")
        appendLine()

        // Primary Mission
        state.primaryMission?.let { mission ->
            appendLine("═══ PRIMARY: ${mission.name.uppercase()} ═══")
            appendLine()
            val vpLines = mission.fullText.lines()
                .filter { line ->
                    line.contains("VP", ignoreCase = false) ||
                    line.contains("score", ignoreCase = true) ||
                    line.contains("control", ignoreCase = true) ||
                    line.contains("objective", ignoreCase = true)
                }
                .take(10)
            vpLines.forEach { appendLine("  $it") }
            appendLine()
        } ?: appendLine("No primary mission selected!")

        // Attacker Secondary Mission
        state.attackerSecondaryMission?.let { mission ->
            appendLine("═══ ATTACKER SECONDARY: ${mission.name.uppercase()} ═══")
            appendLine("(Player ${state.attackerPlayerNumber})")
            appendLine()
            val vpLines = mission.fullText.lines()
                .filter { line ->
                    line.contains("VP", ignoreCase = false) ||
                    line.contains("score", ignoreCase = true)
                }
                .take(6)
            vpLines.forEach { appendLine("  $it") }
            appendLine()
        }

        // Defender Secondary Mission
        state.defenderSecondaryMission?.let { mission ->
            appendLine("═══ DEFENDER SECONDARY: ${mission.name.uppercase()} ═══")
            appendLine("(Player ${state.defenderPlayerNumber})")
            appendLine()
            val vpLines = mission.fullText.lines()
                .filter { line ->
                    line.contains("VP", ignoreCase = false) ||
                    line.contains("score", ignoreCase = true)
                }
                .take(6)
            vpLines.forEach { appendLine("  $it") }
            appendLine()
        }

        appendLine("Reminder:")
        appendLine("  Control = your total OC within 3\" exceeds opponent's total OC")
        appendLine("  OC is 0 for Battle-shocked units")

        // Special note for round 5
        if (state.currentRound == 5) {
            appendLine()
            appendLine("ROUND 5 NOTE: If going second, score VP at end of your turn instead!")
        }
    }

    override fun nextPhase(state: GameState): Phase = MovementPhase
}

/**
 * Movement Phase - Move units, Reinforcements
 */
data object MovementPhase : BattlePhaseMarker {
    override val phaseName = "MOVEMENT PHASE"

    override fun displayGuidance(state: GameState): String = buildString {
        appendLine("For each unit, choose one:")
        appendLine()
        appendLine("  REMAIN STATIONARY - No movement (Heavy weapons get +1 to Hit)")
        appendLine("  NORMAL MOVE - Move up to M\"")
        appendLine("  ADVANCE - Move up to M+D6\" (cannot shoot or charge this turn)")
        appendLine("  FALL BACK - Move up to M\" out of Engagement Range (cannot shoot or charge)")
        appendLine()
        appendLine("Movement Rules:")
        appendLine("  Unit Coherency: 2\" horizontal, 5\" vertical (7+ models need 2 connections)")
        appendLine("  Cannot move within Engagement Range of enemies (1\" horiz, 5\" vert)")
        appendLine("  Terrain 2\" or less can be moved over freely")
        appendLine()
        appendLine("REINFORCEMENTS:")
        appendLine("  Set up any Reserves units now")
        appendLine("  Deep Strike: More than 9\" from all enemies")
        appendLine("  Strategic Reserves:")

        if (state.currentRound == 1) {
            appendLine("    Round 1: Cannot arrive yet")
        } else if (state.currentRound == 2) {
            appendLine("    Round 2: Wholly within 6\" of any edge (NOT enemy deployment zone)")
        } else {
            appendLine("    Round 3+: Wholly within 6\" of any battlefield edge")
        }
        appendLine("    Always: More than 9\" from all enemies")
    }

    override fun nextPhase(state: GameState): Phase = ShootingPhase
}

/**
 * Shooting Phase - Ranged attacks and actions
 */
data object ShootingPhase : BattlePhaseMarker {
    override val phaseName = "SHOOTING PHASE"

    override fun displayGuidance(state: GameState): String = buildString {
        appendLine("For each eligible unit:")
        appendLine()
        appendLine("1. SELECT TARGETS")
        appendLine("   Must be visible and within weapon range")
        appendLine("   Cannot target units in Engagement Range (except PISTOL weapons)")
        appendLine()
        appendLine("2. MAKE RANGED ATTACKS")
        appendLine("   Hit Roll: Roll D6 >= BS (unmodified 6 always hits)")
        appendLine("   Wound Roll: Compare S vs T")
        appendLine("     S >= 2x T: 2+  |  S > T: 3+  |  S = T: 4+  |  S < T: 5+  |  S <= T/2: 6+")
        appendLine("   Save: Roll D6 + AP >= Sv (invuln saves ignore AP)")
        appendLine("   Damage: Reduce Wounds, excess does not carry over (except Mortal Wounds)")
        appendLine()
        appendLine("BIG GUNS NEVER TIRE:")
        appendLine("  Monsters/Vehicles in Engagement Range CAN shoot")
        appendLine("  -1 to Hit with ranged attacks (except PISTOL)")
        appendLine("  Can only target units they're in Engagement Range of (or PISTOL)")

        // Check for mission actions
        state.primaryMission?.let { mission ->
            if (mission.hasAction) {
                appendLine()
                appendLine("MISSION ACTIONS (start in Shooting phase):")
                val actionLines = mission.fullText.lines()
                    .dropWhile { !it.contains("ACTION", ignoreCase = true) }
                    .takeWhile { !it.contains("SECOND BATTLE ROUND", ignoreCase = true) && !it.contains("ANY BATTLE ROUND", ignoreCase = true) }
                    .take(8)
                actionLines.forEach { appendLine("  $it") }
            }
        }
    }

    override fun nextPhase(state: GameState): Phase = ChargePhase
}

/**
 * Charge Phase - Declare and make charges
 */
data object ChargePhase : BattlePhaseMarker {
    override val phaseName = "CHARGE PHASE"

    override fun displayGuidance(state: GameState): String = buildString {
        appendLine("1. SELECT UNIT TO CHARGE")
        appendLine("   Must be within 12\" of at least one enemy")
        appendLine("   Cannot be in Engagement Range already")
        appendLine("   Cannot have Advanced or Fell Back this turn")
        appendLine()
        appendLine("2. SELECT CHARGE TARGETS")
        appendLine("   Must be within 12\" of charging unit")
        appendLine("   Can select multiple targets")
        appendLine()
        appendLine("3. ENEMY REACTS (FIRE OVERWATCH)")
        appendLine("   Opponent may use Fire Overwatch Stratagem (1 CP)")
        appendLine("   Requires 6s to hit, can only use once per turn")
        appendLine()
        appendLine("4. MAKE CHARGE ROLL")
        appendLine("   Roll 2D6 = maximum charge distance")
        appendLine("   SUCCESS: End move within Engagement Range of ALL targets")
        appendLine("   FAILURE: Unit does not move")
        appendLine()
        appendLine("CHARGE BONUS:")
        appendLine("  Successful chargers fight first in Fight phase")
    }

    override fun nextPhase(state: GameState): Phase = FightPhase
}

/**
 * Fight Phase - Melee combat
 */
data object FightPhase : BattlePhaseMarker {
    override val phaseName = "FIGHT PHASE"

    override fun displayGuidance(state: GameState): String = buildString {
        appendLine("FIGHT ORDER:")
        appendLine()
        appendLine("1. FIGHTS FIRST STEP")
        appendLine("   Units that charged this turn")
        appendLine("   Units with Fights First ability")
        appendLine("   Alternate selecting units (starting with player whose turn it is)")
        appendLine()
        appendLine("2. REMAINING COMBATS")
        appendLine("   All other eligible units")
        appendLine("   Alternate selecting (starting with player whose turn it is NOT)")
        appendLine()
        appendLine("FOR EACH FIGHTING UNIT:")
        appendLine()
        appendLine("  A. PILE IN (3\")")
        appendLine("     Move each model up to 3\" closer to nearest enemy")
        appendLine("     Must end in Unit Coherency")
        appendLine()
        appendLine("  B. MAKE MELEE ATTACKS")
        appendLine("     Must be within Engagement Range OR in base contact with")
        appendLine("     friendly model that's in base contact with enemy")
        appendLine("     Same attack sequence as shooting (Hit -> Wound -> Save -> Damage)")
        appendLine()
        appendLine("  C. CONSOLIDATE (3\")")
        appendLine("     Move up to 3\" closer to nearest enemy")
        appendLine("     Must end within Engagement Range of an enemy if possible")
        appendLine("     Or move toward closest objective if cannot reach enemy")
    }

    override fun nextPhase(state: GameState): Phase = EndOfTurnPhase
}

/**
 * End of Turn Phase - Check end-of-turn scoring, advance to next turn
 */
data object EndOfTurnPhase : BattlePhaseMarker {
    override val phaseName = "END OF TURN"

    override fun displayGuidance(state: GameState): String = buildString {
        appendLine("Player ${state.activePlayerNumber}'s turn is ending.")
        appendLine()

        // Check for end-of-turn VP scoring
        state.primaryMission?.let { mission ->
            val hasEndOfTurnScoring = mission.fullText.contains("End of your turn", ignoreCase = true) ||
                    mission.fullText.contains("End of the turn", ignoreCase = true) ||
                    mission.fullText.contains("End of either player's turn", ignoreCase = true)

            if (hasEndOfTurnScoring) {
                appendLine("CHECK END-OF-TURN VP SCORING:")
                val turnLines = mission.fullText.lines()
                    .filter { it.contains("end of", ignoreCase = true) && it.contains("turn", ignoreCase = true) }
                    .take(5)
                turnLines.forEach { appendLine("  $it") }
                appendLine()
            }
        }

        appendLine("Complete any end-of-turn effects and proceed to next turn.")
    }

    override fun nextPhase(state: GameState): Phase {
        val newRoundStarted = state.advanceToNextTurn()

        if (state.isGameOver()) {
            return EndGamePhase
        }

        if (newRoundStarted) {
            return EndOfRoundPhase(state.currentRound - 1)
        }

        return CommandPhase
    }
}

/**
 * End of Round Phase - Check end-of-round scoring
 */
data class EndOfRoundPhase(val completedRound: Int) : BattlePhaseMarker {
    override val phaseName = "END OF ROUND $completedRound"

    override fun displayGuidance(state: GameState): String = buildString {
        appendLine("Battle Round $completedRound complete!")
        appendLine()

        // Check for end-of-round VP scoring (e.g., Purge the Foe)
        state.primaryMission?.let { mission ->
            val hasEndOfRoundScoring = mission.fullText.contains("End of the battle round", ignoreCase = true)

            if (hasEndOfRoundScoring) {
                appendLine("CHECK END-OF-ROUND VP SCORING:")
                val roundLines = mission.fullText.lines()
                    .filter { it.contains("battle round", ignoreCase = true) }
                    .take(5)
                roundLines.forEach { appendLine("  $it") }
                appendLine()
            }
        }

        if (completedRound < 5) {
            appendLine("Proceeding to Battle Round ${completedRound + 1}...")
        }
    }

    override fun nextPhase(state: GameState): Phase {
        return if (state.isGameOver()) EndGamePhase else CommandPhase
    }
}

/**
 * End Game Phase - Final scoring and victory determination
 */
data object EndGamePhase : BattlePhaseMarker {
    override val phaseName = "END OF GAME"

    override fun displayGuidance(state: GameState): String = buildString {
        appendLine("THE BATTLE HAS ENDED!")
        appendLine()
        appendLine("Final VP Tallying:")
        appendLine("  1. Count all VP scored during the game")
        appendLine("  2. Check for any end-of-game bonuses")
        appendLine()

        state.primaryMission?.let { mission ->
            appendLine("Mission: ${mission.name}")
            appendLine()
        }

        appendLine("DETERMINE VICTOR:")
        appendLine("  If one army was destroyed: Their opponent wins")
        appendLine("  Otherwise: Player with most VP wins")
        appendLine("  Tie: The game is a draw")
        appendLine()
        appendLine("Thank you for playing!")
    }

    override fun nextPhase(state: GameState): Phase = EndGamePhase // Terminal state
}
