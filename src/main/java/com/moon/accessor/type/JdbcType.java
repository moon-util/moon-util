package com.moon.accessor.type;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.JDBCType;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public enum JdbcType {
    BIT(Types.BIT),
    TINYINT(Types.TINYINT),
    SMALLINT(Types.SMALLINT),
    INTEGER(Types.INTEGER),
    BIGINT(Types.BIGINT),
    FLOAT(Types.FLOAT),
    REAL(Types.REAL),
    DOUBLE(Types.DOUBLE),
    NUMERIC(Types.NUMERIC),
    DECIMAL(Types.DECIMAL),
    CHAR(Types.CHAR),
    VARCHAR(Types.VARCHAR),
    LONGVARCHAR(Types.LONGVARCHAR),
    DATE(Types.DATE),
    TIME(Types.TIME),
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
    BLOB(Types.BLOB),
    CLOB(Types.CLOB),
    REF(Types.REF),
    DATALINK(Types.DATALINK),
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
    TIME_WITH_TIMEZONE(Types.TIME_WITH_TIMEZONE),
    TIMESTAMP_WITH_TIMEZONE(Types.TIMESTAMP_WITH_TIMEZONE),
    /**
     * 根据字段类型自动推断
     */
    AUTO(null);

    private final static Map<Class<?>, JDBCType> JAVA_TYPE_MAP_JDBC_TYPE = new HashMap<>();
    private final static Map<JDBCType, Class<?>> JDBC_TYPE_MAP_JAVA_TYPE = new HashMap<>();
    // private final static Map<Class<?>, JDBCType> classForJdbcType = new HashMap<>();

    private final static Map<Integer, JDBCType> CACHED = new HashMap<>();

    static {
        for (JDBCType value : JDBCType.values()) {
            CACHED.put(value.getVendorTypeNumber(), value);
        }
        final Map<Class<?>, JDBCType> forward = JAVA_TYPE_MAP_JDBC_TYPE;
        forward.put(Byte.class, JDBCType.TINYINT);
        forward.put(byte.class, JDBCType.TINYINT);
        forward.put(Short.class, JDBCType.SMALLINT);
        forward.put(short.class, JDBCType.SMALLINT);
        forward.put(int.class, JDBCType.INTEGER);
        forward.put(Integer.class, JDBCType.INTEGER);
        forward.put(int.class, JDBCType.INTEGER);
        forward.put(Long.class, JDBCType.BIGINT);
        forward.put(long.class, JDBCType.BIGINT);
        forward.put(Float.class, JDBCType.REAL);
        forward.put(float.class, JDBCType.REAL);
        forward.put(Double.class, JDBCType.DOUBLE);
        forward.put(double.class, JDBCType.DOUBLE);
        forward.put(BigInteger.class, JDBCType.BIGINT);
        forward.put(BigDecimal.class, JDBCType.NUMERIC);
        forward.put(String.class, JDBCType.VARCHAR);

        final Map<JDBCType, Class<?>> backward = JDBC_TYPE_MAP_JAVA_TYPE;
        backward.put(JDBCType.LONGVARCHAR, String.class);
        backward.put(JDBCType.VARCHAR, String.class);
        backward.put(JDBCType.CHAR, String.class);
    }

    private final Integer typeCode;
    public final JDBCType jdbcType;

    JdbcType(Integer typeCode) {
        // Class<?> defaultMapped, Class<?>... supportsMapped
        this.typeCode = typeCode;
        this.jdbcType = typeCode == null ? null : JDBCType.valueOf(typeCode);
    }

    public Integer getTypeCode() { return typeCode; }

    public static JDBCType getJDBCType(int codeType) { return CACHED.get(codeType); }

    public static int getTypeCode(JDBCType type) { return type.getVendorTypeNumber(); }
}
