package com.osrsrandomizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/** Newest first. Repeated results are intentional; unknown catalogue IDs are ignored. */
final class HistoryCodec
{
    static List<Activity> decode(Category category, String value)
    {
        List<Activity> result = new ArrayList<>();
        if (value == null) return result;
        for (String id : value.split(","))
        {
            for (Activity activity : category.getActivities())
                if (activity.getId().equals(id)) { result.add(activity); break; }
            if (result.size() == 5) break;
        }
        return result;
    }

    static String encode(Collection<Activity> history)
    {
        return history.stream().limit(5).map(Activity::getId).collect(Collectors.joining(","));
    }
}
