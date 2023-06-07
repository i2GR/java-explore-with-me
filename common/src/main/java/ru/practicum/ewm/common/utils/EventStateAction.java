package ru.practicum.ewm.common.utils;

public enum EventStateAction {
    /**
     * Admin action
     */
    PUBLISH_EVENT(EventState.PUBLISHED),

    /**
     * Admin action
     */
    //НЕЛОГИЧНОЕ ТРЕБУЕМОЕ ПОВЕДЕНИЕ В ТЕСТАХ POSTMAN (Должно быть REJECTED)
    REJECT_EVENT(EventState.CANCELED),

    /**
     * Registered user action (private API)
     */
    SEND_TO_REVIEW(EventState.PENDING),

    /**
     * Registered user action (private API)
     */
    CANCEL_REVIEW(EventState.CANCELED);

    private final EventState actionResult;

    EventStateAction(EventState actionResult) {
        this.actionResult = actionResult;
    }

    public EventState getResult() {
        return actionResult;
    }
}