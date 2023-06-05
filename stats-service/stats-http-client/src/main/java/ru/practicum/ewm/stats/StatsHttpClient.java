package ru.practicum.ewm.stats;

import org.springframework.http.ResponseEntity;
import ru.practicum.ewm.common.utils.StatsAppName;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * HTTP-клиент сервиса статистики
 */
public interface StatsHttpClient {

    /**
     * Перенаправление информации о вызове API на сервер статистики
     * @param statsAppName условное имя приложения
     * @param request HttpServletRequest для получения данных необходимой статистики (ip адрес, путь запроса)
     * @return DTO со статусом ответа от сервера статистики.
     */
    ResponseEntity<Object> sendEndpointHit(String statsAppName, HttpServletRequest request);

    /**
     * Получение статистики по количеству посещений <p>
     * <b>только обязательные параметры API сервиса статистики</b>
     * @param start начало интервала времени посещения (обязательный для API сервиса статистики)
     * @param end конец интервала времени посещения (обязательный для API сервиса статистики)
     */
    ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end);

    /**
     * Получение статистики по количеству посещений <p>
     * @param start начало интервала времени посещения (обязательный для API сервиса статистики)
     * @param end конец интервала времени посещения (обязательный для API сервиса статистики)
     * @param uris пути запроса к API приложения, по которым проводится поиск (к этим эндпойнтам обращались пользователи)
     */
    ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, String[] uris);

    /**
     * Получение статистики по количеству посещений <p>
     * @param start начало интервала времени посещения (обязательный для API сервиса статистики)
     * @param end конец интервала времени посещения (обязательный для API сервиса статистики)
     * @param unique признак поиска только по уникальным ip-адресам
     */
    ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, Boolean unique);

    /**
     * Получение статистики по количеству посещений <p>
     * @param start начало интервала времени посещения (обязательный для API сервиса статистики)
     * @param end конец интервала времени посещения (обязательный для API сервиса статистики)
     * @param uris пути запроса к API приложения, по которым проводится поиск (к этим эндпойнтам обращались пользователи)
     * @param unique признак поиска только по уникальным ip-адресам
     */
    ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique);
}
