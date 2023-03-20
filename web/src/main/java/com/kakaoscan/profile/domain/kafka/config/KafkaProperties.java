package com.kakaoscan.profile.domain.kafka.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class KafkaProperties {
    public static final String TOPIC_EVENT = "kakaoscan";
    public static final String GROUP_EVENT = "group.kakaoscan";

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
}