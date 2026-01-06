package org.example.game

/**
 * Battle size options with their point limits and battlefield dimensions.
 */
enum class BattleSize(val points: Int, val battlefieldSize: String, val duration: String) {
    INCURSION(1000, "44\" x 60\"", "~2 hours"),
    STRIKE_FORCE(2000, "44\" x 60\"", "~3 hours"),
    ONSLAUGHT(3000, "44\" x 90\"", "~4 hours")
}

/**
 * Tracks the current state of the game.
 * Note: This is a minimal state tracker - VP/CP are tracked manually by players.
 */
data class GameState(
    var battleSize: BattleSize = BattleSize.STRIKE_FORCE,
    var currentRound: Int = 0,
    var activePlayerNumber: Int = 1,  // 1 or 2
    var firstPlayerNumber: Int = 1,   // Who goes first each round (set after roll-off)
    var attackerPlayerNumber: Int = 1 // Who is the attacker (set after roll-off)
) {
    /**
     * Returns the player number of the defender.
     */
    val defenderPlayerNumber: Int
        get() = if (attackerPlayerNumber == 1) 2 else 1

    /**
     * Returns whether it's currently the attacker's turn.
     */
    val isAttackerTurn: Boolean
        get() = activePlayerNumber == attackerPlayerNumber

    /**
     * Advances to the next player's turn or the next round.
     * @return true if a new round started, false if just switched players
     */
    fun advanceToNextTurn(): Boolean {
        return if (activePlayerNumber == firstPlayerNumber) {
            // First player finished, switch to second player
            activePlayerNumber = if (firstPlayerNumber == 1) 2 else 1
            false
        } else {
            // Second player finished, advance to next round
            currentRound++
            activePlayerNumber = firstPlayerNumber
            true
        }
    }

    /**
     * Starts the battle (called after setup is complete).
     */
    fun startBattle() {
        currentRound = 1
        activePlayerNumber = firstPlayerNumber
    }

    /**
     * Returns whether the game has ended (after round 5).
     */
    fun isGameOver(): Boolean = currentRound > 5

    /**
     * Returns a display string for the current turn.
     */
    fun currentTurnDisplay(): String {
        val playerRole = if (isAttackerTurn) "ATTACKER" else "DEFENDER"
        return "BATTLE ROUND $currentRound - PLAYER $activePlayerNumber ($playerRole) TURN"
    }
}
