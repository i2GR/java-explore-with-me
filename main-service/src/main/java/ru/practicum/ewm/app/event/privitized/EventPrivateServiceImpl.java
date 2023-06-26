package ru.practicum.ewm.app.event.privitized;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.ewm.app.dto.event.EventOutputShortDtoByFollower;
import ru.practicum.ewm.app.subscription.SubscriptionRepository;
import ru.practicum.ewm.app.subscription.model.Subscription;
import ru.practicum.ewm.common.exception.BadRequestException;
import ru.practicum.ewm.common.exception.ConditionViolationException;
import ru.practicum.ewm.common.utils.DateTimeDeserializer;
import ru.practicum.ewm.common.utils.EventState;
import ru.practicum.ewm.common.utils.EventStateAction;
import ru.practicum.ewm.common.utils.PartRequestStatus;

import ru.practicum.ewm.app.category.CategoryRepository;
import ru.practicum.ewm.app.category.model.Category;
import ru.practicum.ewm.app.dto.event.EventOutputFullDto;
import ru.practicum.ewm.app.dto.event.EventInputDto;
import ru.practicum.ewm.app.dto.event.EventOutputShortDto;
import ru.practicum.ewm.app.event.AbstractEventCommonService;
import ru.practicum.ewm.app.event.EventRepository;
import ru.practicum.ewm.app.event.model.Event;
import ru.practicum.ewm.app.event.model.EventDtoMapper;
import ru.practicum.ewm.app.partrequest.PartRequestRepository;
import ru.practicum.ewm.app.user.UserRepository;
import ru.practicum.ewm.app.user.model.User;

import static ru.practicum.ewm.common.utils.Constants.DATE_TIME_FORMATTER;
import static ru.practicum.ewm.common.utils.Constants.HOUR_OF_SEC;
import static ru.practicum.ewm.common.utils.Constants.CHECK_EVENT_TIME;
import static ru.practicum.ewm.common.utils.Constants.MIN_TIME;
import static ru.practicum.ewm.common.utils.EventState.CANCELED;
import static ru.practicum.ewm.common.utils.EventState.PENDING;

import ru.practicum.ewm.stats.StatsHttpClient;

@Slf4j
@Service
@Transactional(readOnly = true)
public class EventPrivateServiceImpl extends AbstractEventCommonService implements EventPrivateService {

    private final UserRepository userRepo;

    private final SubscriptionRepository subscriptionRepo;

    EventPrivateServiceImpl(EventRepository eventRepo,
                            CategoryRepository categoryRepo,
                            UserRepository userRepository,
                            PartRequestRepository requestRepo,
                            EventDtoMapper eventMapper,
                            StatsHttpClient statsClient,
                            DateTimeDeserializer timeDeserializer,
                            SubscriptionRepository subscriptionRepo) {
        super(eventRepo, categoryRepo, requestRepo, eventMapper, statsClient, timeDeserializer);
        this.userRepo = userRepository;
        this.subscriptionRepo = subscriptionRepo;
    }

    @Transactional
    @Override
    public EventOutputFullDto addEventByUser(Long userId, EventInputDto dto) {
        log.info("Private Service: adding Event {} in category {} by user {} on date {}", dto.getTitle(), dto.getCategory(), userId, dto.getEventDate());
        checkAndSetMissedDefaults(dto);
        User initiator = userRepo.getUserOrThrowNotFound(userId);
        LocalDateTime currentMoment = LocalDateTime.now();
        LocalDateTime eventDate = LocalDateTime.parse(dto.getEventDate(), DATE_TIME_FORMATTER);
        checkPermittedTimeDatesRelation(eventDate, currentMoment, 2 * HOUR_OF_SEC, CHECK_EVENT_TIME, true);
        Category category = getCategoryRepo().getCategoryOrThrowNotFound(dto.getCategory());
        Event newEvent = getEventRepo().save(getEventMapper().fromInputDto(dto, initiator, category, eventDate));
        log.info("event created id {}", newEvent.getId());
        return getEventMapper().toFullDto(newEvent);
    }

    @Override
    public List<EventOutputShortDto> getAllEventsByUser(Long userId, Long from, Integer size) {
        log.info("Private Service: getting events of user {} from {} size {}", userId, from, size);
        Pageable pageable = PageRequest.of((int) (from / size), size, Sort.by("eventDate").descending());
        List<Event> eventList = getEventRepo().findAllByInitiatorId(userId, pageable);
        assignConfirmedPartRequests(eventList);
        assignViewsToEventList(MIN_TIME, MIN_TIME.minusYears(100), eventList);
        return prepareSortedShortDtoList(eventList, null);
    }

    @Override
    public EventOutputFullDto getEventByIdAndUserId(Long userId, Long eventId) {
        log.info("Private Service: getting event by id {} And by user id {}", eventId, userId);
        Event event = getEventRepo().getEventOrThrowNotFound(eventId);
        checkUserAccessForEvent(userId, event);
        event.setConfirmedRequests(getRequestRepo().countByEventIdAndStatus(eventId, PartRequestStatus.CONFIRMED));
        assignViewsToEvent(event);
        return getEventMapper().toFullDto(event);
    }

    @Transactional
    @Override
    public EventOutputFullDto updateEvent(Long userId, Long eventId, EventInputDto dto) {
        log.info("Private Service: updating event id {} by user id {}", eventId, userId);
        Event event = getEventRepo().getEventOrThrowNotFound(eventId);

        checkUserAccessForEvent(userId, event);
        checkPermittedTimeDatesRelation(event.getEventDate(), LocalDateTime.now(), 2 * HOUR_OF_SEC, CHECK_EVENT_TIME, false);
        checkAndUpdateEventState(event, dto);
        Category category = checkAndUpdateCategory(dto, event);

        LocalDateTime newEventDate = checkAndRetrieveNewEventDate(event.getEventDate(), dto);

        getEventMapper().update(dto, category, event, newEventDate);
        event = getEventRepo().save(event);
        event.setConfirmedRequests(getRequestRepo().countByEventIdAndStatus(eventId, PartRequestStatus.CONFIRMED));
        assignViewsToEvent(event);

        log.info("event id {} updated", eventId);
        return getEventMapper().toFullDto(event);
    }

    @Override
    public List<EventOutputShortDtoByFollower> getAllEventsOfSubscribedLeaders(Long followerId, Long from, Integer size) {
        log.info("Private Service: getting events by followed users by follower user id {}", followerId);
        userRepo.getUserOrThrowNotFound(followerId);
        Pageable page = PageRequest.of((int) (from / size), size, Sort.by("eventDate").ascending());
        List<Event> eventList = getEventRepo().getEventsByFollower(followerId, page);
        List<Subscription> subscriptions = new ArrayList<>();
        assignConfirmedPartRequests(eventList);
        assignViewsToEventList(MIN_TIME, MIN_TIME.minusYears(100), eventList);
        Set<Long> leaderIdsWithUpdatedEvents = checkLeadersIdsWithUpdatesEvents(followerId, eventList, subscriptions);
        Map<Long, Long> leaderIdToSubscriptionId = subscriptions
                .stream()
                .collect(Collectors.toMap(s -> s.getLeader().getId(), Subscription::getId));

        //возвращение списка событий с пометкой, что событие является новым для пользователя-подписчика
        //критерий: дата последнего просмотра подписок подписчиком и даты опубликования события
        return eventList
                .stream()
                .map(e -> getEventMapper().toDtoForFollower(
                        e, leaderIdsWithUpdatedEvents.contains(e.getInitiator().getId()),
                        leaderIdToSubscriptionId.get(e.getInitiator().getId())))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventOutputFullDto getEventOfLeaderByFollower(Long eventId, Long leaderId, Long followerId) {
        log.info("Private Service: getting event id {} by follower id {} of leader id {}", eventId, followerId, leaderId);
        Event event = getEventRepo().getEventOrThrowNotFound(eventId);
        Subscription subscription = checkANdRetrieveSubscription(followerId, leaderId);
        subscription.setLastView(LocalDateTime.now());
        subscriptionRepo.save(subscription);
        return getEventMapper().toFullDto(event);
    }

    private Subscription checkANdRetrieveSubscription(Long followerId, Long leaderId) {
        List<Subscription> subs = subscriptionRepo.findAllByFollowerIdAndLeaderIdIn(followerId, List.of(leaderId));
        User follower = userRepo.getUserOrThrowNotFound(followerId);
        User leader = userRepo.getUserOrThrowNotFound(leaderId);
        //установка последнего просмотра
        if (subs.size() == 1) {
            Subscription subscription = subs.get(0);
            if (subscription.getLeader().getId().equals(leader.getId())
                    &&
                    subscription.getFollower().getId().equals(follower.getId()))
                return subscription;
        }
        throw new BadRequestException("error getting subscription data");
    }

    private void checkAndSetMissedDefaults(EventInputDto dto) {
        dto.setPaid(dto.getPaid() != null && dto.getPaid());
        dto.setRequestModeration(dto.getRequestModeration() == null || dto.getRequestModeration());
        dto.setParticipantLimit(dto.getParticipantLimit() == null ? 0 : dto.getParticipantLimit());
    }

    private void checkUserAccessForEvent(Long userId, Event event) {
        if (!userId.equals(event.getInitiator().getId())) {
            throw new BadRequestException("user has not access to event");
        }
    }

    private void checkAndUpdateEventState(Event event, EventInputDto dto) {

        EventState state = event.getState();
        if (!(state == PENDING || state == CANCELED)) {
            throw new ConditionViolationException(format("Cannot update event with %s status", state));
        }
        EventStateAction action = dto.getStateAction();
        if (action != null) event.setState(dto.getStateAction().getResult());
    }

    private Set<Long> checkLeadersIdsWithUpdatesEvents(Long followerId,
                                                       List<Event> eventList,
                                                       List<Subscription> subscriptions) {
        List<Long> leaderIds = eventList
                .stream()
                .map(e -> e.getInitiator().getId())
                .collect(Collectors.toList());
        subscriptions.addAll(subscriptionRepo.findAllByFollowerIdAndLeaderIdIn(followerId, leaderIds));

        Map<Long, LocalDateTime> subscriptionLeaderIdMapViewDate = subscriptions
                .stream()
                .collect(Collectors.toMap(s -> s.getLeader().getId(), Subscription::getLastView));

        return eventList
                .stream()
                .filter(e -> subscriptionLeaderIdMapViewDate.get(e.getInitiator().getId()).isBefore(e.getPublishedOn()))
                .map(e -> e.getInitiator().getId())
                .collect(Collectors.toSet());
    }
}