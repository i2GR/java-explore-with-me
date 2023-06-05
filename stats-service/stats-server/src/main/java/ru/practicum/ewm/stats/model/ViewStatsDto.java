package ru.practicum.ewm.stats.model;

import lombok.*;
import ru.practicum.ewm.utils.StatsAppName;

@Getter
@AllArgsConstructor
public class ViewStatsDto {

    private StatsAppName app;

    private String uri;

    private Long hits;
}
