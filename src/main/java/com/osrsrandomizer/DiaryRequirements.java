package com.osrsrandomizer;

import java.util.*;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.client.plugins.achievementdiary.*;

/** Evaluates tier requirements using one client-thread snapshot, never on Swing. */
final class DiaryRequirements
{
    private static final Map<String, List<Requirement>> RULES = DiaryRequirementsData.rules();

    static Set<Quest> quests()
    {
        Set<Quest> quests = EnumSet.noneOf(Quest.class);
        RULES.values().forEach(rules -> rules.forEach(rule -> collectQuests(rule, quests)));
        return Collections.unmodifiableSet(quests);
    }

    private static void collectQuests(Requirement rule, Set<Quest> quests)
    {
        if (rule instanceof QuestRequirement) quests.add(((QuestRequirement) rule).getQuest());
        if (rule instanceof OrRequirement)
            ((OrRequirement) rule).getRequirements().forEach(child -> collectQuests(child, quests));
    }

    static Map<String, String> evaluate(Map<Skill, Integer> levels, Map<Quest, QuestState> quests,
        Integer combat, Integer questPoints)
    {
        Map<String, String> results = new LinkedHashMap<>();
        RULES.forEach((id, rules) ->
        {
            Set<String> missing = new LinkedHashSet<>();
            boolean unknown = false;
            for (Requirement rule : rules)
            {
                Boolean result = satisfied(rule, levels, quests, combat, questPoints);
                unknown |= result == null;
                if (Boolean.FALSE.equals(result))
                    missing.add(describe(rule));
            }
            // Keep rows compact. The full list is available in the tooltip.
            results.put(id, unknown ? "Waiting for skill/quest data" : missing.isEmpty() ? null : String.join("; ", missing));
        });
        return Collections.unmodifiableMap(results);
    }

    static Boolean satisfied(Requirement rule, Map<Skill, Integer> levels, Map<Quest, QuestState> quests,
        Integer combat, Integer questPoints)
    {
        if (rule instanceof SkillRequirement)
        {
            SkillRequirement skill = (SkillRequirement) rule;
            Integer level = levels.get(skill.getSkill());
            return level == null ? null : level >= skill.getLevel();
        }
        if (rule instanceof QuestRequirement)
        {
            QuestRequirement quest = (QuestRequirement) rule;
            QuestState state = quests.get(quest.getQuest());
            return state == null ? null : quest.isStarted() ? state != QuestState.NOT_STARTED : state == QuestState.FINISHED;
        }
        if (rule instanceof OrRequirement)
        {
            boolean unknown = false;
            for (Requirement child : ((OrRequirement) rule).getRequirements())
            {
                Boolean result = satisfied(child, levels, quests, combat, questPoints);
                if (Boolean.TRUE.equals(result)) return true;
                unknown |= result == null;
            }
            return unknown ? null : Boolean.FALSE;
        }
        if (rule instanceof CombatLevelRequirement)
            return combat == null ? null : combat >= ((CombatLevelRequirement) rule).getLevel();
        if (rule instanceof QuestPointRequirement)
            return questPoints == null ? null : questPoints >= ((QuestPointRequirement) rule).getQp();
        return null;
    }

    private static String describe(Requirement rule)
    {
        if (rule instanceof QuestRequirement)
        {
            QuestRequirement quest = (QuestRequirement) rule;
            return (quest.isStarted() ? "Start " : "Complete ") + quest.getQuest().getName();
        }
        if (rule instanceof OrRequirement)
        {
            List<String> options = new ArrayList<>();
            ((OrRequirement) rule).getRequirements().forEach(child -> options.add(describe(child)));
            return String.join(" or ", options);
        }
        return rule.toString();
    }

    private DiaryRequirements() { }
}
