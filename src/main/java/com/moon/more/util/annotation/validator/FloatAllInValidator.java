package com.moon.more.util.annotation.validator;

import com.moon.more.util.annotation.FloatAllIn;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author benshaoye
 */
public class FloatAllInValidator extends CollectValidator implements ConstraintValidator<FloatAllIn, Object> {

    public FloatAllInValidator() { super(hashSet(), true); }

    @Override
    public void initialize(FloatAllIn annotation) {
        initialArgs(annotation.nullable(), annotation.values(), Float::valueOf);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) { return doValidateValue(value); }

    @Override
    protected Class getTargetClass() { return FloatAllIn.class; }

    @Override
    protected boolean afterPreTransformTest(String value) { return DECIMAL.test(value); }
}
