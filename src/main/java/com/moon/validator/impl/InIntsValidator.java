package com.moon.validator.impl;

import com.moon.validator.annotation.InInts;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author benshaoye
 */
public class InIntsValidator extends BaseValidator implements ConstraintValidator<InInts, Object> {

    public InIntsValidator() { super(hashSet()); }

    @Override
    public void initialize(InInts annotation) {
        initialArgs(annotation.nullable(), annotation.values(), Integer::valueOf);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return doValidateValue(value);
    }

    @Override
    protected Class getSupportedAnnotation() { return InInts.class; }

    @Override
    protected boolean afterPreTransformTest(String value) { return NUMERIC.test(value); }
}
