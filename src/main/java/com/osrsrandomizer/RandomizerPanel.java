package com.osrsrandomizer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;

import javax.swing.Timer;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import net.runelite.client.ui.PluginPanel;

final class RandomizerPanel extends PluginPanel
{
    private static final Color BACKGROUND = new Color(35, 32, 26);
    private static final Color GOLD = new Color(226, 193, 116);
    private final DrawEngine engine;
    private final SelectionStore store;
    private final Map<Category, Set<String>> selections = new EnumMap<>(Category.class);
    private final Map<Category, Activity> results = new EnumMap<>(Category.class);
    private final Map<Category, Deque<Activity>> history = new EnumMap<>(Category.class);
    private final JPanel historyRows = new JPanel(new GridLayout(0, 1, 0, 2));
    private final Timer animation = new Timer(90, e -> advanceAnimation());
    private final List<Activity> previewPool = new ArrayList<>();
    private int animationFrame;
    private final ActivityIcons icons = new ActivityIcons();
    private Category category = Category.SKILLS;
    private final JPanel activities = new JPanel();
    private final JLabel count = new JLabel();
    private final JLabel shown = new JLabel();
    private final JLabel result = new JLabel("", SwingConstants.CENTER);
    private final JButton draw = new JButton("Draw");
    private boolean questFilterEnabled;
    private final JLabel syncStatus = new JLabel();
    private QuestAvailability availability = QuestAvailability.unavailable();
    private Long lastAccount;
    private final JComboBox<String> filter = new JComboBox<>(new String[] {"Show all", "Selected only", "Excluded only"});

    RandomizerPanel(DrawEngine engine, SelectionStore store)
    {
        this.engine = engine;
        this.store = store;
        for (Category c : Category.values())
        {
            selections.put(c, new LinkedHashSet<>(store.load(c)));
            history.put(c, new ArrayDeque<>());
        }
        setLayout(new BorderLayout(0, 8));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(10, 6, 10, 6));
        JPanel header = new JPanel(new BorderLayout(0, 8));
        header.setOpaque(false);
        JLabel title = new JLabel("OSRS RANDOMIZER", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        title.setForeground(GOLD);
        header.add(title, BorderLayout.NORTH);

        JPanel controls = new JPanel();
        controls.setOpaque(false);
        controls.setLayout(new javax.swing.BoxLayout(controls, javax.swing.BoxLayout.Y_AXIS));
        JPanel tabs = new JPanel(new GridLayout(1, Category.values().length, 3, 0));
        tabs.setOpaque(false);
        ButtonGroup group = new ButtonGroup();
        for (Category c : Category.values())
        {
            JToggleButton tab = tile(c.toString());
            tab.setIcon(icons.category(c));
            tab.setFont(tab.getFont().deriveFont(10f));
            tab.setPreferredSize(new Dimension(50, 50));
            tab.setSelected(c == category);
            tab.addActionListener(e ->
            {
                category = c;
                filter.setSelectedIndex(0);
                rebuild();
            });
            group.add(tab);
            tabs.add(tab);
        }
        controls.add(tabs);
        result.setForeground(GOLD);
        result.setFont(result.getFont().deriveFont(Font.BOLD, 18f));
        result.setIconTextGap(10);
        result.setHorizontalTextPosition(SwingConstants.CENTER);
        result.setVerticalTextPosition(SwingConstants.BOTTOM);
        JPanel resultBox = new JPanel(new BorderLayout());
        resultBox.setOpaque(false);
        resultBox.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        resultBox.setPreferredSize(new Dimension(210, 145));
        resultBox.add(result);
        controls.add(resultBox);
        JPanel drawBox = new JPanel(new GridLayout(1, 1));
        drawBox.add(draw);
        controls.add(drawBox);
        JLabel historyTitle = new JLabel("Recent draws");
        historyTitle.setForeground(GOLD);
        historyTitle.setBorder(BorderFactory.createEmptyBorder(5, 0, 4, 0));
        controls.add(historyTitle);
        historyRows.setOpaque(false);
        historyRows.setName("recentDraws");
        historyRows.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        controls.add(historyRows);
        questFilterEnabled = store.questFilterEnabled();
        syncStatus.setBorder(BorderFactory.createEmptyBorder(2, 0, 5, 0));
        controls.add(syncStatus);
        filter.getAccessibleContext().setAccessibleName("Selection filter");
        controls.add(filter);
        controls.add(count);
        JPanel selectionActions = new JPanel(new GridLayout(1, 2, 5, 0));
        selectionActions.setOpaque(false);
        JButton all = new JButton("All");
        JButton none = new JButton("None");
        all.setToolTipText("Include every activity in this category, including hidden activities");
        none.setToolTipText("Exclude every activity in this category, including hidden activities");
        all.addActionListener(e -> selectAll(true));
        none.addActionListener(e -> selectAll(false));
        selectionActions.add(all);
        selectionActions.add(none);
        controls.add(selectionActions);
        shown.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        controls.add(shown);
        for (java.awt.Component component : controls.getComponents())
            ((javax.swing.JComponent) component).setAlignmentX(0f);
        header.add(controls, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);
        activities.setOpaque(false);
        activities.setLayout(new javax.swing.BoxLayout(activities, javax.swing.BoxLayout.Y_AXIS));
        add(activities, BorderLayout.CENTER);
        JLabel hint = new JLabel("<html><small>Click an activity to include or exclude it.<br>Gold rows are included in the draw.</small></html>");
        add(hint, BorderLayout.SOUTH);
        filter.addActionListener(e -> rebuild());
        draw.addActionListener(e -> startDraw());
        rebuild();
    }

    private JToggleButton tile(String text)
    {
        JToggleButton button = new JToggleButton(text)
        {
            @Override protected void paintComponent(Graphics g)
            {
                g.setColor(!isEnabled() ? new Color(40, 40, 40)
                    : isSelected() ? new Color(91, 79, 49) : new Color(46, 41, 32));
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
                if (Boolean.TRUE.equals(getClientProperty("locked")))
                {
                    // A small drawn icon stays crisp without relying on emoji font support.
                    Graphics lock = g.create();
                    lock.setColor(GOLD);
                    int x = getWidth() - 18;
                    int y = (getHeight() - 14) / 2;
                    lock.drawRoundRect(x + 3, y, 6, 9, 5, 5);
                    lock.fillRect(x + 1, y + 6, 11, 8);
                    lock.setColor(BACKGROUND);
                    lock.fillRect(x + 6, y + 9, 1, 3);
                    lock.dispose();
                }
            }
        };
        button.setContentAreaFilled(false);
        button.setForeground(new Color(235, 224, 197));
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setIconTextGap(6);
        button.setBorder(BorderFactory.createLineBorder(new Color(68, 60, 45)));
        button.addItemListener(e -> button.setBorder(BorderFactory.createLineBorder(
            button.isSelected() ? new Color(180, 151, 84) : new Color(68, 60, 45))));
        return button;
    }

    private void selectAll(boolean selected)
    {
        Set<String> ids = selections.get(category);
        ids.clear();
        if (selected) for (Activity activity : category.getActivities()) ids.add(activity.getId());
        store.save(category, ids);
        rebuild();
    }

    private static String shortReason(String reason)
    {
        String[] parts = reason.split("; ");
        return parts[0] + (parts.length > 1 ? " (+" + (parts.length - 1) + " more)" : "");
    }

    private void rebuild()
    {
        cancelAnimation();
        activities.removeAll();
        int visible = 0;
        for (Activity activity : category.getActivities())
        {
            String reason = blockedReason(activity.getId());
            boolean included = selections.get(category).contains(activity.getId()) && reason == null;
            if ( (filter.getSelectedIndex() == 1 && !included)
                || (filter.getSelectedIndex() == 2 && included)) continue;
            boolean locked = reason != null && !reason.startsWith("Waiting");
            JToggleButton card = tile("<html><div style='width:125px'>"
                + (reason == null ? activity.getName() : "<font color='#c6c0b4'>" + activity.getName() + "</font>")
                + (reason == null ? "" : "<br><font color='#d8bc7c'><small>"
                    + (locked ? shortReason(reason) : "Checking requirements...")
                    + "</small></font>") + "</div></html>");
            card.setHorizontalAlignment(SwingConstants.LEFT);
            card.setHorizontalTextPosition(SwingConstants.RIGHT);
            card.setVerticalTextPosition(SwingConstants.CENTER);
            card.setIconTextGap(8);
            card.putClientProperty("activityId", activity.getId());
            card.putClientProperty("locked", locked);
            card.getAccessibleContext().setAccessibleName(activity.getName());
            card.getAccessibleContext().setAccessibleDescription(reason);
            card.setIcon(icons.activity(category, activity, 20));
            int rowHeight = Math.max(reason == null ? 38 : 48, card.getPreferredSize().height + 12);
            card.setPreferredSize(new Dimension(210, rowHeight));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, rowHeight));
            card.setAlignmentX(0f);
            card.setSelected(included);
            card.setEnabled(reason == null);
            card.setToolTipText(reason != null ? (reason.contains("; ") ? "<html>" + reason.replace("; ", "<br>") + "</html>" : reason) : activity.getName() + (included ? " — included" : " — excluded")
                + (questFilterEnabled && !availability.isChecked(category, activity.getId())
                    ? " (requirements not checked)" : ""));
            card.addActionListener(e ->
            {
                cancelAnimation();
                Set<String> ids = selections.get(category);
                if (card.isSelected()) ids.add(activity.getId()); else ids.remove(activity.getId());
                store.save(category, ids);
                if (filter.getSelectedIndex() != 0) rebuild();
                else
                {
                    card.setToolTipText(activity.getName() + (card.isSelected() ? " — included" : " — excluded"));
                    refresh();
                }
            });
            if (visible > 0) activities.add(javax.swing.Box.createVerticalStrut(3));
            activities.add(card);
            visible++;
        }

        shown.setText(visible == 0 ? "No matching activities" : visible + " activities shown");
        refresh();
        revalidate();
        repaint();
    }

    private void refresh()
    {
        int size = effectiveSelection().size();
        count.setText(size + " / " + category.getActivities().size() + " included");
        boolean waiting = questFilterEnabled && !availability.isReady();
        syncStatus.setText(questFilterEnabled
            ? (waiting ? "<html><small>Log in to sync character data.</small></html>"
                : "<html><small>Character checks · partial coverage</small></html>") : "");
        draw.setEnabled(size > 0 && !waiting && !animation.isRunning());
        draw.setText(animation.isRunning() ? "Drawing..." : "Draw");
        Activity previous = results.get(category);
        if (!animation.isRunning())
        {
        result.setIcon(size > 0 && previous != null ? icons.activity(category, previous, 48) : icons.category(category));
        String message = waiting ? "Log in to sync" : size == 0 ? "No eligible activities selected" : previous == null
            ? "No draw yet" : previous.getName();
        result.setText("<html><div style='width:145px;text-align:center'>" + message + "</div></html>");
        }
        historyRows.removeAll();
        int position = 0;
        for (Activity activity : history.get(category))
        {
            JLabel row = new JLabel("<html><div style='width:125px'>" + (++position) + ". " + activity.getName() + "</div></html>",
                icons.activity(category, activity, 18), SwingConstants.LEFT);
            row.setToolTipText(activity.getName());
            historyRows.add(row);
        }
        if (position == 0) historyRows.add(new JLabel("No draws yet"));
        revalidate();
        repaint();
    }

    private void startDraw()
    {
        if (animation.isRunning() || effectiveSelection().isEmpty()
            || (questFilterEnabled && !availability.isReady())) return;
        previewPool.clear();
        for (Activity activity : category.getActivities())
            if (effectiveSelection().contains(activity.getId())) previewPool.add(activity);
        if (previewPool.size() < 2)
        {
            finishDraw();
            return;
        }
        animationFrame = 0;
        animation.start();
        advanceAnimation();
        refresh();
    }

    // Runs on the Swing EDT. Preview frames never enter the draw engine or history.
    void advanceAnimation()
    {
        if (!animation.isRunning()) return;
        if (animationFrame == 9)
        {
            finishDraw();
            return;
        }
        Activity preview = previewPool.get(animationFrame++ % previewPool.size());
        result.setIcon(icons.activity(category, preview, 48));
        result.setText("<html><div style='width:145px;text-align:center'>" + preview.getName() + "</div></html>");
    }

    private void finishDraw()
    {
        animation.stop();
        previewPool.clear();
        engine.draw(category, effectiveSelection()).ifPresent(activity ->
        {
            results.put(category, activity);
            Deque<Activity> recent = history.get(category);
            recent.addFirst(activity);
            while (recent.size() > 5) recent.removeLast();
            if (lastAccount != null)
                store.saveHistory(lastAccount, category, HistoryCodec.encode(recent));
        });
        refresh();
    }

    private void cancelAnimation()
    {
        animation.stop();
        previewPool.clear();
    }

    void dispose() { cancelAnimation(); }

    void setQuestFilterEnabled(boolean enabled)
    {
        if (questFilterEnabled == enabled) return;
        questFilterEnabled = enabled;
        rebuild();
    }

    private String blockedReason(String id)
    {
        return questFilterEnabled ? availability.reason(category, id) : null;
    }

    private Set<String> effectiveSelection()
    {
        Set<String> selected = new LinkedHashSet<>(selections.get(category));
        if (questFilterEnabled) selected.removeIf(id -> availability.reason(category, id) != null);
        return selected;
    }

    void updateAvailability(QuestAvailability snapshot)
    {
        if (availability.equals(snapshot)) return;
        availability = snapshot;
        if (!snapshot.isReady())
        {
            lastAccount = null;
            results.clear();
            for (Deque<Activity> entries : history.values()) entries.clear();
            engine.reset();
        }
        else if (lastAccount == null || lastAccount.longValue() != snapshot.account())
        {
            results.clear();
            engine.reset();
            for (Category c : Category.values())
            {
                Deque<Activity> entries = history.get(c);
                entries.clear();
                entries.addAll(HistoryCodec.decode(c, store.loadHistory(snapshot.account(), c)));
                if (!entries.isEmpty()) results.put(c, entries.getFirst());
                engine.restore(c, entries);
            }
            lastAccount = snapshot.account();
        }
        rebuild();
    }
}
