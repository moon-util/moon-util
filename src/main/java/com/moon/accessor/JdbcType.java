package com.moon.accessor;

import java.sql.Types;

/**
 * @author benshaoye
 */
public enum JdbcType {
    /** -7 */
    BIT(Types.BIT),
    /** -6 */
    TINYINT(Types.TINYINT),
    /** 5 */
    SMALLINT(Types.SMALLINT),
    /** 4 */
    INTEGER(Types.INTEGER),
    /** -5 */
    BIGINT(Types.BIGINT),
    /** 6 */
    FLOAT(Types.FLOAT),
    /** 7 */
    REAL(Types.REAL),
    /** 8 */
    DOUBLE(Types.DOUBLE),
    /** 2 */
    NUMERIC(Types.NUMERIC),
    /** 3 */
    DECIMAL(Types.DECIMAL),
    /** 1 */
    CHAR(Types.CHAR),
    /** 12 */
    VARCHAR(Types.VARCHAR),
    /** -1 */
    LONGVARCHAR(Types.LONGVARCHAR),
    /** 91 */
    DATE(Types.DATE),
    /** 92 */
    TIME(Types.TIME),
    /** 93 */
    TIMESTAMP(Types.TIMESTAMP),
    /** -2 */
    BINARY(Types.BINARY),
    /** -3 */
    VARBINARY(Types.VARBINARY),
    /** -4 */
    LONGVARBINARY(Types.LONGVARBINARY),
    /** 0 */
    NULL(Types.NULL),
    /** 1111 */
    OTHER(Types.OTHER),
    /** 2000 */
    JAVA_OBJECT(Types.JAVA_OBJECT),
    /** 2001 */
    DISTINCT(Types.DISTINCT),
    /** 2002 */
    STRUCT(Types.STRUCT),
    /** 2003 */
    ARRAY(Types.ARRAY),
    /** 2004 */
    BLOB(Types.BLOB),
    /** 2005 */
    CLOB(Types.CLOB),
    /** 2006 */
    REF(Types.REF),
    /** 70 */
    DATALINK(Types.DATALINK),
    /** 16 */
    BOOLEAN(Types.BOOLEAN),
    /** -8 */
    ROWID(Types.ROWID),
    /** -15 */
    NCHAR(Types.NCHAR),
    /** -9 */
    NVARCHAR(Types.NVARCHAR),
    /** -16 */
    LONGNVARCHAR(Types.LONGNVARCHAR),
    /** 2011 */
    NCLOB(Types.NCLOB),
    /** 2009 */
    SQLXML(Types.SQLXML),
    /** 2012 */
    REF_CURSOR(Types.REF_CURSOR),
    /** 2013 */
    TIME_WITH_TIMEZONE(Types.TIME_WITH_TIMEZONE),
    /** 2014 */
    TIMESTAMP_WITH_TIMEZONE(Types.TIMESTAMP_WITH_TIMEZONE),
    ;

    private final int type;

    JdbcType(int type) { this.type = type; }

    public int getType() { return type; }
}
