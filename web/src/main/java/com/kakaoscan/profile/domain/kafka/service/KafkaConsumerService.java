package com.kakaoscan.profile.domain.kafka.service;

import com.kakaoscan.profile.domain.enums.KafkaEventType;
import com.kakaoscan.profile.domain.kafka.config.KafkaProperties;
import com.kakaoscan.profile.domain.kafka.event.KafkaDbAccessEvent;
import com.kakaoscan.profile.domain.kafka.event.KafkaEvent;
import com.kakaoscan.profile.domain.kafka.event.KafkaSendMailEvent;
import com.kakaoscan.profile.domain.kafka.mapper.KafkaMessageDeserializer;
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

import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final ApplicationEventPublisher eventPublisher;


    @KafkaListener(topics = {KafkaProperties.TOPIC_EVENT}, groupId = KafkaProperties.GROUP_EVENT)
    public void onMessage(ConsumerRecord<KafkaEventType, Map<String, Object>> record, Acknowledgment ack) {
        try {

            KafkaEventType eventType = KafkaEventType.valueOf(String.valueOf(record.key()).split(":")[0].toUpperCase());
            Map<String, Object> map = record.value();

            switch (eventType) {
                case DB_ACCESS_EVENT:
                    KafkaEvent kafkaDbAccessEvent = KafkaMessageDeserializer.deserialize(map, KafkaDbAccessEvent.class);
                    eventPublisher.publishEvent(kafkaDbAccessEvent);
                    break;

                case SEND_MAIL_EVENT:
                    KafkaEvent kafkaSendMailEvent = KafkaMessageDeserializer.deserialize(map, KafkaSendMailEvent.class);
                    eventPublisher.publishEvent(kafkaSendMailEvent);
                    break;

                default:
                    log.error("invalid kafka event : {}", eventType);
                    break;
            }

            ack.acknowledge();

        } catch (Exception e) {
            log.error("consumer onMessage error : {}", e.getMessage(), e);
        }
    }

    @Bean
    public ErrorHandler errorHandler(KafkaTemplate<KafkaEventType, Map<String, Object>> kafkaTemplate) {
        DeadLetterPublishingRecoverer deadLetterPublishingRecoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);
        FixedBackOff backOff = new FixedBackOff(1000L, 1L); // n초 대기, n번 재시도
        return new SeekToCurrentErrorHandler(deadLetterPublishingRecoverer, backOff);
    }
}
