package com.moon.accessor.type;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.chrono.JapaneseDate;
import java.util.function.Function;

import static com.moon.accessor.type.TypeUsing2.asInts;
import static com.moon.accessor.type.TypeUsing2.useIfNonNull;

/**
 * @author benshaoye
 */
enum JavaTypeHandlersEnum implements TypeJdbcHandler<Object> {
    /**
     * types
     */
    forBigDecimal(BigDecimal.class, asInts(Types.NUMERIC, Types.DECIMAL)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return resultSet.getBigDecimal(columnIndex);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return resultSet.getBigDecimal(columnName);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setBigDecimal(index, (BigDecimal) value);
        }
    },
    forBigInteger(BigInteger.class, asInts(Types.BIGINT)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return useIfNonNull(resultSet.getBigDecimal(columnIndex), BigDecimal::toBigInteger);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return useIfNonNull(resultSet.getBigDecimal(columnName), BigDecimal::toBigInteger);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setBigDecimal(index, new BigDecimal((BigInteger) value));
        }
    },
    forBoolean(Boolean.class, asInts(Types.BOOLEAN)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            boolean value = resultSet.getBoolean(columnIndex);
            return !value && resultSet.wasNull() ? null : value;
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            boolean value = resultSet.getBoolean(columnName);
            return !value && resultSet.wasNull() ? null : value;
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setBoolean(index, (Boolean) value);
        }
    },
    forBooleanValue(boolean.class, asInts(Types.BOOLEAN)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return resultSet.getBoolean(columnIndex);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return resultSet.getBoolean(columnName);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setBoolean(index, (Boolean) value);
        }
    },
    forDouble(Double.class, asInts(Types.DOUBLE)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            double value = resultSet.getDouble(columnIndex);
            return value == 0 && resultSet.wasNull() ? null : value;
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            double value = resultSet.getDouble(columnName);
            return value == 0 && resultSet.wasNull() ? null : value;
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setDouble(index, (Double) value);
        }
    },
    forDoubleValue(double.class, asInts(Types.DOUBLE)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return resultSet.getDouble(columnIndex);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return resultSet.getDouble(columnName);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setDouble(index, (Double) value);
        }
    },
    forFloat(Float.class, asInts(Types.REAL)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            float value = resultSet.getFloat(columnIndex);
            return value == 0 && resultSet.wasNull() ? null : value;
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            float value = resultSet.getFloat(columnName);
            return value == 0 && resultSet.wasNull() ? null : value;
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setFloat(index, (Float) value);
        }
    },
    forFloatValue(float.class, asInts(Types.REAL)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return resultSet.getFloat(columnIndex);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return resultSet.getFloat(columnName);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setFloat(index, (Float) value);
        }
    },
    forLong(Long.class, asInts(Types.BIGINT)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            long value = resultSet.getLong(columnIndex);
            return value == 0 && resultSet.wasNull() ? null : value;
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            long value = resultSet.getLong(columnName);
            return value == 0 && resultSet.wasNull() ? null : value;
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setLong(index, (Long) value);
        }
    },
    forLongValue(long.class, asInts(Types.BIGINT)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return resultSet.getLong(columnIndex);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return resultSet.getLong(columnName);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setLong(index, (Long) value);
        }
    },
    forInteger(Integer.class, asInts(Types.INTEGER)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            int value = resultSet.getInt(columnIndex);
            return value == 0 && resultSet.wasNull() ? null : value;
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            int value = resultSet.getInt(columnName);
            return value == 0 && resultSet.wasNull() ? null : value;
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setInt(index, (Integer) value);
        }
    },
    forInt(int.class, asInts(Types.INTEGER)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return resultSet.getInt(columnIndex);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return resultSet.getInt(columnName);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setInt(index, (Integer) value);
        }
    },
    forShort(Short.class, asInts(Types.SMALLINT)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            short value = resultSet.getShort(columnIndex);
            return value == 0 && resultSet.wasNull() ? null : value;
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            short value = resultSet.getShort(columnName);
            return value == 0 && resultSet.wasNull() ? null : value;
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setShort(index, (Short) value);
        }
    },
    forShortValue(short.class, asInts(Types.SMALLINT)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return resultSet.getShort(columnIndex);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return resultSet.getShort(columnName);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setShort(index, (Short) value);
        }
    },
    forByte(Byte.class, asInts(Types.TINYINT)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            byte value = resultSet.getByte(columnIndex);
            return value == 0 && resultSet.wasNull() ? null : value;
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            byte value = resultSet.getByte(columnName);
            return value == 0 && resultSet.wasNull() ? null : value;
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setByte(index, (Byte) value);
        }
    },
    forByteValue(byte.class, asInts(Types.TINYINT)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return resultSet.getByte(columnIndex);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return resultSet.getByte(columnName);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setByte(index, (Byte) value);
        }
    },
    forCharacter(Character.class, asInts(Types.CHAR, Types.VARCHAR)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            String columnValue = resultSet.getString(columnIndex);
            return columnValue == null || columnValue.length() == 0 ? null : columnValue.charAt(0);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            String columnValue = resultSet.getString(columnName);
            return columnValue == null || columnValue.length() == 0 ? null : columnValue.charAt(0);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setString(index, String.valueOf(value));
        }
    },
    forChar(char.class, asInts(Types.CHAR, Types.VARCHAR)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return resultSet.getString(columnIndex).charAt(0);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return resultSet.getString(columnName).charAt(0);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setString(index, String.valueOf(value));
        }
    },
    forBlob(Blob.class, asInts(Types.BLOB)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return resultSet.getBlob(columnIndex);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return resultSet.getBlob(columnName);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setBlob(index, (Blob) value);
        }
    },
    forClob(Clob.class, asInts(Types.CLOB)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return resultSet.getClob(columnIndex);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return resultSet.getClob(columnName);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setClob(index, (Clob) value);
        }
    },
    forSqlDate(Date.class, asInts(Types.DATE)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return resultSet.getDate(columnIndex);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return resultSet.getDate(columnName);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setDate(index, (Date) value);
        }
    },
    forSqlTime(Time.class, asInts(Types.TIME)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return resultSet.getTime(columnIndex);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return resultSet.getTime(columnName);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setTime(index, (Time) value);
        }
    },
    forSqlTimestamp(Timestamp.class, asInts(Types.TIMESTAMP)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return resultSet.getTimestamp(columnIndex);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return resultSet.getTimestamp(columnName);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setTimestamp(index, (Timestamp) value);
        }
    },
    forUtilDate(java.util.Date.class, asInts(Types.TIMESTAMP)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return resultSet.getTimestamp(columnIndex);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return resultSet.getTimestamp(columnName);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setTimestamp(index, new Timestamp(((java.util.Date) value).getTime()));
        }
    },
    forLocalDate(LocalDate.class, asInts(Types.DATE)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return useIfNonNull(resultSet.getDate(columnIndex), Date::toLocalDate);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return useIfNonNull(resultSet.getDate(columnName), Date::toLocalDate);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setDate(index, Date.valueOf((LocalDate) value));
        }
    },
    forLocalTime(LocalTime.class, asInts(Types.TIME)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return useIfNonNull(resultSet.getTime(columnIndex), Time::toLocalTime);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return useIfNonNull(resultSet.getTime(columnName), Time::toLocalTime);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setTime(index, Time.valueOf((LocalTime) value));
        }
    },
    forLocalDateTime(LocalDateTime.class, asInts(Types.TIMESTAMP)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return useIfNonNull(resultSet.getTimestamp(columnIndex), Timestamp::toLocalDateTime);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return useIfNonNull(resultSet.getTimestamp(columnName), Timestamp::toLocalDateTime);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setTimestamp(index, Timestamp.valueOf((LocalDateTime) value));
        }
    },
    forInstant(Instant.class, asInts(Types.TIMESTAMP)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return useIfNonNull(resultSet.getTimestamp(columnIndex), Timestamp::toInstant);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return useIfNonNull(resultSet.getTimestamp(columnName), Timestamp::toInstant);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setTimestamp(index, Timestamp.from((Instant) value));
        }
    },
    forJapaneseDate(JapaneseDate.class, asInts(Types.DATE)) {
        private final Function<Date, Object> converter = date -> JapaneseDate.from(date.toLocalDate());

        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return useIfNonNull(resultSet.getDate(columnIndex), converter);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return useIfNonNull(resultSet.getDate(columnName), converter);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setDate(index, java.sql.Date.valueOf(LocalDate.ofEpochDay(((JapaneseDate) value).toEpochDay())));
        }
    },
    forOffsetTime(OffsetTime.class, asInts(Types.TIME_WITH_TIMEZONE)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return resultSet.getObject(columnIndex, OffsetTime.class);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return resultSet.getObject(columnName, OffsetTime.class);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setObject(index, value);
        }
    },
    forOffsetDateTime(OffsetDateTime.class, asInts(Types.TIMESTAMP_WITH_TIMEZONE)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return resultSet.getObject(columnIndex, OffsetDateTime.class);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return resultSet.getObject(columnName, OffsetDateTime.class);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setObject(index, value);
        }
    },
    forZonedDateTime(ZonedDateTime.class, asInts(Types.TIMESTAMP_WITH_TIMEZONE)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return resultSet.getObject(columnIndex, ZonedDateTime.class);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return resultSet.getObject(columnName, ZonedDateTime.class);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setObject(index, value);
        }
    },
    forMonthDay(MonthDay.class, asInts(Types.VARCHAR)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return useIfNonNull(resultSet.getString(columnIndex), MonthDay::parse);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return useIfNonNull(resultSet.getString(columnName), MonthDay::parse);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setString(index, String.valueOf(value));
        }
    },
    forYearMonth(YearMonth.class, asInts(Types.VARCHAR)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return useIfNonNull(resultSet.getString(columnIndex), YearMonth::parse);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return useIfNonNull(resultSet.getString(columnName), YearMonth::parse);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setString(index, String.valueOf(value));
        }
    },
    forMonth(Month.class, asInts(Types.INTEGER)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            int month = resultSet.getInt(columnIndex);
            return month > 0 && month < 13 ? Month.of(month) : null;
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            int month = resultSet.getInt(columnName);
            return month > 0 && month < 13 ? Month.of(month) : null;
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setInt(index, ((Month) value).getValue());
        }
    },
    forMonth_String(Month.class, asInts(Types.VARCHAR)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return useIfNonNull(resultSet.getString(columnIndex), Month::valueOf);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return useIfNonNull(resultSet.getString(columnName), Month::valueOf);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setString(index, ((Month) value).name());
        }
    },
    forYear(Year.class, asInts(Types.INTEGER)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            int year = resultSet.getInt(columnIndex);
            return year == 0 && resultSet.wasNull() ? null : Year.of(year);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            int year = resultSet.getInt(columnName);
            return year == 0 && resultSet.wasNull() ? null : Year.of(year);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setInt(index, ((Year) value).getValue());
        }
    },
    forURL(URL.class, asInts(Types.DATALINK)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return resultSet.getURL(columnIndex);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return resultSet.getURL(columnName);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setURL(index, (URL) value);
        }
    },
    forURL_String(URL.class, asInts(Types.VARCHAR, Types.CHAR, Types.LONGVARCHAR)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return useIfNonNull(resultSet.getString(columnIndex), TypeUsing2::toURL);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return useIfNonNull(resultSet.getString(columnName), TypeUsing2::toURL);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setString(index, String.valueOf(value));
        }
    },
    forString(String.class, asInts(Types.VARCHAR, Types.CHAR, Types.LONGVARCHAR)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return resultSet.getString(columnIndex);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return resultSet.getString(columnName);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setString(index, String.valueOf(value));
        }
    },
    forStringBuilder(StringBuilder.class, asInts(Types.VARCHAR, Types.CHAR, Types.LONGVARCHAR)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return useIfNonNull(resultSet.getString(columnIndex), StringBuilder::new);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return useIfNonNull(resultSet.getString(columnName), StringBuilder::new);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setString(index, String.valueOf(value));
        }
    },
    forStringBuffer(StringBuffer.class, asInts(Types.VARCHAR, Types.CHAR, Types.LONGVARCHAR)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return useIfNonNull(resultSet.getString(columnIndex), StringBuffer::new);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return useIfNonNull(resultSet.getString(columnName), StringBuffer::new);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setString(index, String.valueOf(value));
        }
    },
    forObject(Object.class, asInts(Types.JAVA_OBJECT)) {
        @Override
        public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
            return resultSet.getObject(columnIndex);
        }

        @Override
        public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
            return resultSet.getObject(columnName);
        }

        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setObject(index, value);
        }
    };

    private final int[] supportJdbcTypes;
    public final Class<?> supportClass;

    JavaTypeHandlersEnum(Class<?> supportClass, int[] jdbcTypes) {
        this.supportClass = supportClass;
        this.supportJdbcTypes = jdbcTypes;
    }

    @Override
    public int[] supportJdbcTypes() { return supportJdbcTypes; }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Class supportClass() { return supportClass; }

    public abstract void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException;

    @Override
    public void setParameter(PreparedStatement stmt, int index, Object value, int jdbcType) throws SQLException {
        if (value == null) {
            setNull(stmt, index, jdbcType);
        } else {
            setParameterForNonNull(stmt, index, value);
        }
    }

    @Override
    public abstract Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException;

    @Override
    public abstract Object getResultValue(ResultSet resultSet, String columnName) throws SQLException;
}
