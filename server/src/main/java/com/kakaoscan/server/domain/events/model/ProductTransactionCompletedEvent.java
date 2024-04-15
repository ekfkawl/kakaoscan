package com.kakaoscan.server.domain.events.model;

import com.kakaoscan.server.infrastructure.email.types.ProductTransactionCompletedEmail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductTransactionCompletedEvent extends EventMetadata {
    private ProductTransactionCompletedEmail transactionCompletedEmail;

    public ProductTransactionCompletedEvent(String receiver, String productName, String domain) {
        this.transactionCompletedEmail = new ProductTransactionCompletedEmail(receiver, productName, domain);
    }
}
