package ru.practicum.ewm.app.category.admin;

import ru.practicum.ewm.app.dto.category.CategoryDto;

/**
 * Интерфейс сервис-слоя функционала администрирования категориями событий (Admin API)
 * спецификация этапа 2 дипломного проекта
 */
public interface CategoryAdminService {

    /**
     * Добавление новой категории
     * @param dto тело DTO категории (получено с контроллера)
     * @return полный DTO созданной сущности
     * @implNote имя категории должно быть уникальным (при нарушении выбросится исключение)
     */
    CategoryDto addCategory(CategoryDto dto);

    /**
     * Изменение существующей категории
     * @param id идентификатор категории
     * @param dto тело DTO категории (получено с контроллера) (возможны поля с нулевыми значениями)
     * @return полный DTO измененной категории
     */
    CategoryDto updateCategory(Long id, CategoryDto dto);

    /**
     * Удаление категории по идентификатору
     * @param id идентификатор в хранилище (БД)
     */
    void deleteCategory(Long id);
}