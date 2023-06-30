package ru.practicum.ewm.app.dto.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.common.utils.EventStateAction;
import ru.practicum.ewm.common.validation.NullOrSize;
import ru.practicum.ewm.common.validation.OnCreate;
import ru.practicum.ewm.common.validation.OnUpdate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * DTO нового события<p>
 * Сопоставляется с модель-сущностью Event<p>
 * @implNote Использование для получения нового события с эндпойнта<p>
 *     а также при модификации события
 * {@see ru.practicum.ewm.app.admin.event.model.Event}
 */
@Builder
@Getter
public class EventInputDto {

    @NotBlank(message = "event title must not be blank", groups = {OnCreate.class})
    @Size(min = 3, max = 120, message = "event title must be of from 3 up to 120 symbols", groups = {OnCreate.class})
    @NullOrSize(min = 3, max = 120, message = "event title must be of from 3 up to 120 symbols", groups = {OnUpdate.class})
    private String title;

    @NotBlank(message = "event annotation must not be blank", groups = {OnCreate.class})
    @Size(min = 20, max = 2000, message = "event annotation must be of from 20 up to 2000 symbols", groups = {OnCreate.class})
    @NullOrSize(min = 20, max = 2000, message = "event annotation must be of from 20 up to 2000 symbols", groups = {OnUpdate.class})
    private String annotation;

    @NotBlank(message = "event description must not be blank", groups = {OnCreate.class})
    @Size(min = 20, max = 7000, message = "event description must be of from 20 up to 7000 symbols", groups = {OnCreate.class})
    @NullOrSize(min = 20, max = 7000, message = "event description must be of from 20 up to 7000 symbols", groups = {OnUpdate.class})
    private String description;

    @NotNull(message = "event date required", groups = {OnCreate.class})
    private String eventDate;

    @NotNull(message = "event category required", groups = {OnCreate.class})
    private Long category;

    @NotNull(message = "event location must be specified", groups = {OnCreate.class})
    private EventInputDto.Location location;

    @Setter
    private Boolean paid;

    @Setter
    private Boolean requestModeration;

    @Setter
    private Integer participantLimit;

    private EventStateAction stateAction;

    @Builder
    @Getter
    public static class Location {

        @NotNull(message = "Location latitude must be set", groups = {OnCreate.class})
        private Float lat;

        @NotNull(message = "Location longitude must be set", groups = {OnCreate.class})
        private Float lon;
    }
}