package ru.practicum.ewm.app.event.publish;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import ru.practicum.ewm.app.dto.EventOutputFullDto;
import ru.practicum.ewm.app.dto.EventOutputShortDto;

/**
 * Интерфейс сервис-слоя публичного функционала относительно событий
 * спецификация этапа 2 дипломного проекта
 */
public interface EventPublicService {

    /**
     * Поиск событий по критериям<p>
     * @param query текст для поиска в содержимом аннотации и подробном описании события
     * @param categoryIds список id категорий в которых будет вестись поиск
     * @param paid поиск только платных/бесплатных событий
     * @param rangeStart дата и время не раньше которых должно произойти событие
     * @param rangeEnd дата и время не позже которых должно произойти событие
     * @param onlyAvailable только события у которых не исчерпан лимит запросов на участие
     * @param sort параметр сортировки (должен соотноситься с EventSortType)
     * @param from параметр пагинации - индекс первого элемента (нумерация начинается с 0)
     * @param size параметр пагинации - количество элементов для отображения
     * @param request Http-request для сбора статистики
     * @return список событий по критериям (допускается получение пустого списка)
     * @implNote - в выдаче должны быть только опубликованные события<p>
     * - текстовый поиск (по аннотации и подробному описанию) должен быть без учета регистра букв<p>
     * - если в запросе не указан диапазон дат [rangeStart-rangeEnd], то нужно выгружать события, которые произойдут позже текущей даты и времени<p>
     * - информация о каждом событии должна включать в себя количество просмотров и количество уже одобренных заявок на участие<p>
     * - информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики<p>
     */
    List<EventOutputShortDto> getEventListByConditions(String query,
                                                       List<Long> categoryIds,
                                                       Boolean paid,
                                                       String rangeStart,
                                                       String rangeEnd,
                                                       Boolean onlyAvailable,
                                                       String sort,
                                                       long from,
                                                       int size,
                                                       HttpServletRequest request);
    /**
     * Получение полной информации о событии по идентификатору
     * @param eventId идентификатор события
     * @return Полный DTO события в случае, если событие принадлежит пользователю
     * @implNote событие должно быть опубликовано<p>
     * Информация о событии должна включать в себя количество просмотров и количество подтвержденных запросов<p>
     * Информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
     */
    EventOutputFullDto getEventById(Long eventId, HttpServletRequest request);
}