package org.example.phase

import org.example.game.BattleSize
import org.example.game.GameState
import org.example.game.SecondaryMissionType
import org.example.guidance.GuidanceContent

/**
 * Setup phases that occur before the battle begins.
 */
sealed class SetupPhase : SetupPhaseMarker {

    /**
     * Step 1: Muster Armies - Select battle size
     */
    data object MusterArmies : SetupPhase() {
        override val phaseName = "Muster Armies"

        override fun displayGuidance(state: GameState): String = buildString {
            appendLine("Select battle size:")
            appendLine()
            appendLine("1. Incursion  (1000 pts, battlefield 44\" x 60\", ~2 hours)")
            appendLine("2. Strike Force (2000 pts, battlefield 44\" x 60\", ~3 hours)")
            appendLine("3. Onslaught  (3000 pts, battlefield 44\" x 90\", ~4 hours)")
        }

        override fun displayStructuredGuidance(state: GameState): List<GuidanceContent> = buildList {
            add(
                GuidanceContent.Table(
                    headers = listOf("Size", "Points", "Battlefield"),
                    rows = listOf(
                        listOf("Incursion", "1000 pts", "44\" x 60\""),
                        listOf("Strike Force", "2000 pts", "44\" x 60\""),
                        listOf("Onslaught", "3000 pts", "44\" x 90\"")
                    )
                )
            )
        }

        override fun requiresInput(): Boolean = true

        override fun processInput(input: String, state: GameState): Phase? {
            val size = when (input.trim()) {
                "1" -> BattleSize.INCURSION
                "2" -> BattleSize.STRIKE_FORCE
                "3" -> BattleSize.ONSLAUGHT
                else -> return null
            }
            state.battleSize = size
            return DrawPrimaryMission
        }

        override fun nextPhase(state: GameState): Phase = DrawPrimaryMission
    }

    /**
     * Step 2: Draw Primary Mission
     */
    data object DrawPrimaryMission : SetupPhase() {
        override val phaseName = "Draw Primary Mission"

        override fun displayGuidance(state: GameState): String = buildString {
            appendLine("Draw a card from the Primary Mission deck and place it face-up.")
            appendLine()
            appendLine("Read the mission rules and set up any mission-specific objective markers.")
        }

        override fun displayStructuredGuidance(state: GameState): List<GuidanceContent> = buildList {
            add(GuidanceContent.Paragraph("Draw a card from the Primary Mission deck and place it face-up."))
            add(GuidanceContent.Paragraph("Read the mission rules and set up any mission-specific objective markers."))
        }

        override fun nextPhase(state: GameState): Phase = CreateBattlefield
    }

    /**
     * Step 3: Create the Battlefield
     */
    data object CreateBattlefield : SetupPhase() {
        override val phaseName = "Create the Battlefield"

        override fun displayGuidance(state: GameState): String = buildString {
            appendLine(
                "Battlefield size for ${
                    state.battleSize.name.replace(
                        "_",
                        " "
                    )
                }: ${state.battleSize.battlefieldSize}"
            )
            appendLine()
            appendLine("Set up terrain features and objective markers according to mission rules.")
        }

        override fun displayStructuredGuidance(state: GameState): List<GuidanceContent> = buildList {
            add(
                GuidanceContent.KeyValue(
                    listOf(
                        "Battle Size" to state.battleSize.name.replace("_", " "),
                        "Battlefield" to state.battleSize.battlefieldSize
                    )
                )
            )

            add(GuidanceContent.Paragraph("Set up terrain features and objective markers according to mission rules."))

            add(
                GuidanceContent.InfoBox(
                    title = "Terrain Guidelines",
                    content = listOf(
                        GuidanceContent.BulletList(
                            listOf(
                                "Ruins block line of sight",
                                "Woods: Units wholly within are never fully visible",
                                "Benefit of Cover: +1 to armor saves vs ranged"
                            )
                        )
                    ),
                    variant = GuidanceContent.BoxVariant.REMINDER
                )
            )
        }

        override fun nextPhase(state: GameState): Phase = DetermineAttacker
    }

    /**
     * Step 4: Determine Attacker and Defender
     */
    data object DetermineAttacker : SetupPhase() {
        override val phaseName = "Determine Attacker & Defender"

        override fun displayGuidance(state: GameState): String = buildString {
            appendLine("Both players roll off (D6).")
            appendLine()
            appendLine("  Winner is the Attacker")
            appendLine("  Loser is the Defender")
            appendLine()
            appendLine("Who won the roll-off?")
            appendLine("1. Player 1 is the Attacker")
            appendLine("2. Player 2 is the Attacker")
        }

        override fun displayStructuredGuidance(state: GameState): List<GuidanceContent> = buildList {
            add(GuidanceContent.Paragraph("Both players roll off (D6)."))

            add(
                GuidanceContent.KeyValue(
                    listOf(
                        "Winner" to "becomes the Attacker",
                        "Loser" to "becomes the Defender"
                    )
                )
            )
        }

        override fun requiresInput(): Boolean = true

        override fun processInput(input: String, state: GameState): Phase? {
            val attacker = when (input.trim()) {
                "1" -> 1
                "2" -> 2
                else -> return null
            }
            state.attackerPlayerNumber = attacker
            return DrawAttackerSecondary
        }

        override fun nextPhase(state: GameState): Phase = DrawAttackerSecondary
    }

    /**
     * Step 5: Attacker Selects Secondary Mission
     */
    data object DrawAttackerSecondary : SetupPhase() {
        override val phaseName = "Attacker Secondary Mission"

        override fun displayGuidance(state: GameState): String = buildString {
            appendLine("Attacker (Player ${state.attackerPlayerNumber}):")
            appendLine()
            appendLine("Select your secondary mission type:")
            appendLine()
            appendLine("1. Fixed - Choose specific missions that remain the same all game")
            appendLine("2. Tactical - Draw from a deck, replacing missions as you complete them")
        }

        override fun displayStructuredGuidance(state: GameState): List<GuidanceContent> = buildList {
            add(
                GuidanceContent.KeyValue(
                    listOf(
                        "1. Fixed" to "Choose 2 specific missions that stay the same all game (max 20VP each)",
                        "2. Tactical" to "Draw from a deck, complete for 4-5VP each, draw replacements"
                    )
                )
            )
        }

        override fun requiresInput(): Boolean = true

        override fun processInput(input: String, state: GameState): Phase? {
            val type = when (input.trim()) {
                "1" -> SecondaryMissionType.FIXED
                "2" -> SecondaryMissionType.TACTICAL
                else -> return null
            }
            state.setSecondaryTypeForPlayer(state.attackerPlayerNumber, type)
            return DrawDefenderSecondary
        }

        override fun nextPhase(state: GameState): Phase = DrawDefenderSecondary
    }

    /**
     * Step 6: Defender Selects Secondary Mission
     */
    data object DrawDefenderSecondary : SetupPhase() {
        override val phaseName = "Defender Secondary Mission"

        override fun displayGuidance(state: GameState): String = buildString {
            appendLine("Defender (Player ${state.defenderPlayerNumber}):")
            appendLine()
            appendLine("Select your secondary mission type:")
            appendLine()
            appendLine("1. Fixed - Choose specific missions that remain the same all game")
            appendLine("2. Tactical - Draw from a deck, replacing missions as you complete them")
        }

        override fun displayStructuredGuidance(state: GameState): List<GuidanceContent> = buildList {
            add(
                GuidanceContent.KeyValue(
                    listOf(
                        "1. Fixed" to "Choose 2 specific missions that stay the same all game (max 20VP each)",
                        "2. Tactical" to "Draw from a deck, complete for 4-5VP each, draw replacements"
                    )
                )
            )
        }

        override fun requiresInput(): Boolean = true

        override fun processInput(input: String, state: GameState): Phase? {
            val type = when (input.trim()) {
                "1" -> SecondaryMissionType.FIXED
                "2" -> SecondaryMissionType.TACTICAL
                else -> return null
            }
            state.setSecondaryTypeForPlayer(state.defenderPlayerNumber, type)
            return DeclareBattleFormations
        }

        override fun nextPhase(state: GameState): Phase = DeclareBattleFormations
    }

    /**
     * Step 7: Declare Battle Formations
     */
    data object DeclareBattleFormations : SetupPhase() {
        override val phaseName = "Declare Battle Formations"

        override fun displayGuidance(state: GameState): String = buildString {
            appendLine("Both players now secretly note down:")
            appendLine()
            appendLine("1. Attached Leaders")
            appendLine("   Which Leader units will start attached to which Bodyguard units")
            appendLine()
            appendLine("2. EMBARKED UNITS")
            appendLine("   Which units will start embarked within Transport models")
            appendLine()
            appendLine("3. RESERVES")
            appendLine("   Which units will start in Reserves (including Strategic Reserves)")
            appendLine("   - Strategic Reserves: Max 25% of army points")
            appendLine("   - Deep Strike units can be set up in Reserves")
            appendLine()
            appendLine("When both players are ready, declare your selections to your opponent.")
        }

        override fun displayStructuredGuidance(state: GameState): List<GuidanceContent> = buildList {
            add(GuidanceContent.Paragraph("Both players now secretly note down:"))

            add(
                GuidanceContent.NumberedList(
                    listOf(
                        "Attached Leaders - Which Leader units will start attached to which Bodyguard units",
                        "Embarked Units - Which units will start embarked within Transport models",
                        "Reserves - Which units will start in Reserves (arrive from Round 2)"
                    )
                )
            )

            add(
                GuidanceContent.InfoBox(
                    title = "Reserves Quick Reference",
                    content = listOf(
                        GuidanceContent.BulletList(
                            listOf(
                                "Strategic Reserves: Max 25% of army points",
                                "Deep Strike units can also be placed in Reserves",
                                "All reserves arrive from Round 2 onwards"
                            )
                        )
                    ),
                    variant = GuidanceContent.BoxVariant.INFO
                )
            )

            add(GuidanceContent.Paragraph("When both players are ready, declare your selections to your opponent."))
        }

        override fun nextPhase(state: GameState): Phase = DeployArmies
    }

    /**
     * Step 6: Deploy Armies
     */
    data object DeployArmies : SetupPhase() {
        override val phaseName = "Deploy Armies"

        override fun displayGuidance(state: GameState): String = buildString {
            val attackerPlayer = state.attackerPlayerNumber
            val defenderPlayer = state.defenderPlayerNumber

            appendLine("Players alternate deploying units, one at a time.")
            appendLine()
            appendLine("Deployment order:")
            appendLine("  1. Player $attackerPlayer (ATTACKER) deploys first")
            appendLine("  2. Player $defenderPlayer (DEFENDER) deploys next")
            appendLine("  3. Continue alternating...")
            appendLine()
            appendLine("Deployment rules:")
            appendLine("  Models must be set up wholly within their deployment zone")
            appendLine("  Continue until all units are deployed (or no room remains)")
            appendLine()
            appendLine("Infiltrators:")
            appendLine("  After all other units deployed, roll off")
            appendLine("  Winner alternates setting up Infiltrators units")
            appendLine("  Infiltrators: Set up anywhere 9\"+ from enemy deployment zone and models")
        }

        override fun displayStructuredGuidance(state: GameState): List<GuidanceContent> = buildList {
            add(GuidanceContent.Paragraph("Players alternate deploying units, one at a time."))
            add(
                GuidanceContent.NumberedList(
                    listOf(
                        "Player ${state.attackerPlayerNumber} (Attacker) deploys first",
                        "Player ${state.defenderPlayerNumber} (Defender) deploys next",
                        "Continue alternating until all units are deployed"
                    )
                )
            )

            add(
                GuidanceContent.InfoBox(
                    title = "Deployment Rules",
                    content = listOf(
                        GuidanceContent.BulletList(
                            listOf(
                                "Models must be set up wholly within their deployment zone",
                                "Continue until all units are deployed (or no room remains)"
                            )
                        )
                    ),
                    variant = GuidanceContent.BoxVariant.REMINDER
                )
            )

            add(
                GuidanceContent.InfoBox(
                    title = "Infiltrators",
                    content = listOf(
                        GuidanceContent.Paragraph("After all other units deployed:"),
                        GuidanceContent.BulletList(
                            listOf(
                                "Roll off - winner alternates setting up Infiltrators",
                                "Set up anywhere 9\"+ from enemy deployment zone and models"
                            )
                        )
                    ),
                    variant = GuidanceContent.BoxVariant.INFO
                )
            )
        }

        override fun nextPhase(state: GameState): Phase = PreBattleRules
    }

    /**
     * Step 7: Resolve Pre-Battle Rules
     */
    data object PreBattleRules : SetupPhase() {
        override val phaseName = "Resolve Pre-battle Rules"

        override fun displayGuidance(state: GameState): String = buildString {
            appendLine("Players alternate resolving any pre-battle rules,")
            appendLine("starting with the player who will take the first turn.")
            appendLine()
            appendLine("Common pre-battle rules:")
            appendLine()
            appendLine("Scouts X\":")
            appendLine("  Before the first turn, unit can make a Normal move up to X\"")
            appendLine("  Must end 9\"+ from all enemy models")
            appendLine("  Dedicated Transports with only Scouts-equipped models can also Scout")
            appendLine()
            appendLine("Note: If both players have Scouts units, the player going first moves theirs first.")
        }

        override fun displayStructuredGuidance(state: GameState): List<GuidanceContent> = buildList {
            add(GuidanceContent.Paragraph("Players alternate resolving any pre-battle rules, starting with the player who will take the first turn."))

            add(
                GuidanceContent.InfoBox(
                    title = "Scouts X\"",
                    content = listOf(
                        GuidanceContent.Paragraph("Before the first turn, unit can make a Normal move up to X\":"),
                        GuidanceContent.BulletList(
                            listOf(
                                "Must end 9\"+ from all enemy models",
                                "Dedicated Transports with only Scouts-equipped models can also Scout"
                            )
                        )
                    ),
                    variant = GuidanceContent.BoxVariant.INFO
                )
            )

            add(
                GuidanceContent.InfoBox(
                    title = "Note",
                    content = listOf(
                        GuidanceContent.Paragraph("If both players have Scouts units, the player going first moves theirs first.")
                    ),
                    variant = GuidanceContent.BoxVariant.REMINDER
                )
            )
        }

        override fun nextPhase(state: GameState): Phase = DetermineFirstTurn
    }

    /**
     * Step 8: Determine First Turn
     */
    data object DetermineFirstTurn : SetupPhase() {
        override val phaseName = "Determine First Turn"

        override fun displayGuidance(state: GameState): String = buildString {
            appendLine("Players roll off to determine who takes the first turn.")
            appendLine()
            appendLine("Who won the roll-off?")
            appendLine("1. Player 1 goes first")
            appendLine("2. Player 2 goes first")
        }

        override fun displayStructuredGuidance(state: GameState): List<GuidanceContent> = buildList {
            add(GuidanceContent.Paragraph("Players roll off to determine who takes the first turn."))
        }

        override fun requiresInput(): Boolean = true

        override fun processInput(input: String, state: GameState): Phase? {
            val firstPlayer = when (input.trim()) {
                "1" -> 1
                "2" -> 2
                else -> return null
            }
            state.firstPlayerNumber = firstPlayer
            state.startBattle()
            return CommandPhase
        }

        override fun nextPhase(state: GameState): Phase = CommandPhase
    }
}
