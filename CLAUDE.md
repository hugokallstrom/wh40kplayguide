# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A Kotlin-based Warhammer bot project using Gradle as the build system.

## Build Commands

```bash
# Build the project
./gradlew build

# Run the application
./gradlew run

# Run all tests
./gradlew test

# Run a specific test class
./gradlew test --tests "ClassName"

# Run a specific test method
./gradlew test --tests "ClassName.methodName"

# Clean build artifacts
./gradlew clean
```

## Technical Stack

- **Language:** Kotlin 2.2.20
- **JVM:** Java 19
- **Build System:** Gradle with Kotlin DSL
- **Testing:** JUnit Platform (via `kotlin("test")`)

## Project Structure

- `src/main/kotlin/` - Main Kotlin source files
- `src/test/kotlin/` - Test source files
- `build.gradle.kts` - Build configuration
- `settings.gradle.kts` - Project settings
- `core_rules.txt` - Extracted text from Warhammer 40K Core Rules PDF

## Warhammer 40,000 Core Rules Summary

The core rules (extracted to `core_rules.txt`) cover the 10th Edition rules structure:

### Core Concepts (pg 5-9)
- **Armies, Units, Models** - basic definitions
- **Datasheets & Keywords** - how unit rules are organized (Faction keywords + other keywords)
- **Unit Coherency** - 2" horizontal/5" vertical (units of 7+ models need 2 connections)
- **Engagement Range** - 1" horizontal/5" vertical
- **Visibility** - true line of sight rules (Model Visible, Unit Visible, Fully Visible)
- **Dice** - D6, D3, re-rolls (before modifiers), roll-offs, sequencing

### Battle Round Phases (pg 10-36)
1. **Command Phase** - both players gain 1CP, Battle-shock tests for Below Half-strength units (2D6 >= Leadership)
2. **Movement Phase** - Normal (up to M"), Advance (M+D6"), Fall Back moves; Reinforcements step; Transport rules
3. **Shooting Phase** - select targets, make ranged attacks; Big Guns Never Tire (Monsters/Vehicles can shoot in engagement)
4. **Charge Phase** - 2D6" charge roll, must end within Engagement Range of all targets
5. **Fight Phase** - Fights First step, then Remaining Combats; sequence: Pile In (3") -> Attack -> Consolidate (3")

### Making Attacks (pg 21-28)
Attack sequence: Hit Roll -> Wound Roll -> Allocate Attack -> Saving Throw -> Inflict Damage

**Wound Roll Table (Strength vs Toughness):**
| Condition | Required Roll |
|-----------|---------------|
| S >= 2x T | 2+ |
| S > T | 3+ |
| S = T | 4+ |
| S < T | 5+ |
| S <= T/2 | 6+ |

**Key Mechanics:**
- Critical Hit: Unmodified 6 (always hits)
- Critical Wound: Unmodified 6 (always wounds)
- Mortal Wounds: No saves allowed, excess damage carries over
- Feel No Pain x+: Roll to ignore each wound
- Deadly Demise x: On destruction, roll D6; on 6, units within 6" suffer x mortal wounds

### Weapon Abilities
| Ability | Effect |
|---------|--------|
| Assault | Can shoot after Advancing |
| Heavy | +1 to Hit if Remained Stationary |
| Rapid Fire X | +X Attacks at half range |
| Pistol | Can shoot in Engagement Range (only at engaged units) |
| Melta X | +X Damage at half range |
| Blast | +1 Attack per 5 models in target |
| Torrent | Auto-hits |
| Lethal Hits | Critical Hits auto-wound |
| Devastating Wounds | Critical Wounds inflict mortal wounds equal to Damage |
| Sustained Hits X | Critical Hits score X additional hits |
| Twin-linked | Re-roll Wound rolls |
| Anti-X Y+ | Unmodified Y+ wounds are Critical Wounds vs keyword X |
| Hazardous | After shooting/fighting, roll D6 per weapon; on 1, model destroyed (Characters/Monsters/Vehicles take 3MW instead) |
| Precision | Can allocate wounds to visible Characters in Attached units |
| Indirect Fire | Can target non-visible units (-1 to Hit, target gets cover) |
| Ignores Cover | Target cannot have Benefit of Cover |
| Lance | +1 to Wound if charged this turn |
| Extra Attacks | Attacks in addition to chosen weapon |

### Special Rules (pg 37-54)
**Deployment Abilities:**
- **Deep Strike**: Set up in Reserves, arrive 9"+ from enemies
- **Scouts X"**: Move up to X" before first turn (must end 9"+ from enemies)
- **Infiltrators**: Deploy anywhere 9"+ from enemy deployment zone and models
- **Leader**: Attach to Bodyguard units; attacks cannot be allocated to Characters in Attached units

**Core Stratagems (1-2CP each):**
- Command Re-roll (1CP): Re-roll any roll
- Counter-offensive (2CP): Fight next after enemy fights
- Fire Overwatch (1CP): Shoot at enemy after they move/charge (6s to hit only)
- Grenade (1CP): Roll 6D6 at enemy within 8", 4+ = 1 mortal wound each
- Tank Shock (1CP): After charge, roll D6s equal to weapon Strength; 5+ = mortal wounds
- Go to Ground (1CP): Infantry gets Benefit of Cover + Stealth
- Smokescreen (1CP): 6+ invuln + Benefit of Cover
- Heroic Intervention (2CP): Counter-charge after enemy charges
- Insane Bravery (1CP): Auto-pass Battle-shock test
- Epic Challenge (1CP): Character melee gains Precision
- Rapid Ingress (1CP): Reserves arrive at end of opponent's Movement phase

**Strategic Reserves:**
- Max 25% of army points
- Arrive from battle round 2+ (wholly within 6" of battlefield edge, 9"+ from enemies)
- Round 2: Cannot enter enemy deployment zone
- Round 3+: Any edge

**Terrain:**
- Benefit of Cover: +1 to armor saves vs ranged (not for 3+ save vs AP 0)
- Ruins: Block LOS; Infantry/Beasts move through walls; Plunging Fire (+1 AP from 6"+ height)
- Woods: Units wholly within never fully visible; gives cover

### Army Building (pg 55-56)
**Battle Sizes:**
| Size | Points | Duration |
|------|--------|----------|
| Incursion | 1000 | ~2 hours |
| Strike Force | 2000 | ~3 hours |
| Onslaught | 3000 | ~4 hours |

**Army Construction Rules:**
- All units must share army Faction keyword
- Max 3 of same datasheet (6 for Battleline/Dedicated Transport)
- Must include at least 1 Character
- Max 3 Enhancements total (Characters only, no Epic Heroes)
- Select one Warlord (gains Warlord keyword)

### Mission: Only War (pg 59-60)
- 4 objective markers placed by alternating players (6"+ from edges, 9"+ apart)
- Score 1VP per controlled objective at end of Command phase (from round 2, max 3VP/turn)
- Control = higher total OC within 3" horizontal/5" vertical of marker
- Battle ends after round 5 or when one army destroyed
- Victor: Opponent of destroyed army, or highest VP (tie = draw)