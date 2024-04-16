package com.kakaoscan.server.domain.point.validation;

import com.kakaoscan.server.application.dto.request.PointPaymentRequest;
import com.kakaoscan.server.domain.product.enums.ProductType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PointPaymentAmountValidator implements ConstraintValidator<PointPaymentAmount, Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        ProductType productType = PointPaymentRequest.getProductType(value);

        return productType != ProductType.UNKNOWN;
    }
}
