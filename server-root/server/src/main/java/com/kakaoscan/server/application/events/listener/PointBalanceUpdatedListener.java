package com.kakaoscan.server.application.events.listener;

import com.kakaoscan.server.application.events.handlers.PointBalanceEventHandler;
import com.kakaoscan.server.domain.events.model.PointBalanceUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Log4j2
@Component
@RequiredArgsConstructor
public class PointBalanceUpdatedListener {
    private final PointBalanceEventHandler pointBalanceEventHandler;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPointBalanceChanged(PointBalanceUpdatedEvent event) {
        try {
            pointBalanceEventHandler.handle(event);
        } catch (Exception e) {
            log.error("failed to handle PointBalanceUpdatedEvent: {}", event, e);
        }
    }
}
