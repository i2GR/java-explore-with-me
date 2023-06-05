package ru.practicum.ewm.stats.validation;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.utils.StatsAppName;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JsonBodyIpValidatorTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private EndpointHitDto dto;

    @ParameterizedTest
    @ValueSource(strings = {
            "127.0.0.1",
            "0.0.0.1",
            "0:0:0:0:0:0:0:1",
            "0:0d23:0:0:0:0:0:af12",
            "B12F:CCCC:0:0:0:0:0:AF12"
    })
    void whenValidStringForIp_thenValidationOk(String str) {
        //given
        setupDto(str);
        //when
        Set<ConstraintViolation<EndpointHitDto>> violations = validator.validate(dto);
        //then
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            " ",
            "0.0.1",
            "256.0.0.1",
            "1.0.0.",
            ".1.0.0",
            "G:0:0:0:0:0:0:1",
            "0:0:0:0:0:0:0:",
            "0:0G23:0:0:0:0:0:AF12",
            "0:0g23:0:0:0:0:0:af12",
            "B12F:GGGG:0:0:0:0:0:AF12",
            "any"
    })
    @NullSource
    void whenBadStringForIp_whenThrows (String str) {
        //given
        setupDto(str);
        //when
        Set<ConstraintViolation<EndpointHitDto>> violations = validator.validate(dto);
        //then
        assertEquals(1, violations.size());
        assertEquals("json:ip not ipv4 or ipv6", List.copyOf(violations).get(0).getMessage());
    }

    private void setupDto(String str) {
        dto = EndpointHitDto.builder()
                .ip(str)
                .app(StatsAppName.EWM_MAIN_SERVICE).uri("/hit").timestamp(LocalDateTime.now()).build();
    }
}