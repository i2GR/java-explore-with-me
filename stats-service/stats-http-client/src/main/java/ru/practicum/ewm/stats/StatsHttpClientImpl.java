package ru.practicum.ewm.stats;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import static ru.practicum.ewm.utils.Constants.STATS_HIT_PATH;
import static ru.practicum.ewm.utils.Constants.STATS_GET_PATH;
import static ru.practicum.ewm.utils.Constants.STATS_DTO_TIMESTAMP_PATTERN;

import ru.practicum.ewm.utils.StatsAppName;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
public class StatsHttpClientImpl implements StatsHttpClient {

    private final String START_PARAM = "start";
    private final String END_PARAM = "end";
    private final String URIS_PARAM = "uris";
    private final String UNIQUE_PARAM = "unique";

    private final RestTemplate rest;

    public StatsHttpClientImpl(@Value("${ewm-stats-server.url}") String statsServerUrl, RestTemplateBuilder builder) {
        this.rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(statsServerUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    @Override
    public ResponseEntity<Object> sendEndpointHit(StatsAppName statsAppName, HttpServletRequest request) {
        EndpointHitDto dto = EndpointHitDto.builder()
                .app(statsAppName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
        log.info("send endpoint hit with fields app:{}, uri:{}, ip:{}, timestamp:{}",
                dto.getApp(), dto.getUri(), dto.getIp(), dto.getTimestamp());
        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(dto, defaultHeaders());
        ResponseEntity<Object> statsServerResponse;
        try {
            statsServerResponse = rest.exchange(STATS_HIT_PATH, HttpMethod.POST, requestEntity, Object.class);
        } catch (HttpStatusCodeException e) {
            log.warn("Error sending hit to stats-server {}", e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareStatsResponse(statsServerResponse);
    }

    @Override
    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end) {
        return getStats(start, end, null, null);
    }

    @Override
    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, String[] uris) {
        return getStats(start, end, uris, null);
    }

    @Override
    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, Boolean unique) {
        return getStats(start, end, null, unique);
    }

    @Override
    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        ResponseEntity<Object> statsInfoResponse;
        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(null, defaultHeaders());
        String query = prepareQuery(start, end, uris, unique);
        try {
            statsInfoResponse = rest.exchange(STATS_GET_PATH + "?" + query, HttpMethod.GET, requestEntity, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }

        return prepareStatsResponse(statsInfoResponse);
    }


    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private ResponseEntity<Object> prepareStatsResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("stats action completed at code 2xx");
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());
        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }
        return responseBuilder.build();
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