package ru.practicum.ewm.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * класс ответа на HTTP-запрос при выбрасывании исключения на Эндпойнте
 */
@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final String error;
    private final List<String> trace;
}