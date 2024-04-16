package com.kakaoscan.server.application.dto.request;

import com.kakaoscan.server.domain.point.validation.PointPaymentAmount;
import com.kakaoscan.server.domain.product.enums.ProductType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PointPaymentRequest  {

    @PointPaymentAmount
    private int amount;

    public ProductType getProductType() {
        return PointPaymentRequest.getProductType(this.amount);
    }

    public static ProductType getProductType(int amount) {
        return switch (amount) {
            case 500 -> ProductType.P500;
            case 1000 -> ProductType.P1000;
            case 2000 -> ProductType.P2000;
            case 5000 -> ProductType.P5000;
            case 10000 -> ProductType.P10000;
            default -> ProductType.UNKNOWN;
        };
    }
}
