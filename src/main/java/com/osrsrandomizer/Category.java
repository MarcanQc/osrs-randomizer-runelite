package com.osrsrandomizer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Catalogue ported from the user's Electron application, version 1.0.1. */
public enum Category
{
    SKILLS("skills", "Skills", Arrays.asList(
        new Activity("attack", "Attack"),
        new Activity("strength", "Strength"),
        new Activity("defence", "Defence"),
        new Activity("ranged", "Ranged"),
        new Activity("prayer", "Prayer"),
        new Activity("magic", "Magic"),
        new Activity("runecraft", "Runecraft"),
        new Activity("construction", "Construction"),
        new Activity("hitpoints", "Hitpoints"),
        new Activity("agility", "Agility"),
        new Activity("herblore", "Herblore"),
        new Activity("thieving", "Thieving"),
        new Activity("crafting", "Crafting"),
        new Activity("fletching", "Fletching"),
        new Activity("slayer", "Slayer"),
        new Activity("hunter", "Hunter"),
        new Activity("mining", "Mining"),
        new Activity("smithing", "Smithing"),
        new Activity("fishing", "Fishing"),
        new Activity("cooking", "Cooking"),
        new Activity("firemaking", "Firemaking"),
        new Activity("woodcutting", "Woodcutting"),
        new Activity("farming", "Farming"),
        new Activity("sailing", "Sailing"))),
    BOSSES("bosses", "Bosses", Arrays.asList(
        new Activity("mad_angel", "Mad Angel"),
        new Activity("maggot_king", "Maggot King"),
        new Activity("brutus", "Brutus"),
        new Activity("shellbane_gryphon", "Shellbane Gryphon"),
        new Activity("barrows", "Barrows"),
        new Activity("scurrius", "Scurrius"),
        new Activity("giant_mole", "Giant Mole"),
        new Activity("deranged_archaeologist", "Deranged Archaeologist"),
        new Activity("dagannoth_kings", "Dagannoth Kings"),
        new Activity("sarachnis", "Sarachnis"),
        new Activity("perilous_moons", "Perilous Moons"),
        new Activity("kalphite_queen", "Kalphite Queen"),
        new Activity("kree_arra", "Kree'arra"),
        new Activity("commander_zilyana", "Commander Zilyana"),
        new Activity("general_graardor", "General Graardor"),
        new Activity("kril_tsutsaroth", "K'ril Tsutsaroth"),
        new Activity("the_hueycoatl", "The Hueycoatl"),
        new Activity("corporeal_beast", "Corporeal Beast"),
        new Activity("nex", "Nex"),
        new Activity("chaos_fanatic", "Chaos Fanatic"),
        new Activity("crazy_archaeologist", "Crazy Archaeologist"),
        new Activity("scorpia", "Scorpia"),
        new Activity("king_black_dragon", "King Black Dragon"),
        new Activity("chaos_elemental", "Chaos Elemental"),
        new Activity("calvarion", "Calvar'ion"),
        new Activity("vetion", "Vet'ion"),
        new Activity("spindel", "Spindel"),
        new Activity("venenatis", "Venenatis"),
        new Activity("artio", "Artio"),
        new Activity("callisto", "Callisto"),
        new Activity("obor", "Obor"),
        new Activity("bryophyta", "Bryophyta"),
        new Activity("amoxliatl", "Amoxliatl"),
        new Activity("royal_titans", "Royal Titans"),
        new Activity("doom_of_mokhaiotl", "Doom of Mokhaiotl"),
        new Activity("zulrah", "Zulrah"),
        new Activity("vorkath", "Vorkath"),
        new Activity("phantom_muspah", "Phantom Muspah"),
        new Activity("the_nightmare", "The Nightmare"),
        new Activity("phosanis_nightmare", "Phosani's Nightmare"),
        new Activity("yama", "Yama"),
        new Activity("duke_sucellus", "Duke Sucellus"),
        new Activity("the_leviathan", "The Leviathan"),
        new Activity("the_whisperer", "The Whisperer"),
        new Activity("vardorvis", "Vardorvis"),
        new Activity("crystalline_hunllef", "Crystalline Hunllef"),
        new Activity("corrupted_hunllef", "Corrupted Hunllef"),
        new Activity("tztok_jad", "TzTok-Jad"),
        new Activity("tzkal_zuk", "TzKal-Zuk"),
        new Activity("sol_heredit", "Sol Heredit"),
        new Activity("tempoross", "Tempoross"),
        new Activity("zalcano", "Zalcano"),
        new Activity("wintertodt", "Wintertodt"))),
    RAIDS("raids", "Raids", Arrays.asList(
        new Activity("cox", "Chambers of Xeric"),
        new Activity("tob", "Theatre of Blood"),
        new Activity("toa", "Tombs of Amascut")));

    private final String key;
    private final String label;
    private final List<Activity> activities;

    Category(String key, String label, List<Activity> activities)
    {
        this.key = key;
        this.label = label;
        this.activities = Collections.unmodifiableList(activities);
    }

    public String getKey() { return key; }
    public List<Activity> getActivities() { return activities; }
    @Override public String toString() { return label; }
}
