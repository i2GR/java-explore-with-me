package ru.practicum.ewm.common.exception;

import ru.practicum.ewm.common.utils.ExceptionReason;

/**
 * исключение при ошибке в http-запросе (тело или путь)
 * выбрасывается в общем случае и в случае исключений классов/методов JAVA
 */
public class BadRequestException extends EwmAPIException {

    /**
     * @param message : передается информация об причине исключения
     *                (при наличии, или сообщение исключений классов/методов JAVA)
     */
    public BadRequestException(String message) {
        super(ExceptionReason.BAD_REQUEST, message);
    }
}