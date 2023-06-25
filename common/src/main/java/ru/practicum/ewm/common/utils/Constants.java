package ru.practicum.ewm.common.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Constants {

    /**
     * паттерн формата даты-времени на эндпойнтах
     */
    public static final String EWM_TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String IPV4_PATTERN = "(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}";
    public static final String IPV6_PATTERN = "(?:[A-Fa-f0-9]{1,4}:){7}[A-Fa-f0-9]{1,4}";

    /**
     * путь для эндпойнта сервера статистики, по которому поступают данные в сервер
     * @see "ru.practicum.ewm.stats.StatsController"
     */
    public static final String STATS_HIT_PATH = "/hit";

    /**
     * путь для эндпойнта сервера статистики, с которого можно получить статистику
     * @see "ru.practicum.ewm.stats.StatsController"
     */
    public static final String STATS_GET_PATH = "/stats";

    public static final String EMAIL_REGEXP = "[A-Za-z0-9._%+-]{0,64}@([A-Za-z0-9]{1,63}.){1,3}[A-Za-z0-9]{1,63}";//"[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}";

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(EWM_TIMESTAMP_PATTERN);

    public static final LocalDateTime MIN_TIME = LocalDateTime.of(2022, 8, 25, 12, 0);

    public static final int HOUR_OF_SEC = 3600;

    public static final String CHECK_TIME_FILTER = "checking request time parameters";
    public static final String CHECK_EVENT_TIME = "checking event time to add/update";
}