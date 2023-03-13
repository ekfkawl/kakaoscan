package com.kakaoscan.profile.domain.kafka.event;

import com.kakaoscan.profile.domain.model.EmailMessage;
import com.kakaoscan.profile.domain.service.EmailService;
import com.kakaoscan.profile.domain.service.UserHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class KafkaEventListener {
    private final UserHistoryService userHistoryService;

    private final EmailService emailService;

    @Async
    @EventListener
    public void onEvent(KafkaEvent event) {

        switch (event.getValue().getType()) {
            case UPSERT:
                // email, phone, json
                userHistoryService.updateHistory(event.getKey(), event.getValue().getSubMessage(), event.getValue().getMessage());
                break;

            case EMAIL:
                EmailMessage emailMessage = EmailMessage.builder()
                        .to(event.getKey())
                        .subject("[카카오스캔] 서비스 사용 허가 안내")
                        .message("")
                        .build();
                emailService.send(emailMessage, "email");
                log.info("send mail : {}", event.getKey());
                break;
        }

//        log.info("Received event with key: {}, value: {}, {}", event.getKey(), event.getValue().getType(), event.getValue().getMessage());
    }
}
