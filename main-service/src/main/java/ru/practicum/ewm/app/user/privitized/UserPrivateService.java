package ru.practicum.ewm.app.user.privitized;

import ru.practicum.ewm.app.dto.user.SubscriptionCountedUserDto;
import ru.practicum.ewm.app.dto.user.UserCommonFullDto;

import java.util.List;

/**
 * Интерфейс сервис-слоя функционала администрирования пользователей
 * спецификация этапа 2 дипломного проекта
 */
public interface UserPrivateService {

    /**
     * Получение информации о другом пользователе по идентификатору<p>
     * @param userId id пользователя, который осуществляет поиск
     * @param anotherUserId id пользователя для отображения (поиска)
     * @return DTO пользователя c числом подписчиков
     */
    SubscriptionCountedUserDto getUser(Long userId, Long anotherUserId);

    /**
     * Поиск пользователя по критериям<p>
     * @param requesterId id пользователя, делающего запрос
     * @param userIds список id пользователей для отображения (поиска)
     * @param from параметр пагинации - индекс первого элемента (нумерация начинается с 0)
     * @param size параметр пагинации - количество элементов для отображения
     * @return Список пользователей по критериям c подсчетом количества подписчиков (допускается получение пустого списка)
     */
     List<SubscriptionCountedUserDto> getUserListByConditions(Long requesterId, Long[] userIds, long from, int size);

    /**
     * Изменение статуса возможности подписки<p>
     * @param userId id пользователя который подает запрос о смене статуса подписки на себя
     * @param subjectId id пользователя, для которого меняется статус (должен совпадать с userId)
     * @param newStatus статус, разрешающий/запрешающий подписку на себя
     * @return DTO пользователя
     */
    UserCommonFullDto changeSubscriptionMode(Long userId, Long subjectId, boolean newStatus);
}