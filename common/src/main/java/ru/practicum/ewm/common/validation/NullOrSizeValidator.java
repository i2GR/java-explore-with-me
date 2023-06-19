package ru.practicum.ewm.common.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 *  валидация DTO для события при запросе обновления <p>
 */
public class NullOrSizeValidator implements ConstraintValidator<NullOrSize, String> {

    private Integer min;
    private Integer max;

    @Override
    public void initialize(NullOrSize constraint) {
        this.min = constraint.min();
        this.max = constraint.max();
    }

    @Override
    public boolean isValid(String str, ConstraintValidatorContext context) {
        if (str == null) return true;
        if (str.isBlank()) return false;
        return str.length() >= min && str.length() <= max;
    }
}