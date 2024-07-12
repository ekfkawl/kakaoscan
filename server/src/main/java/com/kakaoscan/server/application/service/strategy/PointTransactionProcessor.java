package com.kakaoscan.server.application.service.strategy;

import com.kakaoscan.server.domain.product.entity.ProductTransaction;
import com.kakaoscan.server.domain.product.enums.ProductType;
import com.kakaoscan.server.domain.product.model.ProductOrderClient;
import com.kakaoscan.server.infrastructure.cache.CacheUpdateObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.kakaoscan.server.infrastructure.constants.RedisKeyPrefixes.LOCK_PEND_POINTS_PAYMENT_KEY_PREFIX;

@Log4j2
@Service
@RequiredArgsConstructor
public class PointTransactionProcessor extends ProductTransactionProcessor<ProductTransaction> {
    private final CacheUpdateObserver cacheUpdateObserver;

    @Override
    public List<ProductType> getProductTypes() {
        return List.of(ProductType.P500, ProductType.P1000, ProductType.P2000, ProductType.P5000, ProductType.P10000);
    }

    @Override
    public String getLockPrefix() {
        return LOCK_PEND_POINTS_PAYMENT_KEY_PREFIX;
    }

    @Override
    public void cancelRequest(ProductTransaction transaction) {
    }

    @Override
    public void approve(ProductTransaction transaction) {
        transaction.getWallet().addBalance(transaction.getAmount());
        cacheUpdateObserver.update(transaction.getUser().getEmail(), transaction.getWallet().getBalance());
    }

    @Override
    public void cancelApproval(ProductTransaction transaction) {
        if (transaction.getWallet().getBalance() < transaction.getAmount()) {
            throw new IllegalStateException("not enough points needed to cancel");
        }

        transaction.getWallet().deductBalance(transaction.getAmount());
        cacheUpdateObserver.update(transaction.getUser().getEmail(), transaction.getWallet().getBalance());
    }
}
