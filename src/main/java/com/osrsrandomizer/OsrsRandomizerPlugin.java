package com.osrsrandomizer;

import java.util.Random;
import java.util.Set;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import com.google.inject.Provides;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.api.VarPlayer;
import net.runelite.api.WorldType;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;

@PluginDescriptor(name = "OSRS Randomizer", description = "Choose your next skill, boss, raid or diary",
    tags = {"random", "skills", "bosses", "raids", "diaries"})
public class OsrsRandomizerPlugin extends Plugin
{
    private static final String CONFIG_GROUP = "osrsrandomizer";
    @Inject private ClientToolbar toolbar;
    @Inject private ConfigManager configManager;
    @Inject private OsrsRandomizerConfig config;
    @Inject private Client client;
    @Inject private ClientThread clientThread;
    private final AtomicInteger generation = new AtomicInteger();
    private QuestAvailability lastSnapshot = QuestAvailability.unavailable();
    private int ticksUntilRefresh;
    private NavigationButton navigation;
    private RandomizerPanel panel;
    // Accessed only on the EDT, including queued lifecycle callbacks.
    private boolean active;

    @Provides
    OsrsRandomizerConfig provideConfig(ConfigManager manager)
    {
        return manager.getConfig(OsrsRandomizerConfig.class);
    }

    @Override
    protected void startUp()
    {
        int token = generation.incrementAndGet();
        SwingUtilities.invokeLater(() ->
        {
            if (token != generation.get()) return;
            if (active) return;
            active = true;
            SelectionStore store = new SelectionStore()
            {
                public boolean questFilterEnabled()
                {
                    return config.questFilterEnabled();
                }

                public Set<String> load(Category category)
                {
                    return SelectionCodec.decode(category,
                        configManager.getConfiguration(CONFIG_GROUP, category.getKey()));
                }

                public void save(Category category, Set<String> selected)
                {
                    configManager.setConfiguration(CONFIG_GROUP, category.getKey(), SelectionCodec.encode(selected));
                }

                public String loadHistory(long account, Category category)
                {
                    return configManager.getConfiguration(CONFIG_GROUP, historyKey(account, category));
                }

                public void saveHistory(long account, Category category, String value)
                {
                    configManager.setConfiguration(CONFIG_GROUP, historyKey(account, category), value);
                }

                private String historyKey(long account, Category category)
                {
                    return "historyV1_" + Long.toUnsignedString(account) + "_" + category.getKey();
                }
            };
            panel = new RandomizerPanel(new DrawEngine(new Random()), store);
            navigation = NavigationButton.builder().tooltip("OSRS Randomizer")
                .icon(ImageUtil.loadImageResource(OsrsRandomizerPlugin.class, "navigation.png"))
                .priority(8).panel(panel).build();
            toolbar.addNavigation(navigation);
            clientThread.invokeLater(() ->
            {
                if (token != generation.get()) return;
                lastSnapshot = null;
                ticksUntilRefresh = 0;
                // Query after the next server tick, once login variables are populated.
                if (client.getGameState() != GameState.LOGGED_IN) publish(QuestAvailability.unavailable());
            });
        });
    }

    @Override
    protected void shutDown()
    {
        generation.incrementAndGet();
        SwingUtilities.invokeLater(() ->
        {
            active = false;
            if (panel != null) panel.dispose();
            panel = null;
            if (navigation != null) toolbar.removeNavigation(navigation);
            navigation = null;
        });
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (!CONFIG_GROUP.equals(event.getGroup()) || !"questFilter".equals(event.getKey())) return;
        int token = generation.get();
        SwingUtilities.invokeLater(() ->
        {
            if (token == generation.get() && active && panel != null)
                panel.setQuestFilterEnabled(config.questFilterEnabled());
        });
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        ticksUntilRefresh = 0;
        // Also invalidate during hopping/reconnection; never reuse another session's data.
        if (event.getGameState() != GameState.LOGGED_IN) publish(QuestAvailability.unavailable());
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (client.getGameState() != GameState.LOGGED_IN || client.getLocalPlayer() == null) return;
        if (ticksUntilRefresh-- > 0) return;
        ticksUntilRefresh = 9;
        Map<Quest, QuestState> states = new EnumMap<>(Quest.class);
        for (Quest quest : QuestAvailability.quests())
        {
            try { states.put(quest, quest.getState(client)); }
            catch (RuntimeException ignored) { /* Missing data remains unknown and is retried. */ }
        }
        Boolean wildernessMediumComplete = null;
        try
        {
            wildernessMediumComplete = client.getVarbitValue(VarbitID.WILDERNESS_DIARY_MEDIUM_COMPLETE) == 1;
        }
        catch (RuntimeException ignored) { /* Unknown diary state remains blocked until the next refresh. */ }
        Boolean member = null;
        try
        {
            // Subscription days also identify members visiting a free world.
            member = QuestAvailability.membershipFrom(client.getVarpValue(VarPlayer.MEMBERSHIP_DAYS),
                client.getWorldType().contains(WorldType.MEMBERS));
        }
        catch (RuntimeException ignored) { /* Retry unknown membership on the next refresh. */ }
        Map<String, Boolean> completedDiaries = new java.util.LinkedHashMap<>();
        DiaryCatalog.COMPLETION_VARBITS.forEach((id, varbit) ->
        {
            try { completedDiaries.put(id, client.getVarbitValue(varbit) == 1); }
            catch (RuntimeException ignored) { /* Retry missing diary data on the next refresh. */ }
        });
        Map<net.runelite.api.Skill, Integer> levels = new EnumMap<>(net.runelite.api.Skill.class);
        for (net.runelite.api.Skill skill : net.runelite.api.Skill.values())
        {
            if (skill == net.runelite.api.Skill.OVERALL) continue;
            try { levels.put(skill, client.getRealSkillLevel(skill)); }
            catch (RuntimeException ignored) { /* Missing skill data remains blocked. */ }
        }
        Integer combat = null;
        Integer questPoints = null;
        try
        {
            combat = client.getLocalPlayer().getCombatLevel();
            questPoints = client.getVarpValue(net.runelite.api.gameval.VarPlayerID.QP);
        }
        catch (RuntimeException ignored) { /* Retry after the next refresh. */ }
        publish(new QuestAvailability(true, client.getAccountHash(), states, wildernessMediumComplete, member, completedDiaries,
            DiaryRequirements.evaluate(levels, states, combat, questPoints)));
    }

    private void publish(QuestAvailability snapshot)
    {
        if (snapshot.equals(lastSnapshot)) return;
        lastSnapshot = snapshot;
        int token = generation.get();
        SwingUtilities.invokeLater(() ->
        {
            if (token == generation.get() && active && panel != null) panel.updateAvailability(snapshot);
        });
    }

}
