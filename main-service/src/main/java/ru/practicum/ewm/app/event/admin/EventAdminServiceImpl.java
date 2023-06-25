package ru.practicum.ewm.app.event.admin;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import static java.lang.String.format;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.ewm.common.exception.BadRequestException;
import ru.practicum.ewm.common.exception.ConditionViolationException;
import ru.practicum.ewm.common.utils.DateTimeDeserializer;
import ru.practicum.ewm.common.utils.EventState;
import ru.practicum.ewm.common.utils.EventStateAction;
import ru.practicum.ewm.common.utils.PartRequestStatus;

import ru.practicum.ewm.stats.StatsHttpClient;

import ru.practicum.ewm.app.category.CategoryRepository;
import ru.practicum.ewm.app.category.model.Category;
import ru.practicum.ewm.app.event.AbstractEventCommonService;
import ru.practicum.ewm.app.dto.EventInputDto;
import ru.practicum.ewm.app.dto.EventOutputFullDto;
import ru.practicum.ewm.app.event.EventRepository;
import ru.practicum.ewm.app.event.model.Event;
import ru.practicum.ewm.app.event.model.EventDtoMapper;
import ru.practicum.ewm.app.partrequest.PartRequestRepository;

import static ru.practicum.ewm.common.utils.Constants.MIN_TIME;
import static ru.practicum.ewm.common.utils.EventState.PENDING;
import static ru.practicum.ewm.common.utils.EventState.PUBLISHED;
import static ru.practicum.ewm.common.utils.EventStateAction.PUBLISH_EVENT;
import static ru.practicum.ewm.common.utils.EventStateAction.REJECT_EVENT;

@Slf4j
@Service
@Transactional(readOnly = true)
public class EventAdminServiceImpl extends AbstractEventCommonService implements EventAdminService {

    EventAdminServiceImpl(EventRepository eventRepo,
                          CategoryRepository categoryRepo,
                          PartRequestRepository requestRepo,
                          EventDtoMapper eventMapper,
                          StatsHttpClient statsClient,
                          DateTimeDeserializer timeDeserializer) {
        super(eventRepo, categoryRepo, requestRepo, eventMapper, statsClient, timeDeserializer);
    }

    /**
     * Admin API
     */
    @Override
    public List<EventOutputFullDto> getEventListByConditions(List<Long> userIds,
                                                             List<EventState> states,
                                                             List<Long> categoryIds,
                                                             String rangeStart,
                                                             String rangeEnd,
                                                             Long from,
                                                             Integer size) {
        log.info("Admin Service: getting Events between {} and {}", rangeStart, rangeEnd);
        states = states == null ? List.of(EventState.values()) : states;
        LocalDateTime start = parseOrSetDefaultTime(rangeStart, MIN_TIME);
        LocalDateTime end = parseOrSetDefaultTime(rangeEnd, MIN_TIME.plusYears(100));
        if (start.isAfter(end)) {
            throw new BadRequestException("Incorrect request rangeStart is after rangeEnd");
        }
        log.info("Admin Service: receiving event list by paging params: from {} size {}", from, size);

        Pageable page = PageRequest.of((int) (from / size), size);
        List<Event> eventList;
        if (userIds == null && categoryIds == null) {
            eventList = getEventRepo().findAllByStateInAndEventDateBetween(states, start, end, page);
        } else if (userIds == null) {
            eventList = getEventRepo().findAllByStateInAndCategoryIdInAndEventDateBetween(states, categoryIds, start, end, page);
        } else if (categoryIds == null) {
            eventList = getEventRepo().findAllByInitiatorIdInAndStateInAndEventDateBetween(userIds, states, start, end, page);
        } else {
            eventList = getEventRepo().findAllByInitiatorIdInAndStateInAndCategoryIdInAndEventDateBetween(
                    userIds,
                    states,
                    categoryIds,
                    start,
                    end,
                    page
            );
        }
        assignConfirmedPartRequests(eventList);
        assignViewsToEventList(start, end, eventList);
        return eventList.stream()
                .map(getEventMapper()::toFullDto)
                .collect(Collectors.toList());
    }

    /**
     * Admin API
     */
    @Transactional
    @Override
    public EventOutputFullDto updateEvent(Long id, EventInputDto dto) {
        log.info("Admin Service: updating Event id {}", id);
        Event event = getEventRepo().getEventOrThrowNotFound(id);

        LocalDateTime newEventDate = checkAndRetrieveNewEventDate(event.getEventDate(), dto);
        checkPublicationAvailabilityAndSetEventState(event, dto);

        Category category = checkAndUpdateCategory(dto, event);
        getEventMapper().update(dto, category, event, newEventDate);

        event = getEventRepo().save(event);
        event.setConfirmedRequests(getRequestRepo().countByEventIdAndStatus(id, PartRequestStatus.CONFIRMED));
        assignViewsToEvent(event);

        return getEventMapper().toFullDto(event);
    }

    private void checkPublicationAvailabilityAndSetEventState(Event event, EventInputDto dto) {
        EventStateAction action = dto.getStateAction();
        if (action == null) return;
        EventState state = event.getState();
        if (
                (action == PUBLISH_EVENT && state != PENDING)
                        ||
                        (action == REJECT_EVENT && state == PUBLISHED)) {
            throw new ConditionViolationException(
                    format("Cannot publish the event because it's not in the right state: %s", event.getState())
            );
        }
        event.setState(action.getResult());
    }
}