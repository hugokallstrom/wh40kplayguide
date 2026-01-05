package org.example.guidance

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for GuidanceContent sealed class hierarchy.
 */
class GuidanceContentTest {

    // ========== Header Tests ==========

    @Test
    fun `Header stores text and level`() {
        val header = GuidanceContent.Header("Section Title", 1)
        assertEquals("Section Title", header.text)
        assertEquals(1, header.level)
    }

    @Test
    fun `Header defaults to level 1`() {
        val header = GuidanceContent.Header("Title")
        assertEquals(1, header.level)
    }

    // ========== Paragraph Tests ==========

    @Test
    fun `Paragraph stores text`() {
        val para = GuidanceContent.Paragraph("Some guidance text here.")
        assertEquals("Some guidance text here.", para.text)
    }

    // ========== BulletList Tests ==========

    @Test
    fun `BulletList stores items`() {
        val list = GuidanceContent.BulletList(listOf("Item 1", "Item 2", "Item 3"))
        assertEquals(3, list.items.size)
        assertEquals("Item 1", list.items[0])
    }

    // ========== NumberedList Tests ==========

    @Test
    fun `NumberedList stores items`() {
        val list = GuidanceContent.NumberedList(listOf("First", "Second"))
        assertEquals(2, list.items.size)
        assertEquals("First", list.items[0])
    }

    // ========== KeyValue Tests ==========

    @Test
    fun `KeyValue stores pairs`() {
        val kv = GuidanceContent.KeyValue(listOf(
            "Control" to "OC > opponent's OC within 3\"",
            "Battle-shocked" to "OC becomes 0"
        ))
        assertEquals(2, kv.pairs.size)
        assertEquals("Control", kv.pairs[0].first)
        assertEquals("OC > opponent's OC within 3\"", kv.pairs[0].second)
    }

    // ========== InfoBox Tests ==========

    @Test
    fun `InfoBox stores title and content with default variant`() {
        val content = listOf(GuidanceContent.Paragraph("Some info"))
        val box = GuidanceContent.InfoBox("Reminder", content)
        assertEquals("Reminder", box.title)
        assertEquals(1, box.content.size)
        assertEquals(GuidanceContent.BoxVariant.INFO, box.variant)
    }

    @Test
    fun `InfoBox accepts custom variant`() {
        val box = GuidanceContent.InfoBox(
            "Warning",
            listOf(GuidanceContent.Paragraph("Caution!")),
            GuidanceContent.BoxVariant.WARNING
        )
        assertEquals(GuidanceContent.BoxVariant.WARNING, box.variant)
    }

    @Test
    fun `InfoBox can have null title`() {
        val box = GuidanceContent.InfoBox(null, listOf(GuidanceContent.Paragraph("Text")))
        assertEquals(null, box.title)
    }

    // ========== Table Tests ==========

    @Test
    fun `Table stores headers and rows`() {
        val table = GuidanceContent.Table(
            headers = listOf("Condition", "Roll"),
            rows = listOf(
                listOf("S >= 2x T", "2+"),
                listOf("S > T", "3+")
            )
        )
        assertEquals(2, table.headers.size)
        assertEquals(2, table.rows.size)
        assertEquals("S >= 2x T", table.rows[0][0])
    }

    // ========== MissionBlock Tests ==========

    @Test
    fun `MissionBlock stores mission name and scoring rules`() {
        val block = GuidanceContent.MissionBlock(
            missionName = "Hidden Supplies",
            player = null,
            scoringRules = listOf(GuidanceContent.Paragraph("Control objectives for VP"))
        )
        assertEquals("Hidden Supplies", block.missionName)
        assertEquals(null, block.player)
        assertEquals(1, block.scoringRules.size)
    }

    @Test
    fun `MissionBlock can include player info`() {
        val block = GuidanceContent.MissionBlock(
            missionName = "Assassination",
            player = "Player 1 (ATTACKER)",
            scoringRules = listOf(GuidanceContent.Paragraph("Destroy characters"))
        )
        assertEquals("Player 1 (ATTACKER)", block.player)
    }

    // ========== Divider and Spacer Tests ==========

    @Test
    fun `Divider is a singleton-like object`() {
        val divider1 = GuidanceContent.Divider
        val divider2 = GuidanceContent.Divider
        assertTrue(divider1 === divider2)
    }

    @Test
    fun `Spacer is a singleton-like object`() {
        val spacer1 = GuidanceContent.Spacer
        val spacer2 = GuidanceContent.Spacer
        assertTrue(spacer1 === spacer2)
    }

    // ========== Section Tests ==========

    @Test
    fun `Section groups content with optional title`() {
        val section = GuidanceContent.Section(
            title = "Movement Rules",
            content = listOf(
                GuidanceContent.Paragraph("Unit coherency: 2\""),
                GuidanceContent.Paragraph("Cannot enter engagement range")
            )
        )
        assertEquals("Movement Rules", section.title)
        assertEquals(2, section.content.size)
    }

    @Test
    fun `Section can have null title`() {
        val section = GuidanceContent.Section(
            title = null,
            content = listOf(GuidanceContent.Paragraph("Some content"))
        )
        assertEquals(null, section.title)
    }

    // ========== BoxVariant Tests ==========

    @Test
    fun `BoxVariant has expected values`() {
        val variants = GuidanceContent.BoxVariant.entries
        assertEquals(4, variants.size)
        assertTrue(variants.contains(GuidanceContent.BoxVariant.INFO))
        assertTrue(variants.contains(GuidanceContent.BoxVariant.WARNING))
        assertTrue(variants.contains(GuidanceContent.BoxVariant.SUCCESS))
        assertTrue(variants.contains(GuidanceContent.BoxVariant.REMINDER))
    }
}
