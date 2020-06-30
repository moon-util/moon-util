package com.moon.more.util.annotation.validator;

import com.moon.more.util.annotation.LongAllIn;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author benshaoye
 */
public class LongAllInValidator extends CollectValidator implements ConstraintValidator<LongAllIn, Object> {

    public LongAllInValidator() { super(hashSet(), true); }

    @Override
    public void initialize(LongAllIn annotation) {
        initialArgs(annotation.nullable(), annotation.values(), Long::valueOf);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) { return doValidateValue(value); }

    @Override
    protected Class getTargetClass() { return LongAllIn.class; }

    @Override
    protected boolean afterPreTransformTest(String value) { return NUMERIC.test(value); }
}
