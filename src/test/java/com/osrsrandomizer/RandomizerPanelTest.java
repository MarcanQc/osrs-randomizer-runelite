package com.osrsrandomizer;

import java.awt.Component;
import java.awt.Container;
import java.util.*;
import javax.swing.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class RandomizerPanelTest
{
    @Test public void historySurvivesPanelRestartAndStaysSeparateForEachCharacter() throws Exception
    {
        SwingUtilities.invokeAndWait(() ->
        {
            Map<String, String> saved = new HashMap<>();
            SelectionStore store = new SelectionStore()
            {
                public Set<String> load(Category c) { return Collections.singleton("attack"); }
                public void save(Category c, Set<String> ids) { }
                public String loadHistory(long account, Category c) { return saved.get(account + c.getKey()); }
                public void saveHistory(long account, Category c, String value) { saved.put(account + c.getKey(), value); }
            };
            RandomizerPanel first = new RandomizerPanel(new DrawEngine(new Random(1)), store);
            first.updateAvailability(snapshot(true, 1, Collections.emptyMap()));
            for (int i = 0; i < 7; i++) button(first, "Draw").doClick();
            assertEquals("attack,attack,attack,attack,attack", saved.get("1skills"));
            first.dispose();
            RandomizerPanel reopened = new RandomizerPanel(new DrawEngine(new Random(2)), store);
            try
            {
                assertEquals("No draws yet", ((JLabel) history(reopened).getComponent(0)).getText());
                reopened.updateAvailability(snapshot(true, 1, Collections.emptyMap()));
                assertEquals(5, history(reopened).getComponentCount());
                assertTrue(((JLabel) history(reopened).getComponent(0)).getText().contains("Attack"));
                button(reopened, "Raids").doClick();
                assertEquals("No draws yet", ((JLabel) history(reopened).getComponent(0)).getText());
                button(reopened, "Skills").doClick();
                reopened.updateAvailability(snapshot(true, 2, Collections.emptyMap()));
                assertEquals("No draws yet", ((JLabel) history(reopened).getComponent(0)).getText());
                reopened.updateAvailability(snapshot(true, 1, Collections.emptyMap()));
                assertEquals(5, history(reopened).getComponentCount());
                reopened.updateAvailability(QuestAvailability.unavailable());
                reopened.setQuestFilterEnabled(false);
                button(reopened, "Draw").doClick();
                assertEquals(1, saved.size()); // Offline draws are never assigned to the last account.
                assertEquals("attack,attack,attack,attack,attack", saved.get("1skills"));
                reopened.updateAvailability(snapshot(true, 1, Collections.emptyMap()));
                assertEquals(5, history(reopened).getComponentCount());
            }
            finally { reopened.dispose(); }
        });
    }

    @Test public void membershipThenQuestUnlockRestoresSkillSelection() throws Exception
    {
        SwingUtilities.invokeAndWait(() ->
        {
            SelectionStore store = new SelectionStore()
            {
                public Set<String> load(Category c) { return Collections.singleton("sailing"); }
                public void save(Category c, Set<String> ids) { fail("Access changes must preserve choices"); }
            };
            RandomizerPanel panel = new RandomizerPanel(new DrawEngine(new Random(1)), store);
            try
            {
                panel.updateAvailability(new QuestAvailability(true, 1, Collections.emptyMap(), true, false));
                JToggleButton sailing = cards(panel).stream().filter(c -> "sailing".equals(c.getClientProperty("activityId"))).findFirst().get();
                assertEquals("Members only", sailing.getToolTipText());
                assertEquals(Boolean.TRUE, sailing.getClientProperty("locked"));
                assertFalse(button(panel, "Draw").isEnabled());
                panel.updateAvailability(new QuestAvailability(true, 1, Collections.singletonMap(
                    net.runelite.api.Quest.PANDEMONIUM, net.runelite.api.QuestState.NOT_STARTED), true, true));
                assertFalse(button(panel, "Draw").isEnabled());
                panel.updateAvailability(new QuestAvailability(true, 1, Collections.singletonMap(
                    net.runelite.api.Quest.PANDEMONIUM, net.runelite.api.QuestState.FINISHED), true, true));
                assertTrue(button(panel, "Draw").isEnabled());
                button(panel, "Draw").doClick();
                assertTrue(((JLabel) history(panel).getComponent(0)).getText().contains("Sailing"));
                panel.updateAvailability(new QuestAvailability(true, 1, Collections.emptyMap(), true, false));
                assertFalse(button(panel, "Draw").isEnabled());
                panel.setQuestFilterEnabled(false);
                assertTrue(button(panel, "Draw").isEnabled());
            }
            finally { panel.dispose(); }
        });
    }

    @Test public void diaryCompletionRestoresSelectionWithoutSavingOverIt() throws Exception
    {
        SwingUtilities.invokeAndWait(() ->
        {
            SelectionStore store = new SelectionStore()
            {
                public Set<String> load(Category c) { return Collections.singleton("artio"); }
                public void save(Category c, Set<String> ids) { fail("Filtering must preserve saved choices"); }
            };
            RandomizerPanel panel = new RandomizerPanel(new DrawEngine(new Random(1)), store);
            try
            {
                button(panel, "Bosses").doClick();
                panel.updateAvailability(snapshot(true, 1, Collections.emptyMap(), false));
                JToggleButton artio = cards(panel).stream().filter(c -> "artio".equals(c.getClientProperty("activityId"))).findFirst().get();
                assertFalse(artio.isEnabled());
                assertFalse(artio.isSelected());
                assertTrue(artio.getToolTipText().contains("Medium Wilderness Diary"));
                assertFalse(button(panel, "Draw").isEnabled());
                panel.updateAvailability(snapshot(true, 1, Collections.emptyMap(), true));
                assertTrue(button(panel, "Draw").isEnabled());
                button(panel, "Draw").doClick();
                assertTrue(((JLabel) history(panel).getComponent(0)).getText().contains("Artio"));
                panel.updateAvailability(snapshot(true, 1, Collections.emptyMap(), false));
                assertFalse(button(panel, "Draw").isEnabled());
                panel.setQuestFilterEnabled(false);
                assertTrue(button(panel, "Draw").isEnabled());
            }
            finally { panel.dispose(); }
        });
    }

    @Test public void questFilteringPreservesChoicesAndBlocksDrawsUntilSynced() throws Exception
    {
        SwingUtilities.invokeAndWait(() ->
        {
            Map<Category, Set<String>> saved = new EnumMap<>(Category.class);
            SelectionStore store = new SelectionStore()
            {
                public Set<String> load(Category c) { return new LinkedHashSet<>(Collections.singleton("toa")); }
                public void save(Category c, Set<String> ids) { saved.put(c, new LinkedHashSet<>(ids)); }
                public boolean questFilterEnabled() { return true; }
            };
            RandomizerPanel panel = new RandomizerPanel(new DrawEngine(new Random(1)), store);
            try
            {
                button(panel, "Raids").doClick();
                assertFalse(button(panel, "Draw").isEnabled());
                panel.updateAvailability(snapshot(true, 1, Collections.singletonMap(
                    net.runelite.api.Quest.BENEATH_CURSED_SANDS, net.runelite.api.QuestState.NOT_STARTED)));
                JToggleButton toa = cards(panel).stream().filter(c -> "toa".equals(c.getClientProperty("activityId"))).findFirst().get();
                assertFalse(toa.isEnabled());
                assertFalse(toa.isSelected());
                assertTrue(toa.getToolTipText().contains("Beneath Cursed Sands"));
                assertFalse(button(panel, "Draw").isEnabled());
                assertTrue(saved.isEmpty());
                panel.updateAvailability(snapshot(true, 1, Collections.singletonMap(
                    net.runelite.api.Quest.BENEATH_CURSED_SANDS, net.runelite.api.QuestState.FINISHED)));
                assertTrue(button(panel, "Draw").isEnabled());
                button(panel, "Draw").doClick(); // Single selected activity: immediate.
                assertTrue(((JLabel) history(panel).getComponent(0)).getText().contains("Tombs of Amascut"));
                panel.updateAvailability(QuestAvailability.unavailable());
                assertFalse(button(panel, "Draw").isEnabled());
                panel.updateAvailability(snapshot(true, 2, Collections.singletonMap(
                    net.runelite.api.Quest.BENEATH_CURSED_SANDS, net.runelite.api.QuestState.NOT_STARTED)));
                assertEquals("No draws yet", ((JLabel) history(panel).getComponent(0)).getText());
                assertFalse(button(panel, "Draw").isEnabled());
                panel.setQuestFilterEnabled(false);
                assertTrue(button(panel, "Draw").isEnabled()); // Original selection restored.
                assertTrue(saved.isEmpty());
            }
            finally { panel.dispose(); }
        });
    }

    private List<Component> descendants(Container container)
    {
        List<Component> result = new ArrayList<>();
        for (Component c : container.getComponents())
        {
            result.add(c);
            if (c instanceof Container) result.addAll(descendants((Container) c));
        }
        return result;
    }

    private AbstractButton button(Container root, String text)
    {
        return (AbstractButton) descendants(root).stream().filter(c -> c instanceof AbstractButton
            && text.equals(((AbstractButton) c).getText())).findFirst().get();
    }

    private List<JToggleButton> cards(Container root)
    {
        List<JToggleButton> cards = new ArrayList<>();
        for (Component c : descendants(root))
            if (c instanceof JToggleButton && ((JToggleButton) c).getClientProperty("activityId") != null)
                cards.add((JToggleButton) c);
        return cards;
    }

    @Test public void cardsFilterAndPersistWithoutChangingDrawPool() throws Exception
    {
        SwingUtilities.invokeAndWait(() ->
        {
            Map<Category, Set<String>> saved = new EnumMap<>(Category.class);
            SelectionStore store = new SelectionStore()
            {
                public Set<String> load(Category c) { return saved.getOrDefault(c, SelectionCodec.decode(c, null)); }
                public void save(Category c, Set<String> ids) { saved.put(c, new LinkedHashSet<>(ids)); }
            };
            RandomizerPanel panel = new RandomizerPanel(new DrawEngine(new Random(1)), store);
            panel.setQuestFilterEnabled(false);
            assertEquals(24, cards(panel).size());
            cards(panel).get(0).doClick();
            assertFalse(saved.get(Category.SKILLS).contains("attack"));
            assertFalse(cards(panel).get(0).isSelected());
            JComboBox<?> filter = (JComboBox<?>) descendants(panel).stream().filter(c -> c instanceof JComboBox).findFirst().get();
            filter.setSelectedIndex(1);
            assertEquals(23, cards(panel).size());
            filter.setSelectedIndex(2);
            assertEquals(1, cards(panel).size());
            cards(panel).get(0).doClick();
            assertTrue(cards(panel).isEmpty());
            assertEquals(24, saved.get(Category.SKILLS).size());
            assertTrue(cards(panel).isEmpty());
            assertTrue(button(panel, "Draw").isEnabled());
            button(panel, "None").doClick();
            assertFalse(button(panel, "Draw").isEnabled());
            assertTrue(saved.get(Category.SKILLS).isEmpty());
            button(panel, "Raids").doClick();
            assertEquals(4, cards(panel).size());
            assertTrue(button(panel, "Draw").isEnabled());
            button(panel, "Draw").doClick();
            for (int tick = 0; tick < 9; tick++) panel.advanceAnimation();
            button(panel, "Draw").doClick();
            button(panel, "Skills").doClick();
            assertFalse(button(panel, "Draw").isEnabled());
            RandomizerPanel restored = new RandomizerPanel(new DrawEngine(new Random(1)), store);
            assertFalse(button(restored, "Draw").isEnabled());
            button(panel, "All").doClick();
            assertEquals(24, saved.get(Category.SKILLS).size());
            assertTrue(cards(panel).stream().allMatch(AbstractButton::isSelected));
        });
    }

    private JPanel history(Container root)
    {
        return (JPanel) descendants(root).stream().filter(c -> "recentDraws".equals(c.getName())).findFirst().get();
    }

    @Test public void animationCommitsOneDrawAndHistoryKeepsFivePerCategory() throws Exception
    {
        SwingUtilities.invokeAndWait(() ->
        {
            SelectionStore store = new SelectionStore()
            {
                public Set<String> load(Category c) { return SelectionCodec.decode(c, null); }
                public void save(Category c, Set<String> ids) { }
            };
            RandomizerPanel panel = new RandomizerPanel(new DrawEngine(new Random(42)), store);
            panel.setQuestFilterEnabled(false);
            try
            {
                DrawEngine expected = new DrawEngine(new Random(42));
                button(panel, "Draw").doClick();
                assertFalse(button(panel, "Drawing...").isEnabled());
                assertEquals("No draws yet", ((JLabel) history(panel).getComponent(0)).getText());
                for (int tick = 0; tick < 9; tick++) panel.advanceAnimation();
                String first = expected.draw(Category.SKILLS, store.load(Category.SKILLS)).get().getName();
                assertTrue(((JLabel) history(panel).getComponent(0)).getText().contains(first));
                assertEquals(1, history(panel).getComponentCount());
                assertTrue(button(panel, "Draw").isEnabled());
                // Cancelling via a category change does not consume a draw.
                button(panel, "Draw").doClick();
                button(panel, "Raids").doClick();
                panel.advanceAnimation();
                assertEquals("No draws yet", ((JLabel) history(panel).getComponent(0)).getText());
                button(panel, "Skills").doClick();
                button(panel, "Draw").doClick();
                for (int tick = 0; tick < 9; tick++) panel.advanceAnimation();
                String second = expected.draw(Category.SKILLS, store.load(Category.SKILLS)).get().getName();
                assertTrue(((JLabel) history(panel).getComponent(0)).getText().contains(second));
                for (int i = 0; i < 6; i++) { button(panel, "Draw").doClick(); for (int tick = 0; tick < 9; tick++) panel.advanceAnimation(); }
                assertEquals(5, history(panel).getComponentCount());
                button(panel, "Raids").doClick();
                assertEquals("No draws yet", ((JLabel) history(panel).getComponent(0)).getText());
                button(panel, "Draw").doClick();
                for (int tick = 0; tick < 9; tick++) panel.advanceAnimation();
                assertEquals(1, history(panel).getComponentCount());
                button(panel, "Skills").doClick();
                assertEquals(5, history(panel).getComponentCount());
                // A selection edit cancels animation without committing a result.
                String before = ((JLabel) history(panel).getComponent(0)).getText();
                button(panel, "Draw").doClick();
                cards(panel).get(0).doClick();
                panel.advanceAnimation();
                assertTrue(button(panel, "Draw").isEnabled());
                assertEquals(before, ((JLabel) history(panel).getComponent(0)).getText());
                button(panel, "Draw").doClick();
                panel.dispose();
                panel.advanceAnimation();
                assertEquals(before, ((JLabel) history(panel).getComponent(0)).getText());
            }
            finally { panel.dispose(); }
        });
    }

    private static QuestAvailability snapshot(boolean ready, long account, Map<net.runelite.api.Quest, net.runelite.api.QuestState> states)
    {
        return snapshot(ready, account, states, null);
    }

    private static QuestAvailability snapshot(boolean ready, long account, Map<net.runelite.api.Quest, net.runelite.api.QuestState> states, Boolean diary)
    {
        return new QuestAvailability(ready, account, states, diary, true);
    }}
