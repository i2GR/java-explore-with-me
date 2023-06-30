package ru.practicum.ewm.app.event.publish;

import java.time.LocalDateTime;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import static java.lang.String.format;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.ewm.common.exception.BadRequestException;
import ru.practicum.ewm.common.exception.NotFoundException;
import ru.practicum.ewm.common.utils.DateTimeDeserializer;
import ru.practicum.ewm.common.utils.EventState;
import ru.practicum.ewm.common.utils.PartRequestStatus;
import ru.practicum.ewm.common.utils.SortingType;
import ru.practicum.ewm.app.category.CategoryRepository;
import ru.practicum.ewm.app.dto.event.EventOutputFullDto;
import ru.practicum.ewm.app.dto.event.EventOutputShortDto;
import ru.practicum.ewm.app.event.AbstractEventCommonService;
import ru.practicum.ewm.app.event.EventRepository;
import ru.practicum.ewm.app.event.model.Event;
import ru.practicum.ewm.app.event.model.EventDtoMapper;
import ru.practicum.ewm.app.partrequest.PartRequestRepository;

import ru.practicum.ewm.stats.StatsHttpClient;

import static ru.practicum.ewm.common.utils.Constants.MIN_TIME;

@Slf4j
@Service
@Transactional(readOnly = true)
public class EventPublicServiceImpl extends AbstractEventCommonService implements EventPublicService {

    EventPublicServiceImpl(EventRepository eventRepo,
                           CategoryRepository categoryRepo,
                           PartRequestRepository requestRepo,
                           EventDtoMapper eventMapper,
                           StatsHttpClient statsClient,
                           DateTimeDeserializer timeDeserializer) {
        super(eventRepo, categoryRepo, requestRepo, eventMapper, statsClient, timeDeserializer);
    }

    @Override
    public List<EventOutputShortDto> getEventListByConditions(String query,
                                                              List<Long> categoryIds,
                                                              Boolean paid,
                                                              String rangeStart,
                                                              String rangeEnd,
                                                              Boolean onlyAvailable,
                                                              String sortString,
                                                              long from,
                                                              int size,
                                                              HttpServletRequest request) {
        log.info("Public: getting Events between {} and {}", rangeStart, rangeEnd);
        LocalDateTime currentMoment = LocalDateTime.now();
        categoryIds = categoryIds == null ? List.of() : categoryIds;
        List<Boolean> paidList = paid == null ? List.of(true, false) : List.of(paid);
        LocalDateTime start = parseOrSetDefaultTime(rangeStart, currentMoment);
        LocalDateTime end = parseOrSetDefaultTime(rangeEnd, MIN_TIME.plusYears(100));
        if (start.isAfter(end)) {
            throw new BadRequestException("Incorrect request rangeStart is after rangeEnd");
        }
        SortingType sort = SortingType.fromString(sortString);
        Pageable pageable = PageRequest.of((int) (from / size), size, Sort.by("eventDate").descending());
        List<Event> eventList;
        eventList = getEventRepo().findByAnnotationOrDescriptionContainingIgnoreCaseAndCategoryIdInAndPaidInAndEventDateBetweenAndState(
                query,
                query,
                categoryIds,
                paidList,
                start,
                end,
                EventState.PUBLISHED,
                pageable);
        assignConfirmedPartRequests(eventList);
        eventList = filterAvailable(eventList, onlyAvailable);
        assignViewsToEventList(start, end, eventList);
        sendEventEndpointHitToStats(log, request);
        return prepareSortedShortDtoList(eventList, sort);
    }

    @Override
    public EventOutputFullDto getEventById(Long eventId, HttpServletRequest request) {
        log.info("Public: getting Event id {}", eventId);
        sendEventEndpointHitToStats(log, request);
        Event event = getEventRepo().getEventOrThrowNotFound(eventId);
        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException(format("Published event with id=%d was not found", eventId));
        }
        event.setConfirmedRequests(getRequestRepo().countByEventIdAndStatus(eventId, PartRequestStatus.CONFIRMED));
        assignViewsToEvent(event);
        return getEventMapper().toFullDto(event);
    }
}