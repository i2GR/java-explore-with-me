package ru.practicum.ewm.exception;

/**
 * исключение при ошибке в http-запросе (тело или путь)
 * выбрасывается в общем случае и в случае исключений классов/методов JAVA
 */
public class BadRequestException extends RuntimeException {

    /**
     * @param message : передается информация об причине исключения
     *                (при наличии, или сообщение исключений классов/методов JAVA)
     */
    public BadRequestException(String message) {
        super(message);
    }
}