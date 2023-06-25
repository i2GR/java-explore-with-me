package ru.practicum.ewm.common.exception;

import ru.practicum.ewm.common.utils.ExceptionReason;

/**
 * исключение при операциях в слое хранилища при отсутствии в хранилище сущности (целиком или по идентификатору)
 * выбрасывается в специально указанных случаях согласно "бизнес-логике"
 */
public class NotFoundException extends EwmAPIException {

    /**
     * @param message : передается информация об причине исключения
     *                (при наличии, или сообщение исключений классов/методов JAVA)
     */
    public NotFoundException(String message) {
        super(ExceptionReason.OBJECT_NOT_FOUND, message);
    }
}