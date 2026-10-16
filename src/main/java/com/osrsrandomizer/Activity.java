package com.osrsrandomizer;

public final class Activity
{
    private final String id;
    private final String name;

    public Activity(String id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
}
