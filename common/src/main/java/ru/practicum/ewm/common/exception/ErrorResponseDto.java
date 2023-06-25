package ru.practicum.ewm.common.exception;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * класс тела ответа на HTTP-запрос при выбрасывании исключения на Эндпойнте
 */
@Getter
@AllArgsConstructor
public class ErrorResponseDto {

    private final String error;

    private final List<String> trace;
}