package ru.practicum.ewm.stats.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import ru.practicum.ewm.common.utils.Constants;
import ru.practicum.ewm.common.utils.StatsAppName;
import ru.practicum.ewm.stats.validation.JsonBodyIp;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class EndpointHitDto {

    @NotNull
    private StatsAppName app;

    @NotBlank
    private String uri;

    @JsonBodyIp
    private String ip;

    @NotNull
    @PastOrPresent
    @JsonFormat(pattern = Constants.EWM_TIMESTAMP_PATTERN)
    private LocalDateTime timestamp;
}
