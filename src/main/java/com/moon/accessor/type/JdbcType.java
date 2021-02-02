package com.moon.accessor.type;

import java.sql.JDBCType;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public enum JdbcType {
    BIT(Types.BIT),
    /**
     * byte
     */
    TINYINT(Types.TINYINT),
    /**
     * short
     */
    SMALLINT(Types.SMALLINT),
    /**
     * int
     */
    INTEGER(Types.INTEGER),
    /**
     * long, BigInteger
     */
    BIGINT(Types.BIGINT),
    FLOAT(Types.FLOAT),
    /**
     * float
     */
    REAL(Types.REAL),
    /**
     * double
     */
    DOUBLE(Types.DOUBLE),
    /**
     * BigDecimal
     */
    NUMERIC(Types.NUMERIC),
    /**
     * BigDecimal
     */
    DECIMAL(Types.DECIMAL),
    /**
     * String, char
     */
    CHAR(Types.CHAR),
    /**
     * String
     */
    VARCHAR(Types.VARCHAR),
    /**
     * String
     */
    LONGVARCHAR(Types.LONGVARCHAR),
    /**
     * java.sql.Date, java.time.LocalDate
     */
    DATE(Types.DATE),
    /**
     * java.sql.Time, java.time.LocalTime
     */
    TIME(Types.TIME),
    /**
     * java.sql.Timestamp, java.time.LocalDateTime
     */
    TIMESTAMP(Types.TIMESTAMP),
    BINARY(Types.BINARY),
    VARBINARY(Types.VARBINARY),
    LONGVARBINARY(Types.LONGVARBINARY),
    NULL(Types.NULL),
    /**
     * Indicates that the SQL type
     * is database-specific and gets mapped to a Java object that can be
     * accessed via the methods getObject and setObject.
     */
    OTHER(Types.OTHER),
    /**
     * Indicates that the SQL type
     * is database-specific and gets mapped to a Java object that can be
     * accessed via the methods getObject and setObject.
     */
    JAVA_OBJECT(Types.JAVA_OBJECT),
    DISTINCT(Types.DISTINCT),
    STRUCT(Types.STRUCT),
    ARRAY(Types.ARRAY),
    /**
     * java.sql.Blob
     */
    BLOB(Types.BLOB),
    /**
     * java.sql.Clob
     */
    CLOB(Types.CLOB),
    REF(Types.REF),
    /**
     * java.net.URL
     */
    DATALINK(Types.DATALINK),
    /**
     * boolean
     */
    BOOLEAN(Types.BOOLEAN),

    /* JDBC 4.0 Types */
    ROWID(Types.ROWID),
    NCHAR(Types.NCHAR),
    NVARCHAR(Types.NVARCHAR),
    LONGNVARCHAR(Types.LONGNVARCHAR),
    NCLOB(Types.NCLOB),
    SQLXML(Types.SQLXML),

    /* JDBC 4.2 Types */
    REF_CURSOR(Types.REF_CURSOR),
    /**
     * java.time.OffsetTime
     */
    TIME_WITH_TIMEZONE(Types.TIME_WITH_TIMEZONE),
    /**
     * java.time.OffsetDateTime
     */
    TIMESTAMP_WITH_TIMEZONE(Types.TIMESTAMP_WITH_TIMEZONE),
    /**
     * 根据字段类型自动推断
     */
    AUTO(null),
    ;

    private final static Map<Integer, JdbcType> CACHED = new HashMap<>();

    static {
        for (JdbcType value : JdbcType.values()) {
            CACHED.put(value.typeCode, value);
        }
    }

    private final Integer typeCode;
    public final JDBCType jdbcType;

    JdbcType(Integer typeCode) {
        this.typeCode = typeCode;
        this.jdbcType = typeCode == null ? null : JDBCType.valueOf(typeCode);
    }

    public Integer getTypeCode() { return typeCode; }

    public static JdbcType valueOf(int code) { return CACHED.get(code); }
}
