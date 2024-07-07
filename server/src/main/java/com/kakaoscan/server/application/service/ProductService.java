package com.kakaoscan.server.application.service;

import com.kakaoscan.server.application.dto.request.WebhookProductOrderRequest;
import com.kakaoscan.server.application.dto.response.ProductTransactions;
import com.kakaoscan.server.application.service.strategy.ProductTransactionFactory;
import com.kakaoscan.server.application.service.strategy.ProductTransactionProcessor;
import com.kakaoscan.server.domain.point.repository.PointWalletRepository;
import com.kakaoscan.server.domain.product.entity.ProductTransaction;
import com.kakaoscan.server.domain.product.enums.ProductTransactionStatus;
import com.kakaoscan.server.domain.product.model.ProductOrderClient;
import com.kakaoscan.server.domain.product.repository.ProductTransactionRepository;
import com.kakaoscan.server.domain.user.entity.User;
import com.kakaoscan.server.domain.user.repository.UserRepository;
import com.querydsl.core.QueryResults;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Log4j2
@Service
@RequiredArgsConstructor
public class ProductService extends ProductTransactionProcessor<Long> {
    private final UserRepository userRepository;
    private final ProductTransactionRepository productTransactionRepository;
    private final PointWalletRepository pointWalletRepository;
    private final ProductOrderClient productOrderClient;
    private final ProductTransactionFactory productTransactionFactory;

    @Value("${bank.account}")
    private String backAccount;

    @Transactional
    @Override
    public void request(Long productTransactionId) {

    }

    @Transactional
    @Override
    public void cancelRequest(Long productTransactionId) {

    }

    @Transactional
    @Override
    public void approve(Long productTransactionId) {
        processProductTransaction(productTransactionId, (processor, transaction) -> {
            processor.approve(transaction);
            transaction.approve();

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
            if (!ProductTransactionStatus.EARNED.equals(transaction.getTransactionStatus())) {
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