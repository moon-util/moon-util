package com.moon.more.util.annotation.validator;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.lang.String.format;
import static org.springframework.util.StringUtils.trimAllWhitespace;

/**
 * @author benshaoye
 */
public abstract class BaseValidator {

    protected final static Predicate<String> NUMERIC = str -> str.matches("[\\d]+");

    protected final static Predicate<String> DECIMAL = str -> str.matches("^[\\d]+\\.([\\d]+)?$") ||
        str.matches("^[\\d]+(\\.[\\d]+)?$") ||
        str.matches("^[\\d]*\\.[\\d]+$");

    protected final static Set hashSet() { return new HashSet(); }

    protected boolean allowNull;

    protected final Set set;

    protected BaseValidator(Set set) {this.set = set;}

    protected abstract Class getTargetClass();

    protected abstract boolean afterPreTransformTest(String value);

    protected String preTransformItem(String value) { return trimAllWhitespace(value); }

    protected String getTargetName() { return getTargetClass().getSimpleName(); }

    protected String getDelimiter() { return ","; }

    protected String getMessage(Object val, Object values) {
        String template = "Invalid value [%s]. Required in declare @%s#values() of [%s]";
        return format(template, val, getTargetName(), values);
    }

    protected void initialArgs(boolean nullable, String value, Function<String, ?> transformer) {
        boolean allow = this.allowNull = nullable;
        String[] values = value.split(getDelimiter());
        Set set = this.set;
        for (int i = 0; i < values.length; i++) {
            String val = preTransformItem(values[i]);
            if (afterPreTransformTest(val)) {
                set.add(transformer.apply(val));
            } else if (("".equals(val) || "null".equals(val)) && allow) {
                set.add(null);
            } else {
                throw new IllegalArgumentException(getMessage(val, value));
            }
        }
    }

    protected boolean doValidateValue(Object value) { return originalValid(value); }

    protected final boolean originalValid(Object value) { return value == null ? allowNull : set.contains(value); }
}
