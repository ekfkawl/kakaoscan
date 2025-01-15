package com.kakaoscan.server.application.service.strategy;

import com.kakaoscan.server.domain.product.entity.ProductTransaction;
import com.kakaoscan.server.domain.product.enums.ProductType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static io.ekfkawl.ExceptionSupportUtils.handleException;

@Component
public class ProductTransactionFactory {
    private final Map<ProductType, ProductTransactionProcessor<ProductTransaction>> processorMap = new EnumMap<>(ProductType.class);

    public ProductTransactionFactory(List<ProductTransactionProcessor<ProductTransaction>> processorList) {
        for (ProductTransactionProcessor<ProductTransaction> processor : processorList) {
            try {
                processorMap.put(processor.getProductType(), processor);
            } catch (UnsupportedOperationException e) {
                try {
                    for (ProductType productType : processor.getProductTypes()) {
                        processorMap.put(productType, processor);
                    }
                } catch (UnsupportedOperationException ex) {
                    handleException("getProductTypes not implemented", ex);
                }
            }
        }
    }

    public ProductTransactionProcessor<ProductTransaction> getProcessor(ProductType productType) {
        return processorMap.get(productType);
    }
}
