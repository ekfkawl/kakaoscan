package com.kakaoscan.profile.domain.validator.annotation;

import com.kakaoscan.profile.domain.validator.CheckKeyValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 데이터베이스에 값 저장 권한 키 체크
 */
@Documented
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Constraint(validatedBy = CheckKeyValidator.class)
public @interface CheckKey {
    String message() default "";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default {};
}
