package com.moon.more.util.annotation.validator;

import com.moon.more.util.annotation.StringAnyIn;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author benshaoye
 */
public class StringAnyInValidator extends CollectValidator implements ConstraintValidator<StringAnyIn, Object> {

    private String delimiter;

    public StringAnyInValidator() { super(hashSet(), false); }

    @Override
    protected String getDelimiter() { return delimiter; }

    @Override
    protected String preTransformItem(String value) { return value; }

    @Override
    protected boolean afterPreTransformTest(String value) { return true; }

    @Override
    public void initialize(StringAnyIn annotation) {
        this.delimiter = annotation.delimiter();
        initialArgs(annotation.nullable(), annotation.values(), value -> value);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return doValidateValue(value);
    }

    @Override
    protected Class getTargetClass() { return StringAnyIn.class; }
}
