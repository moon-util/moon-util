package com.moon.more.util.annotation.validator;

import com.moon.more.util.annotation.StringAllIn;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author benshaoye
 */
public class StringAllInValidator extends CollectValidator implements ConstraintValidator<StringAllIn, Object> {

    private String delimiter;

    public StringAllInValidator() { super(hashSet(), true); }

    @Override
    protected String getDelimiter() { return delimiter; }

    @Override
    protected String preTransformItem(String value) { return value; }

    @Override
    protected boolean afterPreTransformTest(String value) { return true; }

    @Override
    public void initialize(StringAllIn annotation) {
        this.delimiter = annotation.delimiter();
        initialArgs(annotation.nullable(), annotation.values(), value -> value);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return doValidateValue(value);
    }

    @Override
    protected Class getTargetClass() { return StringAllIn.class; }
}
