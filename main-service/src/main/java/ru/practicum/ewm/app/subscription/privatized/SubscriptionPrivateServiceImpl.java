package ru.practicum.ewm.app.subscription.privatized;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.ewm.app.dto.subscription.SubscriptionOutputDto;
import ru.practicum.ewm.app.event.EventRepository;
import ru.practicum.ewm.app.subscription.SubscriptionRepository;
import ru.practicum.ewm.app.subscription.model.Subscription;
import ru.practicum.ewm.app.subscription.model.SubscriptionDtoMapper;
import ru.practicum.ewm.app.user.UserRepository;
import ru.practicum.ewm.app.user.model.User;
import ru.practicum.ewm.common.exception.ConditionViolationException;
import ru.practicum.ewm.common.exception.NotFoundException;

import static java.lang.String.format;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionPrivateServiceImpl implements SubscriptionPrivateService {

    private final SubscriptionRepository subscriptionRepo;

    private final UserRepository userRepo;

    private final SubscriptionDtoMapper mapper;

    @Transactional
    @Override
    public SubscriptionOutputDto subscribeByFollowerToLeader(Long followerId, Long leaderId) {
        log.info("Private Service: adding subscribe from user id {} to user id{}", followerId, leaderId);
        Map<Long, User> users = checkAndRetrieveUsers(followerId, leaderId);
        Subscription subscription = Subscription.builder()
                .follower(users.get(followerId))
                .leader(users.get(leaderId))
                .created(LocalDateTime.now())
                .build();
        return mapper.toDto(saveOrThrowConflictException(subscription));
    }

    @Override
    public List<SubscriptionOutputDto> getAllSubscriptionsByFollower(Long followerId, Long from, Integer size) {
        log.info("Private Service: getting Subscriptions of user id {} from {} size {}", followerId, from, size);
        userRepo.getUserOrThrowNotFound(followerId);
        Pageable page = PageRequest.of((int) (from / size), size, Sort.by("leader.name").descending());
        return subscriptionRepo.findAllByFollowerId(followerId, page)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void unsubscribe(Long followerId, Long subscriptionId) {
        log.info("Private Service: cancelling subscription id {} from user id {}", subscriptionId,followerId);
        userRepo.getUserOrThrowNotFound(followerId);
        try {
            subscriptionRepo.deleteByFollowerIdAndId(followerId, subscriptionId);
        } catch (DataIntegrityViolationException dive) {
            throw new NotFoundException(String.format("User with id=%d has no given subscription", followerId));
        }
    }

    private Subscription saveOrThrowConflictException(Subscription subscription) {
        try {
            return subscriptionRepo.save(subscription);
        } catch (DataIntegrityViolationException dive) {
            throw new ConditionViolationException("category name clashes with present other category name");
        }
    }

    private Map<Long, User> checkAndRetrieveUsers(Long followerId, Long leaderId) {
        Map<Long, User> users = userRepo.findAllById(List.of(followerId, leaderId))
                .stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        checkUserExists(followerId, users, "Follower");
        checkUserExists(followerId, users, "Leader");
        return users;
    }

    private void checkUserExists(Long id, Map<Long, User> users, String userRef) {
        if (users.get(id) == null) {
            throw new NotFoundException(format("User (%s) with id=%d was not found", userRef, id));
        }
    }
}
