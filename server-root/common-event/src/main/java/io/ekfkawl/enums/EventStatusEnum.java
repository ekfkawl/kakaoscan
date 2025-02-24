package io.ekfkawl.enums;

public enum EventStatusEnum {
    WAITING,
    PROCESSING,
    SUCCESS,
    FAILURE;

    public static EventStatusEnum fromString(String status) {
        try {
            return EventStatusEnum.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
