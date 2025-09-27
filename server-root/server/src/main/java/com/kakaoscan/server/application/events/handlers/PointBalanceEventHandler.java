package com.kakaoscan.server.application.events.handlers;

import com.kakaoscan.server.domain.events.model.PointBalanceUpdatedEvent;
import com.kakaoscan.server.domain.user.repository.UserRepository;
import com.kakaoscan.server.infrastructure.cache.CacheUpdateObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class PointBalanceEventHandler {
    private final CacheUpdateObserver cacheUpdateObserver;
    private final UserRepository userRepository;

    public void handle(PointBalanceUpdatedEvent event) {
        userRepository.findByEmail(event.getEmail()).ifPresent(user -> {
            int balance = user.getPointWallet().getBalance();
            cacheUpdateObserver.update(event.getEmail(), balance);
            log.info("point pushed to {}: {}", event.getEmail(), balance);
        });
    }
}
