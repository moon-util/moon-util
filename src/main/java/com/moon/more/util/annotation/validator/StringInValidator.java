package com.moon.more.util.annotation.validator;

import com.moon.more.util.annotation.StringIn;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author benshaoye
 */
public class StringInValidator extends BaseValidator implements ConstraintValidator<StringIn, Object> {

    private String delimiter;

    public StringInValidator() { super(hashSet()); }

    @Override
    protected String getDelimiter() { return delimiter; }

    @Override
    protected String preTransformItem(String value) { return value; }

    @Override
    protected boolean afterPreTransformTest(String value) { return true; }

    @Override
    public void initialize(StringIn annotation) {
        this.delimiter = annotation.delimiter();
        initialArgs(annotation.nullable(), annotation.values(), value -> value);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return doValidateValue(value == null ? null : value.toString());
    }

    @Override
    protected Class getTargetClass() { return StringIn.class; }
}
