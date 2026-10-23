package com.osrsrandomizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.runelite.api.gameval.VarbitID;

/** Region/tier objectives. Completion flags are read on the client thread. */
final class DiaryCatalog
{
    static final List<Activity> ACTIVITIES;
    static final Map<String, Integer> COMPLETION_VARBITS;
    static
    {
        List<Activity> activities = new ArrayList<>();
        Map<String, Integer> varbits = new LinkedHashMap<>();
        add(activities, varbits, "ardougne", "Ardougne",
            VarbitID.ARDOUGNE_DIARY_EASY_COMPLETE, VarbitID.ARDOUGNE_DIARY_MEDIUM_COMPLETE, VarbitID.ARDOUGNE_DIARY_HARD_COMPLETE, VarbitID.ARDOUGNE_DIARY_ELITE_COMPLETE);
        add(activities, varbits, "desert", "Desert",
            VarbitID.DESERT_DIARY_EASY_COMPLETE, VarbitID.DESERT_DIARY_MEDIUM_COMPLETE, VarbitID.DESERT_DIARY_HARD_COMPLETE, VarbitID.DESERT_DIARY_ELITE_COMPLETE);
        add(activities, varbits, "falador", "Falador",
            VarbitID.FALADOR_DIARY_EASY_COMPLETE, VarbitID.FALADOR_DIARY_MEDIUM_COMPLETE, VarbitID.FALADOR_DIARY_HARD_COMPLETE, VarbitID.FALADOR_DIARY_ELITE_COMPLETE);
        add(activities, varbits, "fremennik", "Fremennik",
            VarbitID.FREMENNIK_DIARY_EASY_COMPLETE, VarbitID.FREMENNIK_DIARY_MEDIUM_COMPLETE, VarbitID.FREMENNIK_DIARY_HARD_COMPLETE, VarbitID.FREMENNIK_DIARY_ELITE_COMPLETE);
        add(activities, varbits, "kandarin", "Kandarin",
            VarbitID.KANDARIN_DIARY_EASY_COMPLETE, VarbitID.KANDARIN_DIARY_MEDIUM_COMPLETE, VarbitID.KANDARIN_DIARY_HARD_COMPLETE, VarbitID.KANDARIN_DIARY_ELITE_COMPLETE);
        add(activities, varbits, "karamja", "Karamja",
            VarbitID.ATJUN_EASY_DONE, VarbitID.ATJUN_MED_DONE, VarbitID.ATJUN_HARD_DONE, VarbitID.KARAMJA_DIARY_ELITE_COMPLETE);
        add(activities, varbits, "kourend_kebos", "Kourend & Kebos",
            VarbitID.KOUREND_DIARY_EASY_COMPLETE, VarbitID.KOUREND_DIARY_MEDIUM_COMPLETE, VarbitID.KOUREND_DIARY_HARD_COMPLETE, VarbitID.KOUREND_DIARY_ELITE_COMPLETE);
        add(activities, varbits, "lumbridge_draynor", "Lumbridge & Draynor",
            VarbitID.LUMBRIDGE_DIARY_EASY_COMPLETE, VarbitID.LUMBRIDGE_DIARY_MEDIUM_COMPLETE, VarbitID.LUMBRIDGE_DIARY_HARD_COMPLETE, VarbitID.LUMBRIDGE_DIARY_ELITE_COMPLETE);
        add(activities, varbits, "morytania", "Morytania",
            VarbitID.MORYTANIA_DIARY_EASY_COMPLETE, VarbitID.MORYTANIA_DIARY_MEDIUM_COMPLETE, VarbitID.MORYTANIA_DIARY_HARD_COMPLETE, VarbitID.MORYTANIA_DIARY_ELITE_COMPLETE);
        add(activities, varbits, "varrock", "Varrock",
            VarbitID.VARROCK_DIARY_EASY_COMPLETE, VarbitID.VARROCK_DIARY_MEDIUM_COMPLETE, VarbitID.VARROCK_DIARY_HARD_COMPLETE, VarbitID.VARROCK_DIARY_ELITE_COMPLETE);
        add(activities, varbits, "western_provinces", "Western Provinces",
            VarbitID.WESTERN_DIARY_EASY_COMPLETE, VarbitID.WESTERN_DIARY_MEDIUM_COMPLETE, VarbitID.WESTERN_DIARY_HARD_COMPLETE, VarbitID.WESTERN_DIARY_ELITE_COMPLETE);
        add(activities, varbits, "wilderness", "Wilderness",
            VarbitID.WILDERNESS_DIARY_EASY_COMPLETE, VarbitID.WILDERNESS_DIARY_MEDIUM_COMPLETE, VarbitID.WILDERNESS_DIARY_HARD_COMPLETE, VarbitID.WILDERNESS_DIARY_ELITE_COMPLETE);
        ACTIVITIES = Collections.unmodifiableList(activities);
        COMPLETION_VARBITS = Collections.unmodifiableMap(varbits);
    }

    private static void add(List<Activity> activities, Map<String, Integer> varbits,
        String region, String name, int... flags)
    {
        String[] tiers = {"Easy", "Medium", "Hard", "Elite"};
        for (int i = 0; i < tiers.length; i++)
        {
            String id = region + "_" + tiers[i].toLowerCase(Locale.ROOT);
            activities.add(new Activity(id, name + " - " + tiers[i]));
            varbits.put(id, flags[i]);
        }
    }

    private DiaryCatalog() { }
}
