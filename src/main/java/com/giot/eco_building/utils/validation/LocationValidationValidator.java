package com.giot.eco_building.utils.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @Author: pyt
 * @Date: 2020/6/11 14:26
 * @Description:
 */
public class LocationValidationValidator implements ConstraintValidator<LocationValidation, Float> {
    private Float max;
    private Float min;

    @Override
    public void initialize(LocationValidation constraintAnnotation) {
        this.max = constraintAnnotation.max();
        this.min = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(Float value, ConstraintValidatorContext context) {
        return value == null || (value >= min && value <= max);
    }
}
