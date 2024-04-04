package com.kakaoscan.server.domain.point.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PointPaymentAmountValidator implements ConstraintValidator<PointPaymentAmount, Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value != null && (value == 500 || value == 1000 || value == 5000);
    }
}
