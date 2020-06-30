package com.moon.more.util.annotation.validator;

import com.moon.more.util.annotation.ByteAllIn;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author benshaoye
 */
public class ByteAllInValidator extends CollectValidator implements ConstraintValidator<ByteAllIn, Object> {

    public ByteAllInValidator() { super(hashSet(), true); }

    @Override
    public void initialize(ByteAllIn annotation) {
        initialArgs(annotation.nullable(), annotation.values(), Byte::valueOf);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) { return doValidateValue(value); }

    @Override
    protected Class getTargetClass() { return ByteAllIn.class; }

    @Override
    protected boolean afterPreTransformTest(String value) { return NUMERIC.test(value); }
}
