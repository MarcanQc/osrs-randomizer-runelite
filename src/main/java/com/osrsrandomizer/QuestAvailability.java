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
import net.runelite.api.Skill;

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
        rules.put("bosses:mad_angel", Quest.FALLEN_FROM_GRACE);
        rules.put("bosses:maggot_king", Quest.THE_BLOOD_MOON_RISES);
        rules.put("bosses:brutus", Quest.THE_IDES_OF_MILK);
        rules.put("bosses:shellbane_gryphon", Quest.TROUBLED_TORTUGANS);
        rules.put("bosses:perilous_moons", Quest.PERILOUS_MOONS);
        rules.put("bosses:doom_of_mokhaiotl", Quest.THE_FINAL_DAWN);
        rules.put("bosses:yama", Quest.A_KINGDOM_DIVIDED);
        rules.put("bosses:nex", Quest.THE_FROZEN_DOOR);
        rules.put("bosses:the_hueycoatl", Quest.CHILDREN_OF_THE_SUN);
        rules.put("bosses:sol_heredit", Quest.CHILDREN_OF_THE_SUN);
        for (String id : new String[] {"barrows", "the_nightmare", "phosanis_nightmare"})
            rules.put("bosses:" + id, Quest.PRIEST_IN_PERIL);
        rules.put("raids:tob", Quest.PRIEST_IN_PERIL);
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
    private final Map<String, String> diaryDetails;
    private final Map<Skill, Integer> levels;
    private final Map<String, String> bossAccess;

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
        this(ready, account, states, wildernessMediumComplete, member, completedDiaries, diaryRequirements, Collections.emptyMap());
    }

    QuestAvailability(boolean ready, long account, Map<Quest, QuestState> states, Boolean wildernessMediumComplete,
        Boolean member, Map<String, Boolean> completedDiaries, Map<String, String> diaryRequirements, Map<String, String> diaryDetails)
    {
        this(ready, account, states, wildernessMediumComplete, member, completedDiaries, diaryRequirements, diaryDetails, Collections.emptyMap());
    }

    QuestAvailability(boolean ready, long account, Map<Quest, QuestState> states, Boolean wildernessMediumComplete,
        Boolean member, Map<String, Boolean> completedDiaries, Map<String, String> diaryRequirements,
        Map<String, String> diaryDetails, Map<Skill, Integer> levels)
    {
        this(ready, account, states, wildernessMediumComplete, member, completedDiaries, diaryRequirements,
            diaryDetails, levels, Collections.emptyMap());
    }

    QuestAvailability(boolean ready, long account, Map<Quest, QuestState> states, Boolean wildernessMediumComplete,
        Boolean member, Map<String, Boolean> completedDiaries, Map<String, String> diaryRequirements,
        Map<String, String> diaryDetails, Map<Skill, Integer> levels, Map<String, String> bossAccess)
    {
        this.bossAccess = Collections.unmodifiableMap(new LinkedHashMap<>(bossAccess));
        this.ready = ready;
        this.account = account;
        Map<Quest, QuestState> copy = new EnumMap<>(Quest.class);
        copy.putAll(states);
        this.states = Collections.unmodifiableMap(copy);
        this.wildernessMediumComplete = wildernessMediumComplete;
        this.member = member;
        this.completedDiaries = Collections.unmodifiableMap(new LinkedHashMap<>(completedDiaries));
        this.diaryRequirements = Collections.unmodifiableMap(new LinkedHashMap<>(diaryRequirements));
        this.diaryDetails = Collections.unmodifiableMap(new LinkedHashMap<>(diaryDetails));
        Map<Skill, Integer> levelCopy = new EnumMap<>(Skill.class);
        levelCopy.putAll(levels);
        this.levels = Collections.unmodifiableMap(levelCopy);
    }

    String requirementDetails(Category category, String id)
    {
        if (category == Category.DIARIES) return diaryDetails(id);
        if (!ready) return "Log in to view character requirements.";
        java.util.List<String> missing = new java.util.ArrayList<>();
        if (membersOnly(category, id))
        {
            if (member == null) missing.add("Membership: waiting for character data.");
            else if (!member) missing.add("Members only: an active membership is required.");
        }
        Quest quest = REQUIREMENTS.get(category.getKey() + ":" + id);
        if (quest != null && states.get(quest) != QuestState.FINISHED)
        {
            QuestState state = states.get(quest);
            missing.add("Complete " + quest.getName() + " ("
                + (state == null ? "waiting for quest data" : state == QuestState.IN_PROGRESS ? "in progress" : "not started") + ")");
        }
        if (needsWildernessMedium(category, id) && !Boolean.TRUE.equals(wildernessMediumComplete))
            missing.add("Complete the Medium Wilderness Diary ("
                + (wildernessMediumComplete == null ? "waiting for diary data" : "not completed") + ")");
        Skill skill = requiredSkill(category, id);
        if (skill != null)
        {
            Integer level = levels.get(skill);
            int required = requiredLevel(id);
            if (level == null || level < required)
                missing.add(skill.getName() + ": " + (level == null ? "?" : level) + " / " + required);
        }
        if (category == Category.BOSSES && BossAccess.IDS.contains(id))
        {
            String access = bossAccess.containsKey(id) ? bossAccess.get(id) : "Waiting for boss access data";
            if (access != null) missing.add(access);
        }
        String detail = missing.isEmpty() ? "No missing tracked requirements." : String.join("\n\n", missing);
        if (category == Category.BOSSES && BossAccess.GOD_WARS.contains(id))
            detail += "\n\nAlso needs a route to God Wars (defeat Dad during Troll Stronghold or use Ghommal's hilt), entry equipment and kill count/key. These are not checked.";
        if (!isChecked(category, id)) detail += "\n\nNo quest or diary requirements are tracked for this activity.";
        return detail;
    }

    String diaryDetails(String id)
    {
        if (!ready) return "Log in to view character requirements.";
        if (Boolean.TRUE.equals(completedDiaries.get(id))) return "Diary already completed.";
        String status = reason(Category.DIARIES, id);
        String detail = diaryDetails.get(id);
        return (status != null && (status.startsWith("Waiting") || status.equals("Members only")) ? status + "\n\n" : "")
            + (detail == null ? "Waiting for requirement details." : detail)
            + "\n\nCompleted tasks are ignored. Base levels only; boosts, items and some unlocks are not checked.";
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
            || REQUIREMENTS.containsKey(category.getKey() + ":" + id) || needsWildernessMedium(category, id)
            || requiredSkill(category, id) != null || (category == Category.BOSSES && BossAccess.IDS.contains(id));
    }

    private static Skill requiredSkill(Category category, String id)
    {
        if (category != Category.BOSSES) return null;
        switch (id)
        {
            case "tempoross": return Skill.FISHING;
            case "wintertodt": return Skill.FIREMAKING;
            case "shellbane_gryphon": return Skill.SLAYER;
            case "general_graardor": return Skill.STRENGTH;
            case "commander_zilyana": return Skill.AGILITY;
            case "kree_arra": return Skill.RANGED;
            case "kril_tsutsaroth": return Skill.HITPOINTS;
            default: return null;
        }
    }

    private static int requiredLevel(String id)
    {
        return id.equals("tempoross") ? 35 : id.equals("wintertodt") ? 50 : id.equals("shellbane_gryphon") ? 51 : 70;
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
        Skill skill = requiredSkill(category, id);
        if (skill != null)
        {
            Integer level = levels.get(skill);
            if (!ready || level == null) return "Waiting for skill data";
            if (level < requiredLevel(id)) return "Requires " + requiredLevel(id) + " " + skill.getName();
        }
        Quest quest = REQUIREMENTS.get(category.getKey() + ":" + id);
        if (quest != null)
        {
            if (!ready || states.get(quest) == null) return "Waiting for quest data";
            if (states.get(quest) != QuestState.FINISHED) return "Complete " + quest.getName();
        }
        if (category == Category.BOSSES && BossAccess.IDS.contains(id))
            return bossAccess.containsKey(id) ? bossAccess.get(id) : "Waiting for boss access data";
        return null;
    }

    @Override public boolean equals(Object other)
    {
        if (!(other instanceof QuestAvailability)) return false;
        QuestAvailability that = (QuestAvailability) other;
        return ready == that.ready && account == that.account && states.equals(that.states)
            && Objects.equals(wildernessMediumComplete, that.wildernessMediumComplete)
            && Objects.equals(member, that.member) && completedDiaries.equals(that.completedDiaries)
            && diaryRequirements.equals(that.diaryRequirements) && diaryDetails.equals(that.diaryDetails) && levels.equals(that.levels) && bossAccess.equals(that.bossAccess);
    }

    @Override public int hashCode() { return Objects.hash(ready, account, states, wildernessMediumComplete, member, completedDiaries, diaryRequirements, diaryDetails, levels, bossAccess); }
}
