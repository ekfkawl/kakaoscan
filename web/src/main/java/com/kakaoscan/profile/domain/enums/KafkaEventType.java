package com.kakaoscan.profile.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum KafkaEventType {
    DB_ACCESS_EVENT("DB_ACCESS_EVENT"),
    SEND_MAIL_EVENT("SEND_MAIL_EVENT");

    private final String value;
}
