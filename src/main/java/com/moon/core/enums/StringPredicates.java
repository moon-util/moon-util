package com.moon.core.enums;

import com.moon.core.lang.StringUtil;

import java.util.function.Predicate;

/**
 * @author benshaoye
 */
public enum StringPredicates implements Predicate<String> {
    isEmpty {
        @Override
        public boolean test(String s) {
            return s == null || s.length() == 0;
        }
    },
    isNotEmpty {
        @Override
        public boolean test(String s) { return s != null && s.length() > 0; }
    },
    isBlank {
        @Override
        public boolean test(String s) { return s == null || s.trim().length() == 0; }
    },
    isNotBlank {
        @Override
        public boolean test(String s) { return s != null && s.trim().length() > 0; }
    },
    isUndefined {
        @Override
        public boolean test(String s) { return StringUtil.isUndefined(s); }
    },
    isNotUndefined {
        @Override
        public boolean test(String s) { return !StringUtil.isUndefined(s); }
    },
    isNullString {
        @Override
        public boolean test(String s) { return StringUtil.isNullString(s); }
    },
    isNotNullString {
        @Override
        public boolean test(String s) { return !StringUtil.isNullString(s); }
    }
}
