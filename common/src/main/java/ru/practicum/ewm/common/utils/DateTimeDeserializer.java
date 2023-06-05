package ru.practicum.ewm.common.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeDeserializer extends StdDeserializer<LocalDateTime> {

    protected DateTimeDeserializer() {
        this(null);
    }

    protected DateTimeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String value = jsonParser.getText();
        if (!value.isBlank()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.STATS_DTO_TIMESTAMP_PATTERN);
            return LocalDateTime.parse(value, formatter);
        }
        return null;
    }
}
