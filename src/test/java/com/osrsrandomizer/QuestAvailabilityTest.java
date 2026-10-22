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
        QuestAvailability free = new QuestAvailability(true, 1, Collections.emptyMap(), true, false);
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
        for (String id : new String[] {"zulrah", "perilous_moons", "nex", "doom_of_mokhaiotl"})
        {
            assertFalse(snapshot.isChecked(Category.BOSSES, id));
            assertNull(snapshot.reason(Category.BOSSES, id));
        }
        assertNull(snapshot.reason(Category.SKILLS, "attack"));
        assertFalse(snapshot.isChecked(Category.RAIDS, "tob"));
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
        QuestAvailability snapshot = snapshot(true, 1, states, true);
        int checked = 0;
        for (Category category : Category.values()) for (Activity activity : category.getActivities())
        {
            if (snapshot.isChecked(category, activity.getId())) checked++;
            assertNull(snapshot.reason(category, activity.getId()));
        }
        assertEquals(18, checked);
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
