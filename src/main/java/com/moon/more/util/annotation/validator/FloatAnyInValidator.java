package com.moon.more.util.annotation.validator;

import com.moon.more.util.annotation.FloatAnyIn;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author benshaoye
 */
public class FloatAnyInValidator extends CollectValidator implements ConstraintValidator<FloatAnyIn, Object> {

    public FloatAnyInValidator() { super(hashSet(), false); }

    @Override
    public void initialize(FloatAnyIn annotation) {
        initialArgs(annotation.nullable(), annotation.values(), Float::valueOf);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) { return doValidateValue(value); }

    @Override
    protected Class getTargetClass() { return FloatAnyIn.class; }

    @Override
    protected boolean afterPreTransformTest(String value) { return DECIMAL.test(value); }
}
