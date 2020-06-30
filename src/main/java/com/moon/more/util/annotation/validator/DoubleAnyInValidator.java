package com.moon.more.util.annotation.validator;

import com.moon.more.util.annotation.DoubleAnyIn;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author benshaoye
 */
public class DoubleAnyInValidator extends CollectValidator implements ConstraintValidator<DoubleAnyIn, Object> {

    public DoubleAnyInValidator() { super(hashSet(), false); }

    @Override
    public void initialize(DoubleAnyIn annotation) {
        initialArgs(annotation.nullable(), annotation.values(), Double::valueOf);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) { return doValidateValue(value); }

    @Override
    protected Class getTargetClass() { return DoubleAnyIn.class; }

    @Override
    protected boolean afterPreTransformTest(String value) { return DECIMAL.test(value); }
}
