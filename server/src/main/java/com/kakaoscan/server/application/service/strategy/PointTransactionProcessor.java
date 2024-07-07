package com.kakaoscan.server.application.service.strategy;

import com.kakaoscan.server.application.dto.request.WebhookProductOrderRequest;
import com.kakaoscan.server.application.service.PointService;
import com.kakaoscan.server.domain.events.model.ProductPurchaseCompleteEvent;
import com.kakaoscan.server.domain.product.entity.ProductTransaction;
import com.kakaoscan.server.domain.product.enums.ProductType;
import com.kakaoscan.server.domain.product.model.ProductOrderClient;
import com.kakaoscan.server.infrastructure.redis.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.kakaoscan.server.infrastructure.redis.enums.Topics.OTHER_EVENT_TOPIC;

@Log4j2
@Service
@RequiredArgsConstructor
public class PointTransactionProcessor extends ProductTransactionProcessor<ProductTransaction> {
    private final PointService pointService;
    private final EventPublisher eventPublisher;
    private final ProductOrderClient productOrderClient;

    @Override
    public List<ProductType> getProductTypes() {
        return List.of(ProductType.P500, ProductType.P1000, ProductType.P2000, ProductType.P5000, ProductType.P10000);
    }

    @Override
    public void request(ProductTransaction transaction) {

    }

    @Override
    public void cancelRequest(ProductTransaction transaction) {

    }

    @Override
    public void approve(ProductTransaction transaction) {
        transaction.getWallet().addBalance(transaction.getAmount());
        pointService.cachePoints(transaction.getWallet().getUser().getEmail(), transaction.getWallet().getBalance());

        ProductPurchaseCompleteEvent transactionCompletedEvent = new ProductPurchaseCompleteEvent(transaction.getWallet().getUser().getEmail(),
                transaction.getProductType().getDisplayName(),
                System.getenv("CURRENT_BASE_URL"));
        eventPublisher.publish(OTHER_EVENT_TOPIC.getTopic(), transactionCompletedEvent);

        productOrderClient.excludeProductOrder(new WebhookProductOrderRequest(transaction.getId().toString()));
    }

    @Override
    public void cancelApproval(ProductTransaction transaction) {
        if (transaction.getWallet().getBalance() < transaction.getAmount()) {
            throw new IllegalStateException("not enough points needed to cancel");
        }

        transaction.getWallet().deductBalance(transaction.getAmount());
        pointService.cachePoints(transaction.getWallet().getUser().getEmail(), transaction.getWallet().getBalance());
    }
}
