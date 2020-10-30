package com.moon.validator.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;
import java.util.function.Predicate;

/**
 * @author moonsky
 */
public class RequireValidator<T extends Annotation> implements ConstraintValidator<T, CharSequence> {

    private Predicate tester;

    public RequireValidator() {
    }

    protected void setTester(Predicate tester) {
        this.tester = tester;
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        return tester.test(value);
    }
}
