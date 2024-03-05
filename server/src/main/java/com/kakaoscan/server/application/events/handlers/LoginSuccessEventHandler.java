package com.kakaoscan.server.application.events.handlers;

import com.kakaoscan.server.application.port.PointPort;
import com.kakaoscan.server.domain.events.model.LoginSuccessEvent;
import com.kakaoscan.server.infrastructure.events.processor.AbstractEventProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class LoginSuccessEventHandler extends AbstractEventProcessor<LoginSuccessEvent> {

    private final PointPort pointPort;

    @Override
    protected void handleEvent(LoginSuccessEvent event) {
        pointPort.cachePoints(event.getEmail());

        log.info("cached point: {}", event.getEmail());
    }
}
