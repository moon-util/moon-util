package com.moon.validator.impl;

import com.moon.validator.annotation.AllInDoubles;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author benshaoye
 */
public class AllInDoublesValidator extends CollectValidator implements ConstraintValidator<AllInDoubles, Object> {

    public AllInDoublesValidator() { super(hashSet(), true); }

    @Override
    public void initialize(AllInDoubles annotation) {
        initialArgs(annotation.nullable(), annotation.values(), Double::valueOf);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) { return doValidateValue(value); }

    @Override
    protected Class getSupportedAnnotation() { return AllInDoubles.class; }

    @Override
    protected boolean afterPreTransformTest(String value) { return DECIMAL.test(value); }
}
