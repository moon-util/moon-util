package com.moon.more.util.annotation.validator;

import com.moon.more.util.annotation.ByteAnyIn;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author benshaoye
 */
public class ByteAnyInValidator extends CollectValidator implements ConstraintValidator<ByteAnyIn, Object> {

    public ByteAnyInValidator() { super(hashSet(), false); }

    @Override
    public void initialize(ByteAnyIn annotation) {
        initialArgs(annotation.nullable(), annotation.values(), Byte::valueOf);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) { return doValidateValue(value); }

    @Override
    protected Class getTargetClass() { return ByteAnyIn.class; }

    @Override
    protected boolean afterPreTransformTest(String value) { return NUMERIC.test(value); }
}
