package ru.practicum.ewm.app.compilation.publish;

import ru.practicum.ewm.app.dto.compilation.CompilationOutputDto;

import java.util.List;

/**
 * Интерфейс сервис-слоя публичного функционала относительно подборок событий (публичный API)
 * спецификация этапа 2 дипломного проекта
 */
public interface CompilationPublicService {

    /**
     * получение подборки событий по идентификаторы
     * @param id идентификатор в репозитории (БД)
     * @return подборка событий по идентификатору
     */
    CompilationOutputDto getCompilationById(Long id);

    /**
     * получение списка Подборок событий
     * @param pinned поиск только закрепленных/не закрепленных подборок
     * @param from параметр пагинации - индекс первого элемента (нумерация начинается с 0)
     * @param size параметр пагинации - количество элементов для отображения
     * @return список подборок событий по критериям (допускается получение пустого списка)
     */
    List<CompilationOutputDto> getCompilationListByConditions(Boolean pinned, Long from, Integer size);
}
