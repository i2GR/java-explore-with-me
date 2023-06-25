package ru.practicum.ewm.common.exception;

/**
 * Исключение при ошибке в запросе http-клиента к сервису статистики<p>
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