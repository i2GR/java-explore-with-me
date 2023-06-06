package ru.practicum.ewm.stats.model;

import lombok.Getter;
import lombok.AllArgsConstructor;
import ru.practicum.ewm.common.utils.StatsAppName;

@Getter
@AllArgsConstructor
public class ViewStatsDto {

    private StatsAppName app;

    private String uri;

    private Long hits;
}
