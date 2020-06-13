package com.moon.more.excel.table;

import com.moon.core.lang.StringUtil;

import java.util.function.Predicate;

/**
 * @author benshaoye
 */
enum TableColDefaultTester implements Predicate {
    /**
     * null
     */
    NULL {
        @Override
        public boolean test(Object o) { return o == null; }
    },
    /**
     * 空字符串
     */
    EMPTY {
        @Override
        public boolean test(Object o) {
            return o == null || o.toString().length() == 0;
        }
    },
    /**
     * 空白字符串
     */
    BLANK {
        @Override
        public boolean test(Object o) {
            return o == null || StringUtil.isBlank(o.toString());
        }
    },
    /**
     * 等于数字 0
     */
    ZERO {
        @Override
        public boolean test(Object o) {
            return ((Number) o).intValue() == 0;
        }
    },
    /**
     * 负数
     */
    NEGATIVE {
        @Override
        public boolean test(Object o) {
            return ((Number) o).intValue() < 0;
        }
    },
    /**
     * 正数
     */
    POSITIVE {
        @Override
        public boolean test(Object o) {
            return ((Number) o).intValue() > 0;
        }
    }
}
