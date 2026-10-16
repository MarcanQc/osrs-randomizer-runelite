package com.osrsrandomizer;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

final class SelectionCodec
{
    static Set<String> decode(Category category, String value)
    {
        Set<String> valid = category.getActivities().stream().map(Activity::getId)
            .collect(Collectors.toCollection(LinkedHashSet::new));
        if (value == null) return valid;
        valid.retainAll(Arrays.asList(value.split(",")));
        return valid;
    }

    static String encode(Set<String> selected) { return String.join(",", selected); }
}
