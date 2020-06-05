package com.moon.core.net.enums;

import com.moon.core.enums.EnumDescriptor;

/**
 * @author benshaoye
 */
@SuppressWarnings("all")
public enum RequestMethod implements EnumDescriptor {
    GET,
    HEAD,
    POST,
    PUT,
    PATCH,
    DELETE,
    OPTIONS,
    TRACE,
    ;

    @Override
    public String getText() { return name(); }
}
