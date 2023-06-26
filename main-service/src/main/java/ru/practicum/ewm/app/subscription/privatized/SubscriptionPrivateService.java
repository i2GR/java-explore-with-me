package ru.practicum.ewm.app.subscription.privatized;

import ru.practicum.ewm.app.dto.subscription.SubscriptionOutputFullDto;
import ru.practicum.ewm.app.dto.subscription.SubscriptionOutputDto;

import java.util.List;

/**
 * Сервис дополнительного функционала (фичи) - подписки (subscriptions)
 */
public interface SubscriptionPrivateService {

    /**
     * Подписка пользователя на любого другого пользователя
     * @param followerId идентификатор пользователя-подписчика
     * @param leaderId идентификатор пользователя, на которого подписываются
     * @return DTO подписки
     */
    SubscriptionOutputDto subscribeByFollowerToLeader(Long followerId, Long leaderId);

    /**
     * Получение списка подписок пользователя-подписчика
     * @param followerId идентификатор пользователя-подписчика
     * @param from параметр пагинации - индекс первого элемента (нумерация начинается с 0)
     * @param size параметр пагинации - количество элементов для отображения
     * @return список DTO подписок
     */
    List<SubscriptionOutputDto> getAllSubscriptionsByFollower(Long followerId, Long from, Integer size);

    /**
     * Отмена пользовательсеой подписки на другого пользователя<p>
     * @param followerId идентификатор пользователя-подписчика
     * @param subscriptionId идентификатор подписки
     */
    void unsubscribe(Long followerId, Long subscriptionId);
}
