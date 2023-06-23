package ru.practicum.ewm.app.event.admin;

import java.util.List;

import ru.practicum.ewm.app.dto.EventInputDto;
import ru.practicum.ewm.app.dto.EventOutputFullDto;
import ru.practicum.ewm.common.utils.EventState;

/**
 * Интерфейс сервис-слоя функционала администрирования категориями событий (Admin API)
 * спецификация этапа 2 дипломного проекта
 */
public interface EventAdminService {

    /**
     * Поиск событий по критериям<p>
     * @param userIds список id пользователей, чьи события нужно найти
     * @param states список состояний в которых находятся искомые события
     * @param categoryIds список id категорий в которых будет вестись поиск
     * @param rangeStart дата и время не раньше которых должно произойти событие
     * @param rangeEnd дата и время не позже которых должно произойти событие
     * @param from параметр пагинации - индекс первого элемента (нумерация начинается с 0)
     * @param size параметр пагинации - количество элементов для отображения
     * @return список событий по критериям (допускается получение пустого списка)
     */
    List<EventOutputFullDto> getEventListByConditions(List<Long> userIds,
                                                      List<EventState> states,
                                                      List<Long> categoryIds,
                                                      String rangeStart,
                                                      String rangeEnd,
                                                      Long from,
                                                      Integer size);

    /**
     * Обновление данных любого события администратором. Обратите внимание<p>
     * @param dto тело категории (получено с контроллера) (возможны поля с нулевыми значениями)
     * @return измененная категория
     * @implNote Валидация данных не проводится (не требуется по ТЗ)
     */
    EventOutputFullDto updateEvent(Long id, EventInputDto dto);
}