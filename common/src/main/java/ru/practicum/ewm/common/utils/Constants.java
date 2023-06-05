package ru.practicum.ewm.common.utils;

public class Constants {

    /**
     * паттерн формата даты-времени на эндпойнтах
     */
    public static final String STATS_DTO_TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String IPV4_PATTERN = "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$";
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
}
