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
    private static final Map<String, List<DiaryTask>> TASKS = DiaryRequirementsData.tasks();

    static final class Evaluation
    {
        final Map<String, String> reasons;
        final Map<String, String> details;
        Evaluation(Map<String, String> reasons, Map<String, String> details)
        {
            this.reasons = Collections.unmodifiableMap(new LinkedHashMap<>(reasons));
            this.details = Collections.unmodifiableMap(new LinkedHashMap<>(details));
        }
    }

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
        return evaluate(levels, quests, combat, questPoints, id -> null, id -> null).reasons;
    }

    static Evaluation evaluate(Map<Skill, Integer> levels, Map<Quest, QuestState> quests,
        Integer combat, Integer questPoints, java.util.function.IntFunction<Integer> varps,
        java.util.function.IntFunction<Integer> varbits)
    {
        Map<String, String> results = new LinkedHashMap<>();
        Map<String, String> details = new LinkedHashMap<>();
        TASKS.forEach((id, tasks) ->
        {
            Set<String> missing = new LinkedHashSet<>();
            Set<String> lines = new LinkedHashSet<>();
            boolean unknown = false;
            for (DiaryTask task : tasks)
            {
                if (task.completed(varps, varbits)) continue;
                List<String> taskMissing = new ArrayList<>();
                for (Requirement rule : task.requirements)
                {
                    Boolean result = satisfied(rule, levels, quests, combat, questPoints);
                    unknown |= result == null;
                    if (!Boolean.TRUE.equals(result))
                    {
                        taskMissing.add(describeCurrent(rule, levels, quests, combat, questPoints));
                        if (Boolean.FALSE.equals(result)) missing.add(describe(rule));
                    }
                }
                if (!taskMissing.isEmpty()) lines.add(task.name + "\n" + String.join("\n", taskMissing));
            }
            if (id.equals("lumbridge_draynor_elite") || id.equals("falador_elite"))
            {
                boolean lumbridge = id.equals("lumbridge_draynor_elite");
                Integer bits = varps.apply(lumbridge
                    ? net.runelite.api.gameval.VarPlayerID.LUMB_DRAY_ACHIEVEMENT_DIARY2
                    : net.runelite.api.gameval.VarPlayerID.FALADOR_ACHIEVEMENT_DIARY2);
                boolean done = bits != null && ((bits >>> (lumbridge ? 9 : 8)) & 1) == 1;
                boolean skillCape = !lumbridge && levels.entrySet().stream()
                    .anyMatch(e -> e.getKey() != Skill.OVERALL && e.getValue() != null && e.getValue() >= 99);
                if (!done && !skillCape)
                {
                    Integer maxPoints = varbits.apply(net.runelite.api.gameval.VarbitID.QP_MAX);
                    boolean qpUnknown = maxPoints == null || maxPoints <= 0 || questPoints == null;
                    if (qpUnknown || questPoints < maxPoints)
                    {
                        boolean skillsUnknown = !lumbridge && Arrays.stream(Skill.values())
                            .filter(s -> s != Skill.OVERALL).anyMatch(s -> levels.get(s) == null);
                        unknown |= qpUnknown || skillsUnknown;
                        String requirement = lumbridge ? "Quest point cape" : "Any level 99 skill or quest point cape";
                        if (!qpUnknown && !skillsUnknown) missing.add(requirement);
                        lines.add("Perform the cape emote\n" + requirement + "\nQuest points: "
                            + current(questPoints) + " / " + (maxPoints == null || maxPoints <= 0 ? "?" : maxPoints));
                    }
                }
            }
            results.put(id, unknown ? "Waiting for skill/quest data" : missing.isEmpty() ? null : String.join("; ", missing));
            details.put(id, lines.isEmpty() ? "No missing tracked requirements for unfinished tasks." : String.join("\n\n", lines));
        });
        return new Evaluation(results, details);
    }

    private static String describeCurrent(Requirement rule, Map<Skill, Integer> levels,
        Map<Quest, QuestState> quests, Integer combat, Integer questPoints)
    {
        if (rule instanceof SkillRequirement)
        {
            SkillRequirement skill = (SkillRequirement) rule;
            return skill.getSkill().getName() + ": " + current(levels.get(skill.getSkill())) + " / " + skill.getLevel();
        }
        if (rule instanceof QuestRequirement)
        {
            QuestRequirement quest = (QuestRequirement) rule;
            QuestState state = quests.get(quest.getQuest());
            return describe(rule) + " (" + (state == null ? "unknown" : state == QuestState.NOT_STARTED ? "not started"
                : state == QuestState.IN_PROGRESS ? "in progress" : "completed") + ")";
        }
        if (rule instanceof OrRequirement)
        {
            List<String> options = new ArrayList<>();
            ((OrRequirement) rule).getRequirements().forEach(child ->
                options.add(describeCurrent(child, levels, quests, combat, questPoints)));
            return String.join(" OR ", options);
        }
        if (rule instanceof CombatLevelRequirement)
            return "Combat: " + current(combat) + " / " + ((CombatLevelRequirement) rule).getLevel();
        if (rule instanceof QuestPointRequirement)
            return "Quest points: " + current(questPoints) + " / " + ((QuestPointRequirement) rule).getQp();
        return describe(rule);
    }

    private static String current(Integer value) { return value == null ? "?" : value.toString(); }

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
