package ru.practicum.ewm.app.dto.user;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.ewm.common.utils.Constants;
import ru.practicum.ewm.common.validation.OnCreate;
import ru.practicum.ewm.common.validation.OnUpdate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * DTO категорий пользователя<p>
 * Сопоставляется с модель-сущностью User<p>
 * @implNote Использование для получения и предоставления данных<p>
 * а также для модификации данных<p>
 * {@see ru.practicum.ewm.app.model.User}
 */
@Builder
@Getter
public class UserCommonFullDto {

    @NotNull(message = "user id required", groups = {OnUpdate.class})
    private Long id;

    @NotBlank(message = "user email must not be null", groups = {OnCreate.class, OnUpdate.class})
    @Size(min = 6, max = 254, groups = {OnCreate.class, OnUpdate.class})
    @Email(message = "user email breaks format", regexp = Constants.EMAIL_REGEXP, groups = {OnCreate.class, OnUpdate.class})
    private String email;

    @NotBlank(message = "user name required", groups = {OnCreate.class, OnUpdate.class})
    @Size(min = 2, max = 250, groups = {OnCreate.class, OnUpdate.class})
    private String name;

    @Builder.Default
    private boolean subscriptionsAllowed = false;
}