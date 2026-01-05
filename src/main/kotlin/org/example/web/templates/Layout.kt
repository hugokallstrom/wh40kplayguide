package org.example.web.templates

import kotlinx.html.*

/**
 * Renders the base HTML layout with PicoCSS and HTMX.
 */
fun HTML.renderLayout(title: String, content: MAIN.() -> Unit) {
    head {
        meta { charset = "utf-8" }
        meta {
            name = "viewport"
            this.content = "width=device-width, initial-scale=1"
        }
        title { +title }

        // PicoCSS - classless CSS framework
        link {
            rel = "stylesheet"
            href = "https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css"
        }

        // HTMX for dynamic updates
        script {
            src = "https://unpkg.com/htmx.org@1.9.10"
        }

        // Custom styles
        style {
            unsafe {
                raw(
                    """
                    :root {
                        --pico-font-size: 16px;
                    }

                    body {
                        min-height: 100vh;
                    }

                    .container {
                        max-width: 1200px;
                        padding: 1rem;
                    }

                    .game-layout {
                        display: grid;
                        grid-template-columns: 1fr 300px;
                        gap: 2rem;
                        align-items: start;
                    }

                    @media (max-width: 768px) {
                        .game-layout {
                            grid-template-columns: 1fr;
                        }
                    }

                    .phase-content {
                        background: var(--pico-card-background-color);
                        border-radius: var(--pico-border-radius);
                        padding: 1.5rem;
                    }

                    .status-sidebar {
                        position: sticky;
                        top: 1rem;
                    }

                    .status-sidebar article {
                        margin-bottom: 0;
                    }

                    .guidance-text {
                        white-space: pre-wrap;
                        font-family: var(--pico-font-family-monospace);
                        font-size: 0.9rem;
                        line-height: 1.6;
                        background: var(--pico-code-background-color);
                        padding: 1rem;
                        border-radius: var(--pico-border-radius);
                        overflow-x: auto;
                    }

                    /* Structured Guidance Content Styles */
                    .guidance-content {
                        line-height: 1.7;
                    }

                    .guidance-content p {
                        margin: 0.75rem 0;
                    }

                    .guidance-header-1 {
                        font-size: 1.25rem;
                        font-weight: 600;
                        margin: 1.5rem 0 0.75rem;
                        color: var(--pico-h2-color);
                        border-bottom: 2px solid var(--pico-primary);
                        padding-bottom: 0.25rem;
                    }

                    .guidance-header-1:first-child {
                        margin-top: 0;
                    }

                    .guidance-header-2 {
                        font-size: 1.1rem;
                        font-weight: 600;
                        margin: 1rem 0 0.5rem;
                        color: var(--pico-h3-color);
                    }

                    .guidance-header-3 {
                        font-size: 1rem;
                        font-weight: 600;
                        margin: 0.75rem 0 0.5rem;
                        color: var(--pico-h4-color);
                    }

                    /* Mission Block */
                    .mission-block {
                        background: var(--pico-card-background-color);
                        border-left: 4px solid var(--pico-primary);
                        padding: 1rem 1.25rem;
                        margin: 1rem 0;
                        border-radius: 0 var(--pico-border-radius) var(--pico-border-radius) 0;
                    }

                    .mission-block-header {
                        font-weight: 600;
                        font-size: 1.1rem;
                        margin-bottom: 0.5rem;
                        color: var(--pico-primary);
                    }

                    .mission-player {
                        font-size: 0.85rem;
                        color: var(--pico-muted-color);
                        margin-bottom: 0.75rem;
                        font-style: italic;
                    }

                    .mission-block-content {
                        font-size: 0.95rem;
                    }

                    .mission-block-content ul {
                        margin: 0.5rem 0;
                        padding-left: 1.25rem;
                    }

                    .mission-block-content li {
                        margin: 0.25rem 0;
                    }

                    /* Info Boxes */
                    .info-box {
                        padding: 1rem;
                        border-radius: var(--pico-border-radius);
                        margin: 1rem 0;
                    }

                    .info-box.info {
                        background: rgba(var(--pico-primary-rgb, 24, 144, 255), 0.1);
                        border: 1px solid var(--pico-primary);
                    }

                    .info-box.reminder {
                        background: rgba(100, 149, 237, 0.15);
                        border: 1px solid cornflowerblue;
                    }

                    .info-box.warning {
                        background: rgba(255, 193, 7, 0.15);
                        border: 1px solid #ffc107;
                    }

                    .info-box.success {
                        background: rgba(40, 167, 69, 0.15);
                        border: 1px solid #28a745;
                    }

                    .info-box-title {
                        font-weight: 600;
                        margin-bottom: 0.5rem;
                        font-size: 0.95rem;
                    }

                    .info-box-content {
                        font-size: 0.9rem;
                    }

                    .info-box-content p {
                        margin: 0.25rem 0;
                    }

                    /* Key-Value Pairs */
                    .key-value-list {
                        margin: 0.5rem 0;
                    }

                    .key-value-item {
                        display: flex;
                        padding: 0.25rem 0;
                        gap: 0.75rem;
                    }

                    .key-value-key {
                        font-weight: 600;
                        min-width: 100px;
                        color: var(--pico-h6-color);
                    }

                    .key-value-key::after {
                        content: ":";
                    }

                    .key-value-value {
                        flex: 1;
                    }

                    /* Tables */
                    .guidance-table {
                        width: 100%;
                        margin: 1rem 0;
                        font-size: 0.9rem;
                        border-collapse: collapse;
                    }

                    .guidance-table th {
                        background: var(--pico-table-row-stripped-background-color);
                        font-weight: 600;
                        text-align: left;
                    }

                    .guidance-table td, .guidance-table th {
                        padding: 0.5rem 0.75rem;
                        border-bottom: 1px solid var(--pico-muted-border-color);
                    }

                    .guidance-table tbody tr:hover {
                        background: var(--pico-table-row-stripped-background-color);
                    }

                    /* Divider and Spacer */
                    .guidance-divider {
                        border: none;
                        border-top: 1px solid var(--pico-muted-border-color);
                        margin: 1.5rem 0;
                    }

                    .guidance-spacer {
                        height: 1rem;
                    }

                    /* Section */
                    .guidance-section {
                        margin: 1rem 0;
                    }

                    .guidance-section-title {
                        font-size: 1rem;
                        font-weight: 600;
                        margin-bottom: 0.5rem;
                        color: var(--pico-h4-color);
                    }

                    .phase-header {
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        flex-wrap: wrap;
                        gap: 1rem;
                        margin-bottom: 1rem;
                    }

                    .turn-info {
                        font-size: 0.85rem;
                        color: var(--pico-muted-color);
                    }

                    .choice-group {
                        display: flex;
                        flex-direction: column;
                        gap: 0.5rem;
                        margin: 1rem 0;
                    }

                    .choice-button {
                        text-align: left;
                        justify-content: flex-start;
                    }

                    .action-buttons {
                        display: flex;
                        gap: 1rem;
                        flex-wrap: wrap;
                        margin-top: 1.5rem;
                    }

                    .hero-section {
                        text-align: center;
                        padding: 3rem 1rem;
                    }

                    .status-item {
                        display: flex;
                        justify-content: space-between;
                        padding: 0.5rem 0;
                        border-bottom: 1px solid var(--pico-muted-border-color);
                    }

                    .status-item:last-child {
                        border-bottom: none;
                    }

                    .status-label {
                        color: var(--pico-muted-color);
                    }

                    .htmx-indicator {
                        opacity: 0;
                        transition: opacity 200ms ease-in;
                    }

                    .htmx-request .htmx-indicator {
                        opacity: 1;
                    }

                    .loading {
                        display: inline-block;
                        width: 1rem;
                        height: 1rem;
                        border: 2px solid var(--pico-muted-color);
                        border-radius: 50%;
                        border-top-color: var(--pico-primary);
                        animation: spin 1s linear infinite;
                    }

                    @keyframes spin {
                        to { transform: rotate(360deg); }
                    }
                    """.trimIndent()
                )
            }
        }
    }

    body {
        attributes["data-theme"] = "dark"

        main(classes = "container") {
            content()
        }
    }
}
