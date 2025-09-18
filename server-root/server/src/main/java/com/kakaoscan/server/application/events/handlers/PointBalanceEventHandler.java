package com.kakaoscan.server.application.events.handlers;

import com.kakaoscan.server.domain.events.model.PointBalanceUpdatedEvent;
import com.kakaoscan.server.infrastructure.cache.CacheUpdateObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class PointBalanceEventHandler {
    private final CacheUpdateObserver cacheUpdateObserver;

    public void handle(PointBalanceUpdatedEvent event) {
        cacheUpdateObserver.update(event.getEmail(), event.getNewBalance());
        log.info("point pushed to {}: {}", event.getEmail(), event.getNewBalance());
    }
}
