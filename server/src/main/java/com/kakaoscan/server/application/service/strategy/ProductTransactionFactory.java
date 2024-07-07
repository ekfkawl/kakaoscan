package com.kakaoscan.server.application.service.strategy;

import com.kakaoscan.server.domain.product.enums.ProductType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class ProductTransactionFactory {
    private final Map<ProductType, ProductTransactionProcessor> processorMap = new EnumMap<>(ProductType.class);

    public ProductTransactionFactory(List<ProductTransactionProcessor> processorList) {
        for (ProductTransactionProcessor processor : processorList) {
            if (!(processor instanceof PointTransactionProcessor)) {
                continue;
            }

            if (processor.getProductType() == null) {
                for (ProductType productType : processor.getProductTypes()) {
                    processorMap.put(productType, processor);
                }
            }else {
                processorMap.put(processor.getProductType(), processor);
            }
        }
    }

    public ProductTransactionProcessor getProcessor(ProductType productType) {
        return processorMap.get(productType);
    }
}
