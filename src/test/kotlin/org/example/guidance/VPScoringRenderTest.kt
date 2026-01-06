package org.example.guidance

import kotlinx.html.div
import kotlinx.html.stream.createHTML
import org.example.fixtures.TestFixtures
import org.example.game.GameState
import org.example.phase.*
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Integration tests that verify battle phases render correctly to HTML.
 */
class BattlePhaseRenderTest {

    // ========== Command Phase ==========

    @Test
    fun `Command Phase renders numbered list and info box`() {
        val state = TestFixtures.gameStateAtRound(1)
        val content = CommandPhase.displayStructuredGuidance(state)
        val html = createHTML().div { HtmlRenderer.render(this, content) }

        assertTrue(html.contains("<ol>"), "Should render numbered list")
        assertTrue(html.contains("COMMAND"), "Should have COMMAND step")
        assertTrue(html.contains("BATTLE-SHOCK"), "Should have BATTLE-SHOCK step")
        assertTrue(html.contains("info-box"), "Should render info box")
        assertTrue(html.contains("warning"), "Should have warning variant for consequences")
    }

    // ========== Movement Phase ==========

    @Test
    fun `Movement Phase renders key-value pairs and headers`() {
        val state = TestFixtures.gameStateAtRound(2)
        val content = MovementPhase.displayStructuredGuidance(state)
        val html = createHTML().div { HtmlRenderer.render(this, content) }

        assertTrue(html.contains("key-value"), "Should render key-value pairs")
        assertTrue(html.contains("REMAIN STATIONARY"), "Should have movement option")
        assertTrue(html.contains("NORMAL MOVE"), "Should have normal move option")
        assertTrue(html.contains("ADVANCE"), "Should have advance option")
        assertTrue(html.contains("guidance-header-2"), "Should have h4 header for Reinforcements")
        assertTrue(html.contains("Reinforcements"), "Should have reinforcements section")
        assertTrue(html.contains("Deep Strike"), "Should mention Deep Strike")
    }

    // ========== Shooting Phase ==========

    @Test
    fun `Shooting Phase renders table and attack sequence`() {
        val state = TestFixtures.gameStateAtRound(1)
        val content = ShootingPhase.displayStructuredGuidance(state)
        val html = createHTML().div { HtmlRenderer.render(this, content) }

        assertTrue(html.contains("<ol>"), "Should render numbered list")
        assertTrue(html.contains("SELECT TARGETS"), "Should have target selection step")
        assertTrue(html.contains("guidance-table"), "Should render wound table")
        assertTrue(html.contains("S vs T"), "Should have S vs T header")
        assertTrue(html.contains("2+"), "Should have wound roll values")
        assertTrue(html.contains("Big Guns Never Tire"), "Should have Big Guns info box")
    }

    // ========== Charge Phase ==========

    @Test
    fun `Charge Phase renders sequence and success box`() {
        val state = TestFixtures.gameStateAtRound(1)
        val content = ChargePhase.displayStructuredGuidance(state)
        val html = createHTML().div { HtmlRenderer.render(this, content) }

        assertTrue(html.contains("<ol>"), "Should render numbered list")
        assertTrue(html.contains("SELECT UNIT TO CHARGE"), "Should have charge step")
        assertTrue(html.contains("CHARGE ROLL"), "Should have charge roll step")
        assertTrue(html.contains("SUCCESS"), "Should have success outcome")
        assertTrue(html.contains("FAILURE"), "Should have failure outcome")
        assertTrue(html.contains("success"), "Should have success info box variant")
        assertTrue(html.contains("fight first"), "Should mention fights first bonus")
    }

    // ========== Fight Phase ==========

    @Test
    fun `Fight Phase renders fight order and pile in`() {
        val state = TestFixtures.gameStateAtRound(1)
        val content = FightPhase.displayStructuredGuidance(state)
        val html = createHTML().div { HtmlRenderer.render(this, content) }

        assertTrue(html.contains("<ol>"), "Should render numbered list")
        assertTrue(html.contains("FIGHTS FIRST"), "Should have fights first step")
        assertTrue(html.contains("guidance-divider"), "Should have divider")
        assertTrue(html.contains("Pile In"), "Should mention Pile In")
        assertTrue(html.contains("Consolidate"), "Should mention Consolidate")
        assertTrue(html.contains("reminder"), "Should have reminder info box")
    }

    // ========== End of Turn Phase ==========

    @Test
    fun `End of Turn Phase renders player info`() {
        val state = TestFixtures.gameStateAtRound(1)
        val content = EndOfTurnPhase.displayStructuredGuidance(state)
        val html = createHTML().div { HtmlRenderer.render(this, content) }

        assertTrue(html.contains("Player"), "Should mention player")
        assertTrue(html.contains("turn is ending"), "Should indicate turn ending")
    }

    // ========== End Game Phase ==========

    @Test
    fun `End Game Phase renders victory conditions`() {
        val state = TestFixtures.gameStateAtRound(5)
        val content = EndGamePhase.displayStructuredGuidance(state)
        val html = createHTML().div { HtmlRenderer.render(this, content) }

        assertTrue(html.contains("BATTLE HAS ENDED"), "Should have end game header")
        assertTrue(html.contains("info-box"), "Should render info box for VP breakdown")
        assertTrue(html.contains("VP"), "Should mention VP")
        assertTrue(html.contains("Victor"), "Should have determine victor section")
        assertTrue(html.contains("wins"), "Should explain who wins")
        assertTrue(html.contains("success"), "Should have success variant for thank you")
    }

    // ========== Setup Phases ==========

    @Test
    fun `MusterArmies renders battle size table`() {
        val state = GameState()
        val content = SetupPhase.MusterArmies.displayStructuredGuidance(state)
        val html = createHTML().div { HtmlRenderer.render(this, content) }

        assertTrue(html.contains("guidance-table"), "Should render table")
        assertTrue(html.contains("Incursion"), "Should have Incursion option")
        assertTrue(html.contains("Strike Force"), "Should have Strike Force option")
        assertTrue(html.contains("Onslaught"), "Should have Onslaught option")
        assertTrue(html.contains("1000 pts"), "Should show points")
    }

    @Test
    fun `CreateBattlefield renders terrain info`() {
        val state = TestFixtures.gameStateInBattle()
        val content = SetupPhase.CreateBattlefield.displayStructuredGuidance(state)
        val html = createHTML().div { HtmlRenderer.render(this, content) }

        assertTrue(html.contains("key-value"), "Should render key-value pairs")
        assertTrue(html.contains("Battlefield"), "Should mention battlefield")
        assertTrue(html.contains("Terrain"), "Should have terrain guidelines")
    }

    @Test
    fun `DetermineAttacker renders roll-off info`() {
        val state = GameState()
        val content = SetupPhase.DetermineAttacker.displayStructuredGuidance(state)
        val html = createHTML().div { HtmlRenderer.render(this, content) }

        assertTrue(html.contains("key-value"), "Should render key-value pairs")
        assertTrue(html.contains("Winner"), "Should mention winner")
        assertTrue(html.contains("Attacker"), "Should mention attacker")
        assertTrue(html.contains("Defender"), "Should mention defender")
    }

    @Test
    fun `DeclareBattleFormations renders formation steps`() {
        val state = GameState()
        val content = SetupPhase.DeclareBattleFormations.displayStructuredGuidance(state)
        val html = createHTML().div { HtmlRenderer.render(this, content) }

        assertTrue(html.contains("<ol>"), "Should render numbered list")
        assertTrue(html.contains("Leader"), "Should mention leaders")
        assertTrue(html.contains("Embarked"), "Should mention embarked units")
        assertTrue(html.contains("Reserves"), "Should mention reserves")
    }

    @Test
    fun `DeployArmies renders deployment order`() {
        val state = TestFixtures.gameStateInBattle()
        val content = SetupPhase.DeployArmies.displayStructuredGuidance(state)
        val html = createHTML().div { HtmlRenderer.render(this, content) }

        assertTrue(html.contains("<ol>"), "Should render numbered list")
        assertTrue(html.contains("Attacker"), "Should mention attacker")
        assertTrue(html.contains("Defender"), "Should mention defender")
        assertTrue(html.contains("Infiltrators"), "Should mention infiltrators")
    }

    @Test
    fun `PreBattleRules renders scouts info`() {
        val state = GameState()
        val content = SetupPhase.PreBattleRules.displayStructuredGuidance(state)
        val html = createHTML().div { HtmlRenderer.render(this, content) }

        assertTrue(html.contains("info-box"), "Should have info boxes")
        assertTrue(html.contains("Scouts"), "Should mention Scouts")
        assertTrue(html.contains("9"), "Should mention 9 inch distance")
    }

    @Test
    fun `DetermineFirstTurn renders first turn info`() {
        val state = GameState()
        val content = SetupPhase.DetermineFirstTurn.displayStructuredGuidance(state)
        val html = createHTML().div { HtmlRenderer.render(this, content) }

        assertTrue(html.contains("first turn"), "Should mention first turn")
        assertTrue(html.contains("roll"), "Should mention roll-off")
    }
}
