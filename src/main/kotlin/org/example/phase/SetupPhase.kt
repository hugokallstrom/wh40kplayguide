package org.example.phase

import org.example.game.BattleSize
import org.example.game.GameState
import org.example.guidance.GuidanceContent
import org.example.mission.Mission

/**
 * Setup phases that occur before the battle begins.
 */
sealed class SetupPhase : SetupPhaseMarker {

    /**
     * Step 1: Muster Armies - Select battle size
     */
    data object MusterArmies : SetupPhase() {
        override val phaseName = "MUSTER ARMIES"

        override fun displayGuidance(state: GameState): String = buildString {
            appendLine("Select battle size:")
            appendLine()
            appendLine("1. Incursion  (1000 pts, battlefield 44\" x 60\", ~2 hours)")
            appendLine("2. Strike Force (2000 pts, battlefield 44\" x 60\", ~3 hours)")
            appendLine("3. Onslaught  (3000 pts, battlefield 44\" x 90\", ~4 hours)")
        }

        override fun displayStructuredGuidance(state: GameState): List<GuidanceContent> = buildList {
            add(GuidanceContent.Table(
                headers = listOf("Size", "Points", "Battlefield", "Duration"),
                rows = listOf(
                    listOf("Incursion", "1000 pts", "44\" x 60\"", "~2 hours"),
                    listOf("Strike Force", "2000 pts", "44\" x 60\"", "~3 hours"),
                    listOf("Onslaught", "3000 pts", "44\" x 90\"", "~4 hours")
                )
            ))

            add(GuidanceContent.InfoBox(
                title = "Army Construction",
                content = listOf(
                    GuidanceContent.BulletList(listOf(
                        "All units must share army Faction keyword",
                        "Max 3 of same datasheet (6 for Battleline/Dedicated Transport)",
                        "Must include at least 1 Character",
                        "Max 3 Enhancements total (Characters only)"
                    ))
                ),
                variant = GuidanceContent.BoxVariant.REMINDER
            ))
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
            return ReadMissionObjectives
        }

        override fun nextPhase(state: GameState): Phase = ReadMissionObjectives
    }

    /**
     * Step 2: Read Mission Objectives - Select primary mission
     */
    data object ReadMissionObjectives : SetupPhase() {
        override val phaseName = "READ MISSION OBJECTIVES"

        // Missions will be passed in via the CLI runner
        var availableMissions: List<Mission> = emptyList()

        override fun displayGuidance(state: GameState): String = buildString {
            appendLine("Select Primary Mission:")
            appendLine()
            availableMissions.forEachIndexed { index, mission ->
                val typeIndicator = when {
                    mission.type.name.contains("ASYMMETRIC") -> " [ASYMMETRIC]"
                    else -> ""
                }
                appendLine("${index + 1}. ${mission.name}$typeIndicator")
            }
        }

        override fun requiresInput(): Boolean = true

        override fun processInput(input: String, state: GameState): Phase? {
            val index = input.trim().toIntOrNull()?.minus(1) ?: return null
            if (index !in availableMissions.indices) return null

            val selectedMission = availableMissions[index]
            state.primaryMission = selectedMission
            return DisplayMissionDetails(selectedMission)
        }
        override fun nextPhase(state: GameState): Phase = CreateBattlefield
    }

    /**
     * Intermediate step: Display the selected mission's full details
     */
    data class DisplayMissionDetails(val mission: Mission) : SetupPhase() {
        override val phaseName = "MISSION: ${mission.name.uppercase()}"

        override fun displayGuidance(state: GameState): String = mission.displayText()

        override fun nextPhase(state: GameState): Phase = CreateBattlefield
    }

    /**
     * Step 3: Create the Battlefield
     */
    data object CreateBattlefield : SetupPhase() {
        override val phaseName = "CREATE THE BATTLEFIELD"

        override fun displayGuidance(state: GameState): String = buildString {
            appendLine("Battlefield size for ${state.battleSize.name.replace("_", " ")}: ${state.battleSize.battlefieldSize}")
            appendLine()
            appendLine("Set up terrain features and objective markers according to mission rules.")
            appendLine()
            state.primaryMission?.let { mission ->
                if (mission.fullText.contains("objective marker", ignoreCase = true)) {
                    appendLine("Mission-specific objective setup:")
                    // Extract objective-related text from mission
                    val objectiveLines = mission.fullText.lines()
                        .filter { it.contains("objective", ignoreCase = true) }
                        .take(5)
                    objectiveLines.forEach { appendLine("  $it") }
                    appendLine()
                }
            }
            appendLine("(Deployment zones and detailed terrain layout handled externally)")
        }

        override fun displayStructuredGuidance(state: GameState): List<GuidanceContent> = buildList {
            add(GuidanceContent.KeyValue(listOf(
                "Battle Size" to state.battleSize.name.replace("_", " "),
                "Battlefield" to state.battleSize.battlefieldSize
            )))

            add(GuidanceContent.Paragraph("Set up terrain features and objective markers according to mission rules."))

            state.primaryMission?.let { mission ->
                if (mission.fullText.contains("objective marker", ignoreCase = true)) {
                    val objectiveLines = mission.fullText.lines()
                        .filter { it.contains("objective", ignoreCase = true) }
                        .take(5)

                    if (objectiveLines.isNotEmpty()) {
                        add(GuidanceContent.InfoBox(
                            title = "Mission Objective Setup",
                            content = listOf(GuidanceContent.BulletList(objectiveLines)),
                            variant = GuidanceContent.BoxVariant.INFO
                        ))
                    }
                }
            }

            add(GuidanceContent.InfoBox(
                title = "Terrain Guidelines",
                content = listOf(
                    GuidanceContent.BulletList(listOf(
                        "Ruins block line of sight",
                        "Woods: Units wholly within are never fully visible",
                        "Benefit of Cover: +1 to armor saves vs ranged"
                    ))
                ),
                variant = GuidanceContent.BoxVariant.REMINDER
            ))
        }

        override fun nextPhase(state: GameState): Phase = DetermineAttacker
    }

    /**
     * Step 4: Determine Attacker and Defender
     */
    data object DetermineAttacker : SetupPhase() {
        override val phaseName = "DETERMINE ATTACKER & DEFENDER"

        override fun displayGuidance(state: GameState): String = buildString {
            appendLine("Both players roll off (D6).")
            appendLine()
            appendLine("  Winner is the ATTACKER")
            appendLine("  Loser is the DEFENDER")
            appendLine()
            appendLine("Who won the roll-off?")
            appendLine("1. Player 1 is the Attacker")
            appendLine("2. Player 2 is the Attacker")
        }

        override fun displayStructuredGuidance(state: GameState): List<GuidanceContent> = buildList {
            add(GuidanceContent.Paragraph("Both players roll off (D6)."))

            add(GuidanceContent.KeyValue(listOf(
                "Winner" to "becomes the ATTACKER",
                "Loser" to "becomes the DEFENDER"
            )))

            add(GuidanceContent.InfoBox(
                title = "Attacker Benefits",
                content = listOf(
                    GuidanceContent.BulletList(listOf(
                        "Deploys units first",
                        "Selects secondary mission first",
                        "May have deployment zone advantages (mission-dependent)"
                    ))
                ),
                variant = GuidanceContent.BoxVariant.INFO
            ))
        }

        override fun requiresInput(): Boolean = true

        override fun processInput(input: String, state: GameState): Phase? {
            val attacker = when (input.trim()) {
                "1" -> 1
                "2" -> 2
                else -> return null
            }
            state.attackerPlayerNumber = attacker
            return SelectAttackerSecondary
        }

        override fun nextPhase(state: GameState): Phase = SelectAttackerSecondary
    }

    /**
     * Step 5: Attacker selects Secondary Mission
     */
    data object SelectAttackerSecondary : SetupPhase() {
        override val phaseName = "SELECT ATTACKER SECONDARY MISSION"

        var availableMissions: List<Mission> = emptyList()

        override fun displayGuidance(state: GameState): String = buildString {
            appendLine("Player ${state.attackerPlayerNumber} (ATTACKER) - Select Secondary Mission:")
            appendLine()
            availableMissions.forEachIndexed { index, mission ->
                val typeIndicator = when {
                    mission.type.name.contains("FIXED") -> " [FIXED]"
                    else -> ""
                }
                appendLine("${index + 1}. ${mission.name}$typeIndicator")
            }
        }

        override fun requiresInput(): Boolean = true

        override fun processInput(input: String, state: GameState): Phase? {
            val index = input.trim().toIntOrNull()?.minus(1) ?: return null
            if (index !in availableMissions.indices) return null

            val selectedMission = availableMissions[index]
            state.attackerSecondaryMission = selectedMission
            return DisplayAttackerSecondary(selectedMission)
        }

        override fun nextPhase(state: GameState): Phase = SelectDefenderSecondary
    }

    /**
     * Display Attacker's selected secondary mission
     */
    data class DisplayAttackerSecondary(val mission: Mission) : SetupPhase() {
        override val phaseName = "ATTACKER SECONDARY: ${mission.name.uppercase()}"

        override fun displayGuidance(state: GameState): String = mission.displayText()

        override fun nextPhase(state: GameState): Phase = SelectDefenderSecondary
    }

    /**
     * Step 6: Defender selects Secondary Mission
     */
    data object SelectDefenderSecondary : SetupPhase() {
        override val phaseName = "SELECT DEFENDER SECONDARY MISSION"

        var availableMissions: List<Mission> = emptyList()

        override fun displayGuidance(state: GameState): String = buildString {
            appendLine("Player ${state.defenderPlayerNumber} (DEFENDER) - Select Secondary Mission:")
            appendLine()
            availableMissions.forEachIndexed { index, mission ->
                val typeIndicator = when {
                    mission.type.name.contains("FIXED") -> " [FIXED]"
                    else -> ""
                }
                appendLine("${index + 1}. ${mission.name}$typeIndicator")
            }
        }

        override fun requiresInput(): Boolean = true

        override fun processInput(input: String, state: GameState): Phase? {
            val index = input.trim().toIntOrNull()?.minus(1) ?: return null
            if (index !in availableMissions.indices) return null

            val selectedMission = availableMissions[index]
            state.defenderSecondaryMission = selectedMission
            return DisplayDefenderSecondary(selectedMission)
        }

        override fun nextPhase(state: GameState): Phase = DeclareBattleFormations
    }

    /**
     * Display Defender's selected secondary mission
     */
    data class DisplayDefenderSecondary(val mission: Mission) : SetupPhase() {
        override val phaseName = "DEFENDER SECONDARY: ${mission.name.uppercase()}"

        override fun displayGuidance(state: GameState): String = mission.displayText()

        override fun nextPhase(state: GameState): Phase = DeclareBattleFormations
    }

    /**
     * Step 7: Declare Battle Formations
     */
    data object DeclareBattleFormations : SetupPhase() {
        override val phaseName = "DECLARE BATTLE FORMATIONS"

        override fun displayGuidance(state: GameState): String = buildString {
            appendLine("Both players now secretly note down:")
            appendLine()
            appendLine("1. ATTACHED LEADERS")
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

            add(GuidanceContent.NumberedList(listOf(
                "ATTACHED LEADERS - Which Leader units will start attached to which Bodyguard units",
                "EMBARKED UNITS - Which units will start embarked within Transport models",
                "RESERVES - Which units will start in Reserves (including Strategic Reserves)"
            )))

            add(GuidanceContent.InfoBox(
                title = "Strategic Reserves Rules",
                content = listOf(
                    GuidanceContent.BulletList(listOf(
                        "Max 25% of army points in Strategic Reserves",
                        "Deep Strike units can be set up in Reserves",
                        "Arrive from Round 2+ (wholly within 6\" of battlefield edge)",
                        "Round 2: Cannot enter enemy deployment zone"
                    ))
                ),
                variant = GuidanceContent.BoxVariant.REMINDER
            ))

            add(GuidanceContent.Paragraph("When both players are ready, declare your selections to your opponent."))
        }

        override fun nextPhase(state: GameState): Phase = DeployArmies
    }

    /**
     * Step 6: Deploy Armies
     */
    data object DeployArmies : SetupPhase() {
        override val phaseName = "DEPLOY ARMIES"

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
            appendLine("INFILTRATORS:")
            appendLine("  After all other units deployed, roll off")
            appendLine("  Winner alternates setting up Infiltrators units")
            appendLine("  Infiltrators: Set up anywhere 9\"+ from enemy deployment zone and models")
        }

        override fun displayStructuredGuidance(state: GameState): List<GuidanceContent> = buildList {
            add(GuidanceContent.Paragraph("Players alternate deploying units, one at a time."))
            add(GuidanceContent.NumberedList(listOf(
                "Player ${state.attackerPlayerNumber} (ATTACKER) deploys first",
                "Player ${state.defenderPlayerNumber} (DEFENDER) deploys next",
                "Continue alternating until all units are deployed"
            )))

            add(GuidanceContent.InfoBox(
                title = "Deployment Rules",
                content = listOf(
                    GuidanceContent.BulletList(listOf(
                        "Models must be set up wholly within their deployment zone",
                        "Continue until all units are deployed (or no room remains)"
                    ))
                ),
                variant = GuidanceContent.BoxVariant.REMINDER
            ))

            add(GuidanceContent.InfoBox(
                title = "Infiltrators",
                content = listOf(
                    GuidanceContent.Paragraph("After all other units deployed:"),
                    GuidanceContent.BulletList(listOf(
                        "Roll off - winner alternates setting up Infiltrators",
                        "Set up anywhere 9\"+ from enemy deployment zone and models"
                    ))
                ),
                variant = GuidanceContent.BoxVariant.INFO
            ))
        }

        override fun nextPhase(state: GameState): Phase = PreBattleRules
    }

    /**
     * Step 7: Resolve Pre-Battle Rules
     */
    data object PreBattleRules : SetupPhase() {
        override val phaseName = "RESOLVE PRE-BATTLE RULES"

        override fun displayGuidance(state: GameState): String = buildString {
            appendLine("Players alternate resolving any pre-battle rules,")
            appendLine("starting with the player who will take the first turn.")
            appendLine()
            appendLine("Common pre-battle rules:")
            appendLine()
            appendLine("SCOUTS X\":")
            appendLine("  Before the first turn, unit can make a Normal move up to X\"")
            appendLine("  Must end 9\"+ from all enemy models")
            appendLine("  Dedicated Transports with only Scouts-equipped models can also Scout")
            appendLine()
            appendLine("Note: If both players have Scouts units, the player going first moves theirs first.")
        }

        override fun displayStructuredGuidance(state: GameState): List<GuidanceContent> = buildList {
            add(GuidanceContent.Paragraph("Players alternate resolving any pre-battle rules, starting with the player who will take the first turn."))

            add(GuidanceContent.InfoBox(
                title = "Scouts X\"",
                content = listOf(
                    GuidanceContent.Paragraph("Before the first turn, unit can make a Normal move up to X\":"),
                    GuidanceContent.BulletList(listOf(
                        "Must end 9\"+ from all enemy models",
                        "Dedicated Transports with only Scouts-equipped models can also Scout"
                    ))
                ),
                variant = GuidanceContent.BoxVariant.INFO
            ))

            add(GuidanceContent.InfoBox(
                title = "Note",
                content = listOf(
                    GuidanceContent.Paragraph("If both players have Scouts units, the player going first moves theirs first.")
                ),
                variant = GuidanceContent.BoxVariant.REMINDER
            ))
        }

        override fun nextPhase(state: GameState): Phase = DetermineFirstTurn
    }

    /**
     * Step 8: Determine First Turn
     */
    data object DetermineFirstTurn : SetupPhase() {
        override val phaseName = "DETERMINE FIRST TURN"

        override fun displayGuidance(state: GameState): String = buildString {
            appendLine("Players roll off to determine who takes the first turn.")
            appendLine()
            appendLine("Who won the roll-off?")
            appendLine("1. Player 1 goes first")
            appendLine("2. Player 2 goes first")
        }

        override fun displayStructuredGuidance(state: GameState): List<GuidanceContent> = buildList {
            add(GuidanceContent.Paragraph("Players roll off to determine who takes the first turn."))

            add(GuidanceContent.InfoBox(
                title = "First Turn Advantage",
                content = listOf(
                    GuidanceContent.BulletList(listOf(
                        "First player moves and acts first each round",
                        "Second player gets the final actions of each round",
                        "Second player scores VP at end of turn in Round 5"
                    ))
                ),
                variant = GuidanceContent.BoxVariant.INFO
            ))
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
