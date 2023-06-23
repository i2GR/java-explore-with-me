package ru.practicum.ewm.app.exception;

import java.time.LocalDateTime;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ru.practicum.ewm.common.exception.CommonExceptionHandler;
import ru.practicum.ewm.common.exception.ApiError;
import ru.practicum.ewm.common.exception.NotFoundException;
import ru.practicum.ewm.common.exception.ConditionViolationException;
import ru.practicum.ewm.common.utils.ExceptionReason;

@Slf4j
@RestControllerAdvice
public class MainExceptionHandler extends CommonExceptionHandler {

    @Override
    public Logger log() {
        return log;
    }

    /**
     * обработка исключения: запрошенный элемент не найден (HTTP-кода 404)
     * @param exception исключение
     * @return сообщение об ошибке (ResponseEntity)
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseEntity<ApiError> handleNotFound(NotFoundException exception) {
        List<String> trace = listTrace(exception);
        logStdOut(exception, trace, "not found");
        return new ResponseEntity<>(getApiError(exception, trace), HttpStatus.NOT_FOUND);
    }

    /**
     * обработка исключения: нарушены условия выполнения операции (HTTP-кода 409)
     * @param exception исключение
     * @return сообщение об ошибке (ResponseEntity)
     */
    @ExceptionHandler(ConditionViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ResponseEntity<ApiError> handleNotFound(ConditionViolationException exception) {
        List<String> trace = listTrace(exception);
        logStdOut(exception, trace, "forbidden");
        return new ResponseEntity<>(getApiError(exception, trace), HttpStatus.CONFLICT);
    }

    /**
     * обработка исключения JPA: нарушена целостность данных - конфликт (HTTP-кода 409)
     * @param exception исключение
     * @return сообщение об ошибке (ResponseEntity)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ResponseEntity<ApiError> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        log().info(exception.getMessage());
        ApiError error = ApiError.builder()
                .reason(ExceptionReason.INTEGRITY_VIOLATION)
                .message(exception.getMessage())
                .status(HttpStatus.CONFLICT)
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
}