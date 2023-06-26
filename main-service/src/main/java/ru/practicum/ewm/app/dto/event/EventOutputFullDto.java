package ru.practicum.ewm.app.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import ru.practicum.ewm.app.dto.user.UserOutputShortDto;
import ru.practicum.ewm.app.dto.category.CategoryDto;
import ru.practicum.ewm.common.utils.Constants;
import ru.practicum.ewm.common.utils.EventState;

import java.time.LocalDateTime;

/**
 * DTO события c полными данными<p>
 * Сопоставляется с модель-сущностью Event<p>
 * @implNote Использование для предоставления данных<p>
 * {@see ru.practicum.ewm.app.admin.event.model.Event}
 */
@Builder
@Getter
public class EventOutputFullDto {

    private Long id;

    private String title;

    private String annotation;

    private String description;

    @JsonFormat(pattern = Constants.EWM_TIMESTAMP_PATTERN)
    private LocalDateTime eventDate;

    private CategoryDto category;

    private UserOutputShortDto initiator;

    private EventOutputFullDto.Location location;

    @JsonFormat(pattern = Constants.EWM_TIMESTAMP_PATTERN)
    private LocalDateTime createdOn;

    private Boolean paid;

    private Boolean requestModeration;

    Integer participantLimit;

    @JsonFormat(pattern = Constants.EWM_TIMESTAMP_PATTERN)
    private LocalDateTime publishedOn;

    private EventState state;

    private Long views;

    private Long confirmedRequests;

    @Builder
    @Getter
    public static class Location {

        private Float lat;

        private Float lon;
    }
}