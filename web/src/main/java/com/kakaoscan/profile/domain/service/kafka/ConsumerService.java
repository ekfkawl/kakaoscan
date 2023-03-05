package com.kakaoscan.profile.domain.service.kafka;

import com.kakaoscan.profile.domain.config.kafka.KafkaProperties;
import com.kakaoscan.profile.domain.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class ConsumerService {
    private final EmailService emailService;

    @KafkaListener(topics = {KafkaProperties.TOPIC}, groupId = KafkaProperties.CONSUMER_GROUP_ID)
    public void consumerMessage(ConsumerRecord<String, String> record) {
        log.info("Received - {}", record);
    }
}
