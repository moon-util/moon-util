package com.moon.more.util.annotation.validator;

import com.moon.more.util.annotation.ShortIn;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author benshaoye
 */
public class ShortInValidator extends BaseValidator implements ConstraintValidator<ShortIn, Object> {

    public ShortInValidator() { super(hashSet()); }

    @Override
    public void initialize(ShortIn annotation) {
        initialArgs(annotation.nullable(), annotation.values(), Short::valueOf);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) { return doValidateValue(value); }

    @Override
    protected Class getTargetClass() { return ShortIn.class; }

    @Override
    protected boolean afterPreTransformTest(String value) { return NUMERIC.test(value); }
}
