package ru.practicum.ewm.common.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.common.exception.BadRequestException;
import ru.practicum.ewm.common.exception.ConditionViolationException;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static java.lang.String.format;
import static ru.practicum.ewm.common.utils.Constants.DATE_TIME_FORMATTER;

@Component
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.EWM_TIMESTAMP_PATTERN);
            return LocalDateTime.parse(value, formatter);
        }
        return null;
    }

    /**
     * Декодирование и парсинг даты-времени и получение параметров времени
     * @param str строковое представление даты-времени, полученное с эндпойнта
     * @param timeDef заранее определенное значение даты-времени, используемое в случае ошибок парсинга
     * @param name название для поля времени для логирования, например "start", "end", "eventDate" для логирования (соответственно начало-конец диапазона фильтрации)
     * @return значение даты времени LocalDateTime
     */
    public LocalDateTime parseOrSetDefaultTime(String str, LocalDateTime timeDef, String name) {
        if (str == null) {
            return timeDef;
        }
        try {
            return LocalDateTime.parse(
                    URLDecoder.decode(str, StandardCharsets.UTF_8),
                    DATE_TIME_FORMATTER
            );
        } catch (DateTimeParseException e) {
            return timeDef;
        }
    }

    /**
     * Проверка времени начала события при добавлении события или обновлении
     * @param upperDateTime новое время события (при добавлении нового события или обновления
     * @param lowerDateTime опорный момент времени, относительно которого проверяют время события
     * @param difInSecs запас по времени (выражен в секундах), который должно иметь событие относительно опорного момента времени
     * @param action строка с указанием для логов, в рамках какой операции проводится проверка
     * @implNote upperDateTime получают из DTO события
     */
    public void checkPermittedTimeDatesRelation(LocalDateTime upperDateTime,
                                                   LocalDateTime lowerDateTime,
                                                   int difInSecs,
                                                   String action,
                                                   boolean throwBadRequest) {
        if (lowerDateTime.plusSeconds(difInSecs).isBefore(upperDateTime)
                || lowerDateTime.plusSeconds(difInSecs).isEqual(upperDateTime)) {
            return;
        }
        String message = format("lower time value %s is more than necessary upper time value %s for %d sec when %s",
                lowerDateTime.format(DATE_TIME_FORMATTER),
                upperDateTime.format(DATE_TIME_FORMATTER),
                difInSecs,
                action
        );
        if (throwBadRequest) {
            throw new BadRequestException(message);
        }
        throw new ConditionViolationException(message);
    }
}
