package org.example.phase

import org.example.fixtures.TestFixtures
import org.example.game.GameState
import org.example.guidance.GuidanceContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for structured guidance output from phases.
 */
class StructuredGuidanceTest {

    // ========== VPScoringPhase Tests ==========

    @Test
    fun `VPScoringPhase structured guidance contains intro paragraph`() {
        val state = TestFixtures.gameStateAtRound(2)
        val content = VPScoringPhase.displayStructuredGuidance(state)

        assertTrue(content.isNotEmpty(), "Should have content")
        // First item should be an intro paragraph or header
        val firstItem = content.first()
        assertTrue(
            firstItem is GuidanceContent.Paragraph || firstItem is GuidanceContent.Header,
            "First item should be Paragraph or Header"
        )
    }

    @Test
    fun `VPScoringPhase structured guidance contains primary mission block when set`() {
        val state = TestFixtures.gameStateInBattle().copy(currentRound = 2)
        val content = VPScoringPhase.displayStructuredGuidance(state)

        val missionBlocks = content.filterIsInstance<GuidanceContent.MissionBlock>()
        assertTrue(missionBlocks.isNotEmpty(), "Should contain at least one MissionBlock")

        val primaryBlock = missionBlocks.first()
        assertEquals("Take and Hold", primaryBlock.missionName)
    }

    @Test
    fun `VPScoringPhase structured guidance contains secondary mission blocks when set`() {
        val state = TestFixtures.gameStateInBattle().copy(currentRound = 2)
        val content = VPScoringPhase.displayStructuredGuidance(state)

        val missionBlocks = content.filterIsInstance<GuidanceContent.MissionBlock>()
        // Should have primary + attacker secondary + defender secondary = 3 blocks
        assertEquals(3, missionBlocks.size, "Should have 3 mission blocks (primary + 2 secondaries)")

        // Attacker secondary should have player info
        val attackerBlock = missionBlocks[1]
        assertTrue(
            attackerBlock.player?.contains("ATTACKER") == true,
            "Attacker block should contain ATTACKER"
        )

        // Defender secondary should have player info
        val defenderBlock = missionBlocks[2]
        assertTrue(
            defenderBlock.player?.contains("DEFENDER") == true,
            "Defender block should contain DEFENDER"
        )
    }

    @Test
    fun `VPScoringPhase structured guidance contains reminder info box`() {
        val state = TestFixtures.gameStateAtRound(2)
        val content = VPScoringPhase.displayStructuredGuidance(state)

        val infoBoxes = content.filterIsInstance<GuidanceContent.InfoBox>()
        assertTrue(infoBoxes.isNotEmpty(), "Should contain at least one InfoBox")

        val reminderBox = infoBoxes.find { it.variant == GuidanceContent.BoxVariant.REMINDER }
        assertTrue(reminderBox != null, "Should have a REMINDER info box")
    }

    @Test
    fun `VPScoringPhase structured guidance contains round 5 warning when applicable`() {
        val state = TestFixtures.gameStateAtRound(5)
        val content = VPScoringPhase.displayStructuredGuidance(state)

        val infoBoxes = content.filterIsInstance<GuidanceContent.InfoBox>()
        val warningBox = infoBoxes.find { it.variant == GuidanceContent.BoxVariant.WARNING }
        assertTrue(warningBox != null, "Round 5 should have a WARNING info box")
    }

    @Test
    fun `VPScoringPhase structured guidance has no round 5 warning before round 5`() {
        val state = TestFixtures.gameStateAtRound(3)
        val content = VPScoringPhase.displayStructuredGuidance(state)

        val infoBoxes = content.filterIsInstance<GuidanceContent.InfoBox>()
        val warningBox = infoBoxes.find { it.variant == GuidanceContent.BoxVariant.WARNING }
        assertTrue(warningBox == null, "Round 3 should not have a WARNING info box")
    }

    // ========== CommandPhase Tests ==========

    @Test
    fun `CommandPhase structured guidance contains numbered steps`() {
        val state = TestFixtures.gameStateAtRound(1)
        val content = CommandPhase.displayStructuredGuidance(state)

        assertTrue(content.isNotEmpty(), "Should have content")

        // Should have numbered list for steps
        val numberedLists = content.filterIsInstance<GuidanceContent.NumberedList>()
        assertTrue(numberedLists.isNotEmpty(), "Should contain numbered list for steps")
    }

    @Test
    fun `CommandPhase structured guidance contains battle-shock info box`() {
        val state = TestFixtures.gameStateAtRound(1)
        val content = CommandPhase.displayStructuredGuidance(state)

        val infoBoxes = content.filterIsInstance<GuidanceContent.InfoBox>()
        assertTrue(infoBoxes.isNotEmpty(), "Should contain info box")

        val reminderBox = infoBoxes.find {
            it.title?.contains("Battle-shock", ignoreCase = true) == true ||
            it.content.any { c -> c is GuidanceContent.Paragraph && c.text.contains("Battle-shock") }
        }
        assertTrue(reminderBox != null, "Should have battle-shock info")
    }

    // ========== MovementPhase Tests ==========

    @Test
    fun `MovementPhase structured guidance contains movement options`() {
        val state = TestFixtures.gameStateAtRound(1)
        val content = MovementPhase.displayStructuredGuidance(state)

        assertTrue(content.isNotEmpty(), "Should have content")

        // Should have key-value or bullet list for movement types
        val hasMovementOptions = content.any { item ->
            when (item) {
                is GuidanceContent.KeyValue -> item.pairs.any { it.first.contains("MOVE", ignoreCase = true) }
                is GuidanceContent.BulletList -> item.items.any { it.contains("Move", ignoreCase = true) }
                else -> false
            }
        }
        assertTrue(hasMovementOptions, "Should list movement options")
    }

    @Test
    fun `MovementPhase structured guidance contains reinforcements section`() {
        val state = TestFixtures.gameStateAtRound(2)
        val content = MovementPhase.displayStructuredGuidance(state)

        val hasReinforcementsSection = content.any { item ->
            when (item) {
                is GuidanceContent.Header -> item.text.contains("Reinforcement", ignoreCase = true)
                is GuidanceContent.Section -> item.title?.contains("Reinforcement", ignoreCase = true) == true
                else -> false
            }
        }
        assertTrue(hasReinforcementsSection, "Should have reinforcements section")
    }

    // ========== ShootingPhase Tests ==========

    @Test
    fun `ShootingPhase structured guidance contains attack sequence`() {
        val state = TestFixtures.gameStateAtRound(1)
        val content = ShootingPhase.displayStructuredGuidance(state)

        assertTrue(content.isNotEmpty(), "Should have content")

        val numberedLists = content.filterIsInstance<GuidanceContent.NumberedList>()
        assertTrue(numberedLists.isNotEmpty(), "Should contain numbered list for attack sequence")
    }

    @Test
    fun `ShootingPhase structured guidance contains wound roll table`() {
        val state = TestFixtures.gameStateAtRound(1)
        val content = ShootingPhase.displayStructuredGuidance(state)

        val tables = content.filterIsInstance<GuidanceContent.Table>()
        assertTrue(tables.isNotEmpty(), "Should contain wound roll table")

        val woundTable = tables.find { it.headers.any { h -> h.contains("Wound", ignoreCase = true) || h.contains("S vs T", ignoreCase = true) } }
        assertTrue(woundTable != null, "Should have wound roll reference table")
    }

    // ========== ChargePhase Tests ==========

    @Test
    fun `ChargePhase structured guidance contains charge sequence`() {
        val state = TestFixtures.gameStateAtRound(1)
        val content = ChargePhase.displayStructuredGuidance(state)

        assertTrue(content.isNotEmpty(), "Should have content")

        val numberedLists = content.filterIsInstance<GuidanceContent.NumberedList>()
        assertTrue(numberedLists.isNotEmpty(), "Should contain numbered list for charge sequence")
    }

    @Test
    fun `ChargePhase structured guidance contains success info`() {
        val state = TestFixtures.gameStateAtRound(1)
        val content = ChargePhase.displayStructuredGuidance(state)

        val infoBoxes = content.filterIsInstance<GuidanceContent.InfoBox>()
        val successBox = infoBoxes.find {
            it.title?.contains("Success", ignoreCase = true) == true ||
            it.variant == GuidanceContent.BoxVariant.SUCCESS
        }
        assertTrue(successBox != null, "Should have success/charge bonus info")
    }

    // ========== FightPhase Tests ==========

    @Test
    fun `FightPhase structured guidance contains fight order`() {
        val state = TestFixtures.gameStateAtRound(1)
        val content = FightPhase.displayStructuredGuidance(state)

        assertTrue(content.isNotEmpty(), "Should have content")

        val hasOrderInfo = content.any { item ->
            when (item) {
                is GuidanceContent.Header -> item.text.contains("Order", ignoreCase = true) || item.text.contains("First", ignoreCase = true)
                is GuidanceContent.NumberedList -> true
                else -> false
            }
        }
        assertTrue(hasOrderInfo, "Should describe fight order")
    }

    @Test
    fun `FightPhase structured guidance contains pile in and consolidate`() {
        val state = TestFixtures.gameStateAtRound(1)
        val content = FightPhase.displayStructuredGuidance(state)

        val hasPileIn = content.any { item ->
            when (item) {
                is GuidanceContent.Paragraph -> item.text.contains("Pile In", ignoreCase = true)
                is GuidanceContent.KeyValue -> item.pairs.any { it.first.contains("Pile In", ignoreCase = true) }
                is GuidanceContent.BulletList -> item.items.any { it.contains("Pile In", ignoreCase = true) }
                is GuidanceContent.Header -> item.text.contains("Pile In", ignoreCase = true)
                else -> false
            }
        }
        assertTrue(hasPileIn, "Should mention Pile In")
    }

    // ========== EndOfTurnPhase Tests ==========

    @Test
    fun `EndOfTurnPhase structured guidance mentions current player`() {
        val state = TestFixtures.gameStateAtRound(1)
        val content = EndOfTurnPhase.displayStructuredGuidance(state)

        assertTrue(content.isNotEmpty(), "Should have content")

        val mentionsPlayer = content.any { item ->
            when (item) {
                is GuidanceContent.Paragraph -> item.text.contains("Player")
                is GuidanceContent.Header -> item.text.contains("Player")
                else -> false
            }
        }
        assertTrue(mentionsPlayer, "Should mention which player's turn is ending")
    }

    // ========== EndGamePhase Tests ==========

    @Test
    fun `EndGamePhase structured guidance contains victory conditions`() {
        val state = TestFixtures.gameStateAtRound(5)
        val content = EndGamePhase.displayStructuredGuidance(state)

        assertTrue(content.isNotEmpty(), "Should have content")

        val hasVictoryInfo = content.any { item ->
            when (item) {
                is GuidanceContent.Header -> item.text.contains("Victor", ignoreCase = true) || item.text.contains("Winner", ignoreCase = true)
                is GuidanceContent.BulletList -> item.items.any { it.contains("VP") || it.contains("wins") }
                else -> false
            }
        }
        assertTrue(hasVictoryInfo, "Should explain victory conditions")
    }

    // ========== Setup Phase Tests ==========

    @Test
    fun `MusterArmies structured guidance contains battle size options`() {
        val state = GameState()
        val content = SetupPhase.MusterArmies.displayStructuredGuidance(state)

        assertTrue(content.isNotEmpty(), "Should have content")

        val hasOptions = content.any { item ->
            when (item) {
                is GuidanceContent.Table -> item.headers.any { it.contains("Size", ignoreCase = true) || it.contains("Points", ignoreCase = true) }
                is GuidanceContent.KeyValue -> item.pairs.any { it.first.contains("Incursion", ignoreCase = true) }
                else -> false
            }
        }
        assertTrue(hasOptions, "Should show battle size options")
    }

    @Test
    fun `CreateBattlefield structured guidance contains battlefield info`() {
        val state = TestFixtures.gameStateInBattle()
        val content = SetupPhase.CreateBattlefield.displayStructuredGuidance(state)

        assertTrue(content.isNotEmpty(), "Should have content")

        val hasBattlefieldInfo = content.any { item ->
            when (item) {
                is GuidanceContent.Paragraph -> item.text.contains("Battlefield", ignoreCase = true) || item.text.contains("terrain", ignoreCase = true)
                is GuidanceContent.Header -> item.text.contains("Battlefield", ignoreCase = true)
                else -> false
            }
        }
        assertTrue(hasBattlefieldInfo, "Should contain battlefield information")
    }

    @Test
    fun `DetermineAttacker structured guidance contains roll-off info`() {
        val state = GameState()
        val content = SetupPhase.DetermineAttacker.displayStructuredGuidance(state)

        assertTrue(content.isNotEmpty(), "Should have content")

        val hasRollOffInfo = content.any { item ->
            when (item) {
                is GuidanceContent.Paragraph -> item.text.contains("roll", ignoreCase = true)
                is GuidanceContent.BulletList -> item.items.any { it.contains("ATTACKER") || it.contains("DEFENDER") }
                is GuidanceContent.KeyValue -> item.pairs.any { it.first.contains("Winner") || it.first.contains("Loser") }
                else -> false
            }
        }
        assertTrue(hasRollOffInfo, "Should explain attacker/defender roll-off")
    }

    @Test
    fun `DeclareBattleFormations structured guidance contains formation steps`() {
        val state = GameState()
        val content = SetupPhase.DeclareBattleFormations.displayStructuredGuidance(state)

        assertTrue(content.isNotEmpty(), "Should have content")

        val hasFormationSteps = content.any { item ->
            when (item) {
                is GuidanceContent.NumberedList -> item.items.any { it.contains("Leader", ignoreCase = true) || it.contains("Reserves", ignoreCase = true) }
                is GuidanceContent.BulletList -> item.items.any { it.contains("Leader", ignoreCase = true) }
                is GuidanceContent.KeyValue -> item.pairs.any { it.first.contains("Leader", ignoreCase = true) }
                else -> false
            }
        }
        assertTrue(hasFormationSteps, "Should list formation declaration steps")
    }

    @Test
    fun `DeployArmies structured guidance contains deployment order`() {
        val state = TestFixtures.gameStateInBattle()
        val content = SetupPhase.DeployArmies.displayStructuredGuidance(state)

        assertTrue(content.isNotEmpty(), "Should have content")

        val hasDeploymentInfo = content.any { item ->
            when (item) {
                is GuidanceContent.NumberedList -> item.items.any { it.contains("deploy", ignoreCase = true) || it.contains("ATTACKER") }
                is GuidanceContent.Paragraph -> item.text.contains("deploy", ignoreCase = true)
                else -> false
            }
        }
        assertTrue(hasDeploymentInfo, "Should explain deployment order")
    }

    @Test
    fun `PreBattleRules structured guidance contains scouts info`() {
        val state = GameState()
        val content = SetupPhase.PreBattleRules.displayStructuredGuidance(state)

        assertTrue(content.isNotEmpty(), "Should have content")

        val hasScoutsInfo = content.any { item ->
            when (item) {
                is GuidanceContent.Paragraph -> item.text.contains("Scout", ignoreCase = true)
                is GuidanceContent.InfoBox -> item.title?.contains("Scout", ignoreCase = true) == true
                is GuidanceContent.KeyValue -> item.pairs.any { it.first.contains("Scout", ignoreCase = true) }
                else -> false
            }
        }
        assertTrue(hasScoutsInfo, "Should explain Scouts rule")
    }

    @Test
    fun `DetermineFirstTurn structured guidance contains first turn info`() {
        val state = GameState()
        val content = SetupPhase.DetermineFirstTurn.displayStructuredGuidance(state)

        assertTrue(content.isNotEmpty(), "Should have content")

        val hasFirstTurnInfo = content.any { item ->
            when (item) {
                is GuidanceContent.Paragraph -> item.text.contains("first turn", ignoreCase = true) || item.text.contains("roll", ignoreCase = true)
                is GuidanceContent.Header -> item.text.contains("First Turn", ignoreCase = true)
                else -> false
            }
        }
        assertTrue(hasFirstTurnInfo, "Should explain first turn determination")
    }
}
