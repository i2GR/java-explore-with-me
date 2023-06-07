package ru.practicum.ewm.stats;

import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.EndpointHitResponseDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;

import java.util.List;

/**
 * Интерфейс сервис-слоя для обработки статистики <p>
 */
public interface StatsService {

    /**
     * добавление записи статистики
     * @param dto DTO статистики
     * @return ответ
     */
    EndpointHitResponseDto postStats(EndpointHitDto dto);

    /**
     * Получение статистики
     * @param start Дата и время начала диапазона за который нужно выгрузить статистику
     * @param end Дата и время конца диапазона за который нужно выгрузить статистику
     * @param uris Список URI для которых нужно выгрузить статистику
     * @param fromIpUnique Нужно ли учитывать только уникальные посещения (только с уникальным ip)
     * @return сведения о статистике: приложение, URI, число посещений
     * @implNote значения start, end, uris подлежат кодированию (java.net.URI.Encoder)
     */
    List<ViewStatsDto> getStats(String start, String end, String[] uris, boolean fromIpUnique);
}