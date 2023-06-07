package ru.practicum.ewm.common.exception;

import java.time.LocalDateTime;

import lombok.Getter;
import ru.practicum.ewm.common.utils.ExceptionReason;

/**
 * Класс родитель исключений, специфичных для приложения
 * @implNote наследники устанавливают специфические причины (reason), моменты времени возникновения ошибки
 */
@Getter
public class EwmAPIException extends RuntimeException{

    private final ExceptionReason reason;
    private final LocalDateTime throwTimeStamp;

    public EwmAPIException(ExceptionReason reason, String message) {
        super(message);
        this.throwTimeStamp = LocalDateTime.now();
        this.reason = reason;
    }
}