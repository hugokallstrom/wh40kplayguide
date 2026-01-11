package org.example.web.templates

import kotlinx.html.*

/**
 * Renders the base HTML layout with A24-inspired design system and HTMX.
 */
fun HTML.renderLayout(title: String, content: MAIN.() -> Unit) {
    head {
        meta { charset = "utf-8" }
        meta {
            name = "viewport"
            this.content = "width=device-width, initial-scale=1"
        }
        title { +title }

        // Google Fonts - Nunito for body, Lilita One for headings
        link {
            rel = "preconnect"
            href = "https://fonts.googleapis.com"
        }
        link {
            rel = "preconnect"
            href = "https://fonts.gstatic.com"
            attributes["crossorigin"] = ""
        }
        link {
            rel = "stylesheet"
            href = "https://fonts.googleapis.com/css2?family=Lilita+One&family=Nunito:wght@400;500;600;700&display=swap"
        }

        // HTMX for dynamic updates
        script {
            src = "https://unpkg.com/htmx.org@1.9.10"
        }

        // A24-inspired design system styles
        style {
            unsafe {
                raw(
                    """
/* ============================================
   A24-INSPIRED DESIGN SYSTEM
   Warhammer 40K Game Guide
   ============================================ */

/* ----- CSS Variables ----- */
:root {
    /* Colors - Backgrounds (Dark Theme) */
    --color-bg-primary: #0D0D0D;
    --color-bg-secondary: #1A1A1A;
    --color-bg-tertiary: #252525;
    --color-bg-elevated: #2A2A2A;

    /* Colors - Text */
    --color-text-primary: #F5F5F5;
    --color-text-secondary: #B3B3B3;
    --color-text-muted: #808080;
    --color-text-disabled: #4A4A4A;

    /* Colors - Accent (Warhammer gold/brass) */
    --color-accent-primary: #C9A227;
    --color-accent-hover: #DDB82E;
    --color-accent-focus: #E5C745;
    --color-link: #C9A227;
    --color-link-hover: #DDB82E;

    /* Colors - Borders */
    --color-border-light: #333333;
    --color-border-medium: #444444;
    --color-border-dark: #555555;

    /* Colors - Info Box Variants (Muted, gold-complementary) */
    --color-info-bg: #1F1F1F;
    --color-info-border: #8A8A6C;
    --color-warning-bg: #252218;
    --color-warning-border: #A68B3D;
    --color-success-bg: #1E2218;
    --color-success-border: #7A8A5C;
    --color-reminder-bg: #201E1A;
    --color-reminder-border: #9A8A6A;

    /* Typography */
    --font-heading: 'Lilita One', Impact, 'Arial Black', sans-serif;
    --font-body: 'Nunito', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
    --font-mono: 'JetBrains Mono', 'SF Mono', 'Consolas', monospace;

    /* Spacing */
    --space-1: 0.25rem;
    --space-2: 0.5rem;
    --space-3: 0.75rem;
    --space-4: 1rem;
    --space-5: 1.5rem;
    --space-6: 2rem;
    --space-8: 3rem;
    --space-10: 4rem;
    --space-12: 6rem;

    /* Layout */
    --container-max-width: 1140px;
    --container-padding: 2rem;
    --container-padding-mobile: 1rem;
}

/* ----- CSS Reset ----- */
*, *::before, *::after {
    box-sizing: border-box;
    margin: 0;
    padding: 0;
}

html {
    font-size: 16px;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
}

body {
    font-family: var(--font-body);
    font-size: 1rem;
    line-height: 1.7;
    color: var(--color-text-primary);
    background: var(--color-bg-primary);
    min-height: 100vh;
}

/* ----- Typography ----- */
h1, h2, h3, h4, h5, h6 {
    font-family: var(--font-heading);
    color: var(--color-text-primary);
    line-height: 1.3;
}

h1 { font-size: 2.5rem; font-weight: 600; letter-spacing: -0.02em; }
h2 { font-size: 1.75rem; font-weight: 600; letter-spacing: -0.01em; }
h3 { font-size: 1.25rem; font-weight: 600; font-family: var(--font-body); }
h4 { font-size: 1.125rem; font-weight: 600; font-family: var(--font-body); }
h5 { font-size: 1rem; font-weight: 600; font-family: var(--font-body); }

p { margin: var(--space-3) 0; }

a {
    color: var(--color-link);
    text-decoration: underline;
    text-underline-offset: 3px;
    text-decoration-thickness: 1px;
    transition: all 150ms ease;
}

a:hover {
    color: var(--color-link-hover);
    text-decoration-thickness: 2px;
}

strong { font-weight: 600; color: var(--color-text-primary); }
em { font-style: italic; color: var(--color-text-secondary); }

hr {
    border: none;
    border-top: 1px solid var(--color-border-light);
    margin: var(--space-5) 0;
}

/* ----- Layout ----- */
.container {
    max-width: var(--container-max-width);
    margin: 0 auto;
    padding: var(--container-padding);
}

.game-layout {
    display: grid;
    grid-template-columns: 1fr 320px;
    gap: var(--space-8);
    align-items: start;
}

/* ----- Phase Content Card ----- */
.phase-content {
    background: var(--color-bg-secondary);
    border: 1px solid var(--color-border-light);
    padding: var(--space-8);
}

.phase-content header {
    margin-bottom: var(--space-6);
}

.phase-content footer {
    margin-top: var(--space-6);
    padding-top: var(--space-5);
    border-top: 1px solid var(--color-border-light);
}

/* ----- Phase Header ----- */
.phase-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    flex-wrap: wrap;
    gap: var(--space-4);
    padding-bottom: var(--space-5);
    border-bottom: 2px solid var(--color-text-primary);
    margin-bottom: var(--space-5);
}

.phase-header h2 {
    font-family: var(--font-body);
    font-size: 1.75rem;
    font-weight: 700;
    margin: 0;
}

.turn-info {
    font-size: 0.75rem;
    font-weight: 500;
    color: var(--color-text-muted);
    text-transform: uppercase;
    letter-spacing: 0.05em;
    margin-top: var(--space-2);
}

/* ----- Buttons ----- */
button {
    font-family: var(--font-body);
    font-size: 0.9375rem;
    font-weight: 500;
    letter-spacing: 0.02em;
    text-transform: uppercase;
    background: var(--color-accent-primary);
    color: #FFFFFF;
    border: 2px solid var(--color-accent-primary);
    border-radius: 0;
    padding: 0.875rem 2rem;
    cursor: pointer;
    transition: all 150ms ease;
}

button:hover {
    background: var(--color-accent-hover);
    border-color: var(--color-accent-hover);
}

button:focus-visible {
    outline: 2px solid var(--color-accent-focus);
    outline-offset: 2px;
}

button:active {
    transform: translateY(1px);
}

button.secondary,
button.outline {
    background: transparent;
    color: var(--color-accent-primary);
    border: 2px solid var(--color-accent-primary);
}

button.secondary:hover,
button.outline:hover {
    background: var(--color-accent-primary);
    color: #FFFFFF;
}

.choice-button {
    display: block;
    width: 100%;
    text-align: left;
    background: transparent;
    color: var(--color-accent-primary);
    border: 2px solid var(--color-accent-primary);
    padding: var(--space-4) var(--space-5);
    margin-bottom: var(--space-3);
}

.choice-button:hover {
    background: var(--color-accent-primary);
    color: #FFFFFF;
}

.choice-button:last-child {
    margin-bottom: 0;
}

/* ----- Action Buttons ----- */
.action-buttons {
    display: flex;
    gap: var(--space-4);
    flex-wrap: wrap;
}

.choice-group {
    display: flex;
    flex-direction: column;
}

/* ----- Status Sidebar ----- */
.status-sidebar {
    position: sticky;
    top: var(--space-6);
}

.status-sidebar article {
    background: var(--color-bg-secondary);
    border: 1px solid var(--color-border-light);
    padding: var(--space-5);
    margin-bottom: 0;
}

.status-sidebar article header {
    border-bottom: 1px solid var(--color-border-light);
    padding-bottom: var(--space-3);
    margin-bottom: var(--space-4);
}

.status-sidebar article header h3 {
    font-family: var(--font-body);
    font-size: 1.125rem;
    font-weight: 700;
    margin: 0;
}

.status-item {
    display: flex;
    justify-content: space-between;
    align-items: baseline;
    padding: var(--space-2) 0;
    border-bottom: 1px solid var(--color-border-light);
    font-size: 0.875rem;
}

.status-item:last-child {
    border-bottom: none;
}

.status-label {
    color: var(--color-text-muted);
    font-weight: 400;
    text-transform: uppercase;
    font-size: 0.75rem;
    letter-spacing: 0.05em;
}

.status-item span:last-child {
    font-weight: 500;
    color: var(--color-text-primary);
}

/* ----- Guidance Content ----- */
.guidance-content {
    line-height: 1.7;
}

.guidance-content p {
    margin: var(--space-3) 0;
}

.guidance-text {
    white-space: pre-wrap;
    font-family: var(--font-mono);
    font-size: 0.9rem;
    line-height: 1.6;
    background: var(--color-bg-tertiary);
    padding: var(--space-4);
    overflow-x: auto;
}

.guidance-header-1 {
    font-family: var(--font-body);
    font-size: 1.375rem;
    font-weight: 700;
    margin: var(--space-6) 0 var(--space-4);
    color: var(--color-text-primary);
    padding-bottom: var(--space-2);
    border-bottom: 1px solid var(--color-border-medium);
}

.guidance-header-1:first-child {
    margin-top: 0;
}

.guidance-header-2 {
    font-family: var(--font-body);
    font-size: 1.125rem;
    font-weight: 600;
    margin: var(--space-5) 0 var(--space-3);
    color: var(--color-text-primary);
}

.guidance-header-3 {
    font-family: var(--font-body);
    font-size: 1rem;
    font-weight: 600;
    margin: var(--space-4) 0 var(--space-2);
    color: var(--color-text-secondary);
}

/* ----- Formatted Text ----- */
.formatted-text {
    line-height: 1.7;
}

.formatted-text p {
    margin: var(--space-3) 0;
}

.formatted-text strong {
    font-weight: 600;
    color: var(--color-text-primary);
}

.formatted-text em {
    font-style: italic;
    color: var(--color-text-secondary);
}

/* ----- Lists ----- */
ul, ol {
    margin: var(--space-4) 0;
    padding-left: var(--space-5);
}

ul { list-style: square; }
ol { list-style: decimal; }

li {
    margin-bottom: var(--space-2);
    line-height: 1.6;
    padding-left: var(--space-2);
}

li::marker {
    color: var(--color-text-primary);
}

/* ----- Info Boxes ----- */
.info-box {
    padding: var(--space-5);
    margin: var(--space-5) 0;
    border-radius: 0;
    border-left: 3px solid var(--color-accent-primary);
    background: var(--color-bg-tertiary);
}

.info-box.info {
    background: var(--color-info-bg);
    border-left-color: var(--color-info-border);
}

.info-box.warning {
    background: var(--color-warning-bg);
    border-left-color: var(--color-warning-border);
}

.info-box.success {
    background: var(--color-success-bg);
    border-left-color: var(--color-success-border);
}

.info-box.reminder {
    background: var(--color-reminder-bg);
    border-left-color: var(--color-reminder-border);
}

.info-box-title {
    font-family: var(--font-body);
    font-size: 0.875rem;
    font-weight: 600;
    letter-spacing: 0.03em;
    text-transform: uppercase;
    margin-bottom: var(--space-3);
}

.info-box.info .info-box-title { color: var(--color-info-border); }
.info-box.warning .info-box-title { color: var(--color-warning-border); }
.info-box.success .info-box-title { color: var(--color-success-border); }
.info-box.reminder .info-box-title { color: var(--color-reminder-border); }

.info-box-content {
    font-size: 0.9375rem;
    line-height: 1.6;
}

.info-box-content p {
    margin: var(--space-2) 0;
}

/* ----- Mission Block ----- */
.mission-block {
    background: var(--color-bg-tertiary);
    border-left: 3px solid var(--color-text-primary);
    padding: var(--space-5);
    margin: var(--space-5) 0;
}

.mission-block-header {
    font-family: var(--font-body);
    font-weight: 700;
    font-size: 1.125rem;
    margin-bottom: var(--space-2);
    color: var(--color-text-primary);
}

.mission-player {
    font-size: 0.75rem;
    font-weight: 500;
    color: var(--color-text-muted);
    text-transform: uppercase;
    letter-spacing: 0.05em;
    margin-bottom: var(--space-4);
}

.mission-block-content {
    font-size: 0.9375rem;
    line-height: 1.6;
}

.mission-block-content ul {
    margin: var(--space-3) 0;
    padding-left: var(--space-5);
}

.mission-block-content li {
    margin: var(--space-2) 0;
}

/* ----- Key-Value Pairs ----- */
.key-value-list {
    margin: var(--space-4) 0;
}

.key-value-item {
    display: flex;
    padding: var(--space-2) 0;
    gap: var(--space-4);
    border-bottom: 1px solid var(--color-border-light);
}

.key-value-item:last-child {
    border-bottom: none;
}

.key-value-key {
    font-weight: 600;
    min-width: 120px;
    color: var(--color-text-secondary);
    font-size: 0.875rem;
}

.key-value-key::after {
    content: ":";
}

.key-value-value {
    flex: 1;
    color: var(--color-text-primary);
}

/* ----- Tables ----- */
.guidance-table {
    width: 100%;
    margin: var(--space-5) 0;
    border-collapse: collapse;
    font-size: 0.9375rem;
}

.guidance-table th {
    background: var(--color-bg-tertiary);
    font-weight: 600;
    font-size: 0.75rem;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    text-align: left;
    color: var(--color-text-secondary);
    padding: var(--space-3) var(--space-4);
    border-bottom: 2px solid var(--color-text-primary);
}

.guidance-table td {
    padding: var(--space-3) var(--space-4);
    border-bottom: 1px solid var(--color-border-light);
    vertical-align: top;
}

.guidance-table tbody tr:hover {
    background: var(--color-bg-tertiary);
}

/* ----- Divider & Spacer ----- */
.guidance-divider {
    border: none;
    border-top: 1px solid var(--color-border-light);
    margin: var(--space-5) 0;
}

.guidance-spacer {
    height: var(--space-4);
}

/* ----- Section ----- */
.guidance-section {
    margin: var(--space-5) 0;
}

.guidance-section-title {
    font-family: var(--font-body);
    font-size: 1rem;
    font-weight: 600;
    margin-bottom: var(--space-3);
    color: var(--color-text-secondary);
}

/* ----- Hero Section ----- */
.hero-section {
    text-align: center;
    padding: var(--space-12) var(--space-6);
    max-width: 640px;
    margin: 0 auto;
}

.hero-section h1 {
    font-family: var(--font-heading);
    font-size: 3rem;
    font-weight: 600;
    letter-spacing: -0.02em;
    line-height: 1.1;
    margin-bottom: var(--space-3);
}

.hero-section h2 {
    font-family: var(--font-body);
    font-size: 1.5rem;
    font-weight: 400;
    letter-spacing: 0.1em;
    text-transform: uppercase;
    margin-bottom: var(--space-5);
    color: var(--color-text-secondary);
}

.hero-section p {
    font-size: 1.125rem;
    color: var(--color-text-muted);
    line-height: 1.6;
    margin-bottom: var(--space-8);
}

.hero-section form {
    display: inline-block;
    margin-bottom: var(--space-4);
}

.hero-section button {
    padding: 1rem 3rem;
}

/* ----- Loading Indicator ----- */
.loading {
    display: inline-block;
    width: 1.25rem;
    height: 1.25rem;
    border: 2px solid var(--color-border-light);
    border-radius: 50%;
    border-top-color: var(--color-text-primary);
    animation: spin 0.8s linear infinite;
}

@keyframes spin {
    to { transform: rotate(360deg); }
}

.htmx-indicator {
    opacity: 0;
    transition: opacity 200ms ease;
}

.htmx-request .htmx-indicator {
    opacity: 1;
}

/* ----- Responsive Design ----- */
@media (max-width: 900px) {
    .game-layout {
        grid-template-columns: 1fr;
        gap: var(--space-6);
    }

    .status-sidebar {
        position: static;
        order: -1;
    }

    .phase-content {
        padding: var(--space-6);
    }
}

@media (max-width: 600px) {
    :root {
        --container-padding: var(--container-padding-mobile);
    }

    .hero-section {
        padding: var(--space-8) var(--space-4);
    }

    .hero-section h1 {
        font-size: 2.25rem;
    }

    .hero-section h2 {
        font-size: 1.25rem;
    }

    .phase-content {
        padding: var(--space-5);
    }

    .phase-header {
        flex-direction: column;
        gap: var(--space-2);
    }

    .action-buttons {
        flex-direction: column;
    }

    .action-buttons button {
        width: 100%;
    }

    .key-value-item {
        flex-direction: column;
        gap: var(--space-1);
    }

    .key-value-key {
        min-width: auto;
    }
}
                    """.trimIndent()
                )
            }
        }
    }

    body {
        main(classes = "container") {
            content()
        }
    }
}
