package ru.practicum.ewm.stats.dto;

import lombok.*;
import ru.practicum.ewm.utils.StatsAppName;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode()
public class ViewStatsDto {

    private StatsAppName app;

    private String uri;

    private Long hits;
}
