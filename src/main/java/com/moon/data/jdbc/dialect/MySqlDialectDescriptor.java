package com.moon.data.jdbc.dialect;

import java.util.Set;

/**
 * @author benshaoye
 */
public class MySqlDialectDescriptor implements DialectDescriptor {

    public MySqlDialectDescriptor() { }

    @Override
    public Set<String> getReservedWords() {
        return null;
    }
}
