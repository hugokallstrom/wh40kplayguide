package org.example.cli

import org.example.game.GameState
import org.example.mission.Mission
import org.example.mission.MissionLoader
import org.example.phase.*

/**
 * Runs the CLI game guide.
 */
class CliRunner(
    private val primaryMissionsPath: String,
    private val secondaryMissionsPath: String
) {
    private val state = GameState()
    private var currentPhase: Phase = SetupPhase.MusterArmies
    private var primaryMissions: List<Mission> = emptyList()
    private var secondaryMissions: List<Mission> = emptyList()
    private var running = true

    /**
     * Starts the game guide.
     */
    fun run() {
        loadMissions()
        printHeader()

        while (running && currentPhase !is EndGamePhase) {
            displayPhase()
            val nextPhase = handleInput()
            if (nextPhase != null) {
                currentPhase = nextPhase
            }
        }

        // Show end game
        if (currentPhase is EndGamePhase) {
            displayPhase()
        }
    }

    private fun loadMissions() {
        try {
            primaryMissions = MissionLoader.loadPrimaryMissions(primaryMissionsPath)
            SetupPhase.ReadMissionObjectives.availableMissions = primaryMissions
            println("Loaded ${primaryMissions.size} primary missions.")
        } catch (e: Exception) {
            println("Warning: Could not load primary missions from $primaryMissionsPath")
            println("Error: ${e.message}")
            println()
        }

        try {
            secondaryMissions = MissionLoader.loadSecondaryMissions(secondaryMissionsPath)
            SetupPhase.SelectAttackerSecondary.availableMissions = secondaryMissions
            SetupPhase.SelectDefenderSecondary.availableMissions = secondaryMissions
            println("Loaded ${secondaryMissions.size} secondary missions.")
        } catch (e: Exception) {
            println("Warning: Could not load secondary missions from $secondaryMissionsPath")
            println("Error: ${e.message}")
            println()
        }
    }

    private fun printHeader() {
        println()
        println("═".repeat(55))
        println("  WARHAMMER 40,000 GAME GUIDE")
        println("═".repeat(55))
        println()
        println("Commands: 'quit' to exit, 'help' for help, 'status' for game status")
        println()
    }

    private fun displayPhase() {
        println()
        println("═".repeat(55))

        // Show turn info for battle phases
        if (currentPhase is BattlePhaseMarker && currentPhase !is EndGamePhase) {
            println("── ${state.currentTurnDisplay()} ──")
        }

        println("── ${currentPhase.phaseName} ──")
        println("═".repeat(55))
        println()
        println(currentPhase.displayGuidance(state))
    }

    private fun handleInput(): Phase? {
        println()

        if (currentPhase.requiresInput()) {
            print("Enter choice: ")
        } else {
            print("[Press Enter to continue, or type a command] ")
        }

        val input = readlnOrNull()?.trim()
        if (input == null) {
            // EOF reached
            running = false
            return null
        }

        // Handle special commands
        when (input.lowercase()) {
            "quit", "q", "exit" -> {
                println("\nExiting game guide. Thanks for playing!")
                running = false
                return null
            }
            "help", "h", "?" -> {
                printHelp()
                return null
            }
            "status", "s" -> {
                printStatus()
                return null
            }
            "" -> {
                // Empty input = advance to next phase (if no input required)
                return if (currentPhase.requiresInput()) {
                    println("Please enter a valid choice.")
                    null
                } else {
                    currentPhase.nextPhase(state)
                }
            }
            else -> {
                // Try to process as phase input
                return if (currentPhase.requiresInput()) {
                    val result = currentPhase.processInput(input, state)
                    if (result == null) {
                        println("Invalid choice. Please try again.")
                    }
                    result
                } else {
                    // For non-input phases, any non-command input advances
                    currentPhase.nextPhase(state)
                }
            }
        }
    }

    private fun printHelp() {
        println()
        println("═══ HELP ═══")
        println()
        println("Commands:")
        println("  quit, q, exit  - Exit the game guide")
        println("  help, h, ?     - Show this help")
        println("  status, s      - Show current game status")
        println("  [Enter]        - Advance to next phase")
        println()
        println("When prompted for a choice, enter the number of your selection.")
        println()
    }

    private fun printStatus() {
        println()
        println("═══ GAME STATUS ═══")
        println()
        println("Battle Size: ${state.battleSize.name.replace("_", " ")} (${state.battleSize.points} pts)")
        println("Battlefield: ${state.battleSize.battlefieldSize}")
        println()
        state.primaryMission?.let {
            println("Primary Mission: ${it.name}")
        } ?: println("Primary Mission: Not selected")

        state.attackerSecondaryMission?.let {
            println("Attacker Secondary: ${it.name}")
        }
        state.defenderSecondaryMission?.let {
            println("Defender Secondary: ${it.name}")
        }
        println()
        if (state.currentRound > 0) {
            println("Current Round: ${state.currentRound}")
            println("Active Player: Player ${state.activePlayerNumber}")
            println("Attacker: Player ${state.attackerPlayerNumber}")
            println("Defender: Player ${state.defenderPlayerNumber}")
            println("First Player: Player ${state.firstPlayerNumber}")
        } else {
            println("Game has not started yet (still in setup)")
        }
        println()
    }
}
