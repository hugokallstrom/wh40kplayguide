package org.example.web

import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.sessions.*
import io.ktor.server.testing.*
import org.example.fixtures.TestFixtures
import org.example.game.BattleSize
import org.example.phase.SetupPhase
import kotlin.test.*

/**
 * Tests for browser history functionality (back button support).
 */
class HistoryTest {

    @BeforeTest
    fun setup() {
        TestFixtures.setupAvailableMissions()
    }

    @AfterTest
    fun teardown() {
        TestFixtures.clearAvailableMissions()
    }

    // ========== SessionGameData Tests ==========

    @Test
    fun `new session starts at version 0`() {
        val gameData = SessionGameData()
        assertEquals(0, gameData.version)
        assertTrue(gameData.snapshots.isEmpty())
    }

    @Test
    fun `saveSnapshot stores current state and phase`() {
        val gameData = SessionGameData()
        gameData.state.battleSize = BattleSize.INCURSION
        gameData.currentPhase = SetupPhase.ReadMissionObjectives

        gameData.saveSnapshot()

        val snapshot = gameData.snapshots[0]
        assertNotNull(snapshot)
        assertEquals(BattleSize.INCURSION, snapshot.first.battleSize)
        assertEquals(SetupPhase.ReadMissionObjectives, snapshot.second)
    }

    @Test
    fun `saveSnapshot creates independent copy of state`() {
        val gameData = SessionGameData()
        gameData.state.battleSize = BattleSize.INCURSION

        gameData.saveSnapshot()

        // Modify original state
        gameData.state.battleSize = BattleSize.ONSLAUGHT

        // Snapshot should still have old value
        val snapshot = gameData.snapshots[0]
        assertNotNull(snapshot)
        assertEquals(BattleSize.INCURSION, snapshot.first.battleSize)
    }

    @Test
    fun `restoreSnapshot restores state and phase`() {
        val gameData = SessionGameData()

        // Save initial state
        gameData.state.battleSize = BattleSize.INCURSION
        gameData.currentPhase = SetupPhase.ReadMissionObjectives
        gameData.saveSnapshot()

        // Advance
        gameData.version++
        gameData.state.battleSize = BattleSize.ONSLAUGHT
        gameData.currentPhase = SetupPhase.CreateBattlefield
        gameData.saveSnapshot()

        // Restore to version 0
        val restored = gameData.restoreSnapshot(0)

        assertTrue(restored)
        assertEquals(0, gameData.version)
        assertEquals(BattleSize.INCURSION, gameData.state.battleSize)
        assertEquals(SetupPhase.ReadMissionObjectives, gameData.currentPhase)
    }

    @Test
    fun `restoreSnapshot returns false for non-existent version`() {
        val gameData = SessionGameData()
        val restored = gameData.restoreSnapshot(999)
        assertFalse(restored)
    }

    // ========== Route Tests ==========

    @Test
    fun `advance redirects with version parameter`() = testApplication {
        application {
            configureHistoryTestServer()
        }

        val testClient = createClient {
            install(HttpCookies)
            followRedirects = false
        }

        // Select battle size to advance
        testClient.post("/phase/select") {
            header("Content-Type", "application/x-www-form-urlencoded")
            setBody("choice=1")
        }

        // Check redirect includes version
        val selectResponse = testClient.post("/phase/select") {
            header("Content-Type", "application/x-www-form-urlencoded")
            setBody("choice=1")
        }

        assertEquals(HttpStatusCode.Found, selectResponse.status)
        val location = selectResponse.headers["Location"]
        assertNotNull(location)
        assertTrue(location.contains("v="), "Location should include version: $location")
    }

    @Test
    fun `loading phase with old version restores that state`() = testApplication {
        application {
            configureHistoryTestServer()
        }

        val testClient = createClient {
            install(HttpCookies)
            followRedirects = false
        }

        // Select INCURSION (choice=1)
        val firstResponse = testClient.post("/phase/select") {
            header("Content-Type", "application/x-www-form-urlencoded")
            setBody("choice=1")
        }
        val firstLocation = firstResponse.headers["Location"]
        assertNotNull(firstLocation)

        // Continue to select mission
        testClient.post("/phase/select") {
            header("Content-Type", "application/x-www-form-urlencoded")
            setBody("choice=1")
        }

        // Now go back to first version by loading old URL
        val backResponse = testClient.get(firstLocation)
        assertEquals(HttpStatusCode.OK, backResponse.status)

        // Verify we're back at the expected phase
        val body = backResponse.bodyAsText()
        assertTrue(
            body.contains("Primary Mission") || body.contains("MISSION") || body.contains("READ"),
            "Should be at ReadMissionObjectives phase after going back"
        )
    }

    @Test
    fun `reset clears snapshots and redirects to version 0`() = testApplication {
        application {
            configureHistoryTestServer()
        }

        val testClient = createClient {
            install(HttpCookies)
            followRedirects = false
        }

        // Progress the game
        testClient.post("/phase/select") {
            header("Content-Type", "application/x-www-form-urlencoded")
            setBody("choice=1")
        }

        // Reset
        val resetResponse = testClient.post("/reset")
        assertEquals(HttpStatusCode.Found, resetResponse.status)

        // Verify back at start
        val phaseResponse = testClient.get("/phase")
        val body = phaseResponse.bodyAsText()
        assertTrue(body.contains("MUSTER ARMIES"))
    }

    @Test
    fun `HTMX advance returns HX-Redirect header`() = testApplication {
        application {
            configureHistoryTestServer()
        }

        val testClient = createClient {
            install(HttpCookies)
            followRedirects = false
        }

        // First advance to a non-input phase
        testClient.post("/phase/select") {
            header("Content-Type", "application/x-www-form-urlencoded")
            setBody("choice=1&version=0")
        }
        testClient.post("/phase/select") {
            header("Content-Type", "application/x-www-form-urlencoded")
            setBody("choice=1&version=1")
        }

        // HTMX request to advance (version=2 from previous selections)
        val response = testClient.post("/phase/advance") {
            header("HX-Request", "true")
            header("Content-Type", "application/x-www-form-urlencoded")
            setBody("version=2")
        }

        // Should have HX-Redirect header (not partial HTML)
        val hxRedirect = response.headers["HX-Redirect"]
        assertNotNull(hxRedirect, "Should have HX-Redirect header for HTMX requests")
        assertTrue(hxRedirect.contains("v="), "HX-Redirect should include version")
    }
}

/**
 * Configures a test server with mock data.
 */
private fun Application.configureHistoryTestServer() {
    TestFixtures.setupAvailableMissions()

    install(Sessions) {
        cookie<GameSession>("GAME_SESSION") {
            cookie.path = "/"
        }
    }

    val testServer = HistoryTestWebServer()
    configureRoutes(testServer)
}

/**
 * A simplified WebServer for testing that doesn't load files.
 */
private class HistoryTestWebServer : WebServer("", "")
