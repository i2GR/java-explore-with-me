package ru.practicum.ewm.app.dto.subscription;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.ewm.app.dto.user.UserOutputShortDto;

/**
 * DTO подписки пользователя на другого пользователя<p>
 * Сопоставляется с модель-сущностью Subscription<p>
 * @implNote Использование для предоставления данных<p>
 * {@see ru.practicum.ewm.app.model.user.Subscription}
 */
@Builder
@Getter
public class SubscriptionOutputDto {

    private Long id;

    private UserOutputShortDto follower;

    private UserOutputShortDto leader;
}