package ru.practicum.ewm.app.user.admin;

import java.util.List;

import ru.practicum.ewm.app.dto.UserCommonFullDto;

/**
 * Интерфейс сервис-слоя функционала администрирования пользователей
 * спецификация этапа 2 дипломного проекта
 */
public interface UserAdminService {

    /**
     * Добавление нового пользователя<p>
     * @implNote имя категории должно быть уникальным (при нарушении выбросится исключение)
     * @param dto тело категории (получено с контроллера)
     * @return Созданная категория
     */
    UserCommonFullDto addUser(UserCommonFullDto dto);

    /**
     * Поиск пользователя по критериям<p>
     * @param userIds список id пользователей для отображения (поиска)
     * @param from параметр пагинации - индекс первого элемента (нумерация начинается с 0)
     * @param size параметр пагинации - количество элементов для отображения
     * @return Список пользователей по критериям (допускается получение пустого списка)
     */
    List<UserCommonFullDto> getUserListByConditions(Long[] userIds, long from, int size);

    /**
     * удаление пользователя по идентификатору
     * @param id идентификатор в хранилище (БД)
     */
    void deleteUserById(Long id);
}