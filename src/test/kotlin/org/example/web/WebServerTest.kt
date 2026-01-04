package org.example.web

import io.ktor.client.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.sessions.*
import io.ktor.server.testing.*
import org.example.fixtures.TestFixtures
import org.example.mission.Mission
import org.example.phase.SetupPhase
import kotlin.test.*

/**
 * Tests for the web server routes.
 */
class WebServerTest {

    @BeforeTest
    fun setup() {
        TestFixtures.setupAvailableMissions()
    }

    @AfterTest
    fun teardown() {
        TestFixtures.clearAvailableMissions()
    }

    @Test
    fun `home page returns 200 and contains title`() = testApplication {
        application {
            configureTestServer()
        }

        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("Warhammer 40,000"))
        assertTrue(body.contains("Game Guide"))
    }

    @Test
    fun `start endpoint redirects to phase with version`() = testApplication {
        application {
            configureTestServer()
        }

        val response = client.post("/start") {
            header("Cookie", "GAME_SESSION=test-session-id")
        }
        // Should redirect with version parameter
        assertEquals(HttpStatusCode.Found, response.status)
        val location = response.headers["Location"]
        assertNotNull(location)
        assertTrue(location.startsWith("/phase?v="), "Location should be /phase?v=X but was $location")
    }

    @Test
    fun `phase page returns 200`() = testApplication {
        application {
            configureTestServer()
        }

        val response = client.get("/phase")
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("MUSTER ARMIES"))
    }

    @Test
    fun `phase advance works for non-input phases`() = testApplication {
        application {
            configureTestServer()
        }

        // First select battle size to get to ReadMissionObjectives
        client.post("/phase/select") {
            header("Content-Type", "application/x-www-form-urlencoded")
            setBody("choice=2") // Strike Force
        }

        // Select a primary mission to get to DisplayMissionDetails (non-input)
        client.post("/phase/select") {
            header("Content-Type", "application/x-www-form-urlencoded")
            setBody("choice=1")
        }

        // Now advance should work
        val response = client.post("/phase/advance")
        assertEquals(HttpStatusCode.Found, response.status)
    }

    @Test
    fun `phase select handles battle size selection`() = testApplication {
        application {
            configureTestServer()
        }

        // Create a client that preserves cookies
        val testClient = createClient {
            install(HttpCookies)
            followRedirects = false
        }

        val response = testClient.post("/phase/select") {
            header("Content-Type", "application/x-www-form-urlencoded")
            setBody("choice=1") // Incursion
        }

        // Should redirect to phase
        assertEquals(HttpStatusCode.Found, response.status)

        // Verify we moved to next phase (with same client to preserve session)
        val phaseResponse = testClient.get("/phase")
        val body = phaseResponse.bodyAsText()
        assertTrue(body.contains("Primary Mission") || body.contains("MISSION") || body.contains("READ"))
    }

    @Test
    fun `HTMX request returns HX-Redirect header`() = testApplication {
        application {
            configureTestServer()
        }

        val response = client.post("/phase/select") {
            header("Content-Type", "application/x-www-form-urlencoded")
            header("HX-Request", "true")
            setBody("choice=2")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        // Should return HX-Redirect header for full page navigation
        val hxRedirect = response.headers["HX-Redirect"]
        assertNotNull(hxRedirect, "HTMX request should return HX-Redirect header")
        assertTrue(hxRedirect.contains("v="), "HX-Redirect should include version")
    }

    @Test
    fun `status endpoint returns game status`() = testApplication {
        application {
            configureTestServer()
        }

        val response = client.get("/status")
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("Battle Size") || body.contains("STRIKE FORCE"))
    }

    @Test
    fun `reset endpoint restarts the game`() = testApplication {
        application {
            configureTestServer()
        }

        // First progress the game
        client.post("/phase/select") {
            header("Content-Type", "application/x-www-form-urlencoded")
            setBody("choice=1")
        }

        // Reset
        val resetResponse = client.post("/reset")
        assertEquals(HttpStatusCode.Found, resetResponse.status)

        // Verify we're back at start
        val phaseResponse = client.get("/phase")
        val body = phaseResponse.bodyAsText()
        assertTrue(body.contains("MUSTER ARMIES"))
    }

    @Test
    fun `game state persists across requests`() = testApplication {
        application {
            configureTestServer()
        }

        // Create a client that preserves cookies
        val testClient = createClient {
            install(HttpCookies)
            followRedirects = false
        }

        // Select Incursion (1000 pts)
        testClient.post("/phase/select") {
            header("Content-Type", "application/x-www-form-urlencoded")
            setBody("choice=1")
        }

        // Check status reflects the selection (with same client to preserve session)
        val statusResponse = testClient.get("/status")
        val body = statusResponse.bodyAsText()
        assertTrue(body.contains("INCURSION") || body.contains("1000"))
    }
}

/**
 * Configures a test server with mock data.
 */
private fun Application.configureTestServer() {
    // Set up test missions
    TestFixtures.setupAvailableMissions()

    install(Sessions) {
        cookie<GameSession>("GAME_SESSION") {
            cookie.path = "/"
        }
    }

    // Create a test server instance with in-memory state
    val testServer = TestWebServer()
    configureRoutes(testServer)
}

/**
 * A simplified WebServer for testing that doesn't load files.
 */
private class TestWebServer : WebServer("", "") {
    init {
        // Missions are already set up via TestFixtures
    }
}
