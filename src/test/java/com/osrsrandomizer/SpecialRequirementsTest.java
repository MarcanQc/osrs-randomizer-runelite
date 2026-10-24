package com.osrsrandomizer;

import java.util.*;
import net.runelite.api.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class SpecialRequirementsTest
{
    private Client client(int zulrah, int target, boolean clue, String taskName)
    {
        ItemContainer inventory = (ItemContainer) java.lang.reflect.Proxy.newProxyInstance(
            ItemContainer.class.getClassLoader(), new Class<?>[] {ItemContainer.class}, (proxy, method, args) ->
                method.getName().equals("getItems") ? new Item[] {new Item(clue ? 31270 : -1, clue ? 1 : 0)} : null);
        return (Client) java.lang.reflect.Proxy.newProxyInstance(Client.class.getClassLoader(), new Class<?>[] {Client.class},
            (proxy, method, args) ->
            {
                switch (method.getName())
                {
                    case "getRealSkillLevel": case "getBoostedSkillLevel": return 99;
                    case "getVarbitValue": return (int) args[0] == 4391 ? zulrah : 1;
                    case "getVarpValue": return (int) args[0] == 394 ? (target == 0 ? 0 : 1) : target;
                    case "getItemContainer": return inventory;
                    case "getDBRowsByValue": return Collections.singletonList(123);
                    case "getDBTableField": return (int) args[1] == 4 ? new Object[] {456} : new Object[] {taskName};
                    default: throw new IllegalStateException("Unexpected read " + method.getName());
                }
            });
    }

    @Test public void clientReadsResolveNormalAndBossTasksAndSpecificClue()
    {
        assertNull(BossAccess.read(client(2, 1, false, "Gryphons")).get("shellbane_gryphon"));
        assertNull(BossAccess.read(client(2, 98, false, "Shellbane gryphon")).get("shellbane_gryphon"));
        assertNull(BossAccess.read(client(2, 0, true, "")).get("shellbane_gryphon"));
        assertNotNull(BossAccess.read(client(2, 0, false, "")).get("shellbane_gryphon"));
        assertNotNull(BossAccess.read(client(2, 1, false, "Dragons")).get("shellbane_gryphon"));
    }

    @Test public void priestessAcceptanceIsRequiredForZulrah()
    {
        assertNotNull(BossAccess.read(client(0, 0, false, "")).get("zulrah"));
        assertNotNull(BossAccess.read(client(1, 0, false, "")).get("zulrah"));
        assertNull(BossAccess.read(client(2, 0, false, "")).get("zulrah"));
    }

    @Test public void gryphonAlternativesAndUnknownData()
    {
        assertNull(BossAccess.gryphonReason(true, false));
        assertNull(BossAccess.gryphonReason(false, true));
        assertNull(BossAccess.gryphonReason(null, true));
        assertNull(BossAccess.gryphonReason(true, null));
        assertTrue(BossAccess.gryphonReason(false, false).contains("elite clue"));
        assertTrue(BossAccess.gryphonReason(null, false).startsWith("Waiting"));
    }

    private QuestAvailability boss(Map<Skill, Integer> levels, Map<String, String> access, QuestState quest)
    {
        Map<String, String> gates = new HashMap<>(access);
        BossAccess.GOD_WARS.forEach(id -> gates.put(id, null));
        return new QuestAvailability(true, 1, Collections.singletonMap(Quest.TROUBLED_TORTUGANS, quest),
            true, true, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), levels, gates);
    }

    @Test public void godWarsEntranceAllowsEitherSkill()
    {
        assertNotNull(BossAccess.entranceReason(59, 59));
        assertNull(BossAccess.entranceReason(60, 1));
        assertNull(BossAccess.entranceReason(1, 60));
    }

    @Test public void gryphonTaskNeverWaivesLevelOrQuest()
    {
        Map<Skill, Integer> levels = DiaryRequirementsTest.maxLevels();
        Map<String, String> access = Collections.singletonMap("shellbane_gryphon", null);
        levels.put(Skill.SLAYER, 50);
        assertEquals("Requires 51 Slayer", boss(levels, access, QuestState.FINISHED).reason(Category.BOSSES, "shellbane_gryphon"));
        levels.put(Skill.SLAYER, 51);
        assertTrue(boss(levels, access, QuestState.IN_PROGRESS).reason(Category.BOSSES, "shellbane_gryphon").startsWith("Complete"));
        assertNull(boss(levels, access, QuestState.FINISHED).reason(Category.BOSSES, "shellbane_gryphon"));
        assertNotNull(boss(levels, Collections.emptyMap(), QuestState.FINISHED).reason(Category.BOSSES, "shellbane_gryphon"));
    }

    @Test public void godWarsChamberThresholds()
    {
        String[] ids = {"general_graardor", "commander_zilyana", "kree_arra", "kril_tsutsaroth"};
        Skill[] skills = {Skill.STRENGTH, Skill.AGILITY, Skill.RANGED, Skill.HITPOINTS};
        for (int i = 0; i < ids.length; i++)
        {
            Map<Skill, Integer> levels = DiaryRequirementsTest.maxLevels();
            levels.put(skills[i], 69);
            assertNotNull(boss(levels, Collections.emptyMap(), QuestState.FINISHED).reason(Category.BOSSES, ids[i]));
            levels.put(skills[i], 70);
            assertNull(boss(levels, Collections.emptyMap(), QuestState.FINISHED).reason(Category.BOSSES, ids[i]));
        }
    }

    private DiaryRequirements.Evaluation diaries(Map<Skill, Integer> levels, Integer qp, Integer max, int lumbridge, int falador)
    {
        return DiaryRequirements.evaluate(levels, DiaryRequirementsTest.finishedQuests(), 126, qp,
            id -> id == 1195 ? lumbridge : id == 1187 ? falador : 0, id -> id == 1782 ? max : Integer.valueOf(0));
    }

    @Test public void questCapeUsesDynamicMaximumAndIgnoresCompletedEmote()
    {
        Map<Skill, Integer> levels = DiaryRequirementsTest.maxLevels();
        assertTrue(diaries(levels, 299, 300, 0, 0).reasons.get("lumbridge_draynor_elite").contains("Quest point cape"));
        assertNull(diaries(levels, 300, 300, 0, 0).reasons.get("lumbridge_draynor_elite"));
        assertNotNull(diaries(levels, 300, 301, 0, 0).reasons.get("lumbridge_draynor_elite"));
        assertNull(diaries(levels, 300, 301, 1 << 9, 0).reasons.get("lumbridge_draynor_elite"));
        assertTrue(diaries(levels, 300, null, 0, 0).reasons.get("lumbridge_draynor_elite").startsWith("Waiting"));
    }

    @Test public void faladorCapeAllowsEitherBranchAndSkipsCompletedTask()
    {
        Map<Skill, Integer> levels = DiaryRequirementsTest.maxLevels();
        levels.replaceAll((skill, value) -> 98);
        assertNotNull(diaries(levels, 299, 300, 0, 0).reasons.get("falador_elite"));
        assertNull(diaries(levels, 300, 300, 0, 0).reasons.get("falador_elite"));
        levels.put(Skill.COOKING, 99);
        assertNull(diaries(levels, 299, null, 0, 0).reasons.get("falador_elite"));
        levels.put(Skill.COOKING, 98);
        assertNull(diaries(levels, 299, null, 0, 1 << 8).reasons.get("falador_elite"));
    }
}
