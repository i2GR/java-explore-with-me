package ru.practicum.ewm.common.utils;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum SortingType {
    EVENT_DATE,
    VIEWS;

    private static final Set<String> statuses = Stream.of(SortingType.values())
            .map(SortingType::toString)
            .collect(Collectors.toSet());

    public static SortingType fromString(String str) {
        if (str != null && statuses.contains(str.toUpperCase())) {
            return SortingType.valueOf(SortingType.class, str.toUpperCase());
        }
        return SortingType.EVENT_DATE;
    }
}