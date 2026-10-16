package com.osrsrandomizer;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/** History independent for every category, restored when a character logs in. Used on the Swing EDT. */
public final class DrawEngine
{
    private final Random random;
    private final Map<Category, Deque<String>> recent = new EnumMap<>(Category.class);

    public DrawEngine(Random random)
    {
        this.random = random;
        for (Category category : Category.values()) recent.put(category, new ArrayDeque<>());
    }

    void reset() { for (Deque<String> history : recent.values()) history.clear(); }

    void restore(Category category, Collection<Activity> newestFirst)
    {
        Deque<String> history = recent.get(category);
        history.clear();
        for (Activity activity : newestFirst)
        {
            history.addFirst(activity.getId());
            if (history.size() == 3) break;
        }
    }

    public Optional<Activity> draw(Category category, Collection<String> selected)
    {
        List<Activity> pool = category.getActivities().stream()
            .filter(activity -> selected.contains(activity.getId())).collect(Collectors.toList());
        if (pool.isEmpty()) return Optional.empty();

        Deque<String> history = recent.get(category);
        List<String> exclusions = new ArrayList<>(history); // oldest first
        List<Activity> available;
        while (true)
        {
            Set<String> blocked = new HashSet<>(exclusions);
            available = pool.stream().filter(a -> !blocked.contains(a.getId())).collect(Collectors.toList());
            if (!available.isEmpty()) break;
            exclusions.remove(0);
        }
        Activity result = available.get(random.nextInt(available.size()));
        history.addLast(result.getId());
        while (history.size() > 3) history.removeFirst();
        return Optional.of(result);
    }
}
