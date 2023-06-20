package ru.practicum.ewm.app.event.privitized;

import java.time.LocalDateTime;
import java.util.List;
import static java.lang.String.format;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.ewm.common.exception.BadRequestException;
import ru.practicum.ewm.common.exception.ConditionViolationException;
import ru.practicum.ewm.common.utils.DateTimeDeserializer;
import ru.practicum.ewm.common.utils.EventState;
import ru.practicum.ewm.common.utils.EventStateAction;
import ru.practicum.ewm.common.utils.PartRequestStatus;

import ru.practicum.ewm.app.category.CategoryRepository;
import ru.practicum.ewm.app.category.model.Category;
import ru.practicum.ewm.app.dto.EventOutputFullDto;
import ru.practicum.ewm.app.dto.EventInputDto;
import ru.practicum.ewm.app.dto.EventOutputShortDto;
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

    EventPrivateServiceImpl(EventRepository eventRepo,
                            CategoryRepository categoryRepo,
                            UserRepository userRepository,
                            PartRequestRepository requestRepo,
                            EventDtoMapper eventMapper,
                            StatsHttpClient statsClient,
                            DateTimeDeserializer timeDeserializer) {
        super(eventRepo, categoryRepo, requestRepo, eventMapper, statsClient, timeDeserializer);
        this.userRepo = userRepository;
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

        LocalDateTime persistedEventDate = event.getEventDate();
        LocalDateTime newEventDate = parseOrSetDefaultTime(dto.getEventDate(), persistedEventDate, "eventDate");
        if (newEventDate != persistedEventDate) {
            checkPermittedTimeDatesRelation(newEventDate, LocalDateTime.now(), 0, CHECK_EVENT_TIME, true);
        }
        checkPermittedTimeDatesRelation(persistedEventDate, LocalDateTime.now(), 2 * HOUR_OF_SEC, CHECK_EVENT_TIME, false);



        getEventMapper().update(dto, category, event, newEventDate);
        event = getEventRepo().save(event);
        event.setConfirmedRequests(getRequestRepo().countByEventIdAndStatus(eventId, PartRequestStatus.CONFIRMED));
        assignViewsToEvent(event);
        log.info("event id {} updated", eventId);
        return getEventMapper().toFullDto(event);
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
}