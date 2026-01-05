package org.example.mission

import java.io.File

/**
 * Loads and parses mission data from text files.
 */
object MissionLoader {

    /**
     * Loads primary missions from the given file path.
     */
    fun loadPrimaryMissions(filePath: String): List<Mission> {
        val content = File(filePath).readText()
        return parsePrimaryMissions(content)
    }

    /**
     * Loads secondary missions from the given file path.
     */
    fun loadSecondaryMissions(filePath: String): List<Mission> {
        val content = File(filePath).readText()
        return parseSecondaryMissions(content)
    }

    /**
     * Parses primary missions from text content.
     * Missions are separated by "Primary Mission" or "PRIMARY MISSION" headers (case-insensitive).
     */
    private fun parsePrimaryMissions(content: String): List<Mission> {
        val missions = mutableListOf<Mission>()

        // Split by Primary Mission headers (case-insensitive)
        val pattern = Regex("""(?=Primary Mission(?:\s*-\s*Asymmetric War)?\s*\n)""", RegexOption.IGNORE_CASE)
        val sections = content.split(pattern).filter { it.isNotBlank() }

        for (section in sections) {
            val lines = section.lines().map { it.trim() }.filter { it.isNotEmpty() }
            if (lines.isEmpty()) continue

            // Determine type (case-insensitive check)
            val isAsymmetric = lines[0].contains("Asymmetric War", ignoreCase = true)
            val type = if (isAsymmetric) MissionType.PRIMARY_ASYMMETRIC else MissionType.PRIMARY

            // Get mission name (second non-empty line after header)
            val nameIndex = if (lines[0].startsWith("Primary Mission", ignoreCase = true)) 1 else 0
            if (nameIndex >= lines.size) continue
            val name = lines[nameIndex]

            // Check for actions (case-insensitive, matches "(Action)" or "(ACTION)")
            val hasAction = section.contains("(Action)", ignoreCase = true)

            // Get full text (everything after the name)
            val fullText = lines.drop(nameIndex + 1).joinToString("\n")

            missions.add(Mission(
                name = name,
                type = type,
                fullText = fullText.trim(),
                hasAction = hasAction
            ))
        }

        return missions
    }

    /**
     * Parses secondary missions from text content.
     * Missions are separated by "Secondary Mission" or "Fixed - Secondary Mission" headers (case-insensitive),
     * with "=" separator lines between missions.
     */
    private fun parseSecondaryMissions(content: String): List<Mission> {
        val missions = mutableListOf<Mission>()

        // Split by separator lines (=====)
        val sections = content.split(Regex("""={10,}""")).filter { it.isNotBlank() }

        for (section in sections) {
            val lines = section.lines().map { it.trim() }.filter { it.isNotEmpty() }
            if (lines.isEmpty()) continue

            // Determine type from header (case-insensitive)
            val headerLine = lines[0]
            val isFixed = headerLine.startsWith("Fixed", ignoreCase = true)
            val type = if (isFixed) MissionType.SECONDARY_FIXED else MissionType.SECONDARY

            // Skip if not a mission header (case-insensitive)
            if (!headerLine.contains("Secondary Mission", ignoreCase = true)) continue

            // Get mission name (line after header)
            if (lines.size < 2) continue
            val name = lines[1]

            // Check for actions (case-insensitive)
            val hasAction = section.contains("(Action)", ignoreCase = true)

            // Get full text (everything after the name)
            val fullText = lines.drop(2).joinToString("\n")

            missions.add(Mission(
                name = name,
                type = type,
                fullText = fullText.trim(),
                hasAction = hasAction
            ))
        }

        return missions
    }
}
