package ru.practicum.ewm.app.dto.compilation;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.ewm.common.validation.NullOrSize;
import ru.practicum.ewm.common.validation.OnCreate;
import ru.practicum.ewm.common.validation.OnUpdate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * DTO подборок событий c данными для создания<p>
 * Сопоставляется с модель-сущностью Compilation<p>
 * @implNote Использование для получения данных<p>
 * {@see ru.practicum.ewm.app.model.Compilation}
 */
@Builder
@Getter
public class CompilationInputDto {

    @NotBlank(message = "compilation title must be not blank", groups = {OnCreate.class})
    @Size(min = 1, max = 50,
            message = "compilation title must be from 1 to 50 symbols", groups = {OnCreate.class})
    @NullOrSize(min = 1, max = 50,
            message = "compilation title must be from 1 to 50 symbols", groups = {OnUpdate.class})
    private String title;

    @Builder.Default
    private Boolean pinned = false;

    private List<Long> events;
}