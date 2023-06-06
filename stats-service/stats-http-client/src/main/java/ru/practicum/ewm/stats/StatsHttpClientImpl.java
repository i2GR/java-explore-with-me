package ru.practicum.ewm.stats;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import static ru.practicum.ewm.common.utils.Constants.STATS_HIT_PATH;
import static ru.practicum.ewm.common.utils.Constants.STATS_GET_PATH;
import static ru.practicum.ewm.common.utils.Constants.STATS_DTO_TIMESTAMP_PATTERN;

import ru.practicum.ewm.common.utils.StatsAppName;
import ru.practicum.ewm.stats.dto.EndpointHitResponseDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Slf4j
@Component
public class StatsHttpClientImpl implements StatsHttpClient {

    private static final String START_PARAM = "start";
    private static final String END_PARAM = "end";
    private static final String URIS_PARAM = "uris";
    private static final String UNIQUE_PARAM = "unique";

    private final RestTemplate rest;

    public StatsHttpClientImpl(@Value("${ewm-stats-server.url}") String statsServerUrl, RestTemplateBuilder builder) {
        this.rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(statsServerUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    @Override
    public EndpointHitResponseDto sendEndpointHit(StatsAppName statsAppName, HttpServletRequest request) {
        EndpointHitDto dto = EndpointHitDto.builder()
                .app(statsAppName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
        log.info("send endpoint hit with fields app:{}, uri:{}, ip:{}, timestamp:{}",
                dto.getApp(), dto.getUri(), dto.getIp(), dto.getTimestamp());
        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(dto, defaultHeaders());
        try {
            return rest.exchange(STATS_HIT_PATH, HttpMethod.POST, requestEntity, EndpointHitResponseDto.class).getBody();
        } catch (HttpStatusCodeException e) {
            log.warn("Error sending hit to stats-server {}", e.getMessage());
            return EndpointHitResponseDto.builder().build();
        }
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end) {
        return getStats(start, end, null, null);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, String[] uris) {
        return getStats(start, end, uris, null);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, Boolean unique) {
        return getStats(start, end, null, unique);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        String query = prepareQuery(start, end, uris, unique);
        try {
            return rest.exchange(STATS_GET_PATH + "?" + query,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ViewStatsDto>>(){}
            ).getBody();
        } catch (HttpStatusCodeException e) {
            log.warn("Error getting hit from stats-server {}", e.getMessage());
            return List.of();
        }
    }


    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private Map<String, Object> prepareParameters(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(START_PARAM, start.format(DateTimeFormatter.ofPattern(STATS_DTO_TIMESTAMP_PATTERN)));
        parameters.put(END_PARAM, end.format(DateTimeFormatter.ofPattern(STATS_DTO_TIMESTAMP_PATTERN)));
        if (uris != null) parameters.put(URIS_PARAM, String.join(",", uris));
        if (unique != null) parameters.put(UNIQUE_PARAM, unique);
        return parameters;
    }

    private String prepareQuery(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        return UriComponentsBuilder.newInstance()
                .path(prepareTemplate(uris, unique))
                .build()
                .expand(prepareParameters(start, end, uris, unique))
                .encode()
                .toUriString();
    }

    private String prepareTemplate(String[] uris, Boolean unique) {
        StringBuilder template = new StringBuilder(wrap(START_PARAM) + "&" + wrap(END_PARAM));
        if (uris != null) template.append("&").append(wrap(URIS_PARAM));
        if (unique != null) template.append("&").append(wrap(UNIQUE_PARAM));
        return template.toString();
    }

    private String wrap(String str) {
        return str + "={" + str + "}";
    }
}