package ru.practicum.ewm.common.exception;

/**
 * исключение при ошибке в запросе http-клиента к сервису статистики<p>
 * ТРЕБУЕТСЯ уточнить требования в ТЗ этапа 2 диплома (возможно разделить) на два исключения для POST /hit GET/stats эндпойнтов
 * выбрасывается http-клиентом
 */
public class StatsClientException extends RuntimeException {

    /**
     * @param message : передается информация об причине исключения
     *                (при наличии, или сообщение исключений классов/методов JAVA)
     */
    public StatsClientException(String message) {
        super(message);
    }
}