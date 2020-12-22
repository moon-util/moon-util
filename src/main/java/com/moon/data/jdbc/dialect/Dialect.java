package com.moon.data.jdbc.dialect;

/**
 * @author benshaoye
 */
public enum Dialect {
    /**
     * mysql
     */
    MY_SQL {
        @Override
        public DialectDescriptor getDescriptor() { return new MySqlDialectDescriptor(); }
    },
    MS_SQL {
        @Override
        public DialectDescriptor getDescriptor() { return new MsSqlDialectDescriptor(); }
    },
    ORACLE {
        @Override
        public DialectDescriptor getDescriptor() { return new OracleDialectDescriptor(); }
    },
    /**
     * 自动推断
     */
    AUTO {
        @Override
        public DialectDescriptor getDescriptor() { return new MySqlDialectDescriptor(); }
    },
    ;

    public abstract DialectDescriptor getDescriptor();
}
