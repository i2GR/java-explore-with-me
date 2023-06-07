package ru.practicum.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.practicum.ewm.common.exception.BadRequestException;
import ru.practicum.ewm.common.utils.StatsAppName;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class StatsAppNameTest {

    @ParameterizedTest
    @ValueSource(strings = {"ewm-main-service"})
    void fromString_whenValidString_thenOk(String str) {
        //then
        assertDoesNotThrow(() -> StatsAppName.fromString(str));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "any"})
    @NullSource
    void fromString_whenStringInValid_thenThrows(String str) {
        //then
        assertThrows(BadRequestException.class, () -> StatsAppName.fromString(str));
    }

    @Test
    void jSonPropertyAnnotation_whenExpectedJson_thenOk() throws IOException {
        String json =  "{\"app\": \"ewm-main-service\"}";

        TestDto dto = new ObjectMapper().readValue(json, TestDto.class);
        assertEquals(StatsAppName.EWM_MAIN_SERVICE, dto.getApp());
    }

    @NoArgsConstructor
    @Getter
    private static class TestDto {
        private StatsAppName app;
    }
}