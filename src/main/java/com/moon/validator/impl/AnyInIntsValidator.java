package com.moon.validator.impl;

import com.moon.validator.annotation.AnyInInts;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author benshaoye
 */
public class AnyInIntsValidator extends CollectValidator implements ConstraintValidator<AnyInInts, Object> {

    public AnyInIntsValidator() { super(hashSet(), false); }

    @Override
    public void initialize(AnyInInts annotation) {
        initialArgs(false, annotation.values(), Integer::valueOf);
        if (annotation.nullable()) {
            set.add(null);
        }
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) { return doValidateValue(value); }

    @Override
    protected Class getSupportedAnnotation() { return AnyInInts.class; }

    @Override
    protected boolean afterPreTransformTest(String value) { return NUMERIC.test(value); }
}
