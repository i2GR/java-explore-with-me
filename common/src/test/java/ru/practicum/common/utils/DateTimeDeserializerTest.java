package ru.practicum.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.junit.jupiter.api.Test;
import ru.practicum.ewm.common.utils.DateTimeDeserializer;

import java.io.IOException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateTimeDeserializerTest {

    @Test
    public void deserialize_whenSTATS_TIMESTAMP_PATTERN_thenLocalDateTimeOK() throws IOException {
        String json = "{\"timestamp\":\"2000-01-02 10:11:12\"}";

        TestTimeStamp testTimeStamp = new ObjectMapper().readValue(json, TestTimeStamp.class);

        assertEquals(2000, testTimeStamp.getTimestamp().getYear());
        assertEquals(1, testTimeStamp.getTimestamp().getMonth().getValue());
        assertEquals(2, testTimeStamp.getTimestamp().getDayOfMonth());
        assertEquals(10, testTimeStamp.getTimestamp().getHour());
        assertEquals(11, testTimeStamp.getTimestamp().getMinute());
        assertEquals(12, testTimeStamp.getTimestamp().getSecond());
    }

    private static class TestTimeStamp {
        @JsonDeserialize(using = DateTimeDeserializer.class)
        private LocalDateTime timestamp;

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
}