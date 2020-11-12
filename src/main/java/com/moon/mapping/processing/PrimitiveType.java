package com.moon.mapping.processing;

/**
 * @author benshaoye
 */
public enum PrimitiveType {
    /**
     * 数字
     */
    BYTE(true),
    SHORT(true),
    INT(true),
    LONG(true),
    FLOAT(true),
    DOUBLE(true),
    /**
     * 其他
     */
    CHAR(false),
    BOOLEAN(false);

    private final boolean number;

    PrimitiveType(boolean isNumber) {
        this.number = isNumber;
    }

    public boolean isNumber() { return number; }

    static PrimitiveType from(String value) {
        try {
            return valueOf(value.toUpperCase());
        } catch (Throwable t) {
            // 对应枚举不存在
            return null;
        }
    }

    // static boolean isNumber
}
