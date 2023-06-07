package ru.practicum.ewm.common.utils;

/**
 * Состояния жизненного цикла события
 */
public enum EventState {
    PENDING,
    PUBLISHED,
    CANCELED,
    REJECTED
}