package org.example.phase

import org.example.game.GameState
import org.example.guidance.GuidanceContent

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

    override fun displayStructuredGuidance(state: GameState): List<GuidanceContent> = buildList {
        add(GuidanceContent.NumberedList(listOf(
            "COMMAND - Both players gain 1 CP (track on your roster). Resolve any other Command phase rules.",
            "BATTLE-SHOCK TESTS - Test each unit that is Below Half-strength. Roll 2D6: pass if >= unit's best Leadership."
        )))

        add(GuidanceContent.InfoBox(
            title = "Battle-shocked Units",
            content = listOf(
                GuidanceContent.Paragraph("Failed units are Battle-shocked until your next Command phase:"),
                GuidanceContent.BulletList(listOf(
                    "OC becomes 0",
                    "Cannot be affected by your Stratagems",
                    "Must take Desperate Escape tests if Falling Back"
                ))
            ),
            variant = GuidanceContent.BoxVariant.WARNING
        ))
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

        // Reinforcements section - only show from round 2+
        if (state.currentRound >= 2) {
            appendLine()
            appendLine("REINFORCEMENTS:")
            appendLine("  Set up any Reserves units now")
            appendLine("  Deep Strike: More than 9\" from all enemies")
            appendLine("  Strategic Reserves:")

            if (state.currentRound == 2) {
                appendLine("    Wholly within 6\" of any edge (NOT enemy deployment zone)")
            } else {
                appendLine("    Wholly within 6\" of any battlefield edge")
            }
            appendLine("    Always: More than 9\" from all enemies")
        }
    }

    override fun displayStructuredGuidance(state: GameState): List<GuidanceContent> = buildList {
        add(GuidanceContent.KeyValue(listOf(
            "REMAIN STATIONARY" to "No movement (Heavy weapons get +1 to Hit)",
            "NORMAL MOVE" to "Move up to M\"",
            "ADVANCE" to "Move up to M+D6\" (cannot shoot or charge this turn)",
            "FALL BACK" to "Move up to M\" out of Engagement Range (cannot shoot or charge)"
        )))

        add(GuidanceContent.InfoBox(
            title = "Movement Rules",
            content = listOf(
                GuidanceContent.BulletList(listOf(
                    "Unit Coherency: 2\" horizontal, 5\" vertical (7+ models need 2 connections)",
                    "Cannot move within Engagement Range of enemies (1\" horiz, 5\" vert)",
                    "Terrain 2\" or less can be moved over freely"
                ))
            ),
            variant = GuidanceContent.BoxVariant.REMINDER
        ))

        // Reinforcements section - only show detailed rules from round 2+
        if (state.currentRound >= 2) {
            add(GuidanceContent.Header("Reinforcements", 2))
            add(GuidanceContent.Paragraph("Set up any Reserves units now"))

            add(GuidanceContent.KeyValue(listOf(
                "Deep Strike" to "More than 9\" from all enemies"
            )))

            val reserveRules = if (state.currentRound == 2) {
                "Wholly within 6\" of any edge (NOT enemy deployment zone)"
            } else {
                "Wholly within 6\" of any battlefield edge"
            }

            add(GuidanceContent.InfoBox(
                title = "Strategic Reserves",
                content = listOf(
                    GuidanceContent.Paragraph(reserveRules),
                    GuidanceContent.Paragraph("Always: More than 9\" from all enemies")
                ),
                variant = GuidanceContent.BoxVariant.INFO
            ))
        }
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
        appendLine()
        appendLine("MISSION ACTIONS:")
        appendLine("  Check your mission cards for any actions that can be performed")
        appendLine("  during the Shooting phase.")
    }

    override fun displayStructuredGuidance(state: GameState): List<GuidanceContent> = buildList {
        add(GuidanceContent.NumberedList(listOf(
            "SELECT TARGETS - Must be visible and within weapon range. Cannot target units in Engagement Range (except PISTOL weapons).",
            "MAKE RANGED ATTACKS - Follow attack sequence: Hit Roll → Wound Roll → Save → Damage"
        )))

        add(GuidanceContent.Header("Attack Sequence", 2))
        add(GuidanceContent.KeyValue(listOf(
            "Hit Roll" to "Roll D6 >= BS (unmodified 6 always hits)",
            "Wound Roll" to "Compare S vs T (see table below)",
            "Save" to "Roll D6 + AP >= Sv (invuln saves ignore AP)",
            "Damage" to "Reduce Wounds, excess does not carry over"
        )))

        add(GuidanceContent.Table(
            headers = listOf("S vs T", "Wound Roll"),
            rows = listOf(
                listOf("S >= 2x T", "2+"),
                listOf("S > T", "3+"),
                listOf("S = T", "4+"),
                listOf("S < T", "5+"),
                listOf("S <= T/2", "6+")
            )
        ))

        add(GuidanceContent.InfoBox(
            title = "Big Guns Never Tire",
            content = listOf(
                GuidanceContent.Paragraph("Monsters/Vehicles in Engagement Range CAN shoot:"),
                GuidanceContent.BulletList(listOf(
                    "-1 to Hit with ranged attacks (except PISTOL)",
                    "Can only target units they're in Engagement Range of (or PISTOL)"
                ))
            ),
            variant = GuidanceContent.BoxVariant.REMINDER
        ))

        add(GuidanceContent.InfoBox(
            title = "Mission Actions",
            content = listOf(
                GuidanceContent.Paragraph("Check your mission cards for any actions that can be performed during the Shooting phase.")
            ),
            variant = GuidanceContent.BoxVariant.INFO
        ))
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

    override fun displayStructuredGuidance(state: GameState): List<GuidanceContent> = buildList {
        add(GuidanceContent.NumberedList(listOf(
            "SELECT UNIT TO CHARGE - Must be within 12\" of at least one enemy. Cannot be in Engagement Range already. Cannot have Advanced or Fell Back this turn.",
            "SELECT CHARGE TARGETS - Must be within 12\" of charging unit. Can select multiple targets.",
            "ENEMY REACTS (FIRE OVERWATCH) - Opponent may use Fire Overwatch Stratagem (1 CP). Requires 6s to hit, can only use once per turn.",
            "MAKE CHARGE ROLL - Roll 2D6 = maximum charge distance."
        )))

        add(GuidanceContent.KeyValue(listOf(
            "SUCCESS" to "End move within Engagement Range of ALL targets",
            "FAILURE" to "Unit does not move"
        )))

        add(GuidanceContent.InfoBox(
            title = "Charge Bonus",
            content = listOf(
                GuidanceContent.Paragraph("Successful chargers fight first in Fight phase!")
            ),
            variant = GuidanceContent.BoxVariant.SUCCESS
        ))
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
        appendLine("   Alternate selecting units (starting with player whose turn it is NOT)")
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

    override fun displayStructuredGuidance(state: GameState): List<GuidanceContent> = buildList {
        add(GuidanceContent.NumberedList(listOf(
            "FIGHTS FIRST STEP - Units that charged this turn and units with Fights First ability. Alternate selecting units (starting with player whose turn it is NOT).",
            "REMAINING COMBATS - All other eligible units. Alternate selecting (starting with player whose turn it is NOT)."
        )))

        add(GuidanceContent.Divider)

        add(GuidanceContent.Header("For Each Fighting Unit", 2))

        add(GuidanceContent.KeyValue(listOf(
            "A. Pile In (3\")" to "Move each model up to 3\" closer to nearest enemy. Must end in Unit Coherency.",
            "B. Make Melee Attacks" to "Must be within Engagement Range OR in base contact with friendly model that's in base contact with enemy.",
            "C. Consolidate (3\")" to "Move up to 3\" closer to nearest enemy. Must end within Engagement Range if possible, or move toward closest objective."
        )))

        add(GuidanceContent.InfoBox(
            title = "Attack Sequence",
            content = listOf(
                GuidanceContent.Paragraph("Same as shooting: Hit → Wound → Save → Damage")
            ),
            variant = GuidanceContent.BoxVariant.REMINDER
        ))
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

        // VP scoring from round 2 onwards
        if (state.currentRound >= 2) {
            appendLine("VP SCORING:")
            appendLine("  Score VP from your Primary and Secondary missions.")
            appendLine("  Refer to your mission cards for scoring conditions.")
            appendLine()
            appendLine("VP Breakdown (max 100VP):")
            appendLine("  Primary Mission: up to 50VP")
            appendLine("  Secondary Missions: up to 40VP")
            appendLine("  Battle Ready bonus: 10VP")
            appendLine("  Challenger cards: up to 12VP (optional)")
            appendLine()
        }

        appendLine("Complete any end-of-turn effects and proceed to next turn.")
    }

    override fun displayStructuredGuidance(state: GameState): List<GuidanceContent> = buildList {
        add(GuidanceContent.Header("Player ${state.activePlayerNumber}'s turn is ending", 1))

        // VP scoring from round 2 onwards
        if (state.currentRound >= 2) {
            add(GuidanceContent.InfoBox(
                title = "VP Scoring",
                content = listOf(
                    GuidanceContent.Paragraph("Score VP from your **Primary** and **Secondary** missions."),
                    GuidanceContent.Paragraph("Refer to your mission cards for scoring conditions.")
                ),
                variant = GuidanceContent.BoxVariant.WARNING
            ))

            add(GuidanceContent.InfoBox(
                title = "VP Breakdown (max 100VP)",
                content = listOf(
                    GuidanceContent.KeyValue(listOf(
                        "Primary Mission" to "up to 50VP",
                        "Secondary Missions" to "up to 40VP",
                        "Battle Ready bonus" to "10VP",
                        "Challenger cards" to "up to 12VP (optional)"
                    ))
                ),
                variant = GuidanceContent.BoxVariant.REMINDER
            ))
        }

        add(GuidanceContent.Paragraph("Complete any end-of-turn effects and proceed to next turn."))
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
        appendLine("CHECK END-OF-ROUND VP SCORING:")
        appendLine("  Review your mission cards for any end-of-round scoring conditions.")
        appendLine()

        if (completedRound < 5) {
            appendLine("Proceeding to Battle Round ${completedRound + 1}...")
        }
    }

    override fun displayStructuredGuidance(state: GameState): List<GuidanceContent> = buildList {
        add(GuidanceContent.Header("Battle Round $completedRound Complete!", 1))

        add(GuidanceContent.InfoBox(
            title = "Check End-of-Round VP Scoring",
            content = listOf(
                GuidanceContent.Paragraph("Review your mission cards for any end-of-round scoring conditions.")
            ),
            variant = GuidanceContent.BoxVariant.WARNING
        ))

        if (completedRound < 5) {
            add(GuidanceContent.InfoBox(
                title = "Next Round",
                content = listOf(
                    GuidanceContent.Paragraph("Proceeding to Battle Round ${completedRound + 1}...")
                ),
                variant = GuidanceContent.BoxVariant.INFO
            ))
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
        appendLine("Final VP Tallying (max 100VP):")
        appendLine("  Primary Mission: up to 50VP")
        appendLine("  Secondary Missions: up to 40VP")
        appendLine("  Battle Ready bonus: 10VP (fully painted army)")
        appendLine("  Challenger cards: up to 12VP (optional)")
        appendLine()
        appendLine("DETERMINE VICTOR:")
        appendLine("  If one army was destroyed: Their opponent wins")
        appendLine("  Otherwise: Player with most VP wins")
        appendLine("  Tie: The game is a draw")
        appendLine()
        appendLine("Thank you for playing!")
    }

    override fun displayStructuredGuidance(state: GameState): List<GuidanceContent> = buildList {
        add(GuidanceContent.Header("THE BATTLE HAS ENDED!", 1))

        add(GuidanceContent.Header("Final VP Tallying", 2))
        add(GuidanceContent.InfoBox(
            title = "VP Breakdown (max 100VP)",
            content = listOf(
                GuidanceContent.KeyValue(listOf(
                    "Primary Mission" to "up to 50VP",
                    "Secondary Missions" to "up to 40VP",
                    "Battle Ready bonus" to "10VP (fully painted army)",
                    "Challenger cards" to "up to 12VP (optional)"
                ))
            ),
            variant = GuidanceContent.BoxVariant.INFO
        ))

        add(GuidanceContent.Header("Determine Victor", 2))
        add(GuidanceContent.BulletList(listOf(
            "If one army was destroyed: Their opponent wins",
            "Otherwise: Player with most VP wins",
            "Tie: The game is a draw"
        )))

        add(GuidanceContent.InfoBox(
            title = "Thank you for playing!",
            content = listOf(
                GuidanceContent.Paragraph("We hope you enjoyed your game of Warhammer 40,000!")
            ),
            variant = GuidanceContent.BoxVariant.SUCCESS
        ))
    }

    override fun nextPhase(state: GameState): Phase = EndGamePhase // Terminal state
}
