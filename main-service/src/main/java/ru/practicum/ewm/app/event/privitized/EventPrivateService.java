package ru.practicum.ewm.app.event.privitized;

import java.util.List;

import ru.practicum.ewm.app.dto.event.EventInputDto;
import ru.practicum.ewm.app.dto.event.EventOutputFullDto;
import ru.practicum.ewm.app.dto.event.EventOutputShortDto;

/**
 * Интерфейс сервис-слоя функционала относительно событий (приватный API)
 * спецификация этапа 2 дипломного проекта
 */
public interface EventPrivateService {

    /**
     * Добавление нового события пользователем
     * @param userId идентификатор пользователя
     * @param dto тело события (получено с контроллера)
     * @return созданное событие
    */
    EventOutputFullDto addEventByUser(Long userId, EventInputDto dto);

    /**
     * Получение событий, добавленных текущим пользователем
     * @param userId идентификатор пользователя
     * @param from параметр пагинации - индекс первого элемента (нумерация начинается с 0)
     * @param size параметр пагинации - количество элементов для отображения
     * @return Список событий.<p>
     * В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список
     */
    List<EventOutputShortDto> getAllEventsByUser(Long userId, Long from, Integer size);

    /**
     * Получение полной информации о событии по идентификатору (добавленного текущим пользователем)
     * @param userId идентификатор пользователя
     * @param eventId идентификатор события
     * @return Полный DTO события в случае, если событие принадлежит пользователю
     */
    EventOutputFullDto getEventByIdAndUserId(Long userId, Long eventId);

    /**
     * Изменение существующего события текущим пользователем-автором<p>
     * @param userId идентификатор пользователя
     * @param eventId идентификатор события
     * @param dto тело события (получено с контроллера) (возможны поля с нулевыми значениями)
     * @return измененное событие
     */
    EventOutputFullDto updateEvent(Long userId, Long eventId, EventInputDto dto);

    /**
     * Получение текущим пользователем-подписчиком списка событий по всем пользователям, на которые есть подписка <p>
     *
     * @param followerId идентификатор пользователя-подписчика
     * @param from       параметр пагинации - индекс первого элемента (нумерация начинается с 0)
     * @param size       параметр пагинации - количество элементов для отображения
     * @return список всех событий от пользователей, на которых есть подписка измененное событие<p>
     * В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список<p>
     * @implNote должен быть получен список актуальных опубликованных событий:<p>
     * - не менее чем за два часа от текущего времени<p>
     * - опубликованные<p>
     * - доступные для участия в них<p>
     */
    public List<EventOutputShortDto> getAllEventsOfSubscribedLeaders(Long followerId, Long from, Integer size);

}