package com.kakaoscan.server.application.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.kakaoscan.server.domain.product.enums.ProductType;
import com.kakaoscan.server.domain.product.model.PaymentRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonTypeName("snapshotPreservationPayment")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SnapshotPaymentRequest implements PaymentRequest {
    private int amount;

    public ProductType getProductType() {
        return ProductType.SNAPSHOT_PRESERVATION;
    }
}
