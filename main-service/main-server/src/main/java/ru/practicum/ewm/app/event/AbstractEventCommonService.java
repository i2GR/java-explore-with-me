package ru.practicum.ewm.app.event;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

import ru.practicum.ewm.common.utils.DateTimeDeserializer;
import ru.practicum.ewm.common.utils.SortingType;
import ru.practicum.ewm.common.utils.StatsAppName;
import ru.practicum.ewm.app.category.CategoryRepository;
import ru.practicum.ewm.app.category.model.Category;
import ru.practicum.ewm.app.dto.EventInputDto;
import ru.practicum.ewm.app.dto.EventOutputShortDto;
import ru.practicum.ewm.app.event.model.Event;
import ru.practicum.ewm.app.event.model.EventDtoMapper;
import ru.practicum.ewm.app.partrequest.PartRequestRepository;
import ru.practicum.ewm.app.partrequest.model.ConfirmedRequestCount;

import ru.practicum.ewm.stats.StatsHttpClient;
import ru.practicum.ewm.stats.dto.ViewStatsDto;

import static ru.practicum.ewm.common.utils.Constants.MIN_TIME;

@Getter
@RequiredArgsConstructor
public abstract class AbstractEventCommonService {

    private final EventRepository eventRepo;

    private final CategoryRepository categoryRepo;
    private final PartRequestRepository requestRepo;

    private final EventDtoMapper eventMapper;

    private final StatsHttpClient statsClient;

    private final DateTimeDeserializer timeDeserializer;

    protected void sendEventEndpointHitToStats(Logger log, HttpServletRequest request) {
        log.info("Send hit to stats-service");
        statsClient.sendEndpointHit(StatsAppName.EWM_MAIN_SERVICE, request);
    }

    protected void assignViewsToEventList(LocalDateTime start, LocalDateTime end, List<Event> events) {
        Map<Long, Long> eventIdToViews = getViewsForEvents(start, end, events);
        events.forEach(e -> e.setViews(eventIdToViews.getOrDefault(e.getId(), 0L)));
    }

    protected void assignViewsToEvent(Event event) {
        Map<Long, Long> eventIdToViews = getViewsForEvents(MIN_TIME, LocalDateTime.now(), List.of(event));
        event.setViews(eventIdToViews.getOrDefault(event.getId(), 0L));
    }

    protected void assignConfirmedPartRequests(List<Event> events) {
        List<ConfirmedRequestCount> partRequestCountList = requestRepo.getConfirmedRequestCount(events);
        Map<Long, Long> eventIdToRequestCount = partRequestCountList.stream()
                .collect(Collectors.toMap(prc -> prc.getEvent().getId(), ConfirmedRequestCount::getRequestAmount));
        events.forEach(e -> e.setConfirmedRequests(eventIdToRequestCount.getOrDefault(e.getId(), 0L)));
    }

    /**
     * Выбрасывание из списка событий тех, у которых исчерпан лимит участия
     * @param events обрабатываемый список событий
     * @param available фильтрация только в случае если требуется исходным запросом
     * @return Отфильтрованный список событий. Значение присваивается списку событий, который будет ответом на запрос
     */
    protected List<Event> filterAvailable(List<Event> events, Boolean available) {
        if (available) {
            return events.stream()
                    .filter(e -> e.getParticipantLimit() > e.getConfirmedRequests())
                    .collect(Collectors.toList());
        }
        return events;
    }

    /**
     * преобразование и сортировка списка ShortDto
     * @param events список событий из БД
     * @param sortingType параметр сортировки с эндпойнта (VIEWS - сортировка по параметрам просмотра)<p>
     * по умолчанию сортировка проводится по времени событий </p>
     * @return Список ShortDto
     */
    protected List<EventOutputShortDto> prepareSortedShortDtoList(List<Event> events, SortingType sortingType) {
        if (events.size() == 0) {
            return List.of();
        }
        Stream<EventOutputShortDto> streamShortDto = events.stream()
                .map(getEventMapper()::toShortDto);
        if (sortingType == SortingType.VIEWS) {
            streamShortDto.sorted(Comparator.comparingLong(EventOutputShortDto::getViews));
        }
        return streamShortDto.collect(Collectors.toList());
    }

    /**
     * проверка времени начала события при добавлении события или обновлении
     * @param upperDateTime новое время события (при добавлении нового события или обновления
     * @param lowerDateTime опорный момент времени, относительно которого проверяют время события
     * @param difInSecs запас по времени (выражен в секундах), который должно иметь событие относительно опорного момента времени
     * @param action строка с указанием для логов, в рамках какой операции проводится проверка
     * @implNote upperDateTime получают из DTO события
     */
    protected void checkPermittedTimeDatesRelation(LocalDateTime upperDateTime,
                                                   LocalDateTime lowerDateTime,
                                                   int difInSecs,
                                                   String action,
                                                   boolean throwBadRequest) {
        timeDeserializer.checkPermittedTimeDatesRelation(upperDateTime, lowerDateTime, difInSecs, action, throwBadRequest);
    }

    protected Category checkAndUpdateCategory(EventInputDto dto, Event event) {
        Long categoryId = dto.getCategory();
        if (categoryId == null) {
            return event.getCategory();
        }
        return getCategoryRepo().getCategoryOrThrowNotFound(categoryId);
    }

    /**
     * вспомогательный метод декодирования и парсинга даты-времени и получения параметров фильтрации по времени
     * @param str строковое представление даты-времени, полученное с эндпойнта
     * @param timeDef заранее определенное значение даты-времени, используемое в случае ошибок парсинга
     * @param name название для поля времени для логирования, например "start","end","eventDate" для логирования (соответственно начало-конец диапазона фильтрации)
     * @return значение даты времени LocalDateTime
     */
    protected LocalDateTime parseOrSetDefaultTime(String str, LocalDateTime timeDef, String name) {
        return timeDeserializer.parseOrSetDefaultTime(str, timeDef, name);
    }

    /**
     * вспомогательный метод получения количества просмотров из под-сервиса статистики<p>
     *     используется http-клиент сервиса статистики
     * @param start нижняя граница интервала дат событий для поиска
     * @param end верхняя граница интервала дат событий для поиска
     * @param eventList список (List) событий, для которых осуществляется получение количества просмотров
     * @return Map представление количество просмотров:<p>
     * - ключ: идентификатор события<p>
     * - значение: количество <b>уникальных </b>просмотров
     */
    private Map<Long, Long> getViewsForEvents(LocalDateTime start, LocalDateTime end, List<Event> eventList) {
        String[] uris = eventList.stream()
                .map(e -> "/events/" + e.getId())
                .toArray(String[]::new);
        return statsClient.getStats(start, end, uris, true).stream()
                .collect(
                        Collectors.toMap(
                                vsd -> Long.valueOf(vsd.getUri().substring("/events/".length())), ViewStatsDto::getHits
                        )
                );
    }
}