package ru.practicum.ewm.app.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO категорий пользователя с сокращенным объемом данных<p>
 * Сопоставляется с модель-сущностью User<p>
 * @implNote Использование для предоставления данных<p>
 * {@see ru.practicum.ewm.app.model.User}
 */
@Builder
@Getter
public class UserOutputShortDto {

    private Long id;

    private String name;
}