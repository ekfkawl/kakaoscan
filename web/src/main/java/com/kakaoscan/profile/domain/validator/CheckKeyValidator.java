package com.kakaoscan.profile.domain.validator;

import com.kakaoscan.profile.domain.enums.ApiErrorCase;
import com.kakaoscan.profile.domain.exception.ApiException;
import com.kakaoscan.profile.domain.validator.annotation.CheckKey;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CheckKeyValidator implements ConstraintValidator<CheckKey, String> {

    @Value("${api.key}")
    private String key;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (!this.key.equals(value)) {
            throw new ApiException(ApiErrorCase.INVALID_PARAMETER);
        }

        return true;
    }
}
