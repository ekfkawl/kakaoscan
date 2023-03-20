package com.kakaoscan.profile.domain.kafka.event;

import lombok.Getter;

@Getter
public class KafkaSendMailEvent extends KafkaEvent {

    private String email;

    public KafkaSendMailEvent() {
        super(new Object());
    }
}