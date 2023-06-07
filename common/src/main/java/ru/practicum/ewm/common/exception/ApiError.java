package ru.practicum.ewm.common.exception;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import ru.practicum.ewm.common.utils.ExceptionReason;

import static ru.practicum.ewm.common.utils.Constants.EWM_TIMESTAMP_PATTERN;

/**
 * класс ошибки как ответа на HTTP-запрос при выбрасывании исключения на эндпойнте
 */
@Builder
@Getter
public class ApiError {
    private final List<String> errors;

    @Builder.Default
    private final String message = "message not specified";

    @Builder.Default
    private final ExceptionReason reason = ExceptionReason.NOT_SPECIFIED;

    @Builder.Default
    private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    @JsonFormat(pattern = EWM_TIMESTAMP_PATTERN)
    private LocalDateTime timestamp;
}