package ru.practicum.ewm.app.partrequest.privatized;

import java.util.List;

import ru.practicum.ewm.app.dto.partrequest.PartRequestStatusUpdateRequest;
import ru.practicum.ewm.app.dto.partrequest.PartRequestStatusUpdateResult;
import ru.practicum.ewm.app.dto.partrequest.PartRequestDto;

/**
 * Сервис-слой для работы с запросами на участие текущего пользователя
 */
public interface PartRequestPrivateService {

    /**
     * Добавление новой заявки на участие<p>
     * @param userId идентификатор пользователя
     * @param eventId идентификатор события
     * @return Созданная категория
     * @implNote Нельзя добавить повторный запрос (Ожидается код ошибки 409)<p>
     * - Инициатор события не может добавить запрос на участие в своём событии (Ожидается код ошибки 409)<p>
     * - Нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409)<p>
     * - Если у события достигнут лимит запросов на участие - необходимо вернуть ошибку (Ожидается код ошибки 409)<p>
     * - Если для события отключена пре-модерация запросов на участие,
     * то запрос должен автоматически перейти в состояние подтвержденного<p>
     */
    PartRequestDto addPartRequest(Long userId, Long eventId);

    /**
     * Получение информации о заявках текущего пользователя на участие в чужих событиях
     * @param userId идентификатор пользователя
     * @return Список заявок текущего пользователя<p>
     * В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список
     */
    List<PartRequestDto> getPartRequestListByUser(Long userId);

    /**
     * Отмена своей заявки на участие<p>
     * @param userId идентификатор пользователя
     * @param requestId идентификатор заявки (запроса) на событие
     */
    PartRequestDto cancelPartRequest(Long userId, Long requestId);

    /**
     * Получение запросов на участие в событии по идентификатору (текущим пользователем)
     * @param userId идентификатор пользователя
     * @param eventId идентификатор события
     * @return Список запросов на участие в событии.<p>
     *     В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список
     */
    List<PartRequestDto> getAllPartRequestsByEventIdFromUserId(Long userId, Long eventId);

    /**
     * Изменение существующего запроса на участие в событии<p>
     * @param userId идентификатор пользователя
     * @param eventId идентификатор события
     * @param dto тело запроса на участие в событии (получено с контроллера) (возможны поля с нулевыми значениями)
     * @return измененный запрос на участие событие
     */
    PartRequestStatusUpdateResult updateRequest(Long userId, Long eventId, PartRequestStatusUpdateRequest dto);
}