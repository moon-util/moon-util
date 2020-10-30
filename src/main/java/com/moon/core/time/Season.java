package com.moon.core.time;

import com.moon.core.enums.EnumDescriptor;

/**
 * 季节、季度枚举
 *
 * @author moonsky
 */
public enum Season implements EnumDescriptor {
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

    Season(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getQuarterValue() { return value; }

    public static Season ofMonth(int value) {
        switch (value) {
            case 1:
            case 2:
            case 3:
                return SPRING;
            case 4:
            case 5:
            case 6:
                return SUMMER;
            case 7:
            case 8:
            case 9:
                return AUTUMN;
            case 10:
            case 11:
            case 12:
                return WINTER;
            default:
                throw new IllegalArgumentException("Invalid month value of: " + value + "; Expected 1 ~ 12;");
        }
    }

    public static Season of(int value) {
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
