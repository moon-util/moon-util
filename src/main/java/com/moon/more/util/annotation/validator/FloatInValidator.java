package com.moon.more.util.annotation.validator;

import com.moon.more.util.annotation.FloatIn;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author benshaoye
 */
public class FloatInValidator extends BaseValidator implements ConstraintValidator<FloatIn, Object> {

    public FloatInValidator() { super(hashSet()); }

    @Override
    public void initialize(FloatIn annotation) {
        initialArgs(annotation.nullable(), annotation.values(), Float::valueOf);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) { return doValidateValue(value); }

    @Override
    protected Class getTargetClass() { return FloatIn.class; }

    @Override
    protected boolean afterPreTransformTest(String value) { return DECIMAL.test(value); }
}
