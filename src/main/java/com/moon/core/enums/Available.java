package com.moon.core.enums;

/**
 * 是否有效、可用等
 *
 * @author benshaoye
 */
public enum Available implements EnumDescriptor {
    /**
     * 是
     */
    YES,
    /**
     * 否
     */
    NO,
    ;

    @Override
    public String getText() { return name(); }
}
