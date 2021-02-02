package com.moon.accessor.type;

import com.moon.accessor.function.ThrowingBiApplier;
import com.moon.accessor.function.ThrowingIntBiApplier;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.chrono.JapaneseDate;
import java.util.Arrays;

import static com.moon.accessor.type.TypeUsing2.*;

/**
 * @author benshaoye
 */
enum JavaTypeHandlersEnum implements TypeHandler<Object> {
    /**
     * types
     */
    forBigDecimal(BigDecimal.class,
        asInts(Types.NUMERIC, Types.DECIMAL),
        ResultSet::getBigDecimal,
        ResultSet::getBigDecimal) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setBigDecimal(index, (BigDecimal) value);
        }
    },
    forBigInteger(BigInteger.class,
        asInts(Types.BIGINT),
        (set, idx) -> useIfNonNull(set.getBigDecimal(idx), BigDecimal::toBigInteger),
        (set, name) -> useIfNonNull(set.getBigDecimal(name), BigDecimal::toBigInteger)) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setBigDecimal(index, new BigDecimal((BigInteger) value));
        }
    },
    forBoolean(Boolean.class, asInts(Types.BOOLEAN), (set, idx) -> {
        boolean value = set.getBoolean(idx);
        return !value && set.wasNull() ? null : value;
    }, (set, name) -> {
        boolean value = set.getBoolean(name);
        return !value && set.wasNull() ? null : value;
    }) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setBoolean(index, (Boolean) value);
        }
    },
    forBooleanValue(boolean.class, asInts(Types.BOOLEAN), ResultSet::getBoolean, ResultSet::getBoolean) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setBoolean(index, (Boolean) value);
        }
    },
    forDouble(Double.class, asInts(Types.DOUBLE), (set, idx) -> {
        double value = set.getDouble(idx);
        return value == 0 && set.wasNull() ? null : value;
    }, (set, name) -> {
        double value = set.getDouble(name);
        return value == 0 && set.wasNull() ? null : value;
    }) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setDouble(index, (Double) value);
        }
    },
    forDoubleValue(double.class, asInts(Types.DOUBLE), ResultSet::getDouble, ResultSet::getDouble) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setDouble(index, (Double) value);
        }
    },
    forFloat(Float.class, asInts(Types.REAL), (set, idx) -> {
        float value = set.getFloat(idx);
        return value == 0 && set.wasNull() ? null : value;
    }, (set, name) -> {
        float value = set.getFloat(name);
        return value == 0 && set.wasNull() ? null : value;
    }) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setFloat(index, (Float) value);
        }
    },
    forFloatValue(float.class, asInts(Types.REAL), ResultSet::getFloat, ResultSet::getFloat) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setFloat(index, (Float) value);
        }
    },
    forLong(Long.class, asInts(Types.BIGINT), (set, idx) -> {
        long value = set.getLong(idx);
        return value == 0 && set.wasNull() ? null : value;
    }, (set, name) -> {
        long value = set.getLong(name);
        return value == 0 && set.wasNull() ? null : value;
    }) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setLong(index, (Long) value);
        }
    },
    forLongValue(long.class, asInts(Types.BIGINT), ResultSet::getLong, ResultSet::getLong) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setLong(index, (Long) value);
        }
    },
    forInteger(Integer.class, asInts(Types.INTEGER), (set, idx) -> {
        int value = set.getInt(idx);
        return value == 0 && set.wasNull() ? null : value;
    }, (set, name) -> {
        int value = set.getInt(name);
        return value == 0 && set.wasNull() ? null : value;
    }) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setInt(index, (Integer) value);
        }
    },
    forInt(int.class, asInts(Types.INTEGER), ResultSet::getInt, ResultSet::getInt) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setInt(index, (Integer) value);
        }
    },
    forShort(Short.class, asInts(Types.SMALLINT), (set, idx) -> {
        short value = set.getShort(idx);
        return value == 0 && set.wasNull() ? null : value;
    }, (set, name) -> {
        short value = set.getShort(name);
        return value == 0 && set.wasNull() ? null : value;
    }) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setShort(index, (Short) value);
        }
    },
    forShortValue(short.class, asInts(Types.SMALLINT), ResultSet::getShort, ResultSet::getShort) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setShort(index, (Short) value);
        }
    },
    forByte(Byte.class, asInts(Types.TINYINT), (set, idx) -> {
        byte value = set.getByte(idx);
        return value == 0 && set.wasNull() ? null : value;
    }, (set, name) -> {
        byte value = set.getByte(name);
        return value == 0 && set.wasNull() ? null : value;
    }) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setLong(index, (Long) value);
        }
    },
    forByteValue(byte.class, asInts(Types.TINYINT), ResultSet::getByte, ResultSet::getByte) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setByte(index, (Byte) value);
        }
    },
    forCharacter(Character.class,
        asInts(Types.CHAR, Types.VARCHAR),

        (set, idx) -> set.wasNull() ? null : set.getString(idx).charAt(0),
        (set, name) -> set.wasNull() ? null : set.getString(name).charAt(0)) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setString(index, String.valueOf(value));
        }
    },
    forChar(char.class, asInts(Types.CHAR, Types.VARCHAR), ResultSet::getByte, ResultSet::getByte) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setString(index, String.valueOf(value));
        }
    },
    forBlob(Blob.class, asInts(Types.BLOB), ResultSet::getBlob, ResultSet::getBlob) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setBlob(index, (Blob) value);
        }
    },
    forClob(Clob.class, asInts(Types.CLOB), ResultSet::getClob, ResultSet::getClob) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setClob(index, (Clob) value);
        }
    },
    forSqlDate(Date.class, asInts(Types.DATE), ResultSet::getDate, ResultSet::getDate) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setDate(index, (Date) value);
        }
    },
    forSqlTime(Time.class, asInts(Types.TIME), ResultSet::getTime, ResultSet::getTime) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setTime(index, (Time) value);
        }
    },
    forSqlTimestamp(Timestamp.class, asInts(Types.TIMESTAMP), ResultSet::getTimestamp, ResultSet::getTimestamp) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setTimestamp(index, (Timestamp) value);
        }
    },
    forUtilDate(java.util.Date.class, asInts(Types.TIMESTAMP), ResultSet::getTimestamp, ResultSet::getTimestamp) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setTimestamp(index, new Timestamp(((java.util.Date) value).getTime()));
        }
    },
    forLocalDate(LocalDate.class,
        asInts(Types.DATE),
        (set, idx) -> useIfNonNull(set.getDate(idx), Date::toLocalDate),
        (set, name) -> useIfNonNull(set.getDate(name), Date::toLocalDate)) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setDate(index, Date.valueOf((LocalDate) value));
        }
    },
    forLocalTime(LocalTime.class,
        asInts(Types.TIME),
        (set, idx) -> useIfNonNull(set.getTime(idx), Time::toLocalTime),
        (set, name) -> useIfNonNull(set.getTime(name), Time::toLocalTime)) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setTime(index, Time.valueOf((LocalTime) value));
        }
    },
    forLocalDateTime(LocalDateTime.class,
        asInts(Types.TIMESTAMP),
        (set, idx) -> useIfNonNull(set.getTimestamp(idx), Timestamp::toLocalDateTime),
        (set, name) -> useIfNonNull(set.getTimestamp(name), Timestamp::toLocalDateTime)) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setTimestamp(index, Timestamp.valueOf((LocalDateTime) value));
        }
    },
    forInstant(Instant.class,
        asInts(Types.TIMESTAMP),
        (set, idx) -> useIfNonNull(set.getTimestamp(idx), Timestamp::toInstant),
        (set, name) -> useIfNonNull(set.getTimestamp(name), Timestamp::toInstant)) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setTimestamp(index, Timestamp.from((Instant) value));
        }
    },
    forJapaneseDate(JapaneseDate.class,
        asInts(Types.DATE),
        (set, idx) -> useIfNonNull(set.getDate(idx), date -> JapaneseDate.from(date.toLocalDate())),
        (set, name) -> useIfNonNull(set.getDate(name), date -> JapaneseDate.from(date.toLocalDate()))) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setDate(index, java.sql.Date.valueOf(LocalDate.ofEpochDay(((JapaneseDate) value).toEpochDay())));
        }
    },
    forOffsetTime(OffsetTime.class,
        asInts(Types.TIME_WITH_TIMEZONE),
        (set, idx) -> set.getObject(idx, OffsetTime.class),
        (set, name) -> set.getObject(name, OffsetTime.class)) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setObject(index, value);
        }
    },
    forOffsetDateTime(OffsetTime.class,
        asInts(Types.TIMESTAMP_WITH_TIMEZONE),
        (set, idx) -> set.getObject(idx, OffsetDateTime.class),
        (set, name) -> set.getObject(name, OffsetDateTime.class)) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setObject(index, value);
        }
    },
    forZonedDateTime(ZonedDateTime.class,
        asInts(Types.TIMESTAMP_WITH_TIMEZONE),
        (set, idx) -> set.getObject(idx, ZonedDateTime.class),
        (set, name) -> set.getObject(name, ZonedDateTime.class)) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setObject(index, value);
        }
    },
    forMonthDay(MonthDay.class,
        asInts(Types.VARCHAR),
        (set, idx) -> useIfNonNull(set.getString(idx), MonthDay::parse),
        (set, name) -> useIfNonNull(set.getString(name), MonthDay::parse)) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setString(index, String.valueOf(value));
        }
    },
    forYearMonth(YearMonth.class,
        asInts(Types.VARCHAR),
        (set, idx) -> useIfNonNull(set.getString(idx), YearMonth::parse),
        (set, name) -> useIfNonNull(set.getString(name), YearMonth::parse)) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setString(index, String.valueOf(value));
        }
    },
    forMonth(Month.class, asInts(Types.INTEGER), (set, idx) -> {
        int month = set.getInt(idx);
        return month > 0 && month < 13 ? Month.of(month) : null;
    }, (set, name) -> {
        int month = set.getInt(name);
        return month > 0 && month < 13 ? Month.of(month) : null;
    }) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setInt(index, ((Month) value).getValue());
        }
    },
    forMonth_String(Month.class,
        asInts(Types.VARCHAR),
        (set, idx) -> useIfNonNull(set.getString(idx), Month::valueOf),
        (set, name) -> useIfNonNull(set.getString(name), Month::valueOf)) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setString(index, ((Month) value).name());
        }
    },
    forYear(Year.class, asInts(Types.INTEGER), (set, idx) -> {
        int year = set.getInt(idx);
        return year == 0 && set.wasNull() ? null : Year.of(year);
    }, (set, name) -> {
        int year = set.getInt(name);
        return year == 0 && set.wasNull() ? null : Year.of(year);
    }) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setInt(index, ((Year) value).getValue());
        }
    },
    forURL(URL.class, asInts(Types.DATALINK), ResultSet::getURL, ResultSet::getURL) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setURL(index, (URL) value);
        }
    },
    forURL_String(URL.class,
        asInts(Types.VARCHAR, Types.CHAR, Types.LONGVARCHAR),
        (set, idx) -> useIfNonNull(set.getString(idx), TypeUsing2::toURL),
        (set, name) -> useIfNonNull(set.getString(name), TypeUsing2::toURL)) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setString(index, String.valueOf(value));
        }
    },
    forString(String.class,
        asInts(Types.VARCHAR, Types.CHAR, Types.LONGVARCHAR),
        ResultSet::getString,
        ResultSet::getString) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setString(index, String.valueOf(value));
        }
    },
    forStringBuilder(StringBuilder.class,
        asInts(Types.VARCHAR, Types.CHAR, Types.LONGVARCHAR),
        (set, idx) -> useIfNonNull(set.getString(idx), StringBuilder::new),
        (set, name) -> useIfNonNull(set.getString(name), StringBuilder::new)) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setString(index, String.valueOf(value));
        }
    },
    forStringBuffer(StringBuffer.class,
        asInts(Types.VARCHAR, Types.CHAR, Types.LONGVARCHAR),
        (set, idx) -> useIfNonNull(set.getString(idx), StringBuffer::new),
        (set, name) -> useIfNonNull(set.getString(name), StringBuffer::new)) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setString(index, String.valueOf(value));
        }
    },
    forObject(Object.class, asInts(Types.JAVA_OBJECT), ResultSet::getObject, ResultSet::getObject) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setObject(index, value);
        }
    };

    private final ThrowingIntBiApplier<ResultSet, Object> indexedGetter;
    private final ThrowingBiApplier<ResultSet, String, Object> namedGetter;
    private final int[] compatibleTypes;
    public final int primaryJdbcType;
    public final Class<?> supportClass;

    JavaTypeHandlersEnum(
        Class<?> supportClass,
        int[] jdbcTypes,
        ThrowingIntBiApplier<ResultSet, Object> indexedGetter,
        ThrowingBiApplier<ResultSet, String, Object> namedGetter
    ) {
        this.supportClass = supportClass;
        this.indexedGetter = indexedGetter;
        this.namedGetter = namedGetter;
        // types
        this.primaryJdbcType = jdbcTypes[0];
        int typesLength = jdbcTypes.length;
        if (typesLength == 1) {
            this.compatibleTypes = EMPTY_TYPES;
        } else {
            int[] types = new int[typesLength - 1];
            System.arraycopy(jdbcTypes, 1, types, 0, typesLength - 1);
            this.compatibleTypes = types;
        }
    }

    public abstract void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException;

    public int[] getCompatibleTypes() { return Arrays.copyOf(compatibleTypes, compatibleTypes.length); }

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
