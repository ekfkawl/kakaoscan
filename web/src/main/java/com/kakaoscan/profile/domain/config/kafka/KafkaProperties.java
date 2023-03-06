package com.kakaoscan.profile.domain.config.kafka;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("kafka")
@Getter
public class KafkaProperties {
    public static final String TOPIC = "kakaoscan.mail.send";
    public static final String CONSUMER_GROUP_ID = "group.kakaoscan.mail.send";

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
}