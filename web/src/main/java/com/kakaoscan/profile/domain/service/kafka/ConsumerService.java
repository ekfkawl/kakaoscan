package com.kakaoscan.profile.domain.service.kafka;

import com.kakaoscan.profile.config.KafkaProperties;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;
import org.springframework.kafka.annotation.KafkaListener;

@Log4j2
@Service
public class ConsumerService {
    @KafkaListener(topics = {KafkaProperties.TOPIC}, groupId = KafkaProperties.CONSUMER_GROUP_ID)
    public void consumerMessage(ConsumerRecord<String, String> record) {
        log.info("Received - {}", record);
    }
}
