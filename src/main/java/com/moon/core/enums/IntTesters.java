package com.moon.core.enums;

import com.moon.core.lang.CharUtil;

import java.util.function.IntPredicate;
import java.util.function.Predicate;

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
     * 奇数
     */
    ODD {
        @Override
        public boolean test(int value) { return (value & 1) == 1; }
    },
    /**
     * 偶数
     */
    EVEN {
        @Override
        public boolean test(int value) { return (value & 1) == 0; }
    },
    /**
     * 汉字
     */
    CHINESE_WORD {
        @Override
        public boolean test(int value) { return value > 19967 && value < 40880; }
    };

    public final Predicate<Number> notPredicate;
    public final Predicate<Number> predicate;
    public final IntPredicate not;

    IntTesters() {
        predicate = n -> n != null && test(n.intValue());
        notPredicate = predicate.negate();
        not = value -> !test(value);
    }

    public Predicate<Number> asPredicate() { return predicate; }
}
