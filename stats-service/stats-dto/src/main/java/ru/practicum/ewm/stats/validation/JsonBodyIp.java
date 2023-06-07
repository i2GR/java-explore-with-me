package ru.practicum.ewm.stats.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = JsonBodyIpValidator.class)
public @interface JsonBodyIp {

    String message() default "значение ip не соответствует формату ipv4 или ipv6";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}