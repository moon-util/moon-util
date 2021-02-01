package com.moon.accessor.type;

import com.moon.accessor.function.ThrowingBiApplier;
import com.moon.accessor.function.ThrowingIntBiApplier;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.chrono.JapaneseDate;

import static com.moon.accessor.type.TypeUsing2.useIfNonNull;

/**
 * @author benshaoye
 */
enum JavaTypeHandlersEnum implements TypeHandler {
    forBigDecimal_AS_NUMERIC(Types.NUMERIC,
        (resultSet, idx) -> resultSet.getBigDecimal(idx),
        (resultSet, name) -> resultSet.getBigDecimal(name),
        BigDecimal.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setBigDecimal(index, (BigDecimal) value);
        }
    },
    forBigDecimal_AS_DECIMAL(Types.DECIMAL,
        (resultSet, idx) -> resultSet.getBigDecimal(idx),
        (resultSet, name) -> resultSet.getBigDecimal(name),
        BigDecimal.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setBigDecimal(index, (BigDecimal) value);
        }
    },
    forBigInteger(Types.BIGINT, null, null, BigInteger.class) {
        @Override
        public Object getResultValue(ResultSet set, int columnIndex) throws SQLException {
            BigDecimal decimal = set.getBigDecimal(columnIndex);
            return decimal == null ? null : decimal.toBigInteger();
        }

        @Override
        public Object getResultValue(ResultSet set, String columnName) throws SQLException {
            BigDecimal decimal = set.getBigDecimal(columnName);
            return decimal == null ? null : decimal.toBigInteger();
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setBigDecimal(index, new BigDecimal((BigInteger) value));
        }
    },
    forBoolean(Types.BOOLEAN,
        (set, idx) -> set.wasNull() ? null : set.getBoolean(idx),
        (set, name) -> set.wasNull() ? null : set.getBoolean(name),
        Boolean.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setBoolean(index, (Boolean) value);
        }
    },
    forBooleanValue(Types.BOOLEAN,
        (resultSet, idx) -> resultSet.getBoolean(idx),
        (resultSet, name) -> resultSet.getBoolean(name),
        boolean.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setBoolean(index, (Boolean) value);
        }
    },
    forDouble(Types.DOUBLE,
        (set, idx) -> set.wasNull() ? null : set.getDouble(idx),
        (set, name) -> set.wasNull() ? null : set.getDouble(name),
        Double.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setDouble(index, (Double) value);
        }
    },
    forDoubleValue(Types.DOUBLE,
        (resultSet, idx) -> resultSet.getDouble(idx),
        (resultSet, name) -> resultSet.getDouble(name),
        double.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setDouble(index, (Double) value);
        }
    },
    forFloat(Types.REAL,
        (set, idx) -> set.wasNull() ? null : set.getFloat(idx),
        (set, name) -> set.wasNull() ? null : set.getFloat(name),
        Float.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setFloat(index, (Float) value);
        }
    },
    forFloatValue(Types.REAL,
        (resultSet, idx) -> resultSet.getFloat(idx),
        (resultSet, name) -> resultSet.getFloat(name),
        float.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setFloat(index, (Float) value);
        }
    },
    forLong(Types.BIGINT,
        (set, idx) -> set.wasNull() ? null : set.getLong(idx),
        (set, name) -> set.wasNull() ? null : set.getLong(name),
        Long.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setLong(index, (Long) value);
        }
    },
    forLongValue(Types.BIGINT,
        (resultSet, idx) -> resultSet.getLong(idx),
        (resultSet, name) -> resultSet.getLong(name),
        long.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setLong(index, (Long) value);
        }
    },
    forInteger(Types.INTEGER,
        (set, idx) -> set.wasNull() ? null : set.getInt(idx),
        (set, name) -> set.wasNull() ? null : set.getInt(name),
        Integer.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setInt(index, (Integer) value);
        }
    },
    forInt(Types.INTEGER,
        (resultSet, idx) -> resultSet.getInt(idx),
        (resultSet, name) -> resultSet.getInt(name),
        int.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setInt(index, (Integer) value);
        }
    },
    forShort(Types.SMALLINT,
        (set, idx) -> set.wasNull() ? null : set.getShort(idx),
        (set, name) -> set.wasNull() ? null : set.getShort(name),
        Short.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setShort(index, (Short) value);
        }
    },
    forShortValue(Types.SMALLINT,
        (resultSet, idx) -> resultSet.getShort(idx),
        (resultSet, name) -> resultSet.getShort(name),
        short.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setShort(index, (Short) value);
        }
    },
    forByte(Types.TINYINT,
        (set, idx) -> set.wasNull() ? null : set.getByte(idx),
        (set, name) -> set.wasNull() ? null : set.getByte(name),
        Byte.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setLong(index, (Long) value);
        }
    },
    forByteValue(Types.TINYINT,
        (resultSet, idx) -> resultSet.getByte(idx),
        (resultSet, name) -> resultSet.getByte(name),
        byte.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setByte(index, (Byte) value);
        }
    },
    forCharacter(Types.CHAR,
        (set, idx) -> set.wasNull() ? null : set.getString(idx).charAt(0),
        (set, name) -> set.wasNull() ? null : set.getString(name).charAt(0),
        Character.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setString(index, String.valueOf(value));
        }
    },
    forChar(Types.CHAR,
        (resultSet, idx) -> resultSet.getByte(idx),
        (resultSet, name) -> resultSet.getByte(name),
        char.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setString(index, String.valueOf(value));
        }
    },
    forBlob(Types.BLOB, (set, idx) -> set.getBlob(idx), (set, name) -> set.getBlob(name), Blob.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setBlob(index, (Blob) value);
        }
    },
    forClob(Types.BLOB, (set, idx) -> set.getClob(idx), (set, name) -> set.getClob(name), Clob.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setClob(index, (Clob) value);
        }
    },
    forSqlDate(Types.DATE, (set, idx) -> set.getDate(idx), (set, name) -> set.getDate(name), Date.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setDate(index, (Date) value);
        }
    },
    forSqlTime(Types.TIME, (set, idx) -> set.getTime(idx), (set, name) -> set.getTime(name), Time.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setTime(index, (Time) value);
        }
    },
    forSqlTimestamp(Types.TIMESTAMP,
        (set, idx) -> set.getTimestamp(idx),
        (set, name) -> set.getTimestamp(name),
        Timestamp.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setTimestamp(index, (Timestamp) value);
        }
    },
    forUtilDate(Types.TIMESTAMP,
        (set, idx) -> set.getTimestamp(idx),
        (set, name) -> set.getTimestamp(name),
        java.util.Date.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setTimestamp(index, new Timestamp(((java.util.Date) value).getTime()));
        }
    },
    forLocalDate(Types.DATE,
        (set, idx) -> useIfNonNull(set.getDate(idx), Date::toLocalDate),
        (set, name) -> useIfNonNull(set.getDate(name), Date::toLocalDate),
        LocalDate.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setDate(index, Date.valueOf((LocalDate) value));
        }
    },
    forLocalTime(Types.TIME,
        (set, idx) -> useIfNonNull(set.getTime(idx), Time::toLocalTime),
        (set, name) -> useIfNonNull(set.getTime(name), Time::toLocalTime),
        LocalTime.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setTime(index, Time.valueOf((LocalTime) value));
        }
    },
    forLocalDateTime(Types.TIMESTAMP,
        (set, idx) -> useIfNonNull(set.getTimestamp(idx), Timestamp::toLocalDateTime),
        (set, name) -> useIfNonNull(set.getTimestamp(name), Timestamp::toLocalDateTime),
        LocalDateTime.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setTimestamp(index, Timestamp.valueOf((LocalDateTime) value));
        }
    },
    forInstant(Types.TIMESTAMP,
        (set, idx) -> useIfNonNull(set.getTimestamp(idx), Timestamp::toInstant),
        (set, name) -> useIfNonNull(set.getTimestamp(name), Timestamp::toInstant),
        Instant.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setTimestamp(index, Timestamp.from((Instant) value));
        }
    },
    forJapaneseDate(Types.DATE,
        (set, idx) -> useIfNonNull(set.getDate(idx), date -> JapaneseDate.from(date.toLocalDate())),
        (set, name) -> useIfNonNull(set.getDate(name), date -> JapaneseDate.from(date.toLocalDate())),
        Instant.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setDate(index, java.sql.Date.valueOf(LocalDate.ofEpochDay(((JapaneseDate) value).toEpochDay())));
        }
    },
    forOffsetTime(Types.TIME_WITH_TIMEZONE,
        (set, idx) -> set.getObject(idx, OffsetTime.class),
        (set, name) -> set.getObject(name, OffsetTime.class),
        OffsetTime.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setObject(index, value, jdbcType);
        }
    },
    setOffsetDateTime(Types.TIMESTAMP_WITH_TIMEZONE,
        (set, idx) -> set.getObject(idx, OffsetDateTime.class),
        (set, name) -> set.getObject(name, OffsetDateTime.class),
        OffsetTime.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setObject(index, value, jdbcType);
        }
    },
    forURL(Types.DATALINK,
        (resultSet, idx) -> resultSet.getURL(idx),
        (resultSet, name) -> resultSet.getURL(name),
        URL.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setURL(index, (URL) value);
        }
    },
    forURLString(Types.VARCHAR,
        (set, idx) -> useIfNonNull(set.getString(idx), TypeUsing2::toURL),
        (set, name) -> useIfNonNull(set.getString(name), TypeUsing2::toURL),
        URL.class) {
        @Override
        public int[] getCompatibleTypes() {
            return TypeUsing2.as(Types.CHAR, Types.LONGVARCHAR);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setString(index, String.valueOf(value));
        }
    },
    forString(Types.VARCHAR, (set, idx) -> set.getString(idx), (set, name) -> set.getString(name), String.class) {
        @Override
        public int[] getCompatibleTypes() {
            return TypeUsing2.as(Types.CHAR, Types.LONGVARCHAR);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setString(index, String.valueOf(value));
        }
    },
    forStringBuilder(Types.VARCHAR,
        (set, idx) -> useIfNonNull(set.getString(idx), StringBuilder::new),
        (set, name) -> useIfNonNull(set.getString(name), StringBuilder::new),
        String.class) {
        @Override
        public int[] getCompatibleTypes() {
            return TypeUsing2.as(Types.CHAR, Types.LONGVARCHAR);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setString(index, String.valueOf(value));
        }
    },
    forStringBuffer(Types.VARCHAR,
        (set, idx) -> useIfNonNull(set.getString(idx), StringBuffer::new),
        (set, name) -> useIfNonNull(set.getString(name), StringBuffer::new),
        String.class) {
        @Override
        public int[] getCompatibleTypes() {
            return TypeUsing2.as(Types.CHAR, Types.LONGVARCHAR);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setString(index, String.valueOf(value));
        }
    },
    ;

    private final ThrowingIntBiApplier<ResultSet, Object> indexedGetter;
    private final ThrowingBiApplier<ResultSet, String, Object> namedGetter;
    public final Class<?> supportClasses;
    public final int jdbcType;

    JavaTypeHandlersEnum(
        int jdbcType,
        ThrowingIntBiApplier<ResultSet, Object> indexedGetter,
        ThrowingBiApplier<ResultSet, String, Object> namedGetter,
        Class<?> supportClasses
    ) {
        this.supportClasses = supportClasses;
        this.indexedGetter = indexedGetter;
        this.namedGetter = namedGetter;
        this.jdbcType = jdbcType;
    }

    public abstract void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException;

    public int[] getCompatibleTypes() { return TypeUsing2.EMPTY_TYPES; }

    @Override
    public void setParameter(PreparedStatement stmt, int index, Object value, int jdbcType) throws SQLException {
        if (value == null) {
            setNull(stmt, index, jdbcType);
        } else {
            setParameterForNonNull(stmt, index, value);
        }
    }

    @Override
    public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
        return indexedGetter.apply(resultSet, columnIndex);
    }

    @Override
    public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
        return namedGetter.apply(resultSet, columnName);
    }
}
