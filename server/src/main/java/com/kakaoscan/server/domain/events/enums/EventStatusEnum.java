package com.kakaoscan.server.domain.events.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum EventStatusEnum {
    WAITING, PROCESSING, SUCCESS, FAILURE;

    private String message;

    public static EventStatusEnum fromString(String status) {
        try {
            return EventStatusEnum.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
