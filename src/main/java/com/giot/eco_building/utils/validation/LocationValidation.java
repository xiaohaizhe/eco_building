package com.giot.eco_building.utils.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @Author: pyt
 * @Date: 2020/6/11 14:25
 * @Description:
 */
@Documented
@Constraint(
        validatedBy = {LocationValidationValidator.class}
)
@SupportedValidationTarget({ValidationTarget.ANNOTATED_ELEMENT})
@Target({METHOD, FIELD, ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RUNTIME)
public @interface LocationValidation {
    float min() default -180F;
    float max() default 180F;


    String message() default "经纬度范围错误";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        LocationValidation[] value();
    }
}
