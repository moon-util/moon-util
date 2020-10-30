package com.moon.validator.impl;

import com.moon.core.enums.Patterns;
import com.moon.validator.annotation.RequirePatternOf;

/**
 * @author moonsky
 */
public class RequirePatternValidator extends RequireValidator<RequirePatternOf> {

    public RequirePatternValidator() { }

    @Override
    public void initialize(RequirePatternOf constraintAnnotation) {
        Patterns tester = constraintAnnotation.value();
        boolean not = constraintAnnotation.not();
        setTester(not ? tester.not : tester);
    }
}
