package com.moon.more.validator.impl;

import com.moon.more.validator.annotation.AllInStrings;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author benshaoye
 */
public class AllInStringsValidator extends CollectValidator implements ConstraintValidator<AllInStrings, Object> {

    private String delimiter;
    private boolean ignoreSpace;

    public AllInStringsValidator() { super(hashSet(), true); }

    @Override
    protected String getDelimiter() { return delimiter; }

    @Override
    protected String preTransformItem(String value) { return ignoreSpace ? value.trim() : value; }

    @Override
    protected boolean afterPreTransformTest(String value) { return true; }

    @Override
    public void initialize(AllInStrings annotation) {
        this.delimiter = annotation.delimiter();
        this.ignoreSpace = annotation.trimmed();
        initialArgs(annotation.nullable(), annotation.values(), value -> value);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return doValidateValue(value);
    }

    @Override
    protected Class getSupportedAnnotation() { return AllInStrings.class; }
}
