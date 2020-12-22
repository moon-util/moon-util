package com.moon.data.jdbc.dialect;

import java.util.Set;

/**
 * @author benshaoye
 */
public class MsSqlDialectDescriptor implements DialectDescriptor {

    public MsSqlDialectDescriptor() { }

    @Override
    public Set<String> getReservedWords() {
        return null;
    }
}
