package org.example.mission

import org.example.fixtures.TestFixtures
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for Mission data class.
 */
class MissionTest {

    // ========== displayText Tests ==========

    @Test
    fun `displayText includes mission name`() {
        val mission = TestFixtures.samplePrimaryMission()
        val display = mission.displayText()
        assertTrue(display.contains(mission.name))
    }

    @Test
    fun `displayText includes full text`() {
        val mission = TestFixtures.samplePrimaryMission()
        val display = mission.displayText()
        assertTrue(display.contains("Control objective markers"))
    }

    @Test
    fun `displayText formats PRIMARY type`() {
        val mission = Mission(
            name = "Test Mission",
            type = MissionType.PRIMARY,
            fullText = "Some rules"
        )
        val display = mission.displayText()
        assertTrue(display.contains("PRIMARY MISSION: Test Mission"))
    }

    @Test
    fun `displayText formats PRIMARY_ASYMMETRIC type`() {
        val mission = TestFixtures.sampleAsymmetricMission()
        val display = mission.displayText()
        assertTrue(display.contains("PRIMARY MISSION - ASYMMETRIC WAR"))
    }

    @Test
    fun `displayText formats SECONDARY type`() {
        val mission = TestFixtures.sampleSecondaryMission()
        val display = mission.displayText()
        assertTrue(display.contains("SECONDARY MISSION"))
    }

    @Test
    fun `displayText formats SECONDARY_FIXED type`() {
        val mission = TestFixtures.sampleFixedSecondaryMission()
        val display = mission.displayText()
        assertTrue(display.contains("FIXED - SECONDARY MISSION"))
    }

    // ========== Data Class Tests ==========

    @Test
    fun `mission equality by content`() {
        val mission1 = Mission("Test", MissionType.PRIMARY, "Rules", false)
        val mission2 = Mission("Test", MissionType.PRIMARY, "Rules", false)
        val mission3 = Mission("Different", MissionType.PRIMARY, "Rules", false)

        assertEquals(mission1, mission2)
        assertFalse(mission1 == mission3)
    }

    @Test
    fun `mission with action flag`() {
        val missionWithAction = TestFixtures.missionWithAction()
        val missionWithoutAction = TestFixtures.samplePrimaryMission()

        assertTrue(missionWithAction.hasAction)
        assertFalse(missionWithoutAction.hasAction)
    }
}
