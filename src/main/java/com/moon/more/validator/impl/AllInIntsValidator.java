package com.moon.more.validator.impl;

import com.moon.more.validator.annotation.AllInInts;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author benshaoye
 */
public class AllInIntsValidator extends CollectValidator implements ConstraintValidator<AllInInts, Object> {

    public AllInIntsValidator() { super(hashSet(), true); }

    @Override
    public void initialize(AllInInts annotation) {
        initialArgs(annotation.nullable(), annotation.values(), Integer::valueOf);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return doValidateValue(value);
    }

    @Override
    protected Class getSupportedAnnotation() { return AllInInts.class; }

    @Override
    protected boolean afterPreTransformTest(String value) { return NUMERIC.test(value); }
}
