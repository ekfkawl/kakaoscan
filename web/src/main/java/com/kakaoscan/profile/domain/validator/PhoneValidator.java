package com.kakaoscan.profile.domain.validator;

import com.kakaoscan.profile.domain.validator.annotation.Phone;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

public class PhoneValidator implements ConstraintValidator<Phone, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        String v = Optional.ofNullable(value).orElseGet(String::new);

        return v.length() == 11;
    }
}
