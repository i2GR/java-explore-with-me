package ru.practicum.ewm.app.compilation.admin;

import ru.practicum.ewm.app.dto.CompilationOutputDto;
import ru.practicum.ewm.app.dto.CompilationInputDto;

/**
 * Интерфейс сервис-слоя функционала администрирования подборками событий (Admin API)
 * спецификация этапа 2 дипломного проекта
 */
public interface CompilationAdminService {

    /**
     * Добавление новой подборки
     * @param dto тело dto категории (получено с контроллера)
     * @return полный DTO созданной сущности
     * @implNote подборка может не содержать событий
     */
    CompilationOutputDto addCompilation(CompilationInputDto dto);

    /**
     * Изменение существующей подборками
     * @param id идентификатор подборки
     * @param dto тело DTO подборки (получено с контроллера) (возможны поля с нулевыми значениями)
     * @return полный DTO измененной категории
     */
    CompilationOutputDto updateCompilation(Long id,CompilationInputDto dto);

    /**
     * Удаление подборками по идентификатору
     * @param id идентификатор в хранилище (БД)
     */
    void deleteCompilation(Long id);
}