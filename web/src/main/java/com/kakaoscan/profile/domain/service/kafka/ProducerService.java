package com.kakaoscan.profile.domain.service.kafka;

import com.kakaoscan.profile.config.kafka.KafkaProperties;
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
public class ProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void send(String K, String V) {
        ListenableFuture<SendResult<String, String>> listenableFuture = kafkaTemplate.send(KafkaProperties.TOPIC, K, V);
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
