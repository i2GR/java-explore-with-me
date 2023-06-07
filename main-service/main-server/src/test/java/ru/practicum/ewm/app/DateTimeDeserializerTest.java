package ru.practicum.ewm.app;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import ru.practicum.ewm.common.utils.DateTimeDeserializer;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DateTimeDeserializerTest {

    private final DateTimeDeserializer deserializer;
    private final LocalDateTime expected = LocalDateTime.of(2000, 1, 1, 12, 0);
    private final LocalDateTime ref = LocalDateTime.of(2001, 1, 1, 12, 0);

    @ParameterizedTest
    @ValueSource(strings = {"2000-01-01 12:00:00", "2000-01-01%2012%3A00%3A00"})
    public void parseTime_whenStringFormatOk_thenDateFromString(String encoded) {
        //given
        //when
        LocalDateTime actual = deserializer.parseOrSetDefaultTime(encoded, ref, "");
        //then
        assertTrue(actual.isEqual(expected));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", " 2000-01-01 12-00", "2000-01-01T12-00", "2000-01-01T12:00:00", "2000-01-01T12%3A00%3A00"})
    public void parseTime_whenStringBadFormat_thenRefDate(String encoded) {
        //given
        //when
        LocalDateTime actual = deserializer.parseOrSetDefaultTime(encoded, ref, "");
        //then
        assertTrue(actual.isEqual(ref));
    }
}