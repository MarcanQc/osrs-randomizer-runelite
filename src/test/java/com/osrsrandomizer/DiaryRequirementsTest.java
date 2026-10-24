package com.osrsrandomizer;

import java.util.*;
import net.runelite.api.*;
import net.runelite.client.plugins.achievementdiary.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class DiaryRequirementsTest
{
    @Test public void completedTaskWaivesOnlyItsOwnRequirements()
    {
        Map<Skill, Integer> levels = maxLevels();
        levels.put(Skill.THIEVING, 1);
        Map<Quest, QuestState> quests = finishedQuests();
        quests.put(Quest.BIOHAZARD, QuestState.NOT_STARTED);
        // Ardougne: cake task is bit 1, combat training camp is bit 6.
        DiaryRequirements.Evaluation cakeDone = DiaryRequirements.evaluate(levels, quests, 126, 999,
            id -> id == 1196 ? 1 << 1 : 0, id -> 0);
        assertEquals("Complete Biohazard", cakeDone.reasons.get("ardougne_easy"));
        assertFalse(cakeDone.details.get("ardougne_easy").contains("Thieving"));
        assertNull(DiaryRequirements.evaluate(levels, quests, 126, 999,
            id -> id == 1196 ? (1 << 1) | (1 << 6) : 0, id -> 0).reasons.get("ardougne_easy"));
        assertTrue(DiaryRequirements.evaluate(levels, quests, 126, 999,
            id -> null, id -> null).reasons.get("ardougne_easy").contains("5 Thieving"));
    }

    @Test public void partialKaramjaTaskDoesNotWaiveRequirements()
    {
        Map<Skill, Integer> levels = maxLevels();
        levels.put(Skill.WOODCUTTING, 1);
        // Collect FIVE palm leaves: 4 is incomplete, 5 complete.
        assertNotNull(DiaryRequirements.evaluate(levels, finishedQuests(), 126, 999,
            id -> 0, id -> id == 3607 ? 4 : 1).reasons.get("karamja_hard"));
        assertNull(DiaryRequirements.evaluate(levels, finishedQuests(), 126, 999,
            id -> 0, id -> id == 3607 ? 5 : 1).reasons.get("karamja_hard"));
    }

    @Test public void signBitCompletionAndUnknownReadsAreHandled()
    {
        DiaryTask task = DiaryRequirementsData.tasks().get("ardougne_hard").stream()
            .filter(t -> t.name.contains("Red Salamander")).findFirst().get();
        assertTrue(task.completed(id -> Integer.MIN_VALUE, id -> 0));
        assertFalse(task.completed(id -> Integer.MAX_VALUE, id -> 0));
        assertFalse(task.completed(id -> null, id -> null));
    }

    @Test public void completedTasksAcrossAllTiersIgnoreEvenUnknownRequirements()
    {
        DiaryRequirements.Evaluation allDone = DiaryRequirements.evaluate(Collections.emptyMap(), Collections.emptyMap(), null, null,
            id -> -1, id -> 100);
        assertEquals(48, allDone.reasons.size());
        allDone.reasons.values().forEach(reason -> assertNull(reason));
        allDone.details.values().forEach(detail -> assertEquals("No missing tracked requirements for unfinished tasks.", detail));
    }

    @Test public void detailsShowCurrentLevelsAndQuestStateAndRefreshOnLevelChange()
    {
        Map<Skill, Integer> levels = maxLevels();
        levels.put(Skill.THIEVING, 3);
        Map<Quest, QuestState> quests = finishedQuests();
        quests.put(Quest.BIOHAZARD, QuestState.IN_PROGRESS);
        DiaryRequirements.Evaluation first = DiaryRequirements.evaluate(levels, quests, 126, 999, id -> 0, id -> 0);
        String detail = first.details.get("ardougne_easy");
        assertTrue(detail.contains("Thieving: 3 / 5"));
        assertTrue(detail.contains("Complete Biohazard (in progress)"));
        levels.put(Skill.THIEVING, 4);
        DiaryRequirements.Evaluation second = DiaryRequirements.evaluate(levels, quests, 126, 999, id -> 0, id -> 0);
        assertEquals(first.reasons, second.reasons);
        assertNotEquals(new QuestAvailability(true, 1, quests, false, true, Collections.emptyMap(), first.reasons, first.details),
            new QuestAvailability(true, 1, quests, false, true, Collections.emptyMap(), second.reasons, second.details));
    }

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
        return DiaryRequirements.evaluate(maxLevels(), finishedQuests(), 126, 999, id -> 0, id -> id == 1782 ? 999 : 0).reasons;
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
