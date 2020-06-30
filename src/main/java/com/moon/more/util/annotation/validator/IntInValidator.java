package com.moon.more.util.annotation.validator;

import com.moon.more.util.annotation.IntIn;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author benshaoye
 */
public class IntInValidator extends BaseValidator implements ConstraintValidator<IntIn, Object> {

    public IntInValidator() { super(hashSet()); }

    @Override
    public void initialize(IntIn annotation) {
        initialArgs(annotation.nullable(), annotation.values(), Integer::valueOf);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) { return doValidateValue(value); }

    @Override
    protected Class getTargetClass() { return IntIn.class; }

    @Override
    protected boolean afterPreTransformTest(String value) { return NUMERIC.test(value); }
}
