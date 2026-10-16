package com.osrsrandomizer;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;
import org.junit.Test;
import static org.junit.Assert.*;

public class HistoryCodecTest
{
    @Test public void savedHistoryPreservesOrderAndDuplicatesAndIgnoresInvalidIds()
    {
        List<Activity> history = HistoryCodec.decode(Category.SKILLS,
            "missing,attack,attack,strength,defence,ranged,magic");
        assertEquals("attack,attack,strength,defence,ranged", HistoryCodec.encode(history));
        assertTrue(HistoryCodec.decode(Category.RAIDS, "attack,unknown").isEmpty());
        assertTrue(HistoryCodec.decode(Category.SKILLS, null).isEmpty());
        assertTrue(HistoryCodec.decode(Category.SKILLS, "").isEmpty());
    }

    @Test public void restoredMemoryExcludesLatestThreeAndReleasesOldestFirst()
    {
        DrawEngine engine = new DrawEngine(new Random(1));
        List<Activity> history = HistoryCodec.decode(Category.SKILLS, "attack,strength,defence,ranged,magic");
        Set<String> pool = new HashSet<>(java.util.Arrays.asList("attack", "strength", "defence", "ranged"));
        engine.restore(Category.SKILLS, history);
        assertEquals("ranged", engine.draw(Category.SKILLS, pool).get().getId());
        engine.restore(Category.SKILLS, history);
        pool.remove("ranged");
        assertEquals("defence", engine.draw(Category.SKILLS, pool).get().getId());
    }
}
