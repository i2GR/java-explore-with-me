package ru.practicum.ewm.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import ru.practicum.ewm.common.utils.Constants;

import java.time.LocalDateTime;

/**
 * DTO события с сокращенным объемом данных<p>
 * Сопоставляется с модель-сущностью Event<p>
 * @implNote Использование для предоставления данных<p>
 * {@see ru.practicum.ewm.app.admin.event.model.Event}
 */
@Builder
@Getter
public class EventOutputShortDto {

    private Long id;

    private String title;

    private String annotation;

    private String description;

    @JsonFormat(pattern = Constants.EWM_TIMESTAMP_PATTERN)
    private LocalDateTime eventDate;

    private CategoryDto category;

    private UserOutputShortDto initiator;

    private Boolean paid;

    private Integer views;

    private Long confirmedRequests;
}