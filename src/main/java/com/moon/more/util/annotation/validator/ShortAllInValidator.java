package com.moon.more.util.annotation.validator;

import com.moon.more.util.annotation.ShortAllIn;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author benshaoye
 */
public class ShortAllInValidator extends CollectValidator implements ConstraintValidator<ShortAllIn, Object> {

    public ShortAllInValidator() { super(hashSet(), true); }

    @Override
    public void initialize(ShortAllIn annotation) {
        initialArgs(annotation.nullable(), annotation.values(), Short::valueOf);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) { return doValidateValue(value); }

    @Override
    protected Class getTargetClass() { return ShortAllIn.class; }

    @Override
    protected boolean afterPreTransformTest(String value) { return NUMERIC.test(value); }
}
