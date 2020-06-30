package com.moon.more.util.annotation.validator;

import com.moon.more.util.annotation.IntAnyIn;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author benshaoye
 */
public class IntAnyInValidator extends CollectValidator implements ConstraintValidator<IntAnyIn, Object> {

    public IntAnyInValidator() { super(hashSet(), false); }

    @Override
    public void initialize(IntAnyIn annotation) {
        initialArgs(annotation.nullable(), annotation.values(), Integer::valueOf);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) { return doValidateValue(value); }

    @Override
    protected Class getTargetClass() { return IntAnyIn.class; }

    @Override
    protected boolean afterPreTransformTest(String value) { return NUMERIC.test(value); }
}
