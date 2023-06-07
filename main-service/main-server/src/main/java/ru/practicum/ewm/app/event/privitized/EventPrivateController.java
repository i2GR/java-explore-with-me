package ru.practicum.ewm.app.event.privitized;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import ru.practicum.ewm.common.validation.OnCreate;
import ru.practicum.ewm.common.validation.OnUpdate;

import ru.practicum.ewm.app.dto.EventInputDto;
import ru.practicum.ewm.app.dto.EventOutputFullDto;
import ru.practicum.ewm.app.dto.EventOutputShortDto;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users/{userId}/events")
@Validated
public class EventPrivateController {

    private final EventPrivateService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EventOutputFullDto> AddEventByUser(@PathVariable @Positive Long userId,
                                                             @RequestBody @Validated(value = OnCreate.class) EventInputDto dto) {
        log.info("Private API: add event {} from userId {}", dto.getTitle(), userId);
        return new ResponseEntity<>(service.addEventByUser(userId, dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EventOutputShortDto>> getAllEventsByUser(
            @PathVariable @PositiveOrZero Long userId,
            @RequestParam(name = "from", required = false, defaultValue = "0") @PositiveOrZero Long from,
            @RequestParam(name = "size", required = false, defaultValue = "10") @Positive Integer size) {
        log.info("Private API: get events of user id {} from {} size {}", userId, from, size);
        return new ResponseEntity<>(service.getAllEventsByUser(userId, from, size), HttpStatus.OK);
    }

    @GetMapping(path = "/{eventId}")
    public ResponseEntity<EventOutputFullDto> getEventByIdAndUserId(
            @PathVariable @PositiveOrZero Long userId,
            @PathVariable(name = "eventId") @PositiveOrZero Long eventId) {
        log.info("Private API: get event id {} user id", eventId, userId);
        return new ResponseEntity<>(service.getEventByIdAndUserId(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping(path = "/{eventId}")
    public ResponseEntity<EventOutputFullDto> updateEvent(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId,
            @RequestBody @Validated(value = OnUpdate.class) EventInputDto dto) {
        log.info("Private API: update event {} id {} from userId {}", dto.getTitle(), eventId, userId);
        return new ResponseEntity<>(service.updateEvent(userId, eventId, dto), HttpStatus.OK);
    }
}