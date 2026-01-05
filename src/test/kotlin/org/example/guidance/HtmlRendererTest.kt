package org.example.guidance

import kotlinx.html.div
import kotlinx.html.stream.createHTML
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Unit tests for HtmlRenderer.
 */
class HtmlRendererTest {

    private fun renderToString(content: List<GuidanceContent>): String {
        return createHTML().div {
            HtmlRenderer.render(this, content)
        }
    }

    // ========== Header Tests ==========

    @Test
    fun `Header level 1 renders as h3 with correct class`() {
        val content = listOf(GuidanceContent.Header("Test Header", 1))
        val html = renderToString(content)

        assertTrue(html.contains("<h3"), "Should contain h3 tag")
        assertTrue(html.contains("guidance-header-1"), "Should have guidance-header-1 class")
        assertTrue(html.contains("Test Header"), "Should contain header text")
    }

    @Test
    fun `Header level 2 renders as h4 with correct class`() {
        val content = listOf(GuidanceContent.Header("Subheader", 2))
        val html = renderToString(content)

        assertTrue(html.contains("<h4"), "Should contain h4 tag")
        assertTrue(html.contains("guidance-header-2"), "Should have guidance-header-2 class")
    }

    // ========== Paragraph Tests ==========

    @Test
    fun `Paragraph renders as p tag`() {
        val content = listOf(GuidanceContent.Paragraph("Some text here."))
        val html = renderToString(content)

        assertTrue(html.contains("<p"), "Should contain p tag")
        assertTrue(html.contains("Some text here."), "Should contain paragraph text")
    }

    // ========== BulletList Tests ==========

    @Test
    fun `BulletList renders as ul with li items`() {
        val content = listOf(GuidanceContent.BulletList(listOf("Item 1", "Item 2")))
        val html = renderToString(content)

        assertTrue(html.contains("<ul"), "Should contain ul tag")
        assertTrue(html.contains("<li"), "Should contain li tags")
        assertTrue(html.contains("Item 1"), "Should contain first item")
        assertTrue(html.contains("Item 2"), "Should contain second item")
    }

    // ========== NumberedList Tests ==========

    @Test
    fun `NumberedList renders as ol with li items`() {
        val content = listOf(GuidanceContent.NumberedList(listOf("First", "Second")))
        val html = renderToString(content)

        assertTrue(html.contains("<ol"), "Should contain ol tag")
        assertTrue(html.contains("<li"), "Should contain li tags")
        assertTrue(html.contains("First"), "Should contain first item")
    }

    // ========== KeyValue Tests ==========

    @Test
    fun `KeyValue renders with key-value classes`() {
        val content = listOf(GuidanceContent.KeyValue(listOf(
            "Control" to "OC value",
            "Range" to "12\""
        )))
        val html = renderToString(content)

        assertTrue(html.contains("key-value-list"), "Should have key-value-list class")
        assertTrue(html.contains("key-value-item"), "Should have key-value-item class")
        assertTrue(html.contains("key-value-key"), "Should have key-value-key class")
        assertTrue(html.contains("Control"), "Should contain key")
        assertTrue(html.contains("OC value"), "Should contain value")
    }

    // ========== InfoBox Tests ==========

    @Test
    fun `InfoBox renders with title and correct variant class`() {
        val content = listOf(GuidanceContent.InfoBox(
            "Reminder",
            listOf(GuidanceContent.Paragraph("Remember this.")),
            GuidanceContent.BoxVariant.REMINDER
        ))
        val html = renderToString(content)

        assertTrue(html.contains("info-box"), "Should have info-box class")
        assertTrue(html.contains("reminder"), "Should have reminder variant class")
        assertTrue(html.contains("Reminder"), "Should contain title")
        assertTrue(html.contains("Remember this."), "Should contain content")
    }

    @Test
    fun `InfoBox renders without title when null`() {
        val content = listOf(GuidanceContent.InfoBox(
            null,
            listOf(GuidanceContent.Paragraph("Just content.")),
            GuidanceContent.BoxVariant.INFO
        ))
        val html = renderToString(content)

        assertTrue(html.contains("info-box"), "Should have info-box class")
        assertTrue(html.contains("Just content."), "Should contain content")
        // Title div should not appear
    }

    @Test
    fun `InfoBox warning variant has warning class`() {
        val content = listOf(GuidanceContent.InfoBox(
            "Warning",
            listOf(GuidanceContent.Paragraph("Caution!")),
            GuidanceContent.BoxVariant.WARNING
        ))
        val html = renderToString(content)

        assertTrue(html.contains("warning"), "Should have warning class")
    }

    // ========== Table Tests ==========

    @Test
    fun `Table renders with headers and rows`() {
        val content = listOf(GuidanceContent.Table(
            headers = listOf("Condition", "Roll"),
            rows = listOf(
                listOf("S >= 2x T", "2+"),
                listOf("S > T", "3+")
            )
        ))
        val html = renderToString(content)

        assertTrue(html.contains("<table"), "Should contain table tag")
        assertTrue(html.contains("<thead"), "Should contain thead")
        assertTrue(html.contains("<th"), "Should contain th tags")
        assertTrue(html.contains("<tbody"), "Should contain tbody")
        assertTrue(html.contains("<td"), "Should contain td tags")
        assertTrue(html.contains("Condition"), "Should contain header")
        assertTrue(html.contains("2+"), "Should contain cell value")
        assertTrue(html.contains("guidance-table"), "Should have guidance-table class")
    }

    // ========== MissionBlock Tests ==========

    @Test
    fun `MissionBlock renders with mission name and content`() {
        val content = listOf(GuidanceContent.MissionBlock(
            missionName = "Hidden Supplies",
            player = null,
            scoringRules = listOf(GuidanceContent.Paragraph("Control objectives"))
        ))
        val html = renderToString(content)

        assertTrue(html.contains("mission-block"), "Should have mission-block class")
        assertTrue(html.contains("Hidden Supplies"), "Should contain mission name")
        assertTrue(html.contains("Control objectives"), "Should contain scoring rules")
    }

    @Test
    fun `MissionBlock renders with player info when provided`() {
        val content = listOf(GuidanceContent.MissionBlock(
            missionName = "Assassination",
            player = "Player 1 (ATTACKER)",
            scoringRules = listOf(GuidanceContent.Paragraph("Destroy characters"))
        ))
        val html = renderToString(content)

        assertTrue(html.contains("mission-player"), "Should have mission-player class")
        assertTrue(html.contains("Player 1 (ATTACKER)"), "Should contain player info")
    }

    // ========== Divider Tests ==========

    @Test
    fun `Divider renders as hr with class`() {
        val content = listOf(GuidanceContent.Divider)
        val html = renderToString(content)

        assertTrue(html.contains("<hr"), "Should contain hr tag")
        assertTrue(html.contains("guidance-divider"), "Should have guidance-divider class")
    }

    // ========== Spacer Tests ==========

    @Test
    fun `Spacer renders as empty div with class`() {
        val content = listOf(GuidanceContent.Spacer)
        val html = renderToString(content)

        assertTrue(html.contains("guidance-spacer"), "Should have guidance-spacer class")
    }

    // ========== Section Tests ==========

    @Test
    fun `Section renders with title and content`() {
        val content = listOf(GuidanceContent.Section(
            title = "Movement Rules",
            content = listOf(GuidanceContent.Paragraph("Unit coherency"))
        ))
        val html = renderToString(content)

        assertTrue(html.contains("guidance-section"), "Should have guidance-section class")
        assertTrue(html.contains("Movement Rules"), "Should contain section title")
        assertTrue(html.contains("Unit coherency"), "Should contain section content")
    }

    @Test
    fun `Section renders without title when null`() {
        val content = listOf(GuidanceContent.Section(
            title = null,
            content = listOf(GuidanceContent.Paragraph("Just content"))
        ))
        val html = renderToString(content)

        assertTrue(html.contains("guidance-section"), "Should have guidance-section class")
        assertTrue(html.contains("Just content"), "Should contain content")
    }

    // ========== Multiple Content Items ==========

    @Test
    fun `Multiple content items render in order`() {
        val content = listOf(
            GuidanceContent.Header("Title", 1),
            GuidanceContent.Paragraph("First paragraph"),
            GuidanceContent.Paragraph("Second paragraph")
        )
        val html = renderToString(content)

        val titleIndex = html.indexOf("Title")
        val firstIndex = html.indexOf("First paragraph")
        val secondIndex = html.indexOf("Second paragraph")

        assertTrue(titleIndex < firstIndex, "Title should come before first paragraph")
        assertTrue(firstIndex < secondIndex, "First paragraph should come before second")
    }

    // ========== Empty Content ==========

    @Test
    fun `Empty content list renders empty container`() {
        val content = emptyList<GuidanceContent>()
        val html = renderToString(content)

        // Should just be the outer div
        assertTrue(html.contains("<div"), "Should contain div")
    }
}
