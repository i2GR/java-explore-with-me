package ru.practicum.ewm.app.dto.compilation;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.ewm.app.dto.event.EventOutputShortDto;

import java.util.List;

/**
 * DTO подборок событий c полными данными<p>
 * Сопоставляется с модель-сущностью Compilation<p>
 * @implNote Использование для предоставления данных<p>
 * {@see ru.practicum.ewm.app.model.Compilation}
 */
@Builder
@Getter
public class CompilationOutputDto {

    private Long id;

    private String title;

    private Boolean pinned;

    private List<EventOutputShortDto> events;
}