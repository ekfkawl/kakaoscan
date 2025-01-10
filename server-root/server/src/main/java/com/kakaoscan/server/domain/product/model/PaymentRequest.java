package com.kakaoscan.server.domain.product.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.kakaoscan.server.application.dto.request.PointPaymentRequest;
import com.kakaoscan.server.application.dto.request.SnapshotPaymentRequest;
import com.kakaoscan.server.domain.product.enums.ProductType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PointPaymentRequest.class, name = "pointPayment"),
        @JsonSubTypes.Type(value = SnapshotPaymentRequest.class, name = "snapshotPreservationPayment"),
})
public interface PaymentRequest {
    int getAmount();
    ProductType getProductType();
}
