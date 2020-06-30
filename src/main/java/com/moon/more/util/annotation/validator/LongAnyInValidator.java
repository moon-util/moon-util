package com.moon.more.util.annotation.validator;

import com.moon.more.util.annotation.LongAnyIn;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author benshaoye
 */
public class LongAnyInValidator extends CollectValidator implements ConstraintValidator<LongAnyIn, Object> {

    public LongAnyInValidator() { super(hashSet(), false); }

    @Override
    public void initialize(LongAnyIn annotation) {
        initialArgs(annotation.nullable(), annotation.values(), Long::valueOf);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) { return doValidateValue(value); }

    @Override
    protected Class getTargetClass() { return LongAnyIn.class; }

    @Override
    protected boolean afterPreTransformTest(String value) { return NUMERIC.test(value); }
}
