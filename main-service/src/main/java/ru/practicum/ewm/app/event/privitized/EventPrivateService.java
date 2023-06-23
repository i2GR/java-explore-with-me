package ru.practicum.ewm.app.event.privitized;

import java.util.List;

import ru.practicum.ewm.app.dto.EventInputDto;
import ru.practicum.ewm.app.dto.EventOutputFullDto;
import ru.practicum.ewm.app.dto.EventOutputShortDto;

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
}