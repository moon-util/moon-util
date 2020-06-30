package com.moon.more.util.annotation.validator;

import com.moon.more.util.annotation.DoubleIn;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author benshaoye
 */
public class DoubleInValidator extends BaseValidator implements ConstraintValidator<DoubleIn, Object> {

    public DoubleInValidator() { super(hashSet()); }

    @Override
    public void initialize(DoubleIn annotation) {
        initialArgs(annotation.nullable(), annotation.values(), Double::valueOf);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) { return doValidateValue(value); }

    @Override
    protected Class getTargetClass() { return DoubleIn.class; }

    @Override
    protected boolean afterPreTransformTest(String value) { return DECIMAL.test(value); }
}
