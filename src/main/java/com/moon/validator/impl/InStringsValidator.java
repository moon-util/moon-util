package com.moon.validator.impl;

import com.moon.validator.annotation.InStrings;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author benshaoye
 */
public class InStringsValidator extends BaseValidator implements ConstraintValidator<InStrings, Object> {

    private String delimiter;
    private boolean ignoreSpace;

    public InStringsValidator() { super(hashSet()); }

    @Override
    protected String getDelimiter() { return delimiter; }

    @Override
    protected String preTransformItem(String value) { return ignoreSpace ? value.trim() : value; }

    @Override
    protected boolean afterPreTransformTest(String value) { return true; }

    @Override
    public void initialize(InStrings annotation) {
        this.delimiter = annotation.delimiter();
        this.ignoreSpace = annotation.trimmed();
        initialArgs(annotation.nullable(), annotation.values(), value -> value);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return doValidateValue(value == null ? null : value.toString());
    }

    @Override
    protected Class getSupportedAnnotation() { return InStrings.class; }
}
