package com.moon.more.validator.impl;

import com.moon.core.util.validator.ResidentID18Validator;
import com.moon.more.validator.annotation.ResidentID18;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author moonsky
 */
public class ResidentID18Validation implements ConstraintValidator<ResidentID18, CharSequence> {

    public ResidentID18Validation() {
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        return ResidentID18Validator.isValid(value);
    }
}
