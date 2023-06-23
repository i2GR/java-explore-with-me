package ru.practicum.ewm.app.partrequest.model;

import lombok.Getter;
import lombok.AllArgsConstructor;

import ru.practicum.ewm.app.event.model.Event;

/**
 * класс количество запросов с определенным статусом для события
 * список с этими вспомогательными объектами запрашивается в БД, в тех случаях,<p>
 *     когда событиям в списке нужно присвоить количество просмотров.
 */
@Getter
@AllArgsConstructor
public class ConfirmedRequestCount {

    private Event event;

    private Long requestAmount;
}