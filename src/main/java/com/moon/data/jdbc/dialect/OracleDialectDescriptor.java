package com.moon.data.jdbc.dialect;

import java.util.Set;

/**
 * @author benshaoye
 */
public class OracleDialectDescriptor implements DialectDescriptor {

    public OracleDialectDescriptor() { }

    @Override
    public Set<String> getReservedWords() {
        return null;
    }
}
