package com.kakaoscan.server.application.service.strategy;

import com.kakaoscan.server.domain.product.entity.ProductTransaction;
import com.kakaoscan.server.domain.product.enums.ProductType;

import java.util.List;

public abstract class ProductTransactionProcessor {
    protected abstract ProductType getProductType();
    protected abstract List<ProductType> getProductTypes();
    public abstract void request(ProductTransaction transaction);
    public abstract void cancelRequest(ProductTransaction transaction);
    public abstract void approve(ProductTransaction transaction);
    public abstract void cancelApproval(ProductTransaction transaction);
}
