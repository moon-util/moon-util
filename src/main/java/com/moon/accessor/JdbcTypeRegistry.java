package com.moon.accessor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.JDBCType;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public enum JdbcTypeRegistry {
    /**
     * Identifies the generic SQL type {@code BIT}.
     */
    BIT(Types.BIT),
    /**
     * Identifies the generic SQL type {@code TINYINT}.
     */
    TINYINT(Types.TINYINT),
    /**
     * Identifies the generic SQL type {@code SMALLINT}.
     */
    SMALLINT(Types.SMALLINT),
    /**
     * Identifies the generic SQL type {@code INTEGER}.
     */
    INTEGER(Types.INTEGER),
    /**
     * Identifies the generic SQL type {@code BIGINT}.
     */
    BIGINT(Types.BIGINT),
    /**
     * Identifies the generic SQL type {@code FLOAT}.
     */
    FLOAT(Types.FLOAT),
    /**
     * Identifies the generic SQL type {@code REAL}.
     */
    REAL(Types.REAL),
    /**
     * Identifies the generic SQL type {@code DOUBLE}.
     */
    DOUBLE(Types.DOUBLE),
    /**
     * Identifies the generic SQL type {@code NUMERIC}.
     */
    NUMERIC(Types.NUMERIC),
    /**
     * Identifies the generic SQL type {@code DECIMAL}.
     */
    DECIMAL(Types.DECIMAL),
    /**
     * Identifies the generic SQL type {@code CHAR}.
     */
    CHAR(Types.CHAR),
    /**
     * Identifies the generic SQL type {@code VARCHAR}.
     */
    VARCHAR(Types.VARCHAR),
    /**
     * Identifies the generic SQL type {@code LONGVARCHAR}.
     */
    LONGVARCHAR(Types.LONGVARCHAR),
    /**
     * Identifies the generic SQL type {@code DATE}.
     */
    DATE(Types.DATE),
    /**
     * Identifies the generic SQL type {@code TIME}.
     */
    TIME(Types.TIME),
    /**
     * Identifies the generic SQL type {@code TIMESTAMP}.
     */
    TIMESTAMP(Types.TIMESTAMP),
    /**
     * Identifies the generic SQL type {@code BINARY}.
     */
    BINARY(Types.BINARY),
    /**
     * Identifies the generic SQL type {@code VARBINARY}.
     */
    VARBINARY(Types.VARBINARY),
    /**
     * Identifies the generic SQL type {@code LONGVARBINARY}.
     */
    LONGVARBINARY(Types.LONGVARBINARY),
    /**
     * Identifies the generic SQL value {@code NULL}.
     */
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
    /**
     * Identifies the generic SQL type {@code DISTINCT}.
     */
    DISTINCT(Types.DISTINCT),
    /**
     * Identifies the generic SQL type {@code STRUCT}.
     */
    STRUCT(Types.STRUCT),
    /**
     * Identifies the generic SQL type {@code ARRAY}.
     */
    ARRAY(Types.ARRAY),
    /**
     * Identifies the generic SQL type {@code BLOB}.
     */
    BLOB(Types.BLOB),
    /**
     * Identifies the generic SQL type {@code CLOB}.
     */
    CLOB(Types.CLOB),
    /**
     * Identifies the generic SQL type {@code REF}.
     */
    REF(Types.REF),
    /**
     * Identifies the generic SQL type {@code DATALINK}.
     */
    DATALINK(Types.DATALINK),
    /**
     * Identifies the generic SQL type {@code BOOLEAN}.
     */
    BOOLEAN(Types.BOOLEAN),

    /* JDBC 4.0 Types */

    /**
     * Identifies the SQL type {@code ROWID}.
     */
    ROWID(Types.ROWID),
    /**
     * Identifies the generic SQL type {@code NCHAR}.
     */
    NCHAR(Types.NCHAR),
    /**
     * Identifies the generic SQL type {@code NVARCHAR}.
     */
    NVARCHAR(Types.NVARCHAR),
    /**
     * Identifies the generic SQL type {@code LONGNVARCHAR}.
     */
    LONGNVARCHAR(Types.LONGNVARCHAR),
    /**
     * Identifies the generic SQL type {@code NCLOB}.
     */
    NCLOB(Types.NCLOB),
    /**
     * Identifies the generic SQL type {@code SQLXML}.
     */
    SQLXML(Types.SQLXML),

    /* JDBC 4.2 Types */

    /**
     * Identifies the generic SQL type {@code REF_CURSOR}.
     */
    REF_CURSOR(Types.REF_CURSOR),

    /**
     * Identifies the generic SQL type {@code TIME_WITH_TIMEZONE}.
     */
    TIME_WITH_TIMEZONE(Types.TIME_WITH_TIMEZONE),

    /**
     * Identifies the generic SQL type {@code TIMESTAMP_WITH_TIMEZONE}.
     */
    TIMESTAMP_WITH_TIMEZONE(Types.TIMESTAMP_WITH_TIMEZONE);;

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

    private final int typeCode;
    private final JDBCType jdbcType;

    JdbcTypeRegistry(int typeCode) {
        // Class<?> defaultMapped, Class<?>... supportsMapped
        this.typeCode = typeCode;
        this.jdbcType = JDBCType.valueOf(typeCode);
    }

    public int getTypeCode() { return typeCode; }

    public static JDBCType getJDBCType(int codeType) { return CACHED.get(codeType); }

    public static int getTypeCode(JDBCType type) { return type.getVendorTypeNumber(); }
}
