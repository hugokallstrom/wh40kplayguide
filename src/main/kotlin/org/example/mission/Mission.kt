package org.example.mission

/**
 * Type of mission.
 */
enum class MissionType {
    PRIMARY,
    PRIMARY_ASYMMETRIC,
    SECONDARY,
    SECONDARY_FIXED
}

/**
 * Represents a Warhammer 40K mission with its scoring rules.
 *
 * @property name The mission name (e.g., "Take and Hold")
 * @property type The type of mission
 * @property fullText The complete mission text for display
 * @property hasAction Whether this mission has an action (e.g., Cleanse, Terraform)
 */
data class Mission(
    val name: String,
    val type: MissionType,
    val fullText: String,
    val hasAction: Boolean = false
) {
    /**
     * Returns a formatted display of the mission for the CLI.
     */
    fun displayText(): String {
        val typeLabel = when (type) {
            MissionType.PRIMARY -> "PRIMARY MISSION"
            MissionType.PRIMARY_ASYMMETRIC -> "PRIMARY MISSION - ASYMMETRIC WAR"
            MissionType.SECONDARY -> "SECONDARY MISSION"
            MissionType.SECONDARY_FIXED -> "FIXED - SECONDARY MISSION"
        }
        return buildString {
            appendLine("$typeLabel: $name")
            appendLine("═".repeat(50))
            appendLine()
            appendLine(fullText)
        }
    }
}
