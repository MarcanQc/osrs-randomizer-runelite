package com.osrsrandomizer;

import java.util.*;
import net.runelite.api.*;
import net.runelite.api.gameval.DBTableID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;

/** Reads on the client thread; only immutable strings reach the panel. */
final class BossAccess
{
    static final Set<String> GOD_WARS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
        "general_graardor", "commander_zilyana", "kree_arra", "kril_tsutsaroth", "nex")));
    static final Set<String> IDS;
    static
    {
        Set<String> ids = new HashSet<>(GOD_WARS);
        ids.add("zulrah");
        ids.add("shellbane_gryphon");
        IDS = Collections.unmodifiableSet(ids);
    }

    static Map<String, String> read(Client client)
    {
        Map<String, String> reasons = new LinkedHashMap<>();
        String entrance = "Waiting for God Wars entrance skill data";
        try
        {
            entrance = entranceReason(Math.max(client.getRealSkillLevel(Skill.STRENGTH), client.getBoostedSkillLevel(Skill.STRENGTH)),
                client.getRealSkillLevel(Skill.AGILITY));
        }
        catch (RuntimeException ignored) { /* Retry unread skill data. */ }
        for (String id : GOD_WARS) reasons.put(id, entrance);
        reasons.put("zulrah", "Waiting for Zulrah access data");
        reasons.put("shellbane_gryphon", "Waiting for Slayer task/clue data");
        try
        {
            // PLAYER_EDUCATED_ABOUT_ZULRAH: 2 = priestess accepted the player.
            reasons.put("zulrah", client.getVarbitValue(VarbitID.SNAKEBOSS_INFO) >= 2 ? null
                : "Reach Port Tyras during Regicide and speak to High Priestess Zul-Harcinqa");
        }
        catch (RuntimeException ignored) { /* Retry on next snapshot. */ }
        Boolean clue = null;
        Boolean task = null;
        try
        {
            ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
            if (inventory != null)
                clue = Arrays.stream(inventory.getItems()).anyMatch(item -> item.getId() == ItemID.TRAIL_ELITE_RIDDLE_SAIL);
        }
        catch (RuntimeException ignored) { /* An unread inventory is not an empty one. */ }
        try
        {
            if (client.getVarpValue(VarPlayerID.SLAYER_COUNT) == 0) task = false;
            else
            {
                // Same DB lookup as RuneLite's SlayerPlugin, including boss assignments.
                int target = client.getVarpValue(VarPlayerID.SLAYER_TARGET);
                List<Integer> rows = target == 98
                    ? client.getDBRowsByValue(DBTableID.SlayerTaskSublist.ID, DBTableID.SlayerTaskSublist.COL_TASK_SUBTABLE_ID,
                        0, client.getVarbitValue(VarbitID.SLAYER_TARGET_BOSSID))
                    : client.getDBRowsByValue(DBTableID.SlayerTask.ID, DBTableID.SlayerTask.COL_ID, 0, target);
                if (!rows.isEmpty())
                {
                    int row = target == 98 ? (Integer) client.getDBTableField(rows.get(0), DBTableID.SlayerTaskSublist.COL_TASK, 0)[0] : rows.get(0);
                    String name = (String) client.getDBTableField(row, DBTableID.SlayerTask.COL_NAME_UPPERCASE, 0)[0];
                    task = name.equalsIgnoreCase("Gryphons") || name.equalsIgnoreCase("Shellbane gryphon");
                }
            }
        }
        catch (RuntimeException ignored) { /* Missing DB data remains unknown. */ }
        reasons.put("shellbane_gryphon", gryphonReason(task, clue));
        return reasons;
    }

    static String gryphonReason(Boolean task, Boolean clue)
    {
        if (Boolean.TRUE.equals(task) || Boolean.TRUE.equals(clue)) return null;
        return task == null || clue == null ? "Waiting for Slayer task/clue data"
            : "Requires a gryphon Slayer task or the specific elite clue in your inventory";
    }

    static String entranceReason(int strength, int agility)
    {
        return strength >= 60 || agility >= 60 ? null : "God Wars entrance: requires 60 Strength or 60 Agility (active Strength boosts count)";
    }

    private BossAccess() { }
}
