package ru.practicum.ewm.stats;

import ru.practicum.ewm.common.utils.StatsAppName;
import ru.practicum.ewm.stats.dto.EndpointHitResponseDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

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
    EndpointHitResponseDto sendEndpointHit(StatsAppName statsAppName, HttpServletRequest request);

    /**
     * Получение статистики по количеству посещений <p>
     * <b>только обязательные параметры API сервиса статистики</b>
     * @param start начало интервала времени посещения (обязательный для API сервиса статистики)
     * @param end конец интервала времени посещения (обязательный для API сервиса статистики)
     */
    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end);

    /**
     * Получение статистики по количеству посещений <p>
     * @param start начало интервала времени посещения (обязательный для API сервиса статистики)
     * @param end конец интервала времени посещения (обязательный для API сервиса статистики)
     * @param uris пути запроса к API приложения, по которым проводится поиск (к этим эндпойнтам обращались пользователи)
     */
    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, String[] uris);

    /**
     * Получение статистики по количеству посещений <p>
     * @param start начало интервала времени посещения (обязательный для API сервиса статистики)
     * @param end конец интервала времени посещения (обязательный для API сервиса статистики)
     * @param unique признак поиска только по уникальным ip-адресам
     */
    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, Boolean unique);

    /**
     * Получение статистики по количеству посещений <p>
     * @param start начало интервала времени посещения (обязательный для API сервиса статистики)
     * @param end конец интервала времени посещения (обязательный для API сервиса статистики)
     * @param uris пути запроса к API приложения, по которым проводится поиск (к этим эндпойнтам обращались пользователи)
     * @param unique признак поиска только по уникальным ip-адресам
     */
    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique);
}
