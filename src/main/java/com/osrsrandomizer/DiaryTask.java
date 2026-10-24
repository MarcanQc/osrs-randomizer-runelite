package com.osrsrandomizer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.IntFunction;
import net.runelite.client.plugins.achievementdiary.Requirement;

/** A task's completion flag, independent of intermediate multi-stage progress. */
final class DiaryTask
{
    final String name;
    final boolean varbit;
    final int variable;
    final int thresholdOrBit;
    final List<Requirement> requirements;

    DiaryTask(String name, boolean varbit, int variable, int thresholdOrBit, Requirement... requirements)
    {
        this.name = name;
        this.varbit = varbit;
        this.variable = variable;
        this.thresholdOrBit = thresholdOrBit;
        this.requirements = Collections.unmodifiableList(Arrays.asList(requirements.clone()));
    }

    boolean completed(IntFunction<Integer> varps, IntFunction<Integer> varbits)
    {
        Integer value = (varbit ? varbits : varps).apply(variable);
        // A failed read must never waive requirements. Unsigned shift includes bit 31.
        return value != null && (varbit ? value >= thresholdOrBit : ((value >>> thresholdOrBit) & 1) == 1);
    }
}
