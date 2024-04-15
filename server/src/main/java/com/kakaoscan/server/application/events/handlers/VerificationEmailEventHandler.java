package com.kakaoscan.server.application.events.handlers;

import com.kakaoscan.server.application.port.EmailPort;
import com.kakaoscan.server.domain.events.model.VerificationEmailEvent;
import com.kakaoscan.server.infrastructure.events.processor.AbstractEventProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
public class VerificationEmailEventHandler extends AbstractEventProcessor<VerificationEmailEvent> {
    private final EmailPort emailPort;

    @Override
    protected void handleEvent(VerificationEmailEvent event) {
        Map<String, Object> variables = Map.of(
                "verificationLink", event.getVerificationEmail().getVerificationLink()
        );

        emailPort.send(event.getVerificationEmail(), variables);

        log.info("send verification mail: {}", event.getVerificationEmail().getReceiver());
    }
}
