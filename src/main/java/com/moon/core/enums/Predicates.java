package com.moon.core.enums;

import java.util.function.Predicate;

/**
 * @author benshaoye
 */
public enum Predicates implements Predicate {
    TRUE {
        @Override
        public boolean test(Object object) { return true; }
    },
    FALSE {
        @Override
        public boolean test(Object object) { return false; }
    },
    isNull {
        @Override
        public boolean test(Object object) { return object == null; }
    },
    isNotNull {
        @Override
        public boolean test(Object object) { return object != null; }
    }
}
