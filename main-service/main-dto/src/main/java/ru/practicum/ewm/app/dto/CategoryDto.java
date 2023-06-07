package ru.practicum.ewm.app.dto;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.ewm.common.validation.OnCreate;
import ru.practicum.ewm.common.validation.OnUpdate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * DTO категорий событий<p>
 * Сопоставляется с модель-сущностью Category<p>
 * @implNote Использование для получения и предоставления данных<p>
 * {@see ru.practicum.ewm.app.model.Category}
 */
@Builder
@Getter
public class CategoryDto {

    private Long id;

    @NotBlank(message = "category name cannot be blank", groups = {OnCreate.class, OnUpdate.class})
    @Size(min = 1, max = 50, groups = {OnCreate.class, OnUpdate.class})
    private String name;
}