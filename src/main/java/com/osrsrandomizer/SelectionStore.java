package com.osrsrandomizer;

import java.util.Set;

interface SelectionStore
{
    default boolean questFilterEnabled() { return true; }
    Set<String> load(Category category);
    void save(Category category, Set<String> selected);
    default String loadHistory(long account, Category category) { return null; }
    default void saveHistory(long account, Category category, String value) { }
}
