package org.example.guidance

import kotlinx.html.*

/**
 * Renders GuidanceContent to HTML using kotlinx.html DSL.
 */
object HtmlRenderer {

    /**
     * Renders a list of GuidanceContent items to HTML.
     */
    fun render(container: FlowContent, content: List<GuidanceContent>) {
        content.forEach { item ->
            renderItem(container, item)
        }
    }

    private fun renderItem(container: FlowContent, item: GuidanceContent) {
        when (item) {
            is GuidanceContent.Header -> renderHeader(container, item)
            is GuidanceContent.Paragraph -> renderParagraph(container, item)
            is GuidanceContent.FormattedText -> renderFormattedText(container, item)
            is GuidanceContent.BulletList -> renderBulletList(container, item)
            is GuidanceContent.NumberedList -> renderNumberedList(container, item)
            is GuidanceContent.KeyValue -> renderKeyValue(container, item)
            is GuidanceContent.InfoBox -> renderInfoBox(container, item)
            is GuidanceContent.Table -> renderTable(container, item)
            is GuidanceContent.MissionBlock -> renderMissionBlock(container, item)
            is GuidanceContent.Divider -> renderDivider(container)
            is GuidanceContent.Spacer -> renderSpacer(container)
            is GuidanceContent.Section -> renderSection(container, item)
        }
    }

    private fun renderHeader(container: FlowContent, header: GuidanceContent.Header) {
        when (header.level) {
            1 -> container.h3(classes = "guidance-header-1") { +header.text }
            2 -> container.h4(classes = "guidance-header-2") { +header.text }
            else -> container.h5(classes = "guidance-header-3") { +header.text }
        }
    }

    private fun renderParagraph(container: FlowContent, paragraph: GuidanceContent.Paragraph) {
        container.p { +paragraph.text }
    }

    private fun renderFormattedText(container: FlowContent, formattedText: GuidanceContent.FormattedText) {
        container.div(classes = "formatted-text") {
            unsafe {
                +convertMarkdownToHtml(formattedText.text)
            }
        }
    }

    /**
     * Converts markdown-like text to HTML.
     * Supports: **bold**, *italic*, and preserves line breaks.
     */
    private fun convertMarkdownToHtml(text: String): String {
        return text
            // Escape HTML special characters first
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            // Convert **bold** to <strong>
            .replace(Regex("""\*\*(.+?)\*\*""")) { match ->
                "<strong>${match.groupValues[1]}</strong>"
            }
            // Convert *italic* to <em> (but not **bold** which we already converted)
            .replace(Regex("""\*([^*]+?)\*""")) { match ->
                "<em>${match.groupValues[1]}</em>"
            }
            // Convert newlines to <br> and preserve paragraph breaks
            .replace("\n\n", "</p><p>")
            .replace("\n", "<br>")
            .let { "<p>$it</p>" }
    }

    private fun renderBulletList(container: FlowContent, list: GuidanceContent.BulletList) {
        container.ul {
            list.items.forEach { item ->
                li { +item }
            }
        }
    }

    private fun renderNumberedList(container: FlowContent, list: GuidanceContent.NumberedList) {
        container.ol {
            list.items.forEach { item ->
                li { +item }
            }
        }
    }

    private fun renderKeyValue(container: FlowContent, kv: GuidanceContent.KeyValue) {
        container.div(classes = "key-value-list") {
            kv.pairs.forEach { (key, value) ->
                div(classes = "key-value-item") {
                    span(classes = "key-value-key") { +key }
                    span(classes = "key-value-value") { +value }
                }
            }
        }
    }

    private fun renderInfoBox(container: FlowContent, box: GuidanceContent.InfoBox) {
        val variantClass = when (box.variant) {
            GuidanceContent.BoxVariant.INFO -> "info"
            GuidanceContent.BoxVariant.WARNING -> "warning"
            GuidanceContent.BoxVariant.SUCCESS -> "success"
            GuidanceContent.BoxVariant.REMINDER -> "reminder"
        }

        container.div(classes = "info-box $variantClass") {
            box.title?.let { title ->
                div(classes = "info-box-title") { +title }
            }
            div(classes = "info-box-content") {
                render(this, box.content)
            }
        }
    }

    private fun renderTable(container: FlowContent, table: GuidanceContent.Table) {
        container.table(classes = "guidance-table") {
            thead {
                tr {
                    table.headers.forEach { header ->
                        th { +header }
                    }
                }
            }
            tbody {
                table.rows.forEach { row ->
                    tr {
                        row.forEach { cell ->
                            td { +cell }
                        }
                    }
                }
            }
        }
    }

    private fun renderMissionBlock(container: FlowContent, mission: GuidanceContent.MissionBlock) {
        container.div(classes = "mission-block") {
            div(classes = "mission-block-header") { +mission.missionName }
            mission.player?.let { player ->
                div(classes = "mission-player") { +player }
            }
            div(classes = "mission-block-content") {
                render(this, mission.scoringRules)
            }
        }
    }

    private fun renderDivider(container: FlowContent) {
        container.hr(classes = "guidance-divider")
    }

    private fun renderSpacer(container: FlowContent) {
        container.div(classes = "guidance-spacer")
    }

    private fun renderSection(container: FlowContent, section: GuidanceContent.Section) {
        container.div(classes = "guidance-section") {
            section.title?.let { title ->
                h4(classes = "guidance-section-title") { +title }
            }
            render(this, section.content)
        }
    }
}
