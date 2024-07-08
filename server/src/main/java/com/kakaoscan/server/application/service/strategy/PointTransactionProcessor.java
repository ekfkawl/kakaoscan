package com.kakaoscan.server.application.service.strategy;

import com.kakaoscan.server.application.dto.request.WebhookProductOrderRequest;
import com.kakaoscan.server.application.exception.PendingTransactionExistsException;
import com.kakaoscan.server.application.service.PointService;
import com.kakaoscan.server.domain.events.model.ProductPurchaseCompleteEvent;
import com.kakaoscan.server.domain.product.entity.ProductTransaction;
import com.kakaoscan.server.domain.product.enums.ProductTransactionStatus;
import com.kakaoscan.server.domain.product.enums.ProductType;
import com.kakaoscan.server.domain.product.model.PaymentRequest;
import com.kakaoscan.server.domain.product.model.ProductOrderClient;
import com.kakaoscan.server.domain.product.repository.ProductTransactionRepository;
import com.kakaoscan.server.domain.user.entity.User;
import com.kakaoscan.server.domain.user.repository.UserRepository;
import com.kakaoscan.server.infrastructure.config.WordProperties;
import com.kakaoscan.server.infrastructure.redis.publisher.EventPublisher;
import com.kakaoscan.server.infrastructure.redis.utils.RedissonLockUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.kakaoscan.server.infrastructure.redis.enums.Topics.OTHER_EVENT_TOPIC;

@Log4j2
@Service
@RequiredArgsConstructor
public class PointTransactionProcessor extends ProductTransactionProcessor<ProductTransaction> {
    private final UserRepository userRepository;
    private final RedissonClient redissonClient;
    private final PointService pointService;
    private final EventPublisher eventPublisher;
    private final ProductOrderClient productOrderClient;
    private final ProductTransactionRepository productTransactionRepository;
    private final WordProperties wordProperties;

    private static final String LOCK_PEND_POINTS_PAYMENT_KEY_PREFIX = "pendPointsPaymentLock:";

    @Override
    public List<ProductType> getProductTypes() {
        return List.of(ProductType.P500, ProductType.P1000, ProductType.P2000, ProductType.P5000, ProductType.P10000);
    }

    @Override
    public void request(Long id, PaymentRequest request) {
        RLock lock = redissonClient.getLock(LOCK_PEND_POINTS_PAYMENT_KEY_PREFIX + id);

        if (!RedissonLockUtil.withLock(lock, () -> {
            User user = userRepository.findByIdOrThrow(id);

            if (productTransactionRepository.existsPendingTransaction(user.getPointWallet())) {
                throw new PendingTransactionExistsException("이미 결제 신청 내역이 존재합니다.");
            }

            List<ProductTransaction> pendingTransactions = productTransactionRepository.findByTransactionStatus(ProductTransactionStatus.PENDING);
            String uniqueDepositor = wordProperties.generateUniqueDepositor(
                    pendingTransactions.stream()
                            .map(ProductTransaction::getDepositor)
                            .collect(Collectors.toSet())
            );

            ProductTransaction productTransaction = user.getPointWallet().addPendingTransaction(request, uniqueDepositor);
            productTransaction = productTransactionRepository.save(productTransaction);

            productOrderClient.createProductOrder(WebhookProductOrderRequest.builder()
                    .orderNumber(productTransaction.getId().toString())
                    .orderAmount(request.getAmount())
                    .ordererName(uniqueDepositor)
                    .billingName(uniqueDepositor)
                    .build());
        })) {
            throw new PendingTransactionExistsException("결제 신청 중 입니다.");
        };
    }

    @Override
    public void cancelRequest(ProductTransaction transaction) {
        productOrderClient.excludeProductOrder(new WebhookProductOrderRequest(transaction.getId().toString()));
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
