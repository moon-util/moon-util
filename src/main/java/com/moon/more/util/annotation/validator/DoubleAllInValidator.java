package com.moon.more.util.annotation.validator;

import com.moon.more.util.annotation.DoubleAllIn;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author benshaoye
 */
public class DoubleAllInValidator extends CollectValidator implements ConstraintValidator<DoubleAllIn, Object> {

    public DoubleAllInValidator() { super(hashSet(), true); }

    @Override
    public void initialize(DoubleAllIn annotation) {
        initialArgs(annotation.nullable(), annotation.values(), Double::valueOf);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) { return doValidateValue(value); }

    @Override
    protected Class getTargetClass() { return DoubleAllIn.class; }

    @Override
    protected boolean afterPreTransformTest(String value) { return DECIMAL.test(value); }
}
