package com.moon.validator.impl;

import com.moon.validator.annotation.AnyInStrings;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author benshaoye
 */
public class AnyInStringsValidator extends CollectValidator implements ConstraintValidator<AnyInStrings, Object> {

    private String delimiter;
    private boolean ignoreSpace;

    public AnyInStringsValidator() { super(hashSet(), false); }

    @Override
    protected String getDelimiter() { return delimiter; }

    @Override
    protected String preTransformItem(String value) { return ignoreSpace ? value.trim() : value; }

    @Override
    protected boolean afterPreTransformTest(String value) { return true; }

    @Override
    public void initialize(AnyInStrings annotation) {
        this.delimiter = annotation.delimiter();
        this.ignoreSpace = annotation.trimmed();
        initialArgs(false, annotation.values(), value -> value);
        if (annotation.nullable()) {
            set.add(null);
        }
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return doValidateValue(value);
    }

    @Override
    protected Class getSupportedAnnotation() { return AnyInStrings.class; }
}
