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
