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
     * Missions are separated by "PRIMARY MISSION" or "PRIMARY MISSION - ASYMMETRIC WAR" headers.
     */
    private fun parsePrimaryMissions(content: String): List<Mission> {
        val missions = mutableListOf<Mission>()

        // Split by PRIMARY MISSION headers
        val pattern = Regex("""(?=PRIMARY MISSION(?:\s*-\s*ASYMMETRIC WAR)?\s*\n)""")
        val sections = content.split(pattern).filter { it.isNotBlank() }

        for (section in sections) {
            val lines = section.lines().map { it.trim() }.filter { it.isNotEmpty() }
            if (lines.isEmpty()) continue

            // Determine type
            val isAsymmetric = lines[0].contains("ASYMMETRIC WAR")
            val type = if (isAsymmetric) MissionType.PRIMARY_ASYMMETRIC else MissionType.PRIMARY

            // Get mission name (second non-empty line after header)
            val nameIndex = if (lines[0].startsWith("PRIMARY MISSION")) 1 else 0
            if (nameIndex >= lines.size) continue
            val name = lines[nameIndex]

            // Check for actions
            val hasAction = section.contains("(ACTION)")

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
     * Missions are separated by "SECONDARY MISSION" or "FIXED - SECONDARY MISSION" headers,
     * with "=" separator lines between missions.
     */
    private fun parseSecondaryMissions(content: String): List<Mission> {
        val missions = mutableListOf<Mission>()

        // Split by separator lines (=====)
        val sections = content.split(Regex("""={10,}""")).filter { it.isNotBlank() }

        for (section in sections) {
            val lines = section.lines().map { it.trim() }.filter { it.isNotEmpty() }
            if (lines.isEmpty()) continue

            // Determine type from header
            val headerLine = lines[0]
            val isFixed = headerLine.startsWith("FIXED")
            val type = if (isFixed) MissionType.SECONDARY_FIXED else MissionType.SECONDARY

            // Skip if not a mission header
            if (!headerLine.contains("SECONDARY MISSION")) continue

            // Get mission name (line after header)
            if (lines.size < 2) continue
            val name = lines[1]

            // Check for actions
            val hasAction = section.contains("(ACTION)")

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
