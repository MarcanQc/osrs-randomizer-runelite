package com.osrsrandomizer;

import java.util.*;
import net.runelite.api.gameval.VarbitID;
import org.junit.Test;
import static org.junit.Assert.*;

public class DiaryCatalogTest
{
    @Test public void catalogueHasAllTiersAndUsesKaramjaLegacyCompletionFlags()
    {
        assertEquals(48, Category.DIARIES.getActivities().size());
        assertEquals(48, new HashSet<>(DiaryCatalog.COMPLETION_VARBITS.values()).size());
        assertEquals(Integer.valueOf(VarbitID.ATJUN_EASY_DONE), DiaryCatalog.COMPLETION_VARBITS.get("karamja_easy"));
        assertEquals(Integer.valueOf(VarbitID.ATJUN_MED_DONE), DiaryCatalog.COMPLETION_VARBITS.get("karamja_medium"));
        assertEquals(Integer.valueOf(VarbitID.ATJUN_HARD_DONE), DiaryCatalog.COMPLETION_VARBITS.get("karamja_hard"));
        for (Activity activity : Category.DIARIES.getActivities())
        {
            assertTrue(DiaryCatalog.COMPLETION_VARBITS.containsKey(activity.getId()));
            Set<String> one = Collections.singleton(activity.getId());
            assertEquals(one, SelectionCodec.decode(Category.DIARIES, SelectionCodec.encode(one)));
            assertEquals(activity.getId(), new DrawEngine(new Random(1)).draw(Category.DIARIES, one).get().getId());
            assertEquals(activity.getId(), HistoryCodec.encode(HistoryCodec.decode(Category.DIARIES, activity.getId())));
        }
        assertTrue(SelectionCodec.decode(Category.DIARIES, "").isEmpty());
    }

    @Test public void completionSnapshotIsImmutableAndDistinguishesUnknownFreeAndFinished()
    {
        Map<String, Boolean> flags = new LinkedHashMap<>();
        DiaryCatalog.COMPLETION_VARBITS.keySet().forEach(id -> flags.put(id, false));
        QuestAvailability unfinished = snapshot(true, flags);
        flags.replaceAll((id, value) -> true);
        QuestAvailability completed = snapshot(true, flags);
        QuestAvailability free = snapshot(false, flags);
        QuestAvailability unknown = snapshot(true, Collections.emptyMap());
        for (String id : flags.keySet())
        {
            assertNull(unfinished.reason(Category.DIARIES, id));
            assertEquals("Diary already completed", completed.reason(Category.DIARIES, id));
            assertEquals("Members only", free.reason(Category.DIARIES, id));
            assertEquals("Waiting for diary data", unknown.reason(Category.DIARIES, id));
            assertEquals("Waiting for membership data", QuestAvailability.unavailable().reason(Category.DIARIES, id));
        }
        assertNotEquals(unfinished, completed);
        assertEquals(completed, snapshot(true, flags));
        assertEquals(completed.hashCode(), snapshot(true, flags).hashCode());
    }

    private QuestAvailability snapshot(boolean member, Map<String, Boolean> flags)
    {
        return new QuestAvailability(true, 1, Collections.emptyMap(), false, member, flags, DiaryRequirementsTest.eligible());
    }
}
