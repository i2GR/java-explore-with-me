package ru.practicum.ewm.stats.validation;

import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.common.utils.Constants;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Валидация IP адреса в Json, приходящего в теле запроса для записи статистики
 * @see EndpointHitDto
 */
public class JsonBodyIpValidator implements ConstraintValidator<JsonBodyIp, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }
        return value.matches(Constants.IPV4_PATTERN) || value.matches(Constants.IPV6_PATTERN);
    }
}