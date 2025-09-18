package com.kakaoscan.server.domain.events.model;

import com.kakaoscan.server.domain.events.model.common.EventMetadata;
import com.kakaoscan.server.infrastructure.email.types.ProductPurchaseCompleteEmail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductPurchaseCompleteEvent extends EventMetadata {
    private ProductPurchaseCompleteEmail purchaseCompleteEmail;

    public ProductPurchaseCompleteEvent(String receiver, String productName, String domain) {
        this.purchaseCompleteEmail = new ProductPurchaseCompleteEmail(receiver, productName, domain);
    }
}
