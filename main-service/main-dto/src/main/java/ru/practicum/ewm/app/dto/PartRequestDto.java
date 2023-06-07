package ru.practicum.ewm.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import ru.practicum.ewm.common.utils.PartRequestStatus;

import java.time.LocalDateTime;

import static ru.practicum.ewm.common.utils.Constants.EWM_TIMESTAMP_PATTERN;

/**
 * DTO запроса на участие в событии с полными данными<p>
 * Сопоставляется с модель-сущностью PartRequest<p>
 * @implNote Использование для получения и предоставления данных<p>
 * {@see ru.practicum.ewm.app.model.PartRequest}
 */
@Builder
@Getter
public class PartRequestDto {

    private Long id;

    @JsonFormat(pattern = EWM_TIMESTAMP_PATTERN)
    private LocalDateTime created;

    private Long event;

    private Long requester;

    private PartRequestStatus status;
}