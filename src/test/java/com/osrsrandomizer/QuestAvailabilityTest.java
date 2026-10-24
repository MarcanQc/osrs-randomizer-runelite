package com.osrsrandomizer;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import org.junit.Test;
import static org.junit.Assert.*;

public class QuestAvailabilityTest
{
    private static Map<String, String> unlockedAccess()
    {
        Map<String, String> access = new java.util.HashMap<>();
        BossAccess.IDS.forEach(id -> access.put(id, null));
        return access;
    }
    @Test public void newlyAuditedQuestGatesRequireCompletion()
    {
        Map<String, Quest> bosses = new java.util.LinkedHashMap<>();
        bosses.put("mad_angel", Quest.FALLEN_FROM_GRACE);
        bosses.put("maggot_king", Quest.THE_BLOOD_MOON_RISES);
        bosses.put("brutus", Quest.THE_IDES_OF_MILK);
        bosses.put("shellbane_gryphon", Quest.TROUBLED_TORTUGANS);
        bosses.put("perilous_moons", Quest.PERILOUS_MOONS);
        bosses.put("doom_of_mokhaiotl", Quest.THE_FINAL_DAWN);
        bosses.put("yama", Quest.A_KINGDOM_DIVIDED);
        bosses.put("nex", Quest.THE_FROZEN_DOOR);
        bosses.put("the_hueycoatl", Quest.CHILDREN_OF_THE_SUN);
        bosses.put("sol_heredit", Quest.CHILDREN_OF_THE_SUN);
        for (String id : new String[] {"barrows", "the_nightmare", "phosanis_nightmare"}) bosses.put(id, Quest.PRIEST_IN_PERIL);
        for (Map.Entry<String, Quest> gate : bosses.entrySet())
        {
            for (QuestState state : QuestState.values())
            {
                QuestAvailability snapshot = new QuestAvailability(true, 1, Collections.singletonMap(gate.getValue(), state), true, true,
                    Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), DiaryRequirementsTest.maxLevels(), unlockedAccess());
                assertEquals(gate.getKey(), state == QuestState.FINISHED,
                    snapshot.reason(Category.BOSSES, gate.getKey()) == null);
            }
        }
        assertNotNull(new QuestAvailability(true, 1, Collections.emptyMap(), true, true).reason(Category.RAIDS, "tob"));
        assertNull(new QuestAvailability(true, 1, Collections.singletonMap(Quest.PRIEST_IN_PERIL, QuestState.FINISHED), true, true)
            .reason(Category.RAIDS, "tob"));
        assertNull(new QuestAvailability(true, 1, Collections.singletonMap(Quest.THE_IDES_OF_MILK, QuestState.FINISHED), true, false)
            .reason(Category.BOSSES, "brutus"));
    }

    @Test public void skillingBossThresholdsAndDetailSnapshots()
    {
        for (String id : new String[] {"tempoross", "wintertodt"})
        {
            net.runelite.api.Skill skill = id.equals("tempoross") ? net.runelite.api.Skill.FISHING : net.runelite.api.Skill.FIREMAKING;
            int required = id.equals("tempoross") ? 35 : 50;
            Map<net.runelite.api.Skill, Integer> levels = new EnumMap<>(net.runelite.api.Skill.class);
            levels.put(skill, required - 1);
            QuestAvailability below = withLevels(levels);
            assertNotNull(below.reason(Category.BOSSES, id));
            assertTrue(below.requirementDetails(Category.BOSSES, id).contains((required - 1) + " / " + required));
            levels.put(skill, required);
            assertNull(withLevels(levels).reason(Category.BOSSES, id));
            assertNotNull(below.reason(Category.BOSSES, id)); // immutable snapshot
            assertEquals("Waiting for skill data", withLevels(Collections.emptyMap()).reason(Category.BOSSES, id));
        }
    }

    private QuestAvailability withLevels(Map<net.runelite.api.Skill, Integer> levels)
    {
        return new QuestAvailability(true, 1, Collections.emptyMap(), true, true, Collections.emptyMap(),
            Collections.emptyMap(), Collections.emptyMap(), levels);
    }

    @Test public void requirementDetailsIncludeAllMissingGatesAndClearAfterUnlock()
    {
        Map<Quest, QuestState> states = new EnumMap<>(Quest.class);
        states.put(Quest.PANDEMONIUM, QuestState.IN_PROGRESS);
        states.put(Quest.DRAGON_SLAYER_II, QuestState.NOT_STARTED);
        states.put(Quest.WHILE_GUTHIX_SLEEPS, QuestState.IN_PROGRESS);
        QuestAvailability snapshot = new QuestAvailability(true, 1, states, false, false);
        assertTrue(snapshot.requirementDetails(Category.SKILLS, "sailing").contains("Members only"));
        assertTrue(snapshot.requirementDetails(Category.SKILLS, "sailing").contains("Pandemonium (in progress)"));
        assertTrue(snapshot.requirementDetails(Category.BOSSES, "vorkath").contains("Dragon Slayer II (not started)"));
        assertTrue(snapshot.requirementDetails(Category.RAIDS, "tfa").contains("While Guthix Sleeps (in progress)"));
        assertTrue(snapshot.requirementDetails(Category.BOSSES, "artio").contains("Medium Wilderness Diary (not completed)"));
        assertTrue(snapshot.requirementDetails(Category.BOSSES, "obor").contains("No quest or diary requirements are tracked"));
        states.put(Quest.WHILE_GUTHIX_SLEEPS, QuestState.FINISHED);
        QuestAvailability unlocked = new QuestAvailability(true, 2, states, true, true);
        assertTrue(unlocked.requirementDetails(Category.RAIDS, "tfa").startsWith("No missing tracked requirements."));
        assertFalse(unlocked.requirementDetails(Category.RAIDS, "tfa").contains("in progress"));
        assertTrue(QuestAvailability.unavailable().requirementDetails(Category.RAIDS, "tfa").startsWith("Log in"));
    }

    @Test public void fracturedArchiveRequiresMembershipAndWhileGuthixSleeps()
    {
        for (QuestState state : QuestState.values())
        {
            Map<Quest, QuestState> states = Collections.singletonMap(Quest.WHILE_GUTHIX_SLEEPS, state);
            assertEquals(state == QuestState.FINISHED ? null : "Complete While Guthix Sleeps",
                new QuestAvailability(true, 1, states, true, true).reason(Category.RAIDS, "tfa"));
            assertEquals("Members only",
                new QuestAvailability(true, 1, states, true, false).reason(Category.RAIDS, "tfa"));
        }
        assertEquals("Waiting for quest data", snapshot(true, 1, Collections.emptyMap()).reason(Category.RAIDS, "tfa"));
    }

    @Test public void skillQuestGatesUnlockOnlyAfterCompletion()
    {
        for (QuestState state : QuestState.values())
        {
            Map<Quest, QuestState> states = new EnumMap<>(Quest.class);
            states.put(Quest.PANDEMONIUM, state);
            states.put(Quest.DRUIDIC_RITUAL, state);
            QuestAvailability snapshot = snapshot(true, 1, states);
            assertEquals(state == QuestState.FINISHED ? null : "Complete Pandemonium", snapshot.reason(Category.SKILLS, "sailing"));
            assertEquals(state == QuestState.FINISHED ? null : "Complete Druidic Ritual", snapshot.reason(Category.SKILLS, "herblore"));
            assertNull(snapshot.reason(Category.SKILLS, "runecraft"));
            assertNull(snapshot.reason(Category.SKILLS, "construction"));
        }
    }

    @Test public void freeAccountsKeepFreeSkillsAndBossesButCannotDrawMemberContent()
    {
        QuestAvailability free = new QuestAvailability(true, 1, Collections.singletonMap(Quest.THE_IDES_OF_MILK, QuestState.FINISHED), true, false);
        int freeSkills = 0;
        for (Activity skill : Category.SKILLS.getActivities())
        {
            String reason = free.reason(Category.SKILLS, skill.getId());
            if (reason == null) freeSkills++;
            else assertEquals("Members only", reason);
        }
        assertEquals(15, freeSkills);
        for (String id : new String[] {"brutus", "obor", "bryophyta"})
            assertNull(free.reason(Category.BOSSES, id));
        assertEquals("Members only", free.reason(Category.BOSSES, "vorkath"));
        assertEquals("Members only", free.reason(Category.BOSSES, "artio"));
        for (Activity raid : Category.RAIDS.getActivities())
            assertEquals("Members only", free.reason(Category.RAIDS, raid.getId()));
    }

    @Test public void membershipRefreshAndUnknownStateAreDistinguished()
    {
        QuestAvailability unknown = new QuestAvailability(true, 1, Collections.emptyMap(), true, null);
        QuestAvailability member = new QuestAvailability(true, 1, Collections.emptyMap(), true, true);
        QuestAvailability free = new QuestAvailability(true, 1, Collections.emptyMap(), true, false);
        assertEquals("Waiting for membership data", unknown.reason(Category.SKILLS, "agility"));
        assertNull(unknown.reason(Category.SKILLS, "mining"));
        assertNull(member.reason(Category.SKILLS, "agility"));
        assertNotEquals(free, member);
        assertNotEquals(unknown, free);
        assertEquals(Boolean.TRUE, QuestAvailability.membershipFrom(14, false));
        assertEquals(Boolean.TRUE, QuestAvailability.membershipFrom(0, true));
        assertEquals(Boolean.FALSE, QuestAvailability.membershipFrom(0, false));
        assertNull(QuestAvailability.membershipFrom(-1, false));
    }

    @Test public void unfinishedAndInProgressQuestsBlockRepeatBosses()
    {
        for (QuestState state : new QuestState[] {QuestState.NOT_STARTED, QuestState.IN_PROGRESS})
        {
            QuestAvailability snapshot = snapshot(true, 1,
                Collections.singletonMap(Quest.DRAGON_SLAYER_II, state));
            assertEquals("Complete Dragon Slayer II", snapshot.reason(Category.BOSSES, "vorkath"));
        }
    }

    @Test public void completionUnlocksWhileUnreadDataStaysUnknown()
    {
        QuestAvailability snapshot = snapshot(true, 1,
            Collections.singletonMap(Quest.BENEATH_CURSED_SANDS, QuestState.FINISHED));
        assertNull(snapshot.reason(Category.RAIDS, "toa"));
        assertEquals("Waiting for quest data", snapshot.reason(Category.BOSSES, "vorkath"));
        assertFalse(QuestAvailability.unavailable().isReady());
        assertEquals("Waiting for membership data", QuestAvailability.unavailable().reason(Category.RAIDS, "toa"));
    }

    @Test public void partialQuestAccessAndUnmappedActivitiesAreNotGuessed()
    {
        QuestAvailability snapshot = snapshot(true, 1, Collections.emptyMap());
        for (String id : new String[] {"giant_mole"})
        {
            assertFalse(snapshot.isChecked(Category.BOSSES, id));
            assertNull(snapshot.reason(Category.BOSSES, id));
        }
        assertNull(snapshot.reason(Category.SKILLS, "attack"));
        assertTrue(snapshot.isChecked(Category.RAIDS, "tob"));
        assertEquals("Waiting for quest data", snapshot.reason(Category.RAIDS, "tob"));
    }

    @Test public void snapshotIsImmutableAndAccountSpecific()
    {
        Map<Quest, QuestState> states = new EnumMap<>(Quest.class);
        states.put(Quest.DRAGON_SLAYER_II, QuestState.FINISHED);
        QuestAvailability snapshot = snapshot(true, 1, states);
        states.clear();
        assertNull(snapshot.reason(Category.BOSSES, "vorkath"));
        assertNotEquals(snapshot, snapshot(true, 2,
            Collections.singletonMap(Quest.DRAGON_SLAYER_II, QuestState.FINISHED)));
    }

    @Test public void everyCoveredActivityUnlocksWhenSupportedQuestsFinish()
    {
        Map<Quest, QuestState> states = new EnumMap<>(Quest.class);
        for (Quest quest : QuestAvailability.quests()) states.put(quest, QuestState.FINISHED);
        QuestAvailability snapshot = new QuestAvailability(true, 1, states, true, true,
            Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), DiaryRequirementsTest.maxLevels(), unlockedAccess());
        int checked = 0;
        for (Category category : Category.values()) for (Activity activity : category.getActivities())
        {
            if (category == Category.DIARIES) continue; // Diary completion has separate coverage.
            if (snapshot.isChecked(category, activity.getId())) checked++;
            assertNull(snapshot.reason(category, activity.getId()));
        }
        assertEquals(39, checked);
    }

    @Test public void mediumWildernessDiaryBlocksOnlyTheThreeSinglesBosses()
    {
        QuestAvailability locked = snapshot(true, 1, Collections.emptyMap(), false);
        QuestAvailability unlocked = snapshot(true, 1, Collections.emptyMap(), true);
        QuestAvailability unknown = snapshot(true, 1, Collections.emptyMap());
        for (String id : new String[] {"artio", "spindel", "calvarion"})
        {
            assertTrue(locked.isChecked(Category.BOSSES, id));
            assertEquals("Complete the Medium Wilderness Diary", locked.reason(Category.BOSSES, id));
            assertEquals("Waiting for diary data", unknown.reason(Category.BOSSES, id));
            assertNull(unlocked.reason(Category.BOSSES, id));
            assertEquals("Waiting for membership data", QuestAvailability.unavailable().reason(Category.BOSSES, id));
        }
        for (String id : new String[] {"callisto", "venenatis", "vetion"})
            assertNull(locked.reason(Category.BOSSES, id));
        assertNotEquals(locked, unlocked);
    }

    private static QuestAvailability snapshot(boolean ready, long account, Map<net.runelite.api.Quest, net.runelite.api.QuestState> states)
    {
        return snapshot(ready, account, states, null);
    }

    private static QuestAvailability snapshot(boolean ready, long account, Map<net.runelite.api.Quest, net.runelite.api.QuestState> states, Boolean diary)
    {
        return new QuestAvailability(ready, account, states, diary, true);
    }}
