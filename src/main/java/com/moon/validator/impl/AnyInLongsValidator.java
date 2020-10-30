package com.moon.validator.impl;

import com.moon.validator.annotation.AnyInLongs;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author benshaoye
 */
public class AnyInLongsValidator extends CollectValidator implements ConstraintValidator<AnyInLongs, Object> {

    public AnyInLongsValidator() { super(hashSet(), false); }

    @Override
    public void initialize(AnyInLongs annotation) {
        initialArgs(false, annotation.values(), Long::valueOf);
        if (annotation.nullable()) {
            set.add(null);
        }
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) { return doValidateValue(value); }

    @Override
    protected Class getSupportedAnnotation() { return AnyInLongs.class; }

    @Override
    protected boolean afterPreTransformTest(String value) { return NUMERIC.test(value); }
}
