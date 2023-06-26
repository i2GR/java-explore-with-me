package ru.practicum.ewm.app.partrequest.privatized;

import java.util.List;

import javax.validation.constraints.Positive;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import ru.practicum.ewm.app.dto.partrequest.PartRequestDto;
import ru.practicum.ewm.app.dto.partrequest.PartRequestStatusUpdateRequest;
import ru.practicum.ewm.app.dto.partrequest.PartRequestStatusUpdateResult;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users/{userId}/")
@Validated
public class PartRequestPrivateController {

    private final PartRequestPrivateService service;

    @PostMapping(path = "/requests")
    public ResponseEntity<PartRequestDto> addRequest(
            @PathVariable(name = "userId") @Positive Long userId,
            @RequestParam (name = "eventId") @Positive Long eventId) {
        log.info("Private API:posting eventId {} participation request from userId {} ", eventId, userId);
        return new ResponseEntity<>(service.addPartRequest(userId, eventId), HttpStatus.CREATED);
    }

    @GetMapping(path = "/requests")
    public ResponseEntity<List<PartRequestDto>> getPartRequestListByUser(
            @PathVariable(name = "userId") @Positive Long userId) {
        log.info("Private API:getting requests of userId {} ", userId);
        return new ResponseEntity<>(service.getPartRequestListByUser(userId), HttpStatus.OK);
    }

    @PatchMapping(path = "/requests/{requestId}/cancel")
    public ResponseEntity<PartRequestDto> cancelRequest(
            @PathVariable(name = "userId") @Positive Long userId,
            @PathVariable(name = "requestId") @Positive Long requestId) {
        log.info("Private API:cancelling participation requestId {} from userId {} ", requestId, userId);
        return new ResponseEntity<>(service.cancelPartRequest(userId, requestId), HttpStatus.OK);
    }

    @GetMapping(path = "/events/{eventId}/requests")
    public ResponseEntity<List<PartRequestDto>> getAllPartRequestsByEventIdFromUserId(
            @PathVariable(name = "userId") @Positive Long userId,
            @PathVariable(name = "eventId") @Positive Long eventId) {
        log.info("Private API: get participation requests for event id {} of user {}", eventId, userId);
        return new ResponseEntity<>(service.getAllPartRequestsByEventIdFromUserId(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping(path = "/events/{eventId}/requests")
    public ResponseEntity<PartRequestStatusUpdateResult> updateRequest(
            @PathVariable(name = "userId") @Positive Long userId,
            @PathVariable(name = "eventId") @Positive Long eventId,
            @RequestBody PartRequestStatusUpdateRequest dto) {
        log.info("Private API: participation requests update for event id {} of user {}", eventId, userId);
        return new ResponseEntity<>(service.updateRequest(userId, eventId, dto), HttpStatus.OK);
    }
}