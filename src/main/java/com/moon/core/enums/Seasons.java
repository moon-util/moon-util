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

    public static Seasons of(int value) {
        switch (value) {
            case 1:
                return SPRING;
            case 2:
                return SUMMER;
            case 3:
                return AUTUMN;
            case 4:
                return WINTER;
            default:
                throw new IllegalArgumentException("Invalid quarter value of: " + value + "; Expected 1 ~ 4;");
        }
    }

    @Override
    public String getText() { return text; }
}
