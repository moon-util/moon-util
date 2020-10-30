package com.moon.validator.impl;

import com.moon.validator.annotation.AnyInDoubles;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author benshaoye
 */
public class AnyInDoublesValidator extends CollectValidator implements ConstraintValidator<AnyInDoubles, Object> {

    public AnyInDoublesValidator() { super(hashSet(), false); }

    @Override
    public void initialize(AnyInDoubles annotation) {
        initialArgs(false, annotation.values(), Double::valueOf);
        if (annotation.nullable()) {
            set.add(null);
        }
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) { return doValidateValue(value); }

    @Override
    protected Class getSupportedAnnotation() { return AnyInDoubles.class; }

    @Override
    protected boolean afterPreTransformTest(String value) { return DECIMAL.test(value); }
}
