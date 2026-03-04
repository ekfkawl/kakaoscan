package com.kakaoscan.server.application.events.handlers;

import com.kakaoscan.server.application.dto.request.WebhookProductOrderRequest;
import com.kakaoscan.server.domain.events.model.ApprovalSideEffectsEvent;
import com.kakaoscan.server.domain.events.model.ProductPurchaseCompleteEvent;
import com.kakaoscan.server.domain.product.model.ProductOrderClient;
import com.kakaoscan.server.infrastructure.cache.CacheUpdateObserver;
import com.kakaoscan.server.infrastructure.redis.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.kakaoscan.server.infrastructure.redis.enums.Topics.OTHER_EVENT_TOPIC;

@Component
@RequiredArgsConstructor
public class ApprovalSideEffectsHandler {
    private final CacheUpdateObserver cacheUpdateObserver;
    private final EventPublisher eventPublisher;
    private final ProductOrderClient productOrderClient;

    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ApprovalSideEffectsEvent e) {
        cacheUpdateObserver.update(e.email(), e.newBalance());

        eventPublisher.publish(
                OTHER_EVENT_TOPIC.getTopic(),
                new ProductPurchaseCompleteEvent(e.email(), e.productName(), e.baseUrl())
        );

        try {
            productOrderClient.excludeProductOrder(new WebhookProductOrderRequest(e.transactionId().toString()));
        } catch (Exception ignored) {
        }
    }
}