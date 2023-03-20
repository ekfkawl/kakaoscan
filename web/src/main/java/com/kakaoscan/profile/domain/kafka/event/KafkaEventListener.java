package com.kakaoscan.profile.domain.kafka.event;

import com.kakaoscan.profile.domain.model.EmailMessage;
import com.kakaoscan.profile.domain.model.ScanResult;
import com.kakaoscan.profile.domain.service.AddedNumberService;
import com.kakaoscan.profile.domain.service.EmailService;
import com.kakaoscan.profile.domain.service.UserHistoryService;
import com.kakaoscan.profile.domain.service.UserService;
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
    private final UserService userService;
    private final AddedNumberService addedNumberService;

    private final EmailService emailService;

    @Async
    @EventListener
    public void onDbAccessEvent(KafkaDbAccessEvent event) {
        try {
            ScanResult scanResult = ScanResult.deserialize(event.getScanResultJson());
            if (scanResult != null && scanResult.getErrorMessage() == null) {
                userHistoryService.updateHistory(event.getEmail(), event.getPhoneNumber(), event.getScanResultJson());
                addedNumberService.appendPhoneNumberHash(event.getPhoneNumber());
                userService.incTotalUseCount(event.getEmail());
            }

            log.info("update history : {}", event.getEmail());
        } catch (Exception e){
            log.error("update history event error : {}", e.getMessage(), e);
        }
    }

    @Async
    @EventListener
    public void onSendMailEvent(KafkaSendMailEvent event) {
        try {
            EmailMessage emailMessage = EmailMessage.builder()
                    .to(event.getEmail())
                    .subject("[카카오스캔] 서비스 사용 허가 안내")
                    .message("")
                    .build();
            emailService.send(emailMessage, "email");

            log.info("send mail : {}", event.getEmail());
        } catch (Exception e){
            log.error("send mail event error : {}", e.getMessage(), e);
        }
    }

}
