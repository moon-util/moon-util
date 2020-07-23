package com.moon.core.enums;

/**
 * 是否有效、可用等
 *
 * @author moonsky
 */
public enum Available implements EnumDescriptor {
    /**
     * 否
     */
    NO,
    /**
     * 是
     */
    YES,
    ;

    public final static String WHERE_IDX = " available=1 ";

    public final static String WHERE_STR = " available='YES' ";

    @Override
    public String getText() { return name(); }
}
