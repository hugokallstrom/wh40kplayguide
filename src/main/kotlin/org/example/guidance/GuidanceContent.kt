package org.example.guidance

/**
 * Sealed class hierarchy representing structured guidance content.
 * This abstraction allows phases to define content that can be rendered
 * to either plain text (CLI) or rich HTML (web).
 */
sealed class GuidanceContent {

    /**
     * A section header with optional level (1 = h3, 2 = h4, etc.)
     */
    data class Header(val text: String, val level: Int = 1) : GuidanceContent()

    /**
     * A paragraph of text.
     */
    data class Paragraph(val text: String) : GuidanceContent()

    /**
     * Text with markdown-like formatting support.
     * Supports **bold** and *italic* formatting.
     */
    data class FormattedText(val text: String) : GuidanceContent()

    /**
     * An unordered (bullet) list of items.
     */
    data class BulletList(val items: List<String>) : GuidanceContent()

    /**
     * An ordered (numbered) list of items.
     */
    data class NumberedList(val items: List<String>) : GuidanceContent()

    /**
     * Key-value pairs displayed as label: value.
     */
    data class KeyValue(val pairs: List<Pair<String, String>>) : GuidanceContent()

    /**
     * A callout box with optional title and content.
     */
    data class InfoBox(
        val title: String?,
        val content: List<GuidanceContent>,
        val variant: BoxVariant = BoxVariant.INFO
    ) : GuidanceContent()

    /**
     * A data table with headers and rows.
     */
    data class Table(
        val headers: List<String>,
        val rows: List<List<String>>
    ) : GuidanceContent()

    /**
     * A mission block displaying mission name, optional player, and scoring rules.
     */
    data class MissionBlock(
        val missionName: String,
        val player: String?,
        val scoringRules: List<GuidanceContent>
    ) : GuidanceContent()

    /**
     * A horizontal divider line.
     */
    data object Divider : GuidanceContent()

    /**
     * Vertical spacing between content.
     */
    data object Spacer : GuidanceContent()

    /**
     * A section grouping related content with optional title.
     */
    data class Section(
        val title: String?,
        val content: List<GuidanceContent>
    ) : GuidanceContent()

    /**
     * Variants for InfoBox styling.
     */
    enum class BoxVariant {
        INFO,
        WARNING,
        SUCCESS,
        REMINDER
    }
}
