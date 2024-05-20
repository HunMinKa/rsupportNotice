package io.dodn.springboot.core.api.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NoticeValidator.class)
@Target({ ElementType.TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidNotice {
    String message() default "Invalid Notice";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
