package ru.practicum.ewm.app.event.publish;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import ru.practicum.ewm.app.dto.EventOutputFullDto;
import ru.practicum.ewm.app.dto.EventOutputShortDto;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/events")
@Validated
public class EventPublicController {

    private final EventPublicService service;

    @GetMapping
    public ResponseEntity<List<EventOutputShortDto>> getEvents(
            @RequestParam(name = "text", defaultValue = "") String query,
            @RequestParam(name = "categories", required = false) List<Long> categories,
            @RequestParam(name = "paid", required = false) Boolean paid,
            @RequestParam(name = "rangeStart", required = false) String rangeStart,
            @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(name = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(name = "size", defaultValue = "10") @Positive Integer size,
            HttpServletRequest request) {
        log.info("Public API: get events");
        return new ResponseEntity<>(service.getEventListByConditions(
                query,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                sort,
                from,
                size,
                request),
                HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<EventOutputFullDto> getEventById(
            @PathVariable(name = "id")  @Positive Long eventId,
            HttpServletRequest request) {
        log.info("Public API: get event id {}", eventId);
        return new ResponseEntity<>(service.getEventById(eventId, request), HttpStatus.OK);
    }
}