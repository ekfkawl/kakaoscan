package com.kakaoscan.profile.domain.kafka.event;

import lombok.Getter;

@Getter
public class KafkaDbAccessEvent extends KafkaEvent {
    private String email;
    private String phoneNumber;
    private String scanResultJson;

    public KafkaDbAccessEvent() {
        super(new Object());
    }
}
