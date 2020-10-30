package com.moon.validator.impl;

import com.moon.validator.annotation.InDoubles;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author benshaoye
 */
public class InDoublesValidator extends BaseValidator implements ConstraintValidator<InDoubles, Object> {

    public InDoublesValidator() { super(hashSet()); }

    @Override
    public void initialize(InDoubles annotation) {
        initialArgs(annotation.nullable(), annotation.values(), Double::valueOf);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) { return doValidateValue(value); }

    @Override
    protected Class getSupportedAnnotation() { return InDoubles.class; }

    @Override
    protected boolean afterPreTransformTest(String value) { return DECIMAL.test(value); }
}
