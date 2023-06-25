package ru.practicum.ewm.common.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ExceptionReason {

    @JsonProperty("reason not specified")
    NOT_SPECIFIED,

    @JsonProperty("The required object was not found.")
    OBJECT_NOT_FOUND,

    @JsonProperty("Incorrectly made request.")
    BAD_REQUEST,

    @JsonProperty("Integrity constraint has been violated.")
    INTEGRITY_VIOLATION,

    @JsonProperty("For the requested operation the conditions are not met.")
    FORBIDDEN
}