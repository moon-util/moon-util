package com.moon.accessor.type;

import com.moon.accessor.function.ThrowingBiApplier;
import com.moon.accessor.function.ThrowingIntBiApplier;
import org.joda.time.*;

import java.sql.*;

import static com.moon.accessor.type.TypeUsing2.useIfNonNull;
import static org.joda.time.Instant.ofEpochMilli;

/**
 * @author benshaoye
 */
enum JodaTypeHandlersEnum implements TypeHandler<Object> {
    /**
     * types
     */
    forJodaLocalDate(Types.DATE,
        (set, idx) -> useIfNonNull(set.getDate(idx), LocalDate::fromDateFields),
        (set, name) -> useIfNonNull(set.getDate(name), LocalDate::fromDateFields),
        LocalDate.class) {
        @Override
        @SuppressWarnings("deprecation")
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            LocalDate date = (LocalDate) value;
            stmt.setDate(index, new Date(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth()));
        }
    },
    forJodaLocalTime(Types.TIME,
        (set, idx) -> useIfNonNull(set.getTime(idx), LocalTime::fromDateFields),
        (set, name) -> useIfNonNull(set.getTime(name), LocalTime::fromDateFields),
        LocalTime.class) {
        @Override
        @SuppressWarnings("deprecation")
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            LocalTime time = (LocalTime) value;
            stmt.setDate(index, new Date(time.getHourOfDay(), time.getMinuteOfHour(), time.getSecondOfMinute()));
        }
    },
    forJodaLocalDateTime(Types.TIMESTAMP,
        (set, idx) -> useIfNonNull(set.getTimestamp(idx), LocalDateTime::fromDateFields),
        (set, name) -> useIfNonNull(set.getTimestamp(name), LocalDateTime::fromDateFields),
        LocalDateTime.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setTimestamp(index, new Timestamp(((LocalDateTime) value).toDate().getTime()));
        }
    },
    forDateTime(Types.TIMESTAMP,
        (set, idx) -> useIfNonNull(set.getTimestamp(idx), LocalDateTime::fromDateFields),
        (set, name) -> useIfNonNull(set.getTimestamp(name), LocalDateTime::fromDateFields),
        DateTime.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setTimestamp(index, new Timestamp(((DateTime) value).getMillis()));
        }
    },
    forInstant(Types.TIMESTAMP,
        (set, idx) -> useIfNonNull(set.getTimestamp(idx), t -> ofEpochMilli(t.getTime())),
        (set, name) -> useIfNonNull(set.getTimestamp(name), t -> ofEpochMilli(t.getTime())),
        Instant.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setTimestamp(index, new Timestamp(((Instant) value).getMillis()));
        }
    },
    forMutableDateTime(Types.TIMESTAMP,
        (set, idx) -> useIfNonNull(set.getTimestamp(idx), dt -> new MutableDateTime(dt.getTime())),
        (set, name) -> useIfNonNull(set.getTimestamp(name), dt -> new MutableDateTime(dt.getTime())),
        MutableDateTime.class) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            MutableDateTime datetime = (MutableDateTime) value;
            stmt.setTimestamp(index, new Timestamp(datetime.getMillis()));
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
    forMonthDay(Types.VARCHAR,
        (set, idx) -> set.wasNull() ? null : MonthDay.parse(set.getString(idx)),
        (set, name) -> set.wasNull() ? null : MonthDay.parse(set.getString(name)),
        MonthDay.class) {
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
