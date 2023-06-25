package ru.practicum.ewm.app.event.admin;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import ru.practicum.ewm.app.dto.EventInputDto;
import ru.practicum.ewm.app.dto.EventOutputFullDto;
import ru.practicum.ewm.common.utils.EventState;
import ru.practicum.ewm.common.validation.OnUpdate;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
@Validated
public class EventAdminController {

    private final EventAdminService service;

    @GetMapping
    public ResponseEntity<List<EventOutputFullDto>> getEvents(
            @RequestParam(name = "users", required = false) List<Long> userIds,
            @RequestParam(name = "states", required = false) List<EventState> states,
            @RequestParam(name = "categories", required = false) List<Long> categoryIds,
            @RequestParam(name = "rangeStart", required = false) String rangeStart,
            @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Long from,
            @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Admin API: getting events");
        return new ResponseEntity<>(
                service.getEventListByConditions(
                        userIds,
                        states,
                        categoryIds,
                        rangeStart,
                        rangeEnd,
                        from, size),
                HttpStatus.OK);
    }

    @PatchMapping(path = "/{eventId}")
    public ResponseEntity<EventOutputFullDto> updateEvent(
            @PathVariable(name = "eventId") @Positive Long eventId,
            @RequestBody @Validated(value = OnUpdate.class) EventInputDto dto) {
        log.info("Admin API:patch event of id {}", eventId);
        return new ResponseEntity<>(service.updateEvent(eventId, dto), HttpStatus.OK);
    }
}