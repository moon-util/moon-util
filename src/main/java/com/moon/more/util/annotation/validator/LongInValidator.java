package com.moon.more.util.annotation.validator;

import com.moon.more.util.annotation.LongIn;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author benshaoye
 */
public class LongInValidator extends BaseValidator implements ConstraintValidator<LongIn, Object> {

    public LongInValidator() { super(hashSet()); }

    @Override
    public void initialize(LongIn annotation) {
        initialArgs(annotation.nullable(), annotation.values(), Long::valueOf);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) { return doValidateValue(value); }

    @Override
    protected Class getTargetClass() { return LongIn.class; }

    @Override
    protected boolean afterPreTransformTest(String value) { return NUMERIC.test(value); }
}
