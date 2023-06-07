package ru.practicum.ewm.stats.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.common.utils.StatsAppName;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ViewStatsDto {

    private StatsAppName app;

    private String uri;

    private Long hits;
}
