package com.kakaoscan.profile.domain.service.kafka;

import com.kakaoscan.profile.domain.config.kafka.KafkaProperties;
import com.kakaoscan.profile.domain.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.ErrorHandler;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.util.backoff.FixedBackOff;

@Log4j2
@Service
@RequiredArgsConstructor
public class ConsumerService {
    private final EmailService emailService;

    @KafkaListener(topics = {KafkaProperties.TOPIC}, groupId = KafkaProperties.CONSUMER_GROUP_ID)
    public void consumerMessage(ConsumerRecord<String, String> record, Acknowledgment ack) {
        log.info("Received - {}", record);
        try {
            ack.acknowledge();

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Bean
    public ErrorHandler myErrorHandler(KafkaTemplate<String, String> kafkaTemplate) {
        DeadLetterPublishingRecoverer deadLetterPublishingRecoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);
        FixedBackOff backOff = new FixedBackOff(1000L, 1L); // n초 대기, n번 재시도
        return new SeekToCurrentErrorHandler(deadLetterPublishingRecoverer, backOff);
    }
}
