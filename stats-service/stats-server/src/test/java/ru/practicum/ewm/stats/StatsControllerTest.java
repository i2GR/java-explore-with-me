package ru.practicum.ewm.stats;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.EndpointHitResponseDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.common.utils.Constants;
import ru.practicum.ewm.common.utils.StatsAppName;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatsController.class)
class StatsControllerTest {


    private static final String POST_PATH = "/hit";
    private static final String GET_PATH = "/stats";

    @MockBean
    private StatsService statsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    private EndpointHitDto dto;

    private static final String testIp = "127.0.0.1";
    private static final String testPath = "/events";

    @Test

    void postStats_whenInputDtoOk_thenStatusOk() throws Exception {
        //given
         dto = EndpointHitDto.builder().app(StatsAppName.EWM_MAIN_SERVICE)
                .ip(testIp)
                .uri(testPath)
                .timestamp(LocalDateTime.now())
                .build();
        Mockito.when(statsService.postStats(any())).thenReturn(
                EndpointHitResponseDto
                        .builder()
                        .recorded(true)
                        .uri(testPath)
                        .build());
        //when
        mvc.perform(post(POST_PATH)
                        .content(objectMapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uri", is(testPath)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "any", "null"})
    void postStats_whenBadJSonAppValue_thenThrowsStatus400(String str) throws Exception {
        //given
        dto = EndpointHitDto.builder().app(StatsAppName.EWM_MAIN_SERVICE)
                .ip(testIp)
                .uri(testPath)
                .timestamp(LocalDateTime.now())
                .build();
        String badJsonHitDtoBody = objectMapper.writeValueAsString(dto).replace(StatsAppName.EWM_MAIN_SERVICE.get(), str);
        //when
        mvc.perform(post(POST_PATH)
                        .content(badJsonHitDtoBody)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
        //then
                .andExpect(status().isBadRequest());
        verify(statsService, never()).postStats(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @NullSource
    void postStats_whenBadJSonUriValue_thenThrowsBadRequest(String str) throws Exception {
        //given
        dto = EndpointHitDto.builder().app(StatsAppName.EWM_MAIN_SERVICE)
                .ip(testIp)
                .uri(str)
                .timestamp(LocalDateTime.now())
                .build();
        //when
        mvc.perform(post(POST_PATH)
                        .content(objectMapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
        //then
                .andExpect(status().isBadRequest());
        verify(statsService, never()).postStats(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"any", "null", "2002-02-02T10:00:00", "2002-02-0 10:00:00"})
    void postStats_whenBadJSonTimeStampValue_thenThrowsStatus500(String str) throws Exception {
        //given
        LocalDateTime timestamp = LocalDateTime.now();
        String formattedTime = timestamp.format(DateTimeFormatter.ofPattern(Constants.EWM_TIMESTAMP_PATTERN));
        dto = EndpointHitDto.builder().app(StatsAppName.EWM_MAIN_SERVICE)
                .ip(testIp)
                .uri(testPath)
                .timestamp(timestamp)
                .build();
        String badJsonHitDtoBody = objectMapper.writeValueAsString(dto).replace(formattedTime, str);
        //when
        mvc.perform(post(POST_PATH)
                        .content(badJsonHitDtoBody)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isBadRequest());
        verify(statsService, never()).postStats(any());
    }

    @Test
    void postStats_whenFutureTimeStampValue_thenThrowsStatus500() throws Exception {
        //given
        dto = EndpointHitDto.builder().app(StatsAppName.EWM_MAIN_SERVICE)
                .ip(testIp)
                .uri(testPath)
                .timestamp(LocalDateTime.now().plusSeconds(1))
                .build();
        //when
        mvc.perform(post(POST_PATH)
                        .content(objectMapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isBadRequest());
        verify(statsService, never()).postStats(any());
    }

    @Test
    void getStats_whenParamsOk_thenStatusOk() throws Exception {
        //given
        Mockito.when(statsService.getStats(anyString(), anyString(), any(String[].class), anyBoolean()))
                .thenReturn(
                        List.of(
                                ViewStatsDto.builder().app(StatsAppName.EWM_MAIN_SERVICE).uri(testPath).hits(1L).build()
                        )
                );
        //when
        mvc.perform(get(GET_PATH)
                        .param("start", "2002-02-02 10:00:00")
                        .param("end", "2032-02-02 10:00:00")
                        .param("uris", testPath)
                        .param("unique", "false")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].app", is(StatsAppName.EWM_MAIN_SERVICE.get())))
                .andExpect(jsonPath("$[0].uri", is(testPath)))
                .andExpect(jsonPath("$[0].hits", is(1L), Long.class));
        Mockito.verify(statsService)
                .getStats("2002-02-02 10:00:00",
                        "2032-02-02 10:00:00",
                        new String[]{testPath},
                        false
                );
    }
}