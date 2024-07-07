package com.kakaoscan.server.application.service.strategy;

import com.kakaoscan.server.domain.product.enums.ProductType;

import java.util.List;

public abstract class ProductTransactionProcessor<T> {
    protected ProductType getProductType() {
        throw new UnsupportedOperationException("getProductType not implemented");
    }
    protected List<ProductType> getProductTypes() {
        throw new UnsupportedOperationException("getProductTypes not implemented");
    }
    public abstract void request(T transaction);
    public abstract void cancelRequest(T transaction);
    public abstract void approve(T transaction);
    public abstract void cancelApproval(T transaction);
}
