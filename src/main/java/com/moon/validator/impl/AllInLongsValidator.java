package com.moon.validator.impl;

import com.moon.validator.annotation.AllInLongs;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author benshaoye
 */
public class AllInLongsValidator extends CollectValidator implements ConstraintValidator<AllInLongs, Object> {

    public AllInLongsValidator() { super(hashSet(), true); }

    @Override
    public void initialize(AllInLongs annotation) {
        initialArgs(annotation.nullable(), annotation.values(), Long::valueOf);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) { return doValidateValue(value); }

    @Override
    protected Class getSupportedAnnotation() { return AllInLongs.class; }

    @Override
    protected boolean afterPreTransformTest(String value) { return NUMERIC.test(value); }
}
