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
    /**
     * MariaDB
     */
    MariaDB {
        @Override
        public DialectDescriptor getDescriptor() { return new MariaDBDialectDescriptor(); }
    },
    /**
     * MariaDB
     */
    MS_SQL {
        @Override
        public DialectDescriptor getDescriptor() { return new MsSqlDialectDescriptor(); }
    },
    /**
     * Oracle
     */
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
