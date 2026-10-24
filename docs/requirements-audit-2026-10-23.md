# Requirement audit — 23 October 2026

## Conclusion

Coverage is incomplete. The information button exposes tracked rules; it does not prove that all access conditions are met. This audit found and corrected 14 missing quest gates and two missing skilling-boss level gates. No recommended combat levels were converted into mandatory requirements. Changes are local development changes, not published.

## Corrections implemented

| Activity | Newly checked requirement | Evidence |
| --- | --- | --- |
| Mad Angel | Fallen From Grace completed | [Instance access](https://oldschool.runescape.wiki/w/Instance) |
| Maggot King | The Blood Moon Rises completed | [Requirements](https://oldschool.runescape.wiki/w/Maggot_King/Strategies) |
| Brutus | The Ides of Milk completed, including on free accounts | [Instance access](https://oldschool.runescape.wiki/w/Instance) |
| Shellbane Gryphon | Troubled Tortugans completed | [Boss access](https://oldschool.runescape.wiki/w/Shellbane_gryphon) |
| Perilous Moons | Perilous Moons completed for repeatable encounters | [Quest rewards](https://oldschool.runescape.wiki/w/Perilous_Moons) |
| Doom of Mokhaiotl | The Final Dawn completed | [Instance access](https://oldschool.runescape.wiki/w/Instance) |
| Yama | A Kingdom Divided completed | [Instance access](https://oldschool.runescape.wiki/w/Instance) |
| Nex | The Frozen Door completed | [Requirements](https://oldschool.runescape.wiki/w/Nex/Strategies) |
| The Hueycoatl | Children of the Sun completed | [Access](https://oldschool.runescape.wiki/w/Hueycoatl_guide) |
| Sol Heredit | Children of the Sun completed | [Colosseum access](https://oldschool.runescape.wiki/w/Fortis_Colosseum) |
| Barrows | Priest in Peril completed | [Requirements](https://oldschool.runescape.wiki/w/Money_making_guide/Barrows) |
| The Nightmare / Phosani's Nightmare | Priest in Peril completed | [Requirements](https://oldschool.runescape.wiki/w/Phosani%27s_Nightmare/Strategies) |
| Theatre of Blood | Priest in Peril completed | [Hard access requirement](https://oldschool.runescape.wiki/w/Theatre_of_Blood) |
| Tempoross | 35 Fishing | [Boss access](https://oldschool.runescape.wiki/w/Tempoross) |
| Wintertodt | 50 Firemaking | [Boss access](https://oldschool.runescape.wiki/w/Wintertodt) |

Skill values are copied into the immutable character snapshot. Unknown skill data stays blocked; the detail view includes current/required levels. Existing membership and quest state checks remain in effect.

## Confirmed gaps still remaining

| Content | Missing automatic checks / qualification |
| --- | --- |
| [Zulrah](https://oldschool.runescape.wiki/w/Zulrah) | Regicide must reach Port Tyras, followed by a conversation with High Priestess Zul-Harcinqa. Requiring the entire quest would incorrectly block some eligible players. These conditions are now stated in the info panel but not enforced. |
| [Shellbane Gryphon](https://oldschool.runescape.wiki/w/Shellbane_gryphon) | 51 Slayer and an eligible Slayer assignment OR the specific elite clue step. Only its quest gate is enforced; the remaining conditions are stated in the info panel. |
| [God Wars Dungeon](https://oldschool.runescape.wiki/w/God_Wars_Dungeon) | Route alternatives (Troll Stronghold progression or Combat Achievement teleport), 60 Strength OR Agility, equipment and room access. Boss chambers additionally require 70 Ranged (Kree'arra), 70 Agility (Zilyana), 70 Strength (Graardor), or 70 Hitpoints (K'ril). Boost rules differ, so a blanket base-level check is not equivalent. Kill count / keys are also not checked. |
| [Obor / Bryophyta](https://oldschool.runescape.wiki/w/Guide%3ABossing_Ladder) | Giant key / mossy key possession is not checked. |
| [Inferno](https://oldschool.runescape.wiki/w/Inferno) | Permanent access unlock from sacrificing a fire cape is not checked. Checking current cape possession would be incorrect after unlocking it. |
| Diaries | Quest-cape eligibility for Lumbridge Elite and the skillcape OR quest-cape task in Falador Elite are absent from the imported rule set. [Cape tasks](https://oldschool.runescape.wiki/w/Achievement_Diary). Items, Ironman item acquisition, boosts, and exact intermediate quest stages are not comprehensively checked. |

The 12 current RuneLite Diary Requirements source files were fetched again and match the imported source files exactly. That verifies parity, **not completeness** against every game task. The 377 requirement-bearing entries cover all 48 tiers, preserve the original OR expressions, and skip completed tasks. A complete independent audit of every diary task and special unlock has not been performed; the remaining gaps above are not an exhaustive list.

## Other scope and limits

- Skills: membership plus Sailing/Pandemonium and Herblore/Druidic Ritual are tracked. This selects a skill, not a particular training method; individual training methods have additional requirements. [Sailing unlock](https://oldschool.runescape.wiki/w/Pandemonium).
- Raids: ToB now has its missing quest gate. ToA retains Beneath Cursed Sands. The Fractured Archive retains While Guthix Sleeps, consistent with the current [raid access description](https://oldschool.runescape.wiki/w/The_Fractured_Archive?mobileaction=toggle_view_mobile). Suggested combat levels are not access gates.
- Existing repeatable boss quest gates were retained. Entries without a mapped rule remain only partially checked, not certified accessible.
- No inventory, bank, task assignment, permanent-unlock or quest-stage reader was added in this pass.
- Some Wiki pages cannot be fetched directly; indexed Wiki excerpts were used where necessary. Quest identifiers were checked against the installed RuneLite API. No private-server sources were used.

## Validation

Automated tests cover all newly added quest gates in not-started/in-progress/finished states; Brutus on a free account; Tempoross 34/35 and Wintertodt 49/50; unknown skill data; immutable snapshots; existing diary/history/filter/UI behavior. In-game validation of these newly added gates remains necessary before publication.

## Special access update — 2026-10-24

The previous audit above describes the state on October 23. This local development update adds:

- Shellbane Gryphon: 51 Slayer, Troubled Tortugans, and a current Gryphons / Shellbane gryphon assignment OR elite clue item TRAIL_ELITE_RIDDLE_SAIL in inventory. Reads live client variables and DB tables using the same lookup approach as RuneLite's SlayerPlugin. A missing inventory/DB read remains unknown; a valid alternative can still unlock it.
- Zulrah: SNAKEBOSS_INFO >= 2 (priestess acceptance), rather than requiring full Regicide completion. Missing reads are retried.
- God Wars: 60 Strength OR 60 Agility entrance check, and the four generals' respective level-70 chamber checks. Active Strength boosts count for the entrance; active Hitpoints boosts count for Zamorak. Drained stats never lower the known base. The Troll Stronghold / Combat Achievement route alternative, equipment, keys and kill count remain untracked and are explicitly described in the i panel.
- Lumbridge & Draynor Elite quest-cape task: current quest points versus the game's dynamic QP_MAX.
- Falador Elite cape task: any real level-99 skill OR current quest-cape eligibility. OVERALL is excluded. Both cape checks ignore an already-completed task, preserving completion after new quest releases.

No status icons or extra controls were added. All data is read on the client thread, copied into immutable snapshots, and refreshed through the existing account/logout lifecycle. Diary skill checks continue to use base levels. Inventory checks are limited to the specific clue; bank contents are not read.

Sources:
- https://github.com/runelite/runelite/blob/master/runelite-client/src/main/java/net/runelite/client/plugins/slayer/SlayerPlugin.java
- https://github.com/runelite/runelite/blob/master/runelite-client/src/main/java/net/runelite/client/plugins/cluescrolls/clues/CrypticClue.java
- https://oldschool.runescape.wiki/w/Shellbane_gryphon
- https://oldschool.runescape.wiki/w/RuneScape:Varbit/4391
- https://oldschool.runescape.wiki/w/God_Wars_Dungeon
- Quest Helper LumbridgeElite.java and FaladorElite.java task-completion flags and cape requirements, downloaded under work/diary-task-sources.

Validation: 51 unit tests pass, including client DB task lookup, boss assignment, clue exception, quest/skill conjunction, both God Wars entrance alternatives, dynamic quest-point maximum, cape alternatives and completed-task exemptions. In-game validation is still required before publication. This is not a claim of exhaustive prerequisite coverage.
