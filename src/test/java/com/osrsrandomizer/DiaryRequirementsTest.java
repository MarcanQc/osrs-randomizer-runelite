package com.osrsrandomizer;

import java.util.*;
import net.runelite.api.*;
import net.runelite.client.plugins.achievementdiary.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class DiaryRequirementsTest
{
    static Map<Skill, Integer> maxLevels()
    {
        Map<Skill, Integer> levels = new EnumMap<>(Skill.class);
        for (Skill skill : Skill.values()) levels.put(skill, 99);
        return levels;
    }

    static Map<Quest, QuestState> finishedQuests()
    {
        Map<Quest, QuestState> quests = new EnumMap<>(Quest.class);
        DiaryRequirements.quests().forEach(q -> quests.put(q, QuestState.FINISHED));
        return quests;
    }

    static Map<String, String> eligible()
    {
        return DiaryRequirements.evaluate(maxLevels(), finishedQuests(), 126, 999);
    }

    @Test public void all48TiersHaveRulesAndUnknownDataNeverUnlocks()
    {
        assertEquals(DiaryCatalog.COMPLETION_VARBITS.keySet(), DiaryRequirementsData.rules().keySet());
        assertEquals(48, eligible().size());
        eligible().values().forEach(reason -> assertNull(reason));
        DiaryRequirements.evaluate(Collections.emptyMap(), Collections.emptyMap(), null, null)
            .values().forEach(reason -> assertEquals("Waiting for skill/quest data", reason));
    }

    @Test public void skillBoundaryAndQuestCompletionUnlockArdougneEasy()
    {
        Map<Skill, Integer> levels = maxLevels();
        Map<Quest, QuestState> quests = finishedQuests();
        levels.put(Skill.THIEVING, 4);
        quests.put(Quest.BIOHAZARD, QuestState.IN_PROGRESS);
        String reason = DiaryRequirements.evaluate(levels, quests, 126, 999).get("ardougne_easy");
        assertTrue(reason.contains("5 Thieving"));
        assertTrue(reason.contains("Complete Biohazard"));
        levels.put(Skill.THIEVING, 5);
        assertEquals("Complete Biohazard", DiaryRequirements.evaluate(levels, quests, 126, 999).get("ardougne_easy"));
        quests.put(Quest.BIOHAZARD, QuestState.FINISHED);
        assertNull(DiaryRequirements.evaluate(levels, quests, 126, 999).get("ardougne_easy"));
    }

    @Test public void startedQuestIsDifferentFromCompletedQuest()
    {
        Quest quest = Quest.FAIRYTALE_II__CURE_A_QUEEN;
        for (QuestState state : QuestState.values())
        {
            Map<Quest, QuestState> states = Collections.singletonMap(quest, state);
            assertEquals(state != QuestState.NOT_STARTED,
                DiaryRequirements.satisfied(new QuestRequirement(quest, true), maxLevels(), states, 126, 999));
            assertEquals(state == QuestState.FINISHED,
                DiaryRequirements.satisfied(new QuestRequirement(quest), maxLevels(), states, 126, 999));
        }
    }

    @Test public void alternativeSkillsDoNotBecomeAnAndRequirement()
    {
        Map<Skill, Integer> levels = maxLevels();
        levels.put(Skill.AGILITY, 52);
        levels.put(Skill.STRENGTH, 60);
        assertNull(DiaryRequirements.evaluate(levels, finishedQuests(), 126, 999).get("wilderness_medium"));
        levels.put(Skill.STRENGTH, 59);
        assertTrue(DiaryRequirements.evaluate(levels, finishedQuests(), 126, 999)
            .get("wilderness_medium").contains("60 Agility or 60 Strength"));
        Requirement or = new OrRequirement(new SkillRequirement(Skill.AGILITY, 60), new SkillRequirement(Skill.STRENGTH, 60));
        levels.remove(Skill.AGILITY);
        assertNull(DiaryRequirements.satisfied(or, levels, finishedQuests(), 126, 999));
        levels.put(Skill.STRENGTH, 60);
        assertTrue(DiaryRequirements.satisfied(or, levels, finishedQuests(), 126, 999));
    }

    @Test public void combatAndQuestPointBoundariesAreChecked()
    {
        assertTrue(DiaryRequirements.evaluate(maxLevels(), finishedQuests(), 39, 31)
            .get("varrock_medium").contains("32 Quest points"));
        assertTrue(DiaryRequirements.evaluate(maxLevels(), finishedQuests(), 39, 32)
            .get("varrock_medium").contains("40 Combat"));
        assertNull(DiaryRequirements.evaluate(maxLevels(), finishedQuests(), 40, 32).get("varrock_medium"));
    }
}
