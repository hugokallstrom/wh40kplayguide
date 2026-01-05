package org.example.mission

import org.example.fixtures.TestFixtures
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Unit tests for MissionLoader parsing logic.
 */
class MissionLoaderTest {

    private lateinit var tempDir: File
    private lateinit var primaryMissionsFile: File
    private lateinit var secondaryMissionsFile: File

    @BeforeTest
    fun setUp() {
        tempDir = kotlin.io.path.createTempDirectory("mission_test").toFile()
        primaryMissionsFile = File(tempDir, "primary_missions.txt")
        secondaryMissionsFile = File(tempDir, "secondary_missions.txt")
    }

    @AfterTest
    fun tearDown() {
        tempDir.deleteRecursively()
    }

    // ========== Primary Mission Parsing Tests ==========

    @Test
    fun `parsePrimaryMissions extracts mission names`() {
        primaryMissionsFile.writeText(TestFixtures.samplePrimaryMissionFileContent())
        val missions = MissionLoader.loadPrimaryMissions(primaryMissionsFile.absolutePath)

        assertTrue(missions.any { it.name == "Take and Hold" })
        assertTrue(missions.any { it.name == "Supply Drop" })
        assertTrue(missions.any { it.name == "Purge the Foe" })
    }

    @Test
    fun `parsePrimaryMissions returns correct count`() {
        primaryMissionsFile.writeText(TestFixtures.samplePrimaryMissionFileContent())
        val missions = MissionLoader.loadPrimaryMissions(primaryMissionsFile.absolutePath)

        assertEquals(3, missions.size)
    }

    @Test
    fun `parsePrimaryMissions detects asymmetric type`() {
        primaryMissionsFile.writeText(TestFixtures.samplePrimaryMissionFileContent())
        val missions = MissionLoader.loadPrimaryMissions(primaryMissionsFile.absolutePath)

        val supplyDrop = missions.find { it.name == "Supply Drop" }
        assertEquals(MissionType.PRIMARY_ASYMMETRIC, supplyDrop?.type)
    }

    @Test
    fun `parsePrimaryMissions detects standard type`() {
        primaryMissionsFile.writeText(TestFixtures.samplePrimaryMissionFileContent())
        val missions = MissionLoader.loadPrimaryMissions(primaryMissionsFile.absolutePath)

        val takeAndHold = missions.find { it.name == "Take and Hold" }
        assertEquals(MissionType.PRIMARY, takeAndHold?.type)
    }

    @Test
    fun `parsePrimaryMissions detects actions`() {
        primaryMissionsFile.writeText(TestFixtures.samplePrimaryMissionFileContent())
        val missions = MissionLoader.loadPrimaryMissions(primaryMissionsFile.absolutePath)

        val supplyDrop = missions.find { it.name == "Supply Drop" }
        assertTrue(supplyDrop?.hasAction == true, "Supply Drop should have an action")

        val takeAndHold = missions.find { it.name == "Take and Hold" }
        assertFalse(takeAndHold?.hasAction == true, "Take and Hold should not have an action")
    }

    @Test
    fun `parsePrimaryMissions preserves full text`() {
        primaryMissionsFile.writeText(TestFixtures.samplePrimaryMissionFileContent())
        val missions = MissionLoader.loadPrimaryMissions(primaryMissionsFile.absolutePath)

        val takeAndHold = missions.find { it.name == "Take and Hold" }
        assertTrue(takeAndHold?.fullText?.contains("Control objective markers") == true)
    }

    // ========== Secondary Mission Parsing Tests ==========

    @Test
    fun `parseSecondaryMissions splits by separator`() {
        secondaryMissionsFile.writeText(TestFixtures.sampleSecondaryMissionFileContent())
        val missions = MissionLoader.loadSecondaryMissions(secondaryMissionsFile.absolutePath)

        assertEquals(3, missions.size)
    }

    @Test
    fun `parseSecondaryMissions detects FIXED type`() {
        secondaryMissionsFile.writeText(TestFixtures.sampleSecondaryMissionFileContent())
        val missions = MissionLoader.loadSecondaryMissions(secondaryMissionsFile.absolutePath)

        val assassination = missions.find { it.name == "Assassination" }
        assertEquals(MissionType.SECONDARY_FIXED, assassination?.type)
    }

    @Test
    fun `parseSecondaryMissions detects standard type`() {
        secondaryMissionsFile.writeText(TestFixtures.sampleSecondaryMissionFileContent())
        val missions = MissionLoader.loadSecondaryMissions(secondaryMissionsFile.absolutePath)

        val engage = missions.find { it.name == "Engage on All Fronts" }
        assertEquals(MissionType.SECONDARY, engage?.type)
    }

    // ========== Edge Case Tests ==========

    @Test
    fun `handles empty content gracefully`() {
        primaryMissionsFile.writeText(TestFixtures.emptyMissionContent())
        val missions = MissionLoader.loadPrimaryMissions(primaryMissionsFile.absolutePath)

        assertTrue(missions.isEmpty())
    }

    @Test
    fun `handles content without valid headers gracefully`() {
        // The parser doesn't strictly validate headers, so malformed content
        // may still produce results. This test verifies it doesn't crash.
        primaryMissionsFile.writeText(TestFixtures.malformedMissionContent())
        val missions = MissionLoader.loadPrimaryMissions(primaryMissionsFile.absolutePath)

        // Parser processes content even without proper headers
        // Just verify it completes without exception
        assertNotNull(missions)
    }

    @Test
    fun `loadPrimaryMissions reads from file`() {
        val content = """
PRIMARY MISSION
Simple Test Mission

Just a simple mission for testing file I/O.
        """.trimIndent()

        primaryMissionsFile.writeText(content)
        val missions = MissionLoader.loadPrimaryMissions(primaryMissionsFile.absolutePath)

        assertEquals(1, missions.size)
        assertEquals("Simple Test Mission", missions[0].name)
    }

    // ========== Title Case Format Tests ==========

    @Test
    fun `parsePrimaryMissions handles title case headers`() {
        primaryMissionsFile.writeText(TestFixtures.samplePrimaryMissionTitleCaseContent())
        val missions = MissionLoader.loadPrimaryMissions(primaryMissionsFile.absolutePath)

        assertEquals(3, missions.size)
        assertTrue(missions.any { it.name == "Take and Hold" })
        assertTrue(missions.any { it.name == "Supply Drop" })
        assertTrue(missions.any { it.name == "Purge the Foe" })
    }

    @Test
    fun `parsePrimaryMissions detects asymmetric type with title case`() {
        primaryMissionsFile.writeText(TestFixtures.samplePrimaryMissionTitleCaseContent())
        val missions = MissionLoader.loadPrimaryMissions(primaryMissionsFile.absolutePath)

        val supplyDrop = missions.find { it.name == "Supply Drop" }
        assertEquals(MissionType.PRIMARY_ASYMMETRIC, supplyDrop?.type)
    }

    @Test
    fun `parsePrimaryMissions detects actions with title case`() {
        primaryMissionsFile.writeText(TestFixtures.samplePrimaryMissionTitleCaseContent())
        val missions = MissionLoader.loadPrimaryMissions(primaryMissionsFile.absolutePath)

        val supplyDrop = missions.find { it.name == "Supply Drop" }
        assertTrue(supplyDrop?.hasAction == true, "Supply Drop should have an action")

        val takeAndHold = missions.find { it.name == "Take and Hold" }
        assertFalse(takeAndHold?.hasAction == true, "Take and Hold should not have an action")
    }

    @Test
    fun `parseSecondaryMissions handles title case headers`() {
        secondaryMissionsFile.writeText(TestFixtures.sampleSecondaryMissionTitleCaseContent())
        val missions = MissionLoader.loadSecondaryMissions(secondaryMissionsFile.absolutePath)

        assertEquals(3, missions.size)
        assertTrue(missions.any { it.name == "Engage on All Fronts" })
        assertTrue(missions.any { it.name == "Assassination" })
        assertTrue(missions.any { it.name == "Behind Enemy Lines" })
    }

    @Test
    fun `parseSecondaryMissions detects FIXED type with title case`() {
        secondaryMissionsFile.writeText(TestFixtures.sampleSecondaryMissionTitleCaseContent())
        val missions = MissionLoader.loadSecondaryMissions(secondaryMissionsFile.absolutePath)

        val assassination = missions.find { it.name == "Assassination" }
        assertEquals(MissionType.SECONDARY_FIXED, assassination?.type)

        val engage = missions.find { it.name == "Engage on All Fronts" }
        assertEquals(MissionType.SECONDARY, engage?.type)
    }
}
