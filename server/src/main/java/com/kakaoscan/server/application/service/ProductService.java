package com.kakaoscan.server.application.service;

import com.kakaoscan.server.application.dto.request.WebhookProductOrderRequest;
import com.kakaoscan.server.application.dto.response.ProductTransactions;
import com.kakaoscan.server.domain.events.model.ProductPurchaseCompleteEvent;
import com.kakaoscan.server.domain.point.repository.PointWalletRepository;
import com.kakaoscan.server.domain.product.entity.ProductTransaction;
import com.kakaoscan.server.domain.product.enums.ProductTransactionStatus;
import com.kakaoscan.server.domain.product.model.ProductOrderClient;
import com.kakaoscan.server.domain.product.repository.ProductTransactionRepository;
import com.kakaoscan.server.domain.user.entity.User;
import com.kakaoscan.server.domain.user.repository.UserRepository;
import com.kakaoscan.server.infrastructure.redis.publisher.EventPublisher;
import com.querydsl.core.QueryResults;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.kakaoscan.server.infrastructure.redis.enums.Topics.OTHER_EVENT_TOPIC;

@Log4j2
@Service
@RequiredArgsConstructor
public class ProductService {
    private final UserRepository userRepository;
    private final ProductTransactionRepository productTransactionRepository;
    private final PointWalletRepository pointWalletRepository;
    private final PointService pointService;
    private final EventPublisher eventPublisher;
    private final ProductOrderClient productOrderClient;

    @Value("${bank.account}")
    private String backAccount;

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

    @Transactional
    public void approvalTransaction(Long productTransactionId) {
        productTransactionRepository.findById(productTransactionId).ifPresent(productTransaction -> {
            if (ProductTransactionStatus.PENDING.equals(productTransaction.getTransactionStatus())) {
                productTransaction.getWallet().addBalance(productTransaction.getAmount());
                productTransaction.approvalTransaction();

                pointService.cachePoints(productTransaction.getWallet().getUser().getEmail(), productTransaction.getWallet().getBalance());

                ProductPurchaseCompleteEvent transactionCompletedEvent = new ProductPurchaseCompleteEvent(productTransaction.getWallet().getUser().getEmail(),
                        productTransaction.getProductType().getDisplayName(),
                        System.getenv("CURRENT_BASE_URL"));
                eventPublisher.publish(OTHER_EVENT_TOPIC.getTopic(), transactionCompletedEvent);

                productOrderClient.excludeProductOrder(new WebhookProductOrderRequest(productTransaction.getId().toString()));

                log.info("approval transactionId: " + productTransactionId);
            }else {
                log.info("already approved transactionId: " + productTransactionId);
            }
        });
    }

    @Transactional
    public void cancelTransaction(Long productTransactionId) {
        productTransactionRepository.findById(productTransactionId).ifPresent(productTransaction -> {
            if (ProductTransactionStatus.EARNED.equals(productTransaction.getTransactionStatus())) {
                if (productTransaction.getWallet().getBalance() >= productTransaction.getAmount()) {
                    productTransaction.getWallet().deductBalance(productTransaction.getAmount());
                    productTransaction.cancelTransaction();

                    pointService.cachePoints(productTransaction.getWallet().getUser().getEmail(), productTransaction.getWallet().getBalance());

                    log.info("cancel transactionId: " + productTransactionId);
                }
            }
        });
    }

    @Scheduled(fixedRate = 1000 * 60 * 60)
    @Transactional
    public void cancelOldPendingTransactions() {
        List<ProductTransaction> oldPendingTransactions = productTransactionRepository.findOldPendingTransactions();
        for (ProductTransaction transaction : oldPendingTransactions) {
            transaction.cancelTransaction();
            productOrderClient.excludeProductOrder(new WebhookProductOrderRequest(transaction.getId().toString()));
        }

        log.info("old product transaction cancelled count: {}", oldPendingTransactions.size());
    }
}