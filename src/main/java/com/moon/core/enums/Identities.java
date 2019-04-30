package com.moon.core.enums;

import com.moon.core.util.interfaces.IdGenerator;

/**
 * @author benshaoye
 */
public enum Identities implements IdGenerator<String>, EnumDescriptor {
    UUID {
        @Override
        public String nextId() { return java.util.UUID.randomUUID().toString(); }
    },
    ;

    @Override
    public String getText() { return name(); }
}
