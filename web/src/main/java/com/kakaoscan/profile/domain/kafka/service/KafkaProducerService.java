package com.kakaoscan.profile.domain.kafka.service;

import com.kakaoscan.profile.domain.kafka.config.KafkaProperties;
import com.kakaoscan.profile.domain.model.KafkaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Log4j2
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, KafkaMessage> kafkaTemplate;

    public void send(String K, KafkaMessage V) {
        ListenableFuture<SendResult<String, KafkaMessage>> listenableFuture = kafkaTemplate.send(KafkaProperties.TOPIC_EVENT, K, V);
        listenableFuture.addCallback(new ListenableFutureCallback<Object>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("onFailure", ex);
            }

            @Override
            public void onSuccess(Object result) {
                log.info("onSuccess: {}", result);
            }
        });

        log.info("Send - {}, {}", K, V);
    }
}
