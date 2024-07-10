package com.kakaoscan.server.application.service.strategy;

import com.kakaoscan.server.domain.product.enums.ProductType;
import com.kakaoscan.server.domain.product.model.PaymentRequest;
import com.kakaoscan.server.domain.user.entity.User;

import java.util.List;

public abstract class ProductTransactionProcessor<T> {
    protected ProductType getProductType() {
        throw new UnsupportedOperationException("getProductType not implemented");
    }
    protected List<ProductType> getProductTypes() {
        throw new UnsupportedOperationException("getProductTypes not implemented");
    }

    public String getLockPrefix() {
        throw new UnsupportedOperationException("getLockPrefix not implemented");
    }

    public void request(Long id, PaymentRequest request) {
        throw new UnsupportedOperationException("request(Long id, PaymentRequest request) not implemented");
    }

    public void request(User user, PaymentRequest request) {
        throw new UnsupportedOperationException("request(User user, PaymentRequest request) not implemented");
    }

    public void cancelRequest(T transaction) {
        throw new UnsupportedOperationException("cancelRequest not implemented");
    }
    public abstract void approve(T transaction);
    public abstract void cancelApproval(T transaction);
}
