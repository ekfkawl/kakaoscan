package com.kakaoscan.server.application.service;

import com.kakaoscan.server.application.dto.request.WebhookProductOrderRequest;
import com.kakaoscan.server.application.dto.response.ProductTransactions;
import com.kakaoscan.server.application.exception.PendingTransactionExistsException;
import com.kakaoscan.server.application.exception.TransactionIllegalStateException;
import com.kakaoscan.server.application.service.strategy.ProductTransactionFactory;
import com.kakaoscan.server.application.service.strategy.ProductTransactionProcessor;
import com.kakaoscan.server.domain.events.model.ProductPurchaseCompleteEvent;
import com.kakaoscan.server.domain.point.repository.PointWalletRepository;
import com.kakaoscan.server.domain.product.entity.ProductTransaction;
import com.kakaoscan.server.domain.product.enums.ProductTransactionStatus;
import com.kakaoscan.server.domain.product.model.PaymentRequest;
import com.kakaoscan.server.domain.product.model.ProductOrderClient;
import com.kakaoscan.server.domain.product.repository.ProductTransactionRepository;
import com.kakaoscan.server.domain.user.entity.User;
import com.kakaoscan.server.domain.user.repository.UserRepository;
import com.kakaoscan.server.infrastructure.config.WordProperties;
import com.kakaoscan.server.infrastructure.redis.publisher.EventPublisher;
import com.kakaoscan.server.infrastructure.redis.utils.RedissonLockUtil;
import com.kakaoscan.server.infrastructure.service.AuthenticationService;
import com.querydsl.core.QueryResults;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.kakaoscan.server.domain.product.enums.ProductTransactionStatus.CANCELLED;
import static com.kakaoscan.server.domain.product.enums.ProductTransactionStatus.EARNED;
import static com.kakaoscan.server.infrastructure.redis.enums.Topics.OTHER_EVENT_TOPIC;

@Log4j2
@Service
@RequiredArgsConstructor
public class ProductService extends ProductTransactionProcessor<Long> {
    private final UserRepository userRepository;
    private final ProductTransactionRepository productTransactionRepository;
    private final PointWalletRepository pointWalletRepository;
    private final ProductOrderClient productOrderClient;
    private final ProductTransactionFactory productTransactionFactory;
    private final AuthenticationService authenticationService;
    private final RedissonClient redissonClient;
    private final EventPublisher eventPublisher;
    private final WordProperties wordProperties;

    @Value("${bank.account}")
    private String backAccount;

    @Transactional
    @Override
    public void request(Long id, PaymentRequest request) {
        ProductTransactionProcessor<ProductTransaction> processor = productTransactionFactory.getProcessor(request.getProductType());
        RLock lock = redissonClient.getLock(processor.getLockPrefix() + id);

        if (!RedissonLockUtil.withLock(lock, () -> {
            User user = userRepository.findByIdOrThrow(id);
            if (productTransactionRepository.existsPendingTransaction(user.getPointWallet())) {
                throw new PendingTransactionExistsException("대기 중인 결제 요청이 존재합니다. 이전 결제를 완료 또는 취소 후 신청해 주세요.");
            }

            List<ProductTransaction> pendingTransactions = productTransactionRepository.findByTransactionStatus(ProductTransactionStatus.PENDING);
            String uniqueDepositor = wordProperties.generateUniqueDepositor(
                    pendingTransactions.stream()
                            .map(ProductTransaction::getDepositor)
                            .collect(Collectors.toSet())
            );

            ProductTransaction productTransaction = user.addPendingTransaction(request, uniqueDepositor);
            productTransaction = productTransactionRepository.save(productTransaction);

            productOrderClient.createProductOrder(WebhookProductOrderRequest.builder()
                    .orderNumber(productTransaction.getId().toString())
                    .orderAmount(request.getAmount())
                    .ordererName(uniqueDepositor)
                    .billingName(uniqueDepositor)
                    .build());
        })) {
            throw new PendingTransactionExistsException("결제 신청 중 입니다.");
        }
    }

    @Transactional
    @Override
    public void cancelRequest(Long productTransactionId) {
        processProductTransaction(productTransactionId, (processor, transaction) -> {
            productOrderClient.excludeProductOrder(new WebhookProductOrderRequest(transaction.getId().toString()));
            transaction.cancel();

            log.info("cancel transactionId: " + productTransactionId);
        }, transaction -> {
            Long localId = authenticationService.getCurrentUserDetails().getId();
            if (!localId.equals(transaction.getUser().getId())) {
                throw new IllegalStateException(String.format("trying to cancel another user transaction (%d, %d)", localId, productTransactionId));
            }
            if (EARNED.equals(transaction.getTransactionStatus())) {
                throw new TransactionIllegalStateException("이미 결제가 완료된 내역입니다. 결제 취소가 불가능합니다.");
            }
            if (CANCELLED.equals(transaction.getTransactionStatus())) {
                throw new TransactionIllegalStateException("이미 취소된 거래입니다.");
            }
        });
    }

    @Transactional
    @Override
    public void approve(Long productTransactionId) {
        processProductTransaction(productTransactionId, (processor, transaction) -> {
            processor.approve(transaction);
            transaction.approve();

            eventPublisher.publish(OTHER_EVENT_TOPIC.getTopic(), new ProductPurchaseCompleteEvent(
                    transaction.getUser().getEmail(),
                    transaction.getProductType().getDisplayName(),
                    System.getenv("CURRENT_BASE_URL"))
            );

            productOrderClient.excludeProductOrder(new WebhookProductOrderRequest(transaction.getId().toString()));

            log.info("approval transactionId: " + productTransactionId);
        }, transaction -> {
            if (!ProductTransactionStatus.PENDING.equals(transaction.getTransactionStatus())) {
                throw new IllegalStateException(String.format("transaction status must be PENDING to approve (%d)", productTransactionId));
            }
        });
    }

    @Transactional
    @Override
    public void cancelApproval(Long productTransactionId) {
        processProductTransaction(productTransactionId, (processor, transaction) -> {
            processor.cancelApproval(transaction);
            transaction.cancel();

            log.info("cancel transactionId: " + productTransactionId);
        }, transaction -> {
            if (!EARNED.equals(transaction.getTransactionStatus())) {
                throw new IllegalStateException(String.format("transaction status must be EARNED to cancel (%d)", productTransactionId));
            }
        });
    }

    protected void processProductTransaction(Long productTransactionId, BiConsumer<ProductTransactionProcessor<ProductTransaction>, ProductTransaction> transactionCommandConsumer, Consumer<ProductTransaction> supports) {
        ProductTransaction transaction = productTransactionRepository.findById(productTransactionId)
                .orElseThrow(() -> new IllegalArgumentException("transaction not found"));

        supports.accept(transaction);

        ProductTransactionProcessor<ProductTransaction> processor = productTransactionFactory.getProcessor(transaction.getProductType());
        transactionCommandConsumer.accept(processor, transaction);
    }

    @Transactional(readOnly = true)
    public ProductTransactions findProductTransactionsByPointWallet(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        User user = userRepository.findByEmailOrThrow(userId);
        List<ProductTransaction> productTransactionList = pointWalletRepository.findProductTransactionsByPointWallet(user.getPointWallet(), startDate, endDate);

        return ProductTransactions.convertToProductTransactions(productTransactionList, productTransactionList.size(), backAccount);
    }

    @Transactional(readOnly = true)
    public ProductTransactions findAndFilterTransactions(LocalDateTime startDate, LocalDateTime endDate, ProductTransactionStatus status, String keyword, int page, int pageSize) {
        QueryResults<ProductTransaction> transactionQueryResults = productTransactionRepository.findAndFilterTransactions(startDate, endDate, status, keyword, page, pageSize);

        List<ProductTransaction> filteredTransactions = transactionQueryResults.getResults();

        return ProductTransactions.convertToProductTransactions(filteredTransactions, transactionQueryResults.getTotal(), null);
    }

    @Scheduled(fixedRate = 1000 * 60 * 60)
    @Transactional
    public void cancelOldPendingTransactions() {
        List<ProductTransaction> oldPendingTransactions = productTransactionRepository.findOldPendingTransactions();
        for (ProductTransaction transaction : oldPendingTransactions) {
            transaction.cancel();
            productOrderClient.excludeProductOrder(new WebhookProductOrderRequest(transaction.getId().toString()));
        }

        log.info("old product transaction cancelled count: {}", oldPendingTransactions.size());
    }
}