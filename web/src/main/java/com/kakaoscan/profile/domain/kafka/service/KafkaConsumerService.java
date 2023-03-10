package com.kakaoscan.profile.domain.kafka.service;

import com.kakaoscan.profile.domain.kafka.config.KafkaProperties;
import com.kakaoscan.profile.domain.kafka.event.KafkaEvent;
import com.kakaoscan.profile.domain.kafka.mapper.KafkaMessageDeserializer;
import com.kakaoscan.profile.domain.model.KafkaMessage;
import com.kakaoscan.profile.domain.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.ApplicationEventPublisher;
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
public class KafkaConsumerService {
    private final ApplicationEventPublisher eventPublisher;

    private final EmailService emailService;

    @KafkaListener(topics = {KafkaProperties.TOPIC_EVENT}, groupId = KafkaProperties.GROUP_EVENT)
    public void onMessage(ConsumerRecord<String, String> record, Acknowledgment ack) {
        log.info("Received - {}", record);
        try {
            KafkaEvent kafkaEvent = new KafkaEvent(this, record.key(), KafkaMessageDeserializer.deserialize(record.value()));
            eventPublisher.publishEvent(kafkaEvent);

            ack.acknowledge();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Bean
    public ErrorHandler errorHandler(KafkaTemplate<String, KafkaMessage> kafkaTemplate) {
        DeadLetterPublishingRecoverer deadLetterPublishingRecoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);
        FixedBackOff backOff = new FixedBackOff(1000L, 1L); // n초 대기, n번 재시도
        return new SeekToCurrentErrorHandler(deadLetterPublishingRecoverer, backOff);
    }
}
