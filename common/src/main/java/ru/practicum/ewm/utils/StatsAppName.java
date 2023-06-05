package ru.practicum.ewm.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.practicum.ewm.exception.BadRequestException;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum StatsAppName {
    @JsonProperty("ewm-main-service")
    EWM_MAIN_SERVICE("ewm-main-service");


    private final String value;

    StatsAppName(String str) {
        this.value = str;
    }

    public String get() {
        return value;
    }

   private static final Map<String, StatsAppName> appNames = Stream.of(StatsAppName.values())
          .collect(Collectors.toMap(StatsAppName::get, a -> a));

    public static StatsAppName fromString(String str) {
        if (str != null && appNames.containsKey(str)) {
            return appNames.get(str);
        }
        throw new BadRequestException("Bad App name");
    }
}
