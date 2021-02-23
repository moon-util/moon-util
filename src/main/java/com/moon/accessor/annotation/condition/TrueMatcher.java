package com.moon.accessor.annotation.condition;

/**
 * @author benshaoye
 */
public class TrueMatcher implements PropertyMatcher<String, IfTrue> {

    @Override
    public boolean test(String value, IfTrue annotation) {
        return false;
    }
}
