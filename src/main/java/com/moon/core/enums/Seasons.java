package com.moon.core.enums;

/**
 * 季节、季度枚举
 *
 * @author moonsky
 */
public enum Seasons implements EnumDescriptor {
    /**
     * 春
     */
    SPRING(1, "春季"),
    /**
     * 夏
     */
    SUMMER(2, "夏季"),
    /**
     * 秋
     */
    AUTUMN(3, "秋季"),
    /**
     * 冬
     */
    WINTER(4, "冬季");
    private final int value;
    private final String text;

    Seasons(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getQuarterValue() { return value; }

    @Override
    public String getText() { return text; }
}
