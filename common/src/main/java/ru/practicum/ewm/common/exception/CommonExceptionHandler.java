package ru.practicum.ewm.common.exception;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.ewm.common.utils.ExceptionReason;

import javax.validation.ConstraintViolationException;

/**
 * Обработчик исключений, определенных в классах приложения
 */
@RequiredArgsConstructor
public abstract class CommonExceptionHandler {

    public abstract Logger log();

    /**
     * Обработка исключения валидации параметров передаваемых в эндпойнты (HTTP-код 400)
     * @param exception экземпляр исключения Spring ошибки валидации параметров (MethodArgumentNotValidException)
     * @return сообщение об ошибке (ResponseEntity)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        String badArgumentsInfo = "Method Argument Not Valid - " + getBadArgumentsInfo(exception);
        log().info(badArgumentsInfo);
        ApiError error = ApiError.builder()
                .errors(listTrace(exception))
                .message(getBadArgumentsInfo(exception))
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обработка исключения конфликта данных в БД (HTTP-код 409)
     * @param exception экземпляр исключения ConstraintViolationException
     * @return сообщение об ошибке (ResponseEntity)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException exception) {
        log().info(exception.getMessage());
        ApiError error = ApiError.builder()
                .reason(ExceptionReason.INTEGRITY_VIOLATION)
                .message(exception.getMessage())
                .status(HttpStatus.CONFLICT)
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    /**
     * Обработка исключения в результате неверного ответа от сервиса статистики в http-клиенте при запросе статистики
     * @param exception экземпляр исключения (StatsClientGetStatsException)
     * @return сообщение об ошибке (ResponseEntity)
     */
    @ExceptionHandler(StatsClientException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<ApiError> handleStatsClientException(StatsClientException exception) {
        List<String> trace = listTrace(exception);
        log().info("bad response from stats-server\n{}", trace);
        ApiError error = ApiError.builder()
                .errors(trace)
                .message("bad response from stats-server")
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * обработка исключения: некорректный запрос (HTTP-кода 400)
     * @param exception исключение
     * @return сообщение об ошибке (ResponseEntity)
     */
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException exception) {
        List<String> trace = listTrace(exception);
        logStdOut(exception, trace, "bad request");
        return new ResponseEntity<>(getApiError(exception, trace), HttpStatus.BAD_REQUEST);
    }

    /**
     * Формирование списка стек-трейса
     * @param throwable любое исключение
     * @return список
     */
    protected List<String> listTrace(Throwable throwable) {
        return Arrays.stream(throwable.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());
    }

    protected ApiError getApiError(EwmAPIException exception, List<String> trace) {
        return ApiError.builder()
                .errors(trace)
                .message(exception.getMessage())
                .reason(exception.getReason())
                .timestamp(exception.getThrowTimeStamp())
                .build();
    }

    protected void logStdOut(EwmAPIException exception, List<String> trace, String error) {
        log().info("handling exception on main-server: {}\n{}", error, trace);
        log().info(exception.getMessage());
    }

    private String getBadArgumentsInfo(MethodArgumentNotValidException exception) {
        StringBuilder badArgumentsInfo = new StringBuilder("Field: ");
        exception.getBindingResult().getFieldErrors()
                .forEach(fe -> badArgumentsInfo
                        .append(fe.getField())
                        .append(" Error:")
                        .append(fe.getDefaultMessage())
                        .append( "Value: ")
                        .append(fe.getRejectedValue())
                );
        return badArgumentsInfo.toString();
    }
}