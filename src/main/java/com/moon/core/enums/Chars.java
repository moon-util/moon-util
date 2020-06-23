package com.moon.core.enums;

/**
 * @author benshaoye
 */
public enum Chars implements EnumDescriptor {
    /**
     * 点：,
     */
    DOT(46),
    /**
     * 空格
     */
    SPACE(32),
    /**
     * 百分号：%
     */
    PERCENT(37),
    /**
     * 下划线：_
     */
    UNDERLINE(95),
    /**
     * 乘号：*
     */
    MULTIPLY(42),
    /**
     * 加号：+
     */
    PLUS(43),
    /**
     * 逗号：,
     */
    COMMA(44),
    /**
     * 减号：-
     */
    MINUS(45),
    /**
     * 除号：/
     */
    DIVISION(47),
    /**
     * 冒号：:
     */
    COLON(58),
    /**
     * 竖线：|
     */
    VERTICAL_LINE(124),
    ;

    public final char value;

    Chars(int intValue) { this.value = ((char) intValue); }

    public Character getObject() { return value; }

    @Override
    public String getText() { return toString(); }

    @Override
    public String toString() { return String.valueOf(value); }
}
