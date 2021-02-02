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
enum JavaTypeHandlersEnum implements TypeHandler<Object> {
    /**
     * types
     */
    forBigDecimal_AS_NUMERIC(Types.NUMERIC, ResultSet::getBigDecimal, ResultSet::getBigDecimal, BigDecimal.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setBigDecimal(index, (BigDecimal) value);
        }
    },
    forBigDecimal_AS_DECIMAL(Types.DECIMAL, ResultSet::getBigDecimal, ResultSet::getBigDecimal, BigDecimal.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setBigDecimal(index, (BigDecimal) value);
        }
    },
    forBigInteger(Types.BIGINT, (set, idx) -> {
        BigDecimal decimal = set.getBigDecimal(idx);
        return decimal == null ? null : decimal.toBigInteger();
    }, (set, name) -> {
        BigDecimal decimal = set.getBigDecimal(name);
        return decimal == null ? null : decimal.toBigInteger();
    }, BigInteger.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setBigDecimal(index, new BigDecimal((BigInteger) value));
        }
    },
    forBoolean(Types.BOOLEAN, (set, idx) -> {
        boolean value = set.getBoolean(idx);
        return !value && set.wasNull() ? null : value;
    }, (set, name) -> {
        boolean value = set.getBoolean(name);
        return !value && set.wasNull() ? null : value;
    }, Boolean.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setBoolean(index, (Boolean) value);
        }
    },
    forBooleanValue(Types.BOOLEAN, ResultSet::getBoolean, ResultSet::getBoolean, boolean.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setBoolean(index, (Boolean) value);
        }
    },
    forDouble(Types.DOUBLE, (set, idx) -> {
        double value = set.getDouble(idx);
        return value == 0 && set.wasNull() ? null : value;
    }, (set, name) -> {
        double value = set.getDouble(name);
        return value == 0 && set.wasNull() ? null : value;
    }, Double.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setDouble(index, (Double) value);
        }
    },
    forDoubleValue(Types.DOUBLE, ResultSet::getDouble, ResultSet::getDouble, double.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setDouble(index, (Double) value);
        }
    },
    forFloat(Types.REAL, (set, idx) -> {
        float value = set.getFloat(idx);
        return value == 0 && set.wasNull() ? null : value;
    }, (set, name) -> {
        float value = set.getFloat(name);
        return value == 0 && set.wasNull() ? null : value;
    }, Float.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setFloat(index, (Float) value);
        }
    },
    forFloatValue(Types.REAL, ResultSet::getFloat, ResultSet::getFloat, float.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setFloat(index, (Float) value);
        }
    },
    forLong(Types.BIGINT, (set, idx) -> {
        long value = set.getLong(idx);
        return value == 0 && set.wasNull() ? null : value;
    }, (set, name) -> {
        long value = set.getLong(name);
        return value == 0 && set.wasNull() ? null : value;
    }, Long.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setLong(index, (Long) value);
        }
    },
    forLongValue(Types.BIGINT, ResultSet::getLong, ResultSet::getLong, long.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setLong(index, (Long) value);
        }
    },
    forInteger(Types.INTEGER, (set, idx) -> {
        int value = set.getInt(idx);
        return value == 0 && set.wasNull() ? null : value;
    }, (set, name) -> {
        int value = set.getInt(name);
        return value == 0 && set.wasNull() ? null : value;
    }, Integer.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setInt(index, (Integer) value);
        }
    },
    forInt(Types.INTEGER, ResultSet::getInt, ResultSet::getInt, int.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setInt(index, (Integer) value);
        }
    },
    forShort(Types.SMALLINT, (set, idx) -> {
        short value = set.getShort(idx);
        return value == 0 && set.wasNull() ? null : value;
    }, (set, name) -> {
        short value = set.getShort(name);
        return value == 0 && set.wasNull() ? null : value;
    }, Short.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setShort(index, (Short) value);
        }
    },
    forShortValue(Types.SMALLINT, ResultSet::getShort, ResultSet::getShort, short.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setShort(index, (Short) value);
        }
    },
    forByte(Types.TINYINT, (set, idx) -> {
        byte value = set.getByte(idx);
        return value == 0 && set.wasNull() ? null : value;
    }, (set, name) -> {
        byte value = set.getByte(name);
        return value == 0 && set.wasNull() ? null : value;
    }, Byte.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setLong(index, (Long) value);
        }
    },
    forByteValue(Types.TINYINT, ResultSet::getByte, ResultSet::getByte, byte.class) {
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
    forChar(Types.CHAR, ResultSet::getByte, ResultSet::getByte, char.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setString(index, String.valueOf(value));
        }
    },
    forBlob(Types.BLOB, ResultSet::getBlob, ResultSet::getBlob, Blob.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setBlob(index, (Blob) value);
        }
    },
    forClob(Types.BLOB, ResultSet::getClob, ResultSet::getClob, Clob.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setClob(index, (Clob) value);
        }
    },
    forSqlDate(Types.DATE, ResultSet::getDate, ResultSet::getDate, Date.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setDate(index, (Date) value);
        }
    },
    forSqlTime(Types.TIME, ResultSet::getTime, ResultSet::getTime, Time.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setTime(index, (Time) value);
        }
    },
    forSqlTimestamp(Types.TIMESTAMP, ResultSet::getTimestamp, ResultSet::getTimestamp, Timestamp.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setTimestamp(index, (Timestamp) value);
        }
    },
    forUtilDate(Types.TIMESTAMP, ResultSet::getTimestamp, ResultSet::getTimestamp, java.util.Date.class) {
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
            stmt.setObject(index, value);
        }
    },
    forOffsetDateTime(Types.TIMESTAMP_WITH_TIMEZONE,
        (set, idx) -> set.getObject(idx, OffsetDateTime.class),
        (set, name) -> set.getObject(name, OffsetDateTime.class),
        OffsetTime.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setObject(index, value);
        }
    },
    forZonedDateTime(Types.TIMESTAMP_WITH_TIMEZONE,
        (set, idx) -> set.getObject(idx, ZonedDateTime.class),
        (set, name) -> set.getObject(name, ZonedDateTime.class),
        ZonedDateTime.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setObject(index, value);
        }
    },
    forMonthDay(Types.VARCHAR,
        (set, idx) -> set.wasNull() ? null : MonthDay.parse(set.getString(idx)),
        (set, name) -> set.wasNull() ? null : MonthDay.parse(set.getString(name)),
        MonthDay.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setString(index, String.valueOf(value));
        }
    },
    forYearMonth(Types.VARCHAR,
        (set, idx) -> set.wasNull() ? null : YearMonth.parse(set.getString(idx)),
        (set, name) -> set.wasNull() ? null : YearMonth.parse(set.getString(name)),
        YearMonth.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setString(index, String.valueOf(value));
        }
    },
    forMonth(Types.INTEGER, (set, idx) -> {
        int month = set.getInt(idx);
        return month > 0 && month < 13 ? Month.of(month) : null;
    }, (set, name) -> {
        int month = set.getInt(name);
        return month > 0 && month < 13 ? Month.of(month) : null;
    }, Month.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setInt(index, ((Month) value).getValue());
        }
    },
    forYear(Types.INTEGER, (set, idx) -> {
        int year = set.getInt(idx);
        return year == 0 && set.wasNull() ? null : Year.of(year);
    }, (set, name) -> {
        int year = set.getInt(name);
        return year == 0 && set.wasNull() ? null : Year.of(year);
    }, Year.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setInt(index, ((Year) value).getValue());
        }
    },
    forURL(Types.DATALINK, ResultSet::getURL, ResultSet::getURL, URL.class) {
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
    forString(Types.VARCHAR, ResultSet::getString, ResultSet::getString, String.class) {
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
