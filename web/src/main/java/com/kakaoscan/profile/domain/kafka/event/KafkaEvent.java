package com.kakaoscan.profile.domain.kafka.event;

import com.kakaoscan.profile.domain.model.KafkaMessage;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class KafkaEvent extends ApplicationEvent {
    private String key;
    private KafkaMessage value;

    public KafkaEvent(Object source, String key, KafkaMessage value) {
        super(source);
        this.key = key;
        this.value = value;
    }
}