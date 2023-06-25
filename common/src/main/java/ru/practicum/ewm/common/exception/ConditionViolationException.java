package ru.practicum.ewm.common.exception;

import ru.practicum.ewm.common.utils.ExceptionReason;

/**
 * исключение при операциях в слое хранилища при нарушении условий выполнения операции над сущностью
 * выбрасывается в специально указанных случаях согласно "бизнес-логике"
 */
public class ConditionViolationException extends EwmAPIException {

    /**
     * @param message : передается информация об причине исключения
     *                (при наличии, или сообщение исключений классов/методов JAVA)
     */
    public ConditionViolationException(String message) {
        super(ExceptionReason.FORBIDDEN, message);
    }
}