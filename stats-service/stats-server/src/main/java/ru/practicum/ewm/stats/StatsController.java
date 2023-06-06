package ru.practicum.ewm.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.EndpointHitResponseDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;

import javax.validation.Valid;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * REST-Контроллер статистики
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping
public class StatsController {

    private final StatsService statsService;

    /**
     * Сохранение информации о том, что на URI конкретного сервиса был отправлен запрос пользователем.
     * @param dto Название сервиса, uri и ip пользователя указаны в теле запроса.
     * @return статус ответа
     */
    @PostMapping(path = "/hit")
    public ResponseEntity<EndpointHitResponseDto> postStats(@Valid @RequestBody EndpointHitDto dto) {
        log.info("[post] stats http-request");
        return new ResponseEntity<>(statsService.postStats(dto), HttpStatus.CREATED);
    }

    /**
     * "Получение статистики по посещениям. <p>
     * @param start Дата и время начала диапазона за который нужно выгрузить статистику
     * @param end Дата и время конца диапазона за который нужно выгрузить статистику
     * @param uris Список URI для которых нужно выгрузить статистику
     * @param fromIpUnique Нужно ли учитывать только уникальные посещения (только с уникальным ip)
     * @return сведения о статистике: приложение, URI, число посещений
     * @implNote значения start, end, uris подлежат кодированию (java.net.URI.Encoder)
     */
    @GetMapping(path = "/stats")
    public ResponseEntity<List<ViewStatsDto>> getStats(
            @RequestParam(name = "start") String start,
            @RequestParam(name = "end") String end,
            @RequestParam(name = "uris", required = false) String[] uris,
            @RequestParam(name = "unique", defaultValue = "false") boolean fromIpUnique) {
        log.info("[get] stats http-request");
        String decodedStart = URLDecoder.decode(start, StandardCharsets.UTF_8);
        String decodedEnd = URLDecoder.decode(end, StandardCharsets.UTF_8);
        return new ResponseEntity<>(statsService.getStats(decodedStart, decodedEnd, uris, fromIpUnique), HttpStatus.OK);
    }
}