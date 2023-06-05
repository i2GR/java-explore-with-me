package ru.practicum.ewm.stats.exception;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.common.exception.CommonExceptionHandler;

@Slf4j
@RestControllerAdvice
public class StatsExceptionHandler extends CommonExceptionHandler {

    @Override
    protected Logger log() {
        return log;
    }
}
