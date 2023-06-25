package ru.practicum.ewm.app.category.publish;

import java.util.List;

import ru.practicum.ewm.app.dto.CategoryDto;

/**
 * Интерфейс сервис-слоя публичного функционала работы с категориями событий (Admin API)
 * спецификация этапа 2 дипломного проекта
 */
public interface CategoryPublicService {

    /**
     * Получение информации о категории по ее идентификатору<p>
     * @param id идентификатор категории
     * @return категория по заданному идентификатору
     */
    CategoryDto getCategory(Long id);

    /**
     * Получение списка категорий
     * @param from параметр пагинации - индекс первого элемента (нумерация начинается с 0)
     * @param size параметр пагинации - количество элементов для отображения
     * @return список подборок событий по критериям (допускается получение пустого списка)
     */
    List<CategoryDto> getCategoryListByConditions(Long from, Integer size);
}