package com.moon.more.util.annotation.validator;

import com.moon.more.util.annotation.IntAllIn;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author benshaoye
 */
public class IntAllInValidator extends CollectValidator implements ConstraintValidator<IntAllIn, Object> {

    public IntAllInValidator() { super(hashSet(), true); }

    @Override
    public void initialize(IntAllIn annotation) {
        initialArgs(annotation.nullable(), annotation.values(), Integer::valueOf);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return doValidateValue(value);
    }

    @Override
    protected Class getTargetClass() { return IntAllIn.class; }

    @Override
    protected boolean afterPreTransformTest(String value) { return NUMERIC.test(value); }
}
