package com.osrsrandomizer;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("osrsrandomizer")
public interface OsrsRandomizerConfig extends Config
{
    @ConfigItem(
        keyName = "questFilter",
        name = "Available for my character",
        description = "Exclude activities blocked by membership, supported quests and diaries, and completed diary objectives. Diaries check supported quests and base skill levels (no boosts). Completed diary tasks are skipped. Some items and access requirements are not checked. Requires login.",
        position = 0
    )
    default boolean questFilterEnabled()
    {
        return true;
    }
}
