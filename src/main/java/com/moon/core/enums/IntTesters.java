package com.moon.core.enums;

import com.moon.core.lang.CharUtil;

import java.util.function.IntPredicate;

/**
 * @author moonsky
 */
public enum IntTesters implements IntPredicate {
    /**
     * true
     */
    TRUE {
        @Override
        public boolean test(int value) { return true; }
    },
    /**
     * false
     */
    FALSE {
        @Override
        public boolean test(int value) { return false; }
    },
    /**
     * 数字
     */
    DIGIT {
        @Override
        public boolean test(int value) { return CharUtil.isDigit(value); }
    },
    /**
     * 数字
     */
    NUMERIC {
        @Override
        public boolean test(int value) { return CharUtil.isDigit(value); }
    },
    /**
     * 大写或小写字母
     */
    LETTER {
        @Override
        public boolean test(int value) { return CharUtil.isLetter(value); }
    },
    /**
     * 大写字母
     */
    UPPER {
        @Override
        public boolean test(int value) { return CharUtil.isUpperCase(value); }
    },
    /**
     * 小写字母
     */
    LOWER {
        @Override
        public boolean test(int value) { return CharUtil.isLowerCase(value); }
    },
    /**
     * 空白字符
     */
    BLANK {
        @Override
        public boolean test(int value) { return Character.isWhitespace(value); }
    },
    /**
     * 0
     */
    ZERO {
        @Override
        public boolean test(int value) { return value == 0; }
    },
    /**
     * 1
     */
    ONE {
        @Override
        public boolean test(int value) { return value == 1; }
    },
    /**
     * 负整数
     */
    NEGATIVE {
        @Override
        public boolean test(int value) { return value < 0; }
    },
    /**
     * 负整数和〇
     */
    NEGATIVE_OR_ZERO {
        @Override
        public boolean test(int value) { return value <= 0; }
    },
    /**
     * 正整数
     */
    POSITIVE {
        @Override
        public boolean test(int value) { return value > 0; }
    },
    /**
     * 自然数：正整数和〇
     */
    POSITIVE_OR_ZERO {
        @Override
        public boolean test(int value) { return value >= 0; }
    },
    /**
     * 汉字
     */
    CHINESE_WORD {
        @Override
        public boolean test(int value) { return value > 19967 && value < 40880; }
    };
}
