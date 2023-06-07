package ru.practicum.ewm.common.exception;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Обработчик исключений, определенных в классах приложения
 */
@RequiredArgsConstructor
public abstract class CommonExceptionHandler {

    public abstract Logger log();

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleThrowable(Throwable exception) {
        log().info("internal server error 500 {}", exception.getMessage(), exception);
        ErrorResponse error = new ErrorResponse(exception.getMessage(), listTrace(exception));
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * обработка исключения валидации параметров передаваемых в эндпойнты (HTTP-код 400)
     * @param exception экземпляр исключения Spring ошибки валидации параметров (MethodArgumentNotValidException)
     * @return сообщение об ошибке (ResponseEntity)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        String badArgumentsInfo = "Bad request - " + getBadArgumentsInfo(exception);
        log().info(badArgumentsInfo);
        ErrorResponse error = new ErrorResponse(badArgumentsInfo, listTrace(exception));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * обработка исключения в результате неверного ответа от сервиса статистики в http-клиенте при запросе статистики
     * @param exception экземпляр исключения (StatsClientGetStatsException)
     * @return сообщение об ошибке (ResponseEntity)
     */
    @ExceptionHandler(StatsClientException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleStatsClientException(StatsClientException exception) {
        List<String> trace = listTrace(exception);
        log().info("bad response from stats-server\n{}", trace);
        ErrorResponse error = new ErrorResponse("bad response from stats-server", trace);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Формирование списка стек-трейса
     * @param throwable любое исключение
     * @return список
     */
    private List<String> listTrace(Throwable throwable) {
        return Arrays.stream(throwable.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());
    }

    private String getBadArgumentsInfo(MethodArgumentNotValidException exception) {
        StringBuilder badArgumentsInfo = new StringBuilder("following errors:");
        exception.getBindingResult().getFieldErrors()
                .forEach(fe -> badArgumentsInfo
                                .append("[validation fail: ")
                                .append(fe.getField())
                                .append(" ")
                                .append(fe.getDefaultMessage())
                                .append("]")
                );
        return badArgumentsInfo.toString();
    }
}