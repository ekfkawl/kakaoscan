package com.kakaoscan.profile.domain.kafka.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class KafkaEvent extends ApplicationEvent {

    public KafkaEvent(Object source) {
        super(source);
    }
}