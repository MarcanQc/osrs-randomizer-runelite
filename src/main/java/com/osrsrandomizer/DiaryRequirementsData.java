/*
 * Copyright (c) 2018, Marshall <https://github.com/marshdevs>
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/*
 * Copyright (c) 2019 William <https://github.com/monsterxsync>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.osrsrandomizer;
import java.util.*;
import net.runelite.api.Quest;
import net.runelite.api.Skill;
import net.runelite.client.plugins.achievementdiary.*;
/** Tier grouping adapted from RuneLite Diary Requirements, reviewed 2026-10-22. */
final class DiaryRequirementsData
{
    private static final Map<String, List<Requirement>> RULES = new LinkedHashMap<>();
    static {
        Ardougne();
        Desert();
        Falador();
        Fremennik();
        Kandarin();
        Karamja();
        Kourend();
        Lumbridge();
        Morytania();
        Varrock();
        Western();
        Wilderness();
    }
    static Map<String, List<Requirement>> rules()
    {
        Map<String, List<Requirement>> copy = new LinkedHashMap<>();
        RULES.forEach((id, rules) -> copy.put(id, Collections.unmodifiableList(rules)));
        return Collections.unmodifiableMap(copy);
    }
    private static void add(String id, Requirement... requirements)
    {
        RULES.computeIfAbsent(id, ignored -> new ArrayList<>()).addAll(Arrays.asList(requirements));
    }
    private static void Ardougne() {
        add("ardougne_easy", new QuestRequirement(Quest.RUNE_MYSTERIES));
        add("ardougne_easy", new SkillRequirement(Skill.THIEVING, 5));
        add("ardougne_easy", new QuestRequirement(Quest.BIOHAZARD));
        add("ardougne_medium", new QuestRequirement(Quest.FAIRYTALE_II__CURE_A_QUEEN, true));
        add("ardougne_medium", new SkillRequirement(Skill.AGILITY, 39),
			new SkillRequirement(Skill.STRENGTH, 38),
			new SkillRequirement(Skill.RANGED, 21));
        add("ardougne_medium", new SkillRequirement(Skill.FARMING, 31));
        add("ardougne_medium", new SkillRequirement(Skill.MAGIC, 51),
			new QuestRequirement(Quest.PLAGUE_CITY));
        add("ardougne_medium", new SkillRequirement(Skill.FIREMAKING, 50),
			new QuestRequirement(Quest.ENLIGHTENED_JOURNEY));
        add("ardougne_medium", new SkillRequirement(Skill.CRAFTING, 49),
			new QuestRequirement(Quest.THE_HAND_IN_THE_SAND));
        add("ardougne_medium", new QuestRequirement(Quest.SEA_SLUG, true));
        add("ardougne_medium", new SkillRequirement(Skill.THIEVING, 38));
        add("ardougne_medium", new QuestRequirement(Quest.WATCHTOWER, true));
        add("ardougne_medium", new QuestRequirement(Quest.TOWER_OF_LIFE));
        add("ardougne_medium", new SkillRequirement(Skill.MAGIC, 50),
			new SkillRequirement(Skill.ATTACK, 50),
			new QuestRequirement(Quest.UNDERGROUND_PASS));
        add("ardougne_medium", new QuestRequirement(Quest.FAIRYTALE_II__CURE_A_QUEEN, true));
        add("ardougne_hard", new QuestRequirement(Quest.LEGENDS_QUEST));
        add("ardougne_hard", new QuestRequirement(Quest.LEGENDS_QUEST));
        add("ardougne_hard", new SkillRequirement(Skill.MAGIC, 66));
        add("ardougne_hard", new SkillRequirement(Skill.THIEVING, 72));
        add("ardougne_hard", new QuestRequirement(Quest.MONKEY_MADNESS_I, true));
        add("ardougne_hard", new SkillRequirement(Skill.MAGIC, 58),
			new QuestRequirement(Quest.WATCHTOWER));
        add("ardougne_hard", new SkillRequirement(Skill.HUNTER, 59));
        add("ardougne_hard", new SkillRequirement(Skill.FARMING, 68));
        add("ardougne_hard", new SkillRequirement(Skill.FARMING, 70));
        add("ardougne_hard", new SkillRequirement(Skill.SMITHING, 68));
        add("ardougne_hard", new SkillRequirement(Skill.CONSTRUCTION, 50));
        add("ardougne_hard", new SkillRequirement(Skill.SMITHING, 60),
			new QuestRequirement(Quest.LEGENDS_QUEST));
        add("ardougne_hard", new SkillRequirement(Skill.RUNECRAFT, 65),
			new QuestRequirement(Quest.MOURNINGS_END_PART_II));
        add("ardougne_elite", new SkillRequirement(Skill.FISHING, 81),
			new SkillRequirement(Skill.COOKING, 91));
        add("ardougne_elite", new SkillRequirement(Skill.THIEVING, 82));
        add("ardougne_elite", new SkillRequirement(Skill.THIEVING, 80));
        add("ardougne_elite", new SkillRequirement(Skill.CRAFTING, 10),
			new SkillRequirement(Skill.SMITHING, 91),
			new SkillRequirement(Skill.FLETCHING, 69));
        add("ardougne_elite", new QuestRequirement(Quest.HAUNTED_MINE));
        add("ardougne_elite", new SkillRequirement(Skill.FARMING, 85));
        add("ardougne_elite", new SkillRequirement(Skill.AGILITY, 90));
        add("ardougne_elite", new SkillRequirement(Skill.MAGIC, 94),
			new QuestRequirement(Quest.DESERT_TREASURE_I));
    }
    private static void Desert() {
        add("desert_easy", new SkillRequirement(Skill.HUNTER, 5));
        add("desert_easy", new SkillRequirement(Skill.MINING, 5));
        add("desert_easy", new SkillRequirement(Skill.THIEVING, 21),
			new QuestRequirement(Quest.ICTHLARINS_LITTLE_HELPER, true));
        add("desert_medium", new SkillRequirement(Skill.AGILITY, 30));
        add("desert_medium", new SkillRequirement(Skill.SLAYER, 22));
        add("desert_medium", new SkillRequirement(Skill.HUNTER, 47));
        add("desert_medium", new SkillRequirement(Skill.THIEVING, 25));
        add("desert_medium", new QuestRequirement(Quest.THE_GOLEM));
        add("desert_medium", new QuestRequirement(Quest.EAGLES_PEAK));
        add("desert_medium", new QuestRequirement(Quest.SPIRITS_OF_THE_ELID));
        add("desert_medium", new SkillRequirement(Skill.HERBLORE, 36));
        add("desert_medium", new QuestRequirement(Quest.ENAKHRAS_LAMENT));
        add("desert_medium", new QuestRequirement(Quest.SPIRITS_OF_THE_ELID));
        add("desert_medium", new SkillRequirement(Skill.CONSTRUCTION, 20));
        add("desert_medium", new SkillRequirement(Skill.WOODCUTTING, 35));
        add("desert_hard", new SkillRequirement(Skill.THIEVING, 65),
			new QuestRequirement(Quest.THE_FEUD));
        add("desert_hard", new SkillRequirement(Skill.MINING, 45));
        add("desert_hard", new SkillRequirement(Skill.MAGIC, 68),
			new QuestRequirement(Quest.DREAM_MENTOR));
        add("desert_hard", new SkillRequirement(Skill.AGILITY, 70));
        add("desert_hard", new SkillRequirement(Skill.SLAYER, 65),
			new SkillRequirement(Skill.DEFENCE, 10),
			new SkillRequirement(Skill.CRAFTING, 55),
			new QuestRequirement(Quest.DESERT_TREASURE_I, true));
        add("desert_hard", new QuestRequirement(Quest.DESERT_TREASURE_I));
        add("desert_hard", new SkillRequirement(Skill.ATTACK, 50),
			new QuestRequirement(Quest.CONTACT));
        add("desert_hard", new SkillRequirement(Skill.FIREMAKING, 60));
        add("desert_hard", new SkillRequirement(Skill.SMITHING, 68));
        add("desert_elite", new SkillRequirement(Skill.COOKING, 85));
        add("desert_elite", new SkillRequirement(Skill.MAGIC, 94),
			new QuestRequirement(Quest.DESERT_TREASURE_I));
        add("desert_elite", new SkillRequirement(Skill.FLETCHING, 95),
			new QuestRequirement(Quest.THE_TOURIST_TRAP));
        add("desert_elite", new SkillRequirement(Skill.CONSTRUCTION, 78),
			new QuestRequirement(Quest.PRIEST_IN_PERIL));
        add("desert_elite", new SkillRequirement(Skill.THIEVING, 91),
			new QuestRequirement(Quest.ICTHLARINS_LITTLE_HELPER, true));
        add("desert_elite", new SkillRequirement(Skill.PRAYER, 85),
			new QuestRequirement(Quest.ICTHLARINS_LITTLE_HELPER, true));
    }
    private static void Falador() {
        add("falador_easy", new SkillRequirement(Skill.CONSTRUCTION, 16));
        add("falador_easy", new SkillRequirement(Skill.AGILITY, 5));
        add("falador_easy", new SkillRequirement(Skill.MINING, 10),
			new SkillRequirement(Skill.SMITHING, 13),
			new QuestRequirement(Quest.THE_KNIGHTS_SWORD),
			new QuestRequirement(Quest.DORICS_QUEST));
        add("falador_medium", new SkillRequirement(Skill.FIREMAKING, 49));
        add("falador_medium", new SkillRequirement(Skill.MAGIC, 33));
        add("falador_medium", new SkillRequirement(Skill.FARMING, 23));
        add("falador_medium", new SkillRequirement(Skill.SLAYER, 32),
			new QuestRequirement(Quest.SKIPPY_AND_THE_MOGRES));
        add("falador_medium", new QuestRequirement(Quest.RATCATCHERS, true));
        add("falador_medium", new SkillRequirement(Skill.AGILITY, 11),
			new SkillRequirement(Skill.STRENGTH, 37),
			new SkillRequirement(Skill.RANGED, 19));
        add("falador_medium", new SkillRequirement(Skill.THIEVING, 40));
        add("falador_medium", new SkillRequirement(Skill.PRAYER, 10),
			new SkillRequirement(Skill.DEFENCE, 20),
			new QuestRequirement(Quest.RECRUITMENT_DRIVE));
        add("falador_medium", new SkillRequirement(Skill.CRAFTING, 40),
			new SkillRequirement(Skill.MINING, 40));
        add("falador_medium", new SkillRequirement(Skill.AGILITY, 42));
        add("falador_medium", new SkillRequirement(Skill.WOODCUTTING, 30),
			new SkillRequirement(Skill.FIREMAKING, 30));
        add("falador_medium", new SkillRequirement(Skill.CRAFTING, 36));
        add("falador_medium", new SkillRequirement(Skill.MAGIC, 37));
        add("falador_hard", new SkillRequirement(Skill.RUNECRAFT, 56));
        add("falador_hard", new SkillRequirement(Skill.PRAYER, 70));
        add("falador_hard", new SkillRequirement(Skill.SLAYER, 72));
        add("falador_hard", new SkillRequirement(Skill.AGILITY, 50));
        add("falador_hard", new SkillRequirement(Skill.MINING, 60));
        add("falador_hard", new QuestRequirement(Quest.HEROES_QUEST));
        add("falador_hard", new SkillRequirement(Skill.THIEVING, 50));
        add("falador_hard", new SkillRequirement(Skill.DEFENCE, 30),
			new QuestRequirement(Quest.THE_SLUG_MENACE));
        add("falador_hard", new SkillRequirement(Skill.DEFENCE, 50),
			new QuestRequirement(Quest.GRIM_TALES));
        add("falador_elite", new SkillRequirement(Skill.RUNECRAFT, 88));
        add("falador_elite", new QuestRequirement(Quest.WANTED));
        add("falador_elite", new SkillRequirement(Skill.FARMING, 91),
			new SkillRequirement(Skill.WOODCUTTING, 75));
        add("falador_elite", new SkillRequirement(Skill.AGILITY, 80));
        add("falador_elite", new SkillRequirement(Skill.HERBLORE, 81));
    }
    private static void Fremennik() {
        add("fremennik_easy", new SkillRequirement(Skill.HUNTER, 11));
        add("fremennik_easy", new QuestRequirement(Quest.THE_FREMENNIK_TRIALS));
        add("fremennik_easy", new SkillRequirement(Skill.CRAFTING, 23),
			new SkillRequirement(Skill.MINING, 20),
			new SkillRequirement(Skill.SMITHING, 20),
			new QuestRequirement(Quest.THE_FREMENNIK_TRIALS));
        add("fremennik_easy", new QuestRequirement(Quest.THE_GIANT_DWARF, true));
        add("fremennik_easy", new SkillRequirement(Skill.THIEVING, 5),
			new QuestRequirement(Quest.THE_GIANT_DWARF, true));
        add("fremennik_easy", new QuestRequirement(Quest.DEATH_PLATEAU),
			new QuestRequirement(Quest.TROLL_STRONGHOLD, true));
        add("fremennik_easy", new SkillRequirement(Skill.WOODCUTTING, 15),
			new SkillRequirement(Skill.FIREMAKING, 15));
        add("fremennik_medium", new SkillRequirement(Skill.SLAYER, 47),
			new QuestRequirement(Quest.OLAFS_QUEST, true));
        add("fremennik_medium", new QuestRequirement(Quest.EAGLES_PEAK));
        add("fremennik_medium", new SkillRequirement(Skill.MINING, 30),
			new QuestRequirement(Quest.THE_FREMENNIK_TRIALS));
        add("fremennik_medium", new SkillRequirement(Skill.THIEVING, 42),
			new QuestRequirement(Quest.THE_FREMENNIK_TRIALS));
        add("fremennik_medium", new QuestRequirement(Quest.THE_FREMENNIK_TRIALS),
			new QuestRequirement(Quest.FAIRYTALE_II__CURE_A_QUEEN, true));
        add("fremennik_medium", new SkillRequirement(Skill.HUNTER, 35));
        add("fremennik_medium", new SkillRequirement(Skill.CONSTRUCTION, 37),
			new QuestRequirement(Quest.THE_FREMENNIK_TRIALS));
        add("fremennik_medium", new QuestRequirement(Quest.HORROR_FROM_THE_DEEP),
			new QuestRequirement(Quest.THE_FREMENNIK_TRIALS, true));
        add("fremennik_medium", new SkillRequirement(Skill.MINING, 40),
			new QuestRequirement(Quest.BETWEEN_A_ROCK, true));
        add("fremennik_hard", new SkillRequirement(Skill.MAGIC, 61),
			new QuestRequirement(Quest.EADGARS_RUSE));
        add("fremennik_hard", new SkillRequirement(Skill.HUNTER, 55));
        add("fremennik_hard", new SkillRequirement(Skill.HERBLORE, 66));
        add("fremennik_hard", new SkillRequirement(Skill.THIEVING, 75),
			new QuestRequirement(Quest.THE_GIANT_DWARF, true));
        add("fremennik_hard", new SkillRequirement(Skill.WOODCUTTING, 56),
			new QuestRequirement(Quest.THE_FREMENNIK_ISLES));
        add("fremennik_hard", new SkillRequirement(Skill.MINING, 70),
			new QuestRequirement(Quest.THE_FREMENNIK_ISLES));
        add("fremennik_hard", new QuestRequirement(Quest.THRONE_OF_MISCELLANIA));
        add("fremennik_hard", new SkillRequirement(Skill.MAGIC, 72),
			new QuestRequirement(Quest.LUNAR_DIPLOMACY));
        add("fremennik_hard", new SkillRequirement(Skill.SMITHING, 60),
			new QuestRequirement(Quest.THE_GIANT_DWARF, true));
        add("fremennik_elite", new SkillRequirement(Skill.RUNECRAFT, 82),
			new QuestRequirement(Quest.LUNAR_DIPLOMACY));
        add("fremennik_elite", new SkillRequirement(Skill.CRAFTING, 80),
			new QuestRequirement(Quest.THE_FREMENNIK_ISLES, true));
        add("fremennik_elite", new SkillRequirement(Skill.AGILITY, 80));
        add("fremennik_elite", new SkillRequirement(Skill.AGILITY, 70),
			new SkillRequirement(Skill.STRENGTH, 70),
			new SkillRequirement(Skill.HITPOINTS, 70),
			new SkillRequirement(Skill.RANGED, 70),
			new QuestRequirement(Quest.TROLL_STRONGHOLD));
        add("fremennik_elite", new SkillRequirement(Skill.SLAYER, 83),
			new QuestRequirement(Quest.TROLL_STRONGHOLD));
    }
    private static void Kandarin() {
        add("kandarin_easy", new SkillRequirement(Skill.FISHING, 16));
        add("kandarin_easy", new SkillRequirement(Skill.FARMING, 13));
        add("kandarin_easy", new QuestRequirement(Quest.ELEMENTAL_WORKSHOP_I, true));
        add("kandarin_easy", new SkillRequirement(Skill.AGILITY, 20));
        add("kandarin_medium", new SkillRequirement(Skill.AGILITY, 35),
			new QuestRequirement(Quest.ALFRED_GRIMHANDS_BARCRAWL));
        add("kandarin_medium", new SkillRequirement(Skill.HERBLORE, 48));
        add("kandarin_medium", new SkillRequirement(Skill.RANGED, 40));
        add("kandarin_medium", new SkillRequirement(Skill.AGILITY, 36),
			new SkillRequirement(Skill.STRENGTH, 22),
			new SkillRequirement(Skill.RANGED, 39));
        add("kandarin_medium", new SkillRequirement(Skill.FISHING, 46),
			new SkillRequirement(Skill.COOKING, 43));
        add("kandarin_medium", new SkillRequirement(Skill.MAGIC, 45));
        add("kandarin_medium", new SkillRequirement(Skill.FLETCHING, 50));
        add("kandarin_medium", new SkillRequirement(Skill.FARMING, 26));
        add("kandarin_medium", new QuestRequirement(Quest.ELEMENTAL_WORKSHOP_II));
        add("kandarin_medium", new QuestRequirement(Quest.WATERFALL_QUEST, true));
        add("kandarin_medium", new SkillRequirement(Skill.THIEVING, 47));
        add("kandarin_medium", new QuestRequirement(Quest.FAIRYTALE_II__CURE_A_QUEEN, true));
        add("kandarin_medium", new SkillRequirement(Skill.MINING, 30));
        add("kandarin_hard", new SkillRequirement(Skill.FISHING, 70),
			new SkillRequirement(Skill.AGILITY, 45),
			new SkillRequirement(Skill.STRENGTH, 45));
        add("kandarin_hard", new SkillRequirement(Skill.AGILITY, 60));
        add("kandarin_hard", new SkillRequirement(Skill.WOODCUTTING, 60),
			new SkillRequirement(Skill.FLETCHING, 70),
			new SkillRequirement(Skill.CRAFTING, 10));
        add("kandarin_hard", new SkillRequirement(Skill.PRAYER, 70),
			new SkillRequirement(Skill.DEFENCE, 70),
			new QuestRequirement(Quest.KINGS_RANSOM));
        add("kandarin_hard", new SkillRequirement(Skill.MAGIC, 56));
        add("kandarin_hard", new SkillRequirement(Skill.FIREMAKING, 65));
        add("kandarin_hard", new SkillRequirement(Skill.THIEVING, 53),
			new QuestRequirement(Quest.DESERT_TREASURE_I, true));
        add("kandarin_hard", new SkillRequirement(Skill.STRENGTH, 50),
			new SkillRequirement(Skill.DEFENCE, 50));
        add("kandarin_hard", new SkillRequirement(Skill.CONSTRUCTION, 50));
        add("kandarin_hard", new SkillRequirement(Skill.SMITHING, 75),
			new QuestRequirement(Quest.TAI_BWO_WANNAI_TRIO));
        add("kandarin_elite", new SkillRequirement(Skill.FARMING, 79));
        add("kandarin_elite", new SkillRequirement(Skill.FISHING, 76),
			new SkillRequirement(Skill.COOKING, 80),
			new QuestRequirement(Quest.FAMILY_CREST));
        add("kandarin_elite", new SkillRequirement(Skill.HERBLORE, 86),
			new SkillRequirement(Skill.AGILITY, 60));
        add("kandarin_elite", new SkillRequirement(Skill.SMITHING, 90));
        add("kandarin_elite", new SkillRequirement(Skill.FIREMAKING, 85),
			new SkillRequirement(Skill.CRAFTING, 85));
        add("kandarin_elite", new SkillRequirement(Skill.MAGIC, 87),
			new QuestRequirement(Quest.LUNAR_DIPLOMACY));
    }
    private static void Karamja() {
        add("karamja_easy", new SkillRequirement(Skill.MINING, 40));
        add("karamja_easy", new SkillRequirement(Skill.AGILITY, 15));
        add("karamja_medium", new SkillRequirement(Skill.AGILITY, 30));
        add("karamja_medium", new QuestRequirement(Quest.DRAGON_SLAYER_I, true));
        add("karamja_medium", new QuestRequirement(Quest.DRAGON_SLAYER_I, true));
        add("karamja_medium", new QuestRequirement(Quest.SHILO_VILLAGE));
        add("karamja_medium", new SkillRequirement(Skill.WOODCUTTING, 10),
			new QuestRequirement(Quest.JUNGLE_POTION));
        add("karamja_medium", new SkillRequirement(Skill.COOKING, 16));
        add("karamja_medium", new QuestRequirement(Quest.SHILO_VILLAGE));
        add("karamja_medium", new SkillRequirement(Skill.WOODCUTTING, 35),
			new QuestRequirement(Quest.JUNGLE_POTION));
        add("karamja_medium", new SkillRequirement(Skill.WOODCUTTING, 50),
			new QuestRequirement(Quest.JUNGLE_POTION));
        add("karamja_medium", new SkillRequirement(Skill.FISHING, 65),
			new QuestRequirement(Quest.TAI_BWO_WANNAI_TRIO, true));
        add("karamja_medium", new QuestRequirement(Quest.JUNGLE_POTION));
        add("karamja_medium", new QuestRequirement(Quest.THE_GRAND_TREE));
        add("karamja_medium", new SkillRequirement(Skill.FARMING, 27));
        add("karamja_medium", new SkillRequirement(Skill.HUNTER, 41));
        add("karamja_medium", new SkillRequirement(Skill.WOODCUTTING, 10));
        add("karamja_medium", new SkillRequirement(Skill.AGILITY, 12));
        add("karamja_medium", new SkillRequirement(Skill.WOODCUTTING, 10));
        add("karamja_medium", new QuestRequirement(Quest.THE_GRAND_TREE));
        add("karamja_medium", new SkillRequirement(Skill.MINING, 40),
			new OrRequirement(
				new QuestRequirement(Quest.SHILO_VILLAGE),
				new QuestRequirement(Quest.JUNGLE_POTION)
			));
        add("karamja_hard", new SkillRequirement(Skill.RUNECRAFT, 44));
        add("karamja_hard", new SkillRequirement(Skill.COOKING, 30),
			new QuestRequirement(Quest.TAI_BWO_WANNAI_TRIO));
        add("karamja_hard", new SkillRequirement(Skill.WOODCUTTING, 15),
			new SkillRequirement(Skill.STRENGTH, 50),
			new SkillRequirement(Skill.AGILITY, 50),
			new SkillRequirement(Skill.THIEVING, 50),
			new SkillRequirement(Skill.MINING, 52),
			new QuestRequirement(Quest.LEGENDS_QUEST));
        add("karamja_hard", new SkillRequirement(Skill.AGILITY, 53),
			new SkillRequirement(Skill.RANGED, 42),
			new SkillRequirement(Skill.STRENGTH, 21));
        add("karamja_hard", new SkillRequirement(Skill.WOODCUTTING, 15),
			new QuestRequirement(Quest.LEGENDS_QUEST));
        add("karamja_hard", new CombatLevelRequirement(100),
			new SkillRequirement(Skill.SLAYER, 50),
			new QuestRequirement(Quest.SHILO_VILLAGE));
        add("karamja_elite", new SkillRequirement(Skill.RUNECRAFT, 91));
        add("karamja_elite", new SkillRequirement(Skill.FARMING, 68));
        add("karamja_elite", new SkillRequirement(Skill.HERBLORE, 87));
        add("karamja_elite", new SkillRequirement(Skill.FARMING, 72));
    }
    private static void Kourend() {
        add("kourend_kebos_easy", new SkillRequirement(Skill.MINING, 15));
        add("kourend_kebos_easy", new SkillRequirement(Skill.THIEVING, 25));
        add("kourend_kebos_easy", new SkillRequirement(Skill.CONSTRUCTION, 25));
        add("kourend_kebos_easy", new SkillRequirement(Skill.HERBLORE, 12));
        add("kourend_kebos_easy", new SkillRequirement(Skill.FISHING, 20));
        add("kourend_kebos_medium", new QuestRequirement(Quest.FAIRYTALE_II__CURE_A_QUEEN, true));
        add("kourend_kebos_medium", new QuestRequirement(Quest.THE_DEPTHS_OF_DESPAIR),
			new QuestRequirement(Quest.THE_QUEEN_OF_THIEVES),
			new QuestRequirement(Quest.TALE_OF_THE_RIGHTEOUS),
			new QuestRequirement(Quest.THE_FORSAKEN_TOWER),
			new QuestRequirement(Quest.THE_ASCENT_OF_ARCEUUS));
        add("kourend_kebos_medium", new SkillRequirement(Skill.MINING, 42));
        add("kourend_kebos_medium", new SkillRequirement(Skill.FARMING, 45));
        add("kourend_kebos_medium", new SkillRequirement(Skill.CRAFTING, 30),
			new SkillRequirement(Skill.CONSTRUCTION, 30));
        add("kourend_kebos_medium", new SkillRequirement(Skill.FISHING, 43),
			new SkillRequirement(Skill.HUNTER, 35));
        add("kourend_kebos_medium", new SkillRequirement(Skill.AGILITY, 49));
        add("kourend_kebos_medium", new SkillRequirement(Skill.FIREMAKING, 50));
        add("kourend_kebos_medium", new SkillRequirement(Skill.HUNTER, 53),
			new QuestRequirement(Quest.EAGLES_PEAK));
        add("kourend_kebos_medium", new SkillRequirement(Skill.WOODCUTTING, 50));
        add("kourend_kebos_hard", new SkillRequirement(Skill.WOODCUTTING, 60));
        add("kourend_kebos_hard", new SkillRequirement(Skill.SMITHING, 70),
			new QuestRequirement(Quest.THE_FORSAKEN_TOWER, true));
        add("kourend_kebos_hard", new SkillRequirement(Skill.MINING, 65));
        add("kourend_kebos_hard", new SkillRequirement(Skill.FARMING, 74));
        add("kourend_kebos_hard", new SkillRequirement(Skill.THIEVING, 49));
        add("kourend_kebos_hard", new SkillRequirement(Skill.SLAYER, 62));
        add("kourend_kebos_hard", new SkillRequirement(Skill.MAGIC, 66),
			new QuestRequirement(Quest.DREAM_MENTOR));
        add("kourend_kebos_elite", new SkillRequirement(Skill.RUNECRAFT, 77),
			new SkillRequirement(Skill.MINING, 38),
			new SkillRequirement(Skill.CRAFTING, 38));
        add("kourend_kebos_elite", new SkillRequirement(Skill.WOODCUTTING, 90));
        add("kourend_kebos_elite", new SkillRequirement(Skill.FISHING, 82),
			new SkillRequirement(Skill.COOKING, 84));
        add("kourend_kebos_elite", new SkillRequirement(Skill.SLAYER, 95));
        add("kourend_kebos_elite", new SkillRequirement(Skill.MAGIC, 90),
			new SkillRequirement(Skill.MINING, 38),
			new SkillRequirement(Skill.CRAFTING, 38));
        add("kourend_kebos_elite", new SkillRequirement(Skill.FARMING, 85),
			new SkillRequirement(Skill.FLETCHING, 40));
    }
    private static void Lumbridge() {
        add("lumbridge_draynor_easy", new SkillRequirement(Skill.SLAYER, 7));
        add("lumbridge_draynor_easy", new QuestRequirement(Quest.RUNE_MYSTERIES));
        add("lumbridge_draynor_easy", new SkillRequirement(Skill.RUNECRAFT, 5));
        add("lumbridge_draynor_easy", new SkillRequirement(Skill.WOODCUTTING, 15),
			new SkillRequirement(Skill.FIREMAKING, 15));
        add("lumbridge_draynor_easy", new SkillRequirement(Skill.FISHING, 15));
        add("lumbridge_draynor_easy", new QuestRequirement(Quest.COOKS_ASSISTANT));
        add("lumbridge_draynor_easy", new SkillRequirement(Skill.MINING, 15));
        add("lumbridge_draynor_medium", new SkillRequirement(Skill.AGILITY, 20));
        add("lumbridge_draynor_medium", new SkillRequirement(Skill.AGILITY, 8),
			new SkillRequirement(Skill.STRENGTH, 19),
			new SkillRequirement(Skill.RANGED, 37));
        add("lumbridge_draynor_medium", new SkillRequirement(Skill.RANGED, 50),
			new QuestRequirement(Quest.ANIMAL_MAGNETISM));
        add("lumbridge_draynor_medium", new QuestRequirement(Quest.FAIRYTALE_II__CURE_A_QUEEN, true));
        add("lumbridge_draynor_medium", new SkillRequirement(Skill.MAGIC, 31));
        add("lumbridge_draynor_medium", new SkillRequirement(Skill.FISHING, 30));
        add("lumbridge_draynor_medium", new SkillRequirement(Skill.CRAFTING, 38));
        add("lumbridge_draynor_medium", new SkillRequirement(Skill.WOODCUTTING, 30));
        add("lumbridge_draynor_medium", new SkillRequirement(Skill.THIEVING, 38));
        add("lumbridge_draynor_medium", new CombatLevelRequirement(70),
			new QuestRequirement(Quest.LOST_CITY));
        add("lumbridge_draynor_medium", new SkillRequirement(Skill.HUNTER, 42),
			new QuestRequirement(Quest.LOST_CITY));
        add("lumbridge_draynor_medium", new SkillRequirement(Skill.RUNECRAFT, 23));
        add("lumbridge_draynor_hard", new SkillRequirement(Skill.MAGIC, 60));
        add("lumbridge_draynor_hard", new SkillRequirement(Skill.AGILITY, 46),
			new QuestRequirement(Quest.LOST_CITY));
        add("lumbridge_draynor_hard", new SkillRequirement(Skill.RUNECRAFT, 59),
			new QuestRequirement(Quest.LOST_CITY));
        add("lumbridge_draynor_hard", new SkillRequirement(Skill.WOODCUTTING, 57));
        add("lumbridge_draynor_hard", new QuestRequirement(Quest.TEARS_OF_GUTHIX));
        add("lumbridge_draynor_hard", new QuestRequirement(Quest.ANOTHER_SLICE_OF_HAM));
        add("lumbridge_draynor_hard", new QuestRequirement(Quest.RECIPE_FOR_DISASTER));
        add("lumbridge_draynor_hard", new SkillRequirement(Skill.FARMING, 63));
        add("lumbridge_draynor_hard", new SkillRequirement(Skill.FIREMAKING, 65));
        add("lumbridge_draynor_hard", new SkillRequirement(Skill.PRAYER, 52));
        add("lumbridge_draynor_hard", new SkillRequirement(Skill.CRAFTING, 70),
			new SkillRequirement(Skill.MAGIC, 57));
        add("lumbridge_draynor_elite", new SkillRequirement(Skill.THIEVING, 78),
			new QuestRequirement(Quest.DEATH_TO_THE_DORGESHUUN));
        add("lumbridge_draynor_elite", new SkillRequirement(Skill.AGILITY, 70),
			new SkillRequirement(Skill.RANGED, 70),
			new SkillRequirement(Skill.STRENGTH, 70),
			new QuestRequirement(Quest.DEATH_TO_THE_DORGESHUUN));
        add("lumbridge_draynor_elite", new SkillRequirement(Skill.WOODCUTTING, 75));
        add("lumbridge_draynor_elite", new SkillRequirement(Skill.SMITHING, 88));
        add("lumbridge_draynor_elite", new SkillRequirement(Skill.RUNECRAFT, 76));
    }
    private static void Morytania() {
        add("morytania_easy", new SkillRequirement(Skill.CRAFTING, 15));
        add("morytania_easy", new SkillRequirement(Skill.COOKING, 12));
        add("morytania_easy", new CombatLevelRequirement(20));
        add("morytania_easy", new SkillRequirement(Skill.SLAYER, 15));
        add("morytania_easy", new SkillRequirement(Skill.FARMING, 23));
        add("morytania_easy", new QuestRequirement(Quest.PRIEST_IN_PERIL));
        add("morytania_easy", new QuestRequirement(Quest.NATURE_SPIRIT));
        add("morytania_medium", new SkillRequirement(Skill.HUNTER, 29));
        add("morytania_medium", new SkillRequirement(Skill.AGILITY, 40));
        add("morytania_medium", new SkillRequirement(Skill.WOODCUTTING, 45));
        add("morytania_medium", new SkillRequirement(Skill.SLAYER, 40),
			new QuestRequirement(Quest.LAIR_OF_TARN_RAZORLOR));
        add("morytania_medium", new SkillRequirement(Skill.COOKING, 40),
			new QuestRequirement(Quest.CABIN_FEVER));
        add("morytania_medium", new SkillRequirement(Skill.SMITHING, 35),
			new QuestRequirement(Quest.DWARF_CANNON));
        add("morytania_medium", new SkillRequirement(Skill.SLAYER, 42),
			new QuestRequirement(Quest.RUM_DEAL));
        add("morytania_medium", new QuestRequirement(Quest.GHOSTS_AHOY));
        add("morytania_medium", new SkillRequirement(Skill.HERBLORE, 22),
			new QuestRequirement(Quest.IN_AID_OF_THE_MYREQUE, true));
        add("morytania_hard", new SkillRequirement(Skill.MAGIC, 66),
			new SkillRequirement(Skill.CONSTRUCTION, 50),
			new QuestRequirement(Quest.DESERT_TREASURE_I));
        add("morytania_hard", new SkillRequirement(Skill.AGILITY, 71));
        add("morytania_hard", new SkillRequirement(Skill.FARMING, 47),
			new QuestRequirement(Quest.THE_GREAT_BRAIN_ROBBERY, true));
        add("morytania_hard", new SkillRequirement(Skill.WOODCUTTING, 50),
			new SkillRequirement(Skill.FIREMAKING, 50),
			new QuestRequirement(Quest.CABIN_FEVER));
        add("morytania_hard", new QuestRequirement(Quest.IN_AID_OF_THE_MYREQUE));
        add("morytania_hard", new SkillRequirement(Skill.SLAYER, 58),
			new QuestRequirement(Quest.CABIN_FEVER));
        add("morytania_hard", new SkillRequirement(Skill.FARMING, 53));
        add("morytania_hard", new SkillRequirement(Skill.PRAYER, 70),
			new SkillRequirement(Skill.DEFENCE, 70),
			new QuestRequirement(Quest.NATURE_SPIRIT),
			new QuestRequirement(Quest.KINGS_RANSOM));
        add("morytania_hard", new SkillRequirement(Skill.AGILITY, 65));
        add("morytania_hard", new SkillRequirement(Skill.MINING, 55),
			new QuestRequirement(Quest.HAUNTED_MINE));
        add("morytania_elite", new SkillRequirement(Skill.FISHING, 96),
			new SkillRequirement(Skill.STRENGTH, 76),
			new QuestRequirement(Quest.IN_AID_OF_THE_MYREQUE));
        add("morytania_elite", new SkillRequirement(Skill.FIREMAKING, 80),
			new QuestRequirement(Quest.SHADES_OF_MORTTON));
        add("morytania_elite", new SkillRequirement(Skill.MAGIC, 83),
			new QuestRequirement(Quest.LUNAR_DIPLOMACY));
        add("morytania_elite", new SkillRequirement(Skill.CRAFTING, 84));
        add("morytania_elite", new SkillRequirement(Skill.SLAYER, 85));
        add("morytania_elite", new SkillRequirement(Skill.DEFENCE, 70),
			new OrRequirement(
				new SkillRequirement(Skill.ATTACK, 70),
				new SkillRequirement(Skill.STRENGTH, 70),
				new SkillRequirement(Skill.RANGED, 70),
				new SkillRequirement(Skill.MAGIC, 70)
			));
    }
    private static void Varrock() {
        add("varrock_easy", new QuestRequirement(Quest.RUNE_MYSTERIES));
        add("varrock_easy", new SkillRequirement(Skill.MINING, 15));
        add("varrock_easy", new SkillRequirement(Skill.AGILITY, 13));
        add("varrock_easy", new SkillRequirement(Skill.CRAFTING, 8));
        add("varrock_easy", new SkillRequirement(Skill.RUNECRAFT, 9));
        add("varrock_easy", new SkillRequirement(Skill.FISHING, 20));
        add("varrock_easy", new SkillRequirement(Skill.THIEVING, 5));
        add("varrock_medium", new QuestPointRequirement(32));
        add("varrock_medium", new QuestRequirement(Quest.GARDEN_OF_TRANQUILLITY, true),
			new QuestRequirement(Quest.GERTRUDES_CAT));
        add("varrock_medium", new QuestRequirement(Quest.TREE_GNOME_VILLAGE));
        add("varrock_medium", new QuestRequirement(Quest.A_SOULS_BANE));
        add("varrock_medium", new QuestRequirement(Quest.THE_DIG_SITE));
        add("varrock_medium", new SkillRequirement(Skill.MAGIC, 25));
        add("varrock_medium", new CombatLevelRequirement(40));
        add("varrock_medium", new SkillRequirement(Skill.FARMING, 25),
			new QuestRequirement(Quest.GARDEN_OF_TRANQUILLITY));
        add("varrock_medium", new SkillRequirement(Skill.FIREMAKING, 40),
			new QuestRequirement(Quest.ENLIGHTENED_JOURNEY));
        add("varrock_medium", new SkillRequirement(Skill.AGILITY, 30));
        add("varrock_hard", new SkillRequirement(Skill.HUNTER, 66));
        add("varrock_hard", new SkillRequirement(Skill.WOODCUTTING, 57));
        add("varrock_hard", new SkillRequirement(Skill.MAGIC, 54),
			new QuestRequirement(Quest.DESERT_TREASURE_I));
        add("varrock_hard", new SkillRequirement(Skill.WOODCUTTING, 60),
			new SkillRequirement(Skill.FIREMAKING, 60));
        add("varrock_hard", new SkillRequirement(Skill.CONSTRUCTION, 50));
        add("varrock_hard", new SkillRequirement(Skill.WOODCUTTING, 60),
			new SkillRequirement(Skill.FARMING, 68));
        add("varrock_hard", new SkillRequirement(Skill.PRAYER, 52));
        add("varrock_hard", new SkillRequirement(Skill.AGILITY, 51));
        add("varrock_elite", new SkillRequirement(Skill.HERBLORE, 90),
			new QuestRequirement(Quest.DRUIDIC_RITUAL));
        add("varrock_elite", new SkillRequirement(Skill.MAGIC, 86),
			new QuestRequirement(Quest.DREAM_MENTOR));
        add("varrock_elite", new SkillRequirement(Skill.COOKING, 95));
        add("varrock_elite", new SkillRequirement(Skill.SMITHING, 89),
			new SkillRequirement(Skill.FLETCHING, 81),
			new QuestRequirement(Quest.THE_TOURIST_TRAP));
        add("varrock_elite", new SkillRequirement(Skill.RUNECRAFT, 78));
    }
    private static void Western() {
        add("western_provinces_easy", new SkillRequirement(Skill.HUNTER, 9));
        add("western_provinces_easy", new CombatLevelRequirement(40));
        add("western_provinces_easy", new SkillRequirement(Skill.MINING, 15));
        add("western_provinces_easy", new QuestRequirement(Quest.BIG_CHOMPY_BIRD_HUNTING));
        add("western_provinces_easy", new QuestRequirement(Quest.RUNE_MYSTERIES));
        add("western_provinces_easy", new SkillRequirement(Skill.FLETCHING, 20));
        add("western_provinces_medium", new SkillRequirement(Skill.AGILITY, 37),
			new QuestRequirement(Quest.TREE_GNOME_VILLAGE),
			new QuestRequirement(Quest.THE_GRAND_TREE));
        add("western_provinces_medium", new QuestRequirement(Quest.TREE_GNOME_VILLAGE));
        add("western_provinces_medium", new SkillRequirement(Skill.HUNTER, 31));
        add("western_provinces_medium", new SkillRequirement(Skill.FISHING, 46),
			new QuestRequirement(Quest.MONKEY_MADNESS_I, true));
        add("western_provinces_medium", new SkillRequirement(Skill.WOODCUTTING, 35),
			new SkillRequirement(Skill.FIREMAKING, 35),
			new QuestRequirement(Quest.MONKEY_MADNESS_I, true));
        add("western_provinces_medium", new CombatLevelRequirement(70));
        add("western_provinces_medium", new QuestRequirement(Quest.ONE_SMALL_FAVOUR),
			new QuestRequirement(Quest.THE_GRAND_TREE));
        add("western_provinces_medium", new QuestRequirement(Quest.BIG_CHOMPY_BIRD_HUNTING));
        add("western_provinces_medium", new QuestRequirement(Quest.EAGLES_PEAK));
        add("western_provinces_medium", new SkillRequirement(Skill.COOKING, 42));
        add("western_provinces_medium", new SkillRequirement(Skill.COOKING, 29));
        add("western_provinces_medium", new QuestRequirement(Quest.THE_EYES_OF_GLOUPHRIE));
        add("western_provinces_medium", new SkillRequirement(Skill.MINING, 40),
			new QuestRequirement(Quest.THE_GRAND_TREE));
        add("western_provinces_hard", new SkillRequirement(Skill.RANGED, 70),
			new SkillRequirement(Skill.AGILITY, 56),
			new QuestRequirement(Quest.ROVING_ELVES));
        add("western_provinces_hard", new SkillRequirement(Skill.FISHING, 62),
			new SkillRequirement(Skill.COOKING, 62),
			new QuestRequirement(Quest.SWAN_SONG));
        add("western_provinces_hard", new CombatLevelRequirement(100));
        add("western_provinces_hard", new SkillRequirement(Skill.HUNTER, 69));
        add("western_provinces_hard", new SkillRequirement(Skill.AGILITY, 48),
			new QuestRequirement(Quest.MONKEY_MADNESS_I));
        add("western_provinces_hard", new SkillRequirement(Skill.WOODCUTTING, 50),
			new SkillRequirement(Skill.FIREMAKING, 50),
			new QuestRequirement(Quest.MONKEY_MADNESS_I));
        add("western_provinces_hard", new SkillRequirement(Skill.MINING, 70),
			new QuestRequirement(Quest.REGICIDE));
        add("western_provinces_hard", new SkillRequirement(Skill.FARMING, 68),
			new QuestRequirement(Quest.MOURNINGS_END_PART_I, true));
        add("western_provinces_hard", new QuestRequirement(Quest.BIG_CHOMPY_BIRD_HUNTING));
        add("western_provinces_hard", new SkillRequirement(Skill.CONSTRUCTION, 65),
			new QuestRequirement(Quest.ROVING_ELVES));
        add("western_provinces_hard", new QuestRequirement(Quest.REGICIDE, true));
        add("western_provinces_hard", new SkillRequirement(Skill.MAGIC, 64),
			new QuestRequirement(Quest.RECIPE_FOR_DISASTER, true));
        add("western_provinces_hard", new SkillRequirement(Skill.THIEVING, 75));
        add("western_provinces_elite", new SkillRequirement(Skill.FLETCHING, 85),
			new QuestRequirement(Quest.MOURNINGS_END_PART_I));
        add("western_provinces_elite", new SkillRequirement(Skill.SLAYER, 93));
        add("western_provinces_elite", new SkillRequirement(Skill.FARMING, 75));
        add("western_provinces_elite", new SkillRequirement(Skill.AGILITY, 85),
			new QuestRequirement(Quest.UNDERGROUND_PASS));
        add("western_provinces_elite", new QuestRequirement(Quest.BIG_CHOMPY_BIRD_HUNTING));
        add("western_provinces_elite", new SkillRequirement(Skill.THIEVING, 85),
			new QuestRequirement(Quest.MOURNINGS_END_PART_I, true));
    }
    private static void Wilderness() {
        add("wilderness_easy", new SkillRequirement(Skill.MAGIC, 21));
        add("wilderness_easy", new SkillRequirement(Skill.AGILITY, 15));
        add("wilderness_easy", new SkillRequirement(Skill.MINING, 15));
        add("wilderness_easy", new QuestRequirement(Quest.ENTER_THE_ABYSS));
        add("wilderness_medium", new SkillRequirement(Skill.MINING, 55));
        add("wilderness_medium", new SkillRequirement(Skill.WOODCUTTING, 61));
        add("wilderness_medium", new OrRequirement(
				new SkillRequirement(Skill.AGILITY, 60),
				new SkillRequirement(Skill.STRENGTH, 60)
			));
        add("wilderness_medium", new SkillRequirement(Skill.AGILITY, 52));
        add("wilderness_medium", new SkillRequirement(Skill.MAGIC, 60));
        add("wilderness_medium", new SkillRequirement(Skill.SLAYER, 50));
        add("wilderness_medium", new SkillRequirement(Skill.SMITHING, 50),
			new QuestRequirement(Quest.BETWEEN_A_ROCK, true));
        add("wilderness_hard", new SkillRequirement(Skill.MAGIC, 60),
			new QuestRequirement(Quest.MAGE_ARENA_I));
        add("wilderness_hard", new SkillRequirement(Skill.MAGIC, 66));
        add("wilderness_hard", new SkillRequirement(Skill.HUNTER, 67));
        add("wilderness_hard", new SkillRequirement(Skill.SMITHING, 75));
        add("wilderness_hard", new SkillRequirement(Skill.AGILITY, 64),
			new QuestRequirement(Quest.DEATH_PLATEAU));
        add("wilderness_hard", new SkillRequirement(Skill.SLAYER, 68));
        add("wilderness_hard", new SkillRequirement(Skill.FISHING, 53));
        add("wilderness_elite", new SkillRequirement(Skill.MAGIC, 96),
			new QuestRequirement(Quest.DESERT_TREASURE_I));
        add("wilderness_elite", new SkillRequirement(Skill.FISHING, 85),
			new SkillRequirement(Skill.COOKING, 90));
        add("wilderness_elite", new SkillRequirement(Skill.MINING, 85),
			new SkillRequirement(Skill.SMITHING, 90));
        add("wilderness_elite", new SkillRequirement(Skill.THIEVING, 84));
        add("wilderness_elite", new SkillRequirement(Skill.SLAYER, 83),
			new OrRequirement(
				new SkillRequirement(Skill.AGILITY, 60),
				new SkillRequirement(Skill.STRENGTH, 60)
			));
        add("wilderness_elite", new SkillRequirement(Skill.WOODCUTTING, 75),
			new SkillRequirement(Skill.FIREMAKING, 75));
    }
}
