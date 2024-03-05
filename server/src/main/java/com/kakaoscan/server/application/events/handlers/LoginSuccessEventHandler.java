package com.kakaoscan.server.application.events.handlers;

import com.kakaoscan.server.application.port.PointPort;
import com.kakaoscan.server.domain.events.model.LoginSuccessEvent;
import com.kakaoscan.server.domain.user.entity.User;
import com.kakaoscan.server.domain.user.repository.UserRepository;
import com.kakaoscan.server.infrastructure.events.processor.AbstractEventProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class LoginSuccessEventHandler extends AbstractEventProcessor<LoginSuccessEvent> {

    private final UserRepository userRepository;
    private final PointPort pointPort;

    @Override
    protected void handleEvent(LoginSuccessEvent event) {
        User user = userRepository.findByEmail(event.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        pointPort.cachePoints(event.getEmail(), user.getPoint().getBalance());

        log.info("cached point: {}", event.getEmail());
    }
}
