package com.kakaoscan.profile.domain.kafka.event;

import lombok.Getter;

import java.util.Map;

@Getter
public class KafkaDbAccessEvent extends KafkaEvent {
    private String email;
    private String phoneNumber;
    private String scanResultJson;

    public KafkaDbAccessEvent() {
        super(new Object());
    }

    public KafkaDbAccessEvent(Map<String, Object> source) {
        super(new Object());
        this.email = (String) source.get("email");
        this.phoneNumber = (String) source.get("phoneNumber");
        this.scanResultJson = (String) source.get("scanResultJson");
    }
}
