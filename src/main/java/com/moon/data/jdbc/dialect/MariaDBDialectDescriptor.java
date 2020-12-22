package com.moon.data.jdbc.dialect;

import java.util.Set;

/**
 * @author benshaoye
 */
public class MariaDBDialectDescriptor implements DialectDescriptor {

    public MariaDBDialectDescriptor() { }

    @Override
    public Set<String> getReservedWords() {
        return null;
    }
}
