package ru.practicum.ewm.stats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.stats.model.Stats;
import ru.practicum.ewm.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Интерфейс для Jpa-репозитория статистики
 * Выгрузка статистики посещений
 */
public interface StatsRepository extends JpaRepository<Stats, Long> {

    /**
     * выгрузка статистики по количеству посещений с ip-адресов <p>
     * - выборка в промежутке времени <p>
     * - выборка по путям запроса к API приложения<b>из списка</b> <p>
     * - сортировка по убыванию посещений <p>
     * @param start начало промежутка времени посещения
     * @param end конец промежутка времени посещения
     * @param uris пути запроса к API приложения, по которым проводится поиск (к этим эндпойнтам обращались пользователи)
     */
    @Query(value = "SELECT new ru.practicum.ewm.stats.dto.ViewStatsDto(s.app, s.uri, COUNT(s.ip)) "
            + "FROM Stats AS s "
            + "WHERE s.uri IN (:uris) AND (s.timestamp BETWEEN :start AND :end) "
            + "GROUP BY s.app, s.uri, s.timestamp  "
            + "ORDER BY COUNT(s.ip) DESC")
    List<ViewStatsDto> findInTimePeriodInURIs(@Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end,
                                              @Param("uris") String[] uris);

    /**
     * выгрузка статистики по количеству посещений с ip-адресов <p>
     * - выгрузка статистики по <b>уникальным</b> ip-адресам <p>
     * - выборка в промежутке времени <p>
     * - выборка по путям запроса к API приложения<b>из списка</b> <p>
     * - сортировка по убыванию посещений <p>
     * @param start начало интервала времени посещения
     * @param end конец интервала времени посещения
     * @param uris пути запроса к API приложения, по которым проводится поиск (к этим эндпойнтам обращались пользователи)
     */
    @Query("SELECT new ru.practicum.ewm.stats.dto.ViewStatsDto(s.app, s.uri, COUNT(DISTINCT s.ip)) "
            + "FROM Stats AS s "
            + "WHERE s.uri IN (:uris) AND (s.timestamp BETWEEN :start AND :end) "
            + "GROUP BY s.app, s.uri "
            + "ORDER BY COUNT(DISTINCT s.ip) DESC")
    List<ViewStatsDto> findInTimePeriodDistinctIpInURIs(@Param("start") LocalDateTime start,
                                                        @Param("end") LocalDateTime end,
                                                        @Param("uris") String[] uris);

    /**
     * выгрузка статистики по количеству посещений с ip-адресов <p>
     * - выгрузка статистики по <b>уникальным</b> ip-адресам <p>
     * - выборка в промежутке времени <p>
     * - выборка по <b>любым</b> путям запроса к API приложения<p>
     * - сортировка по убыванию посещений <p>
     * @param start начало промежутка времени посещения
     * @param end конец промежутка времени посещения
     */
    @Query("SELECT new ru.practicum.ewm.stats.dto.ViewStatsDto(s.app, s.uri, COUNT(DISTINCT s.ip)) "
            + "FROM Stats AS s "
            + "WHERE (s.timestamp BETWEEN :start AND :end) "
            + "GROUP BY s.app, s.uri "
            + "ORDER BY COUNT(DISTINCT s.ip) DESC")
    List<ViewStatsDto> findInTimePeriodDistinctIp(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * выгрузка статистики по количеству посещений с ip-адресов <p>
     * - выборка в промежутке времени <p>
     * - выборка по <b>любым</b> путям запроса к API приложения<p>
     * - сортировка по убыванию посещений <p>
     * @param start начало промежутка времени посещения
     * @param end конец промежутка времени посещения
     */
    @Query("SELECT new ru.practicum.ewm.stats.dto.ViewStatsDto(s.app, s.uri, COUNT(s.ip)) "
            + "FROM Stats AS s "
            + "WHERE (s.timestamp BETWEEN :start AND :end) "
            + "GROUP BY s.app, s.uri "
            + "ORDER BY COUNT(s.ip) DESC")
    List<ViewStatsDto> findInTimePeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}