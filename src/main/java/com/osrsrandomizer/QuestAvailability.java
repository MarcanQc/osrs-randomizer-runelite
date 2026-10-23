package com.osrsrandomizer;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;

/** Immutable snapshot. Unknown/unmapped requirements are never declared completed. */
final class QuestAvailability
{
    private static final Map<String, Quest> REQUIREMENTS;
    private static final Set<String> MEMBERS_SKILLS = new HashSet<>(Arrays.asList(
        "agility", "construction", "farming", "fletching", "herblore", "hunter", "sailing", "slayer", "thieving"));
    private static final Set<String> FREE_BOSSES = new HashSet<>(Arrays.asList("brutus", "obor", "bryophyta"));
    private static final Set<String> WILDERNESS_MEDIUM_BOSSES = Collections.unmodifiableSet(
        new HashSet<>(Arrays.asList("artio", "spindel", "calvarion")));
    static
    {
        Map<String, Quest> rules = new LinkedHashMap<>();
        rules.put("skills:sailing", Quest.PANDEMONIUM);
        rules.put("skills:herblore", Quest.DRUIDIC_RITUAL);
        rules.put("bosses:vorkath", Quest.DRAGON_SLAYER_II);
        rules.put("bosses:phantom_muspah", Quest.SECRETS_OF_THE_NORTH);
        for (String id : new String[] {"duke_sucellus", "the_leviathan", "the_whisperer", "vardorvis"})
            rules.put("bosses:" + id, Quest.DESERT_TREASURE_II__THE_FALLEN_EMPIRE);
        for (String id : new String[] {"crystalline_hunllef", "corrupted_hunllef", "zalcano"})
            rules.put("bosses:" + id, Quest.SONG_OF_THE_ELVES);
        rules.put("bosses:amoxliatl", Quest.THE_HEART_OF_DARKNESS);
        rules.put("bosses:deranged_archaeologist", Quest.BONE_VOYAGE);
        rules.put("raids:toa", Quest.BENEATH_CURSED_SANDS);
        rules.put("raids:tfa", Quest.WHILE_GUTHIX_SLEEPS);
        REQUIREMENTS = Collections.unmodifiableMap(rules);
    }

    private final boolean ready;
    private final long account;
    private final Map<Quest, QuestState> states;
    private final Boolean wildernessMediumComplete;
    private final Boolean member;
    private final Map<String, Boolean> completedDiaries;
    private final Map<String, String> diaryRequirements;

    QuestAvailability(boolean ready, long account, Map<Quest, QuestState> states)
    {
        this(ready, account, states, null);
    }

    QuestAvailability(boolean ready, long account, Map<Quest, QuestState> states, Boolean wildernessMediumComplete)
    {
        this(ready, account, states, wildernessMediumComplete, null);
    }

    QuestAvailability(boolean ready, long account, Map<Quest, QuestState> states, Boolean wildernessMediumComplete, Boolean member)
    {
        this(ready, account, states, wildernessMediumComplete, member, Collections.emptyMap());
    }

    QuestAvailability(boolean ready, long account, Map<Quest, QuestState> states, Boolean wildernessMediumComplete,
        Boolean member, Map<String, Boolean> completedDiaries)
    {
        this(ready, account, states, wildernessMediumComplete, member, completedDiaries, Collections.emptyMap());
    }

    QuestAvailability(boolean ready, long account, Map<Quest, QuestState> states, Boolean wildernessMediumComplete,
        Boolean member, Map<String, Boolean> completedDiaries, Map<String, String> diaryRequirements)
    {
        this.ready = ready;
        this.account = account;
        Map<Quest, QuestState> copy = new EnumMap<>(Quest.class);
        copy.putAll(states);
        this.states = Collections.unmodifiableMap(copy);
        this.wildernessMediumComplete = wildernessMediumComplete;
        this.member = member;
        this.completedDiaries = Collections.unmodifiableMap(new LinkedHashMap<>(completedDiaries));
        this.diaryRequirements = Collections.unmodifiableMap(new LinkedHashMap<>(diaryRequirements));
    }

    static QuestAvailability unavailable()
    {
        return new QuestAvailability(false, -1, Collections.emptyMap());
    }

    static Set<Quest> quests()
    {
        Set<Quest> quests = EnumSet.copyOf(REQUIREMENTS.values());
        quests.addAll(DiaryRequirements.quests());
        return Collections.unmodifiableSet(quests);
    }
    boolean isReady() { return ready; }
    long account() { return account; }
    boolean isChecked(Category category, String id)
    {
        return (category == Category.DIARIES && DiaryCatalog.COMPLETION_VARBITS.containsKey(id))
            || REQUIREMENTS.containsKey(category.getKey() + ":" + id) || needsWildernessMedium(category, id);
    }

    private boolean needsWildernessMedium(Category category, String id)
    {
        return category == Category.BOSSES && WILDERNESS_MEDIUM_BOSSES.contains(id);
    }

    static boolean membersOnly(Category category, String id)
    {
        if (category == Category.SKILLS) return MEMBERS_SKILLS.contains(id);
        if (category == Category.BOSSES) return !FREE_BOSSES.contains(id);
        return category == Category.RAIDS || category == Category.DIARIES;
    }

    static Boolean membershipFrom(int days, boolean membersWorld)
    {
        if (membersWorld || days > 0) return true;
        return days == 0 ? Boolean.FALSE : null;
    }

    String reason(Category category, String id)
    {
        if (membersOnly(category, id))
        {
            if (!ready || member == null) return "Waiting for membership data";
            if (!member) return "Members only";
        }
        if (category == Category.DIARIES)
        {
            Boolean complete = completedDiaries.get(id);
            if (!ready || complete == null) return "Waiting for diary data";
            if (complete) return "Diary already completed";
            return diaryRequirements.containsKey(id) ? diaryRequirements.get(id) : "Waiting for skill/quest data";
        }
        if (needsWildernessMedium(category, id))
        {
            if (!ready || wildernessMediumComplete == null) return "Waiting for diary data";
            return wildernessMediumComplete ? null : "Complete the Medium Wilderness Diary";
        }
        Quest quest = REQUIREMENTS.get(category.getKey() + ":" + id);
        if (quest == null) return null;
        if (!ready || states.get(quest) == null) return "Waiting for quest data";
        return states.get(quest) == QuestState.FINISHED ? null : "Complete " + quest.getName();
    }

    @Override public boolean equals(Object other)
    {
        if (!(other instanceof QuestAvailability)) return false;
        QuestAvailability that = (QuestAvailability) other;
        return ready == that.ready && account == that.account && states.equals(that.states)
            && Objects.equals(wildernessMediumComplete, that.wildernessMediumComplete)
            && Objects.equals(member, that.member) && completedDiaries.equals(that.completedDiaries)
            && diaryRequirements.equals(that.diaryRequirements);
    }

    @Override public int hashCode() { return Objects.hash(ready, account, states, wildernessMediumComplete, member, completedDiaries, diaryRequirements); }
}
