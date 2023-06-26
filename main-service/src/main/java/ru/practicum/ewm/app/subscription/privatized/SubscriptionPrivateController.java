package ru.practicum.ewm.app.subscription.privatized;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.app.dto.subscription.SubscriptionOutputDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users/{followerId}/subscriptions")
@Validated
public class SubscriptionPrivateController {

    private final SubscriptionPrivateService service;

    @PostMapping(path = "/{leaderId}")
    public ResponseEntity<SubscriptionOutputDto> subscribeByFollowerToLeader(
            @PathVariable(name = "followerId") @Positive Long followerId,
            @PathVariable (name = "leaderId") @Positive Long leaderId) {
        log.info("Private API: subscription by user id (follower) {} to user id (leader) {}", followerId, leaderId);
        return new ResponseEntity<>(service.subscribeByFollowerToLeader(followerId, leaderId), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionOutputDto>> getSubscriptions(
            @PathVariable(name = "followerId") @Positive Long followerId,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Long from,
            @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Private API: getting subscription list of user id (follower) {}", followerId);
        return new ResponseEntity<>(service.getAllSubscriptionsByFollower(followerId, from, size), HttpStatus.OK);
    }

    @DeleteMapping(path = "/{subscriptionId}/cancel")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void unsubscribe(
            @PathVariable(name = "followerId") @Positive Long followerId,
            @PathVariable(name = "subscriptionId") @Positive Long subscriptionId) {
        log.info("Private API: unsubscribing by user id (follower) {} to subscription id {}", followerId, subscriptionId);
        service.unsubscribe(followerId, subscriptionId);
    }
}