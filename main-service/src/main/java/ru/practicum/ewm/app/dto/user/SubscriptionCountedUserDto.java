package ru.practicum.ewm.app.dto.user;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO категорий пользователя с сокращенным объемом данных и количеством подписчиков<p>
 * Сопоставляется с модель-сущностью User<p>
 * @implNote Использование для предоставления данных<p>
 * {@see ru.practicum.ewm.app.model.User}
 */
@Builder
@Getter
public class SubscriptionCountedUserDto {

    private UserOutputShortDto user;

    private Long subscriptionCount;
}