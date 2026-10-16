# Character filtering — membership, quests and diaries

Open the OSRS Randomizer settings using its gear in the RuneLite plugin list. **Available for my character** is enabled by default; log into RuneLite to use it. Supported quest and diary requirements are read locally from the logged-in character, after a server tick and then every 10 ticks (about six seconds). No account credentials, web request or external synchronization service is used.

The filter preserves manual selections. Locked activities are grey, excluded from draws, and show the missing requirement in a tooltip. Finishing the requirement restores the activity if it was selected. **All** and **None** still change manual choices for the whole category. Turning the filter off restores manual selection behavior. The setting is stored under the existing questFilter key. It defaults to on, preserves existing saved preferences, and updates the panel immediately. The main panel has no toggle.

This is **partial requirement coverage**, not a guarantee that an activity can be completed. Skill levels, equipment, keys, other diary tasks, combat ability and additional non-quest access unlocks are not checked. Unmapped activities remain selectable and have a “requirements not checked” tooltip. Sailing and Herblore have quest gates; nine skills also require membership.

## Supported activities

The following quests must be finished. Boss entries refer to repeatable post-quest encounters, not their quest-version fights.

| Activity | Required quest |
| --- | --- |
| Vorkath | Dragon Slayer II |
| Phantom Muspah | Secrets of the North |
| Duke Sucellus | Desert Treasure II - The Fallen Empire |
| The Leviathan | Desert Treasure II - The Fallen Empire |
| The Whisperer | Desert Treasure II - The Fallen Empire |
| Vardorvis | Desert Treasure II - The Fallen Empire |
| Crystalline Hunllef | Song of the Elves |
| Corrupted Hunllef | Song of the Elves |
| Zalcano | Song of the Elves |
| Amoxliatl | The Heart of Darkness |
| Deranged Archaeologist | Bone Voyage |
| Tombs of Amascut | Beneath Cursed Sands |

Corrupted Gauntlet can have additional access requirements; only its quest gate is checked here. Zulrah, Perilous Moons, Nex, Doom of Mokhaiotl, Theatre of Blood and other entries outside the tables are deliberately unmapped pending verification of their exact access rules. Do not interpret a selectable entry as a verified accessible activity.

## Session behavior

- With the filter enabled, drawing waits for login data. A known requirement with unread quest or diary data remains unavailable until it can be checked.
- Logout, hopping and reconnecting invalidate the previous snapshot. Existing choices are not erased.
- A changed character snapshot cancels an unfinished animation. Preview frames are not counted as draws.
- A different character loads its own saved draw history and repeat memory. Logout hides the previous history until login; manual selections remain shared within the RuneLite configuration profile.
- Repeated identical character snapshots do not rebuild the UI. Only supported quests and the medium Wilderness completion flag are queried, on RuneLite's client thread; Swing receives immutable snapshots on its own event thread.

## Sources reviewed on 15 October 2026

- [RuneLite Quest API](https://static.runelite.net/runelite-api/apidocs/net/runelite/api/Quest.html): quest state reading.
- [Jagex: Tombs of Amascut](https://secure.runescape.com/m=news/tombs-of-amascut?oldschool=1): Beneath Cursed Sands completion.
- [Jagex: Song of the Elves](https://secure.runescape.com/m=news/song-of-the-elves?oldschool=1): Prifddinas, Gauntlet and Zalcano access.
- [Jagex: Secrets of the North](https://secure.runescape.com/m=news/secrets-of-the-north--more?oldschool=1).
- [OSRS Wiki: bosses](https://oldschool.runescape.wiki/w/Boss): repeatable Vorkath, Phantom Muspah and Desert Treasure II boss requirements.
- [OSRS Wiki: Amoxliatl](https://oldschool.runescape.wiki/w/Amoxliatl): repeatable post-quest fight.
- [OSRS Wiki: Fossil Island](https://oldschool.runescape.wiki/w/Fossil_Island): Bone Voyage access.

Some direct wiki pages were unavailable to the research tool; indexed wiki content and official Jagex pages were used. Unverified rules were not inferred from quest titles or recommended combat levels.

## Manual validation in RuneLite

1. Enable the filter while logged out: Draw should be disabled with a login prompt.
2. Log into a character without Beneath Cursed Sands: ToA should be grey with the missing quest in its tooltip.
3. Use a character with that quest completed: ToA should be restored if manually selected.
4. Turn the filter off: the original selections should return, including blocked activities.
5. Hop worlds or reconnect: the filter should wait for fresh data without deleting selections.
6. Finish a supported quest: the row should update within approximately six seconds after quest data becomes available.

Automated tests cover requirement states, immutable snapshots, preserved selections, disabled draws, unknown data, unlocks, manual mode and account changes. Live quest reads still need verification by the user in the game client. The supplied quest-filter preview uses simulated quest states, not a user's account.


## Supported diary gates

| Boss | Required diary |
| --- | --- |
| Artio | Medium Wilderness Diary |
| Spindel | Medium Wilderness Diary |
| Calvar'ion | Medium Wilderness Diary |

The diary rules cover three bosses. With the two skill quest gates below, 17 activities have supported quest or diary checks. The filter reads RuneLite's WILDERNESS_DIARY_MEDIUM_COMPLETE flag. Unknown diary data keeps these entries blocked until refreshed. Callisto, Venenatis and Vet'ion do not inherit this restriction.

Sources: [OSRS Wiki: Wilderness](https://oldschool.runescape.wiki/w/Wilderness) documents the medium diary gate for the weaker variants; [RuneLite VarbitID](https://github.com/runelite/runelite/blob/master/runelite-api/src/main/java/net/runelite/api/gameval/VarbitID.java) defines the completion flag.

Manual check: log in with the medium diary incomplete and confirm these three bosses are grey with the diary reason; with it complete, previously selected entries should return. Turning the filter off restores manual choices. Actual in-game diary synchronization remains to be verified by the user.

## Skill quest gates and membership
Sailing requires [Pandemonium](https://oldschool.runescape.wiki/w/Transcript:Shipwright_Sam); Herblore requires [Druidic Ritual](https://oldschool.runescape.wiki/w/Herblore). Not-started and in-progress states stay blocked. Runecraft is not globally blocked by Rune Mysteries: see [Runecraft training](https://www.theoatrix.net/post/osrs-ultimate-1-99-runecrafting-guide-fastest-afk-profitable-methods).

Membership is required for Agility, Construction, Farming, Fletching, Herblore, Hunter, Sailing, Slayer and Thieving; all raids; and catalogue bosses other than Brutus, Obor and Bryophyta. See [Skills](https://oldschool.runescape.wiki/w/Skills), [Brutus](https://oldschool.runescape.wiki/w/Brutus) and [Bryophyta](https://oldschool.runescape.wiki/w/Bryophyta). These rules only check membership; they do not imply all other access requirements have been verified. Brutus's quest gate is not currently checked.

The plugin reads RuneLite's [MEMBERSHIP_DAYS](https://github.com/runelite/runelite/blob/master/runelite-api/src/main/java/net/runelite/api/VarPlayer.java) after login ticks. Positive subscription days or a members world indicate membership; zero days on a free world indicates a free account. Unknown data keeps member content blocked pending refresh. Membership is checked before quest and diary gates, so free accounts see Members only.

Manual checks: compare a free account and a member account on a free world; check a member missing Pandemonium or Druidic Ritual; confirm unlocks and saved choices after completion or subscription change. All 23 automated tests pass; live account reads still require in-game validation.
