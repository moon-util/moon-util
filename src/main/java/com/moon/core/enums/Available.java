package com.moon.core.enums;

/**
 * @author benshaoye
 */
public enum Available implements EnumDescriptor {
    YES,
    NO,
    ;

    @Override
    public String getText() {
        return name();
    }
}
