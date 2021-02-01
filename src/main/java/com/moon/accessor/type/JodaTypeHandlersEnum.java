package com.moon.accessor.type;

import com.moon.accessor.function.ThrowingBiApplier;
import com.moon.accessor.function.ThrowingIntBiApplier;
import org.joda.time.*;

import java.sql.*;

/**
 * @author benshaoye
 */
enum JodaTypeHandlersEnum implements TypeHandler {
    forJodaLocalDate(Types.DATE,
        (set, idx) -> TypeUsing2.useIfNonNull(set.getDate(idx), LocalDate::fromDateFields),
        (set, name) -> TypeUsing2.useIfNonNull(set.getDate(name), LocalDate::fromDateFields),
        LocalDate.class) {
        @Override
        @SuppressWarnings("deprecation")
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            LocalDate date = (LocalDate) value;
            stmt.setDate(index, new Date(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth()));
        }
    },
    forJodaLocalTime(Types.TIME,
        (set, idx) -> TypeUsing2.useIfNonNull(set.getTime(idx), LocalTime::fromDateFields),
        (set, name) -> TypeUsing2.useIfNonNull(set.getTime(name), LocalTime::fromDateFields),
        LocalTime.class) {
        @Override
        @SuppressWarnings("deprecation")
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            LocalTime time = (LocalTime) value;
            stmt.setDate(index, new Date(time.getHourOfDay(), time.getMinuteOfHour(), time.getSecondOfMinute()));
        }
    },
    forJodaLocalDateTime(Types.TIMESTAMP,
        (set, idx) -> TypeUsing2.useIfNonNull(set.getTimestamp(idx), LocalDateTime::fromDateFields),
        (set, name) -> TypeUsing2.useIfNonNull(set.getTimestamp(name), LocalDateTime::fromDateFields),
        LocalDateTime.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setTimestamp(index, new Timestamp(((LocalDateTime) value).toDate().getTime()));
        }
    },
    forDateTime(Types.TIMESTAMP,
        (set, idx) -> TypeUsing2.useIfNonNull(set.getTimestamp(idx), LocalDateTime::fromDateFields),
        (set, name) -> TypeUsing2.useIfNonNull(set.getTimestamp(name), LocalDateTime::fromDateFields),
        DateTime.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setTimestamp(index, new Timestamp(((DateTime) value).getMillis()));
        }
    },
    forInstant(Types.TIMESTAMP,
        (set, idx) -> TypeUsing2.useIfNonNull(set.getTimestamp(idx), t -> Instant.ofEpochMilli(t.getTime())),
        (set, name) -> TypeUsing2.useIfNonNull(set.getTimestamp(name), t -> Instant.ofEpochMilli(t.getTime())),
        Instant.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setTimestamp(index, new Timestamp(((Instant) value).getMillis()));
        }
    },
    ;

    private final ThrowingIntBiApplier<ResultSet, Object> indexedGetter;
    private final ThrowingBiApplier<ResultSet, String, Object> namedGetter;
    public final Class<?> supportClasses;
    public final int jdbcType;

    JodaTypeHandlersEnum(
        int jdbcType,
        ThrowingIntBiApplier<ResultSet, Object> indexedGetter,
        ThrowingBiApplier<ResultSet, String, Object> namedGetter,
        Class<?> supportClasses
    ) {
        this.supportClasses = supportClasses;
        this.jdbcType = jdbcType;
        this.indexedGetter = indexedGetter;
        this.namedGetter = namedGetter;
    }

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
    public Object getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
        return indexedGetter.apply(resultSet, columnIndex);
    }

    @Override
    public Object getResultValue(ResultSet resultSet, String columnName) throws SQLException {
        return namedGetter.apply(resultSet, columnName);
    }
}
