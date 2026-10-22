package com.osrsrandomizer;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Test;
import static org.junit.Assert.*;

public class DrawEngineTest
{
    private Set<String> first(int n)
    {
        return Category.SKILLS.getActivities().stream().limit(n).map(Activity::getId)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Test public void emptyAndUnknownSelectionsDoNotDraw()
    {
        DrawEngine engine = new DrawEngine(new Random(1));
        assertFalse(engine.draw(Category.SKILLS, Collections.emptySet()).isPresent());
        assertFalse(engine.draw(Category.SKILLS, Collections.singleton("unknown")).isPresent());
    }

    @Test public void smallAndLargePoolsRespectLongestPossibleWindow()
    {
        for (int n = 1; n <= 24; n++)
        {
            DrawEngine engine = new DrawEngine(new Random(n));
            Set<String> selected = first(n);
            Deque<String> recent = new ArrayDeque<>();
            for (int i = 0; i < 500; i++)
            {
                String id = engine.draw(Category.SKILLS, selected).get().getId();
                assertTrue(selected.contains(id));
                assertFalse("Pool " + n + " repeated inside protected window", recent.contains(id));
                recent.addLast(id);
                while (recent.size() > Math.min(3, n - 1)) recent.removeFirst();
            }
        }
    }

    @Test public void categoriesHaveIndependentHistory()
    {
        // A constant RNG makes history influence visible, without RNG interleaving effects.
        Random zero = new Random() { @Override public int nextInt(int bound) { return 0; } };
        DrawEngine engine = new DrawEngine(zero);
        String first = engine.draw(Category.SKILLS, first(5)).get().getId();
        Set<String> raids = SelectionCodec.decode(Category.RAIDS, null);
        for (int i = 0; i < 10; i++) engine.draw(Category.RAIDS, raids);
        assertNotEquals(first, engine.draw(Category.SKILLS, first(5)).get().getId());
    }

    @Test public void selectionChangesReleaseOldestFirstAndKeepMemory()
    {
        Random zero = new Random() { @Override public int nextInt(int bound) { return 0; } };
        DrawEngine engine = new DrawEngine(zero);
        Set<String> all = first(5);
        String a = engine.draw(Category.SKILLS, all).get().getId();
        String b = engine.draw(Category.SKILLS, all).get().getId();
        String c = engine.draw(Category.SKILLS, all).get().getId();
        assertFalse(engine.draw(Category.SKILLS, Collections.emptySet()).isPresent());
        assertEquals(b, engine.draw(Category.SKILLS, Arrays.asList(b, c)).get().getId());
        assertEquals(a, engine.draw(Category.SKILLS, all).get().getId());
    }

    @Test public void selectionsRoundTripIncludingExplicitEmpty()
    {
        assertEquals(24, SelectionCodec.decode(Category.SKILLS, null).size());
        assertTrue(SelectionCodec.decode(Category.SKILLS, "").isEmpty());
        assertEquals(first(3), SelectionCodec.decode(Category.SKILLS, SelectionCodec.encode(first(3))));
        assertEquals(Collections.singleton("attack"), SelectionCodec.decode(Category.SKILLS, "attack,unknown,attack"));
    }

    @Test public void catalogueIdsAreUniqueWithinEachCategory()
    {
        assertEquals(53, Category.BOSSES.getActivities().size());
        assertEquals(4, Category.RAIDS.getActivities().size());
        for (Category category : Category.values())
            assertEquals(category.getActivities().size(), SelectionCodec.decode(category, null).size());
    }

    @Test public void newRaidPreservesOldSelectionsAndCanBeDrawnAndSaved()
    {
        assertEquals(new LinkedHashSet<>(Arrays.asList("cox", "tob", "toa")),
            SelectionCodec.decode(Category.RAIDS, "cox,tob,toa"));
        assertFalse(SelectionCodec.decode(Category.RAIDS, "").contains("tfa"));
        assertTrue(SelectionCodec.decode(Category.RAIDS, null).contains("tfa"));
        Activity raid = new DrawEngine(new Random(1)).draw(Category.RAIDS, Collections.singleton("tfa")).get();
        assertEquals("The Fractured Archive", raid.getName());
        assertEquals("tfa", HistoryCodec.encode(HistoryCodec.decode(Category.RAIDS, "tfa")));
        assertNotNull(ActivityIcons.class.getResource("/com/osrsrandomizer/icons/raids/tfa.png"));
    }
}
