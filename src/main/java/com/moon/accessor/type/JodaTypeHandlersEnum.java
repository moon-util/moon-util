package com.moon.accessor.type;

import com.moon.accessor.function.ThrowingBiApplier;
import com.moon.accessor.function.ThrowingIntBiApplier;
import org.joda.time.*;

import java.sql.*;
import java.util.Arrays;

import static com.moon.accessor.type.TypeUsing2.*;
import static org.joda.time.Instant.ofEpochMilli;

/**
 * @author benshaoye
 */
enum JodaTypeHandlersEnum implements TypeHandler<Object> {
    /**
     * types
     */
    forJodaLocalDate(LocalDate.class,
        asInts(Types.DATE),
        (set, idx) -> useIfNonNull(set.getDate(idx), LocalDate::fromDateFields),
        (set, name) -> useIfNonNull(set.getDate(name), LocalDate::fromDateFields)) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setDate(index, new Date(((LocalDate) value).toDate().getTime()));
        }
    },
    forJodaLocalTime(LocalTime.class,
        asInts(Types.TIME),
        (set, idx) -> useIfNonNull(set.getTime(idx), LocalTime::fromDateFields),
        (set, name) -> useIfNonNull(set.getTime(name), LocalTime::fromDateFields)) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setTime(index, new Time(((LocalTime) value).toDateTimeToday().getMillis()));
        }
    },
    forJodaLocalDateTime(LocalDateTime.class,
        asInts(Types.TIMESTAMP),
        (set, idx) -> useIfNonNull(set.getTimestamp(idx), LocalDateTime::fromDateFields),
        (set, name) -> useIfNonNull(set.getTimestamp(name), LocalDateTime::fromDateFields)) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setTimestamp(index, new Timestamp(((LocalDateTime) value).toDate().getTime()));
        }
    },
    forJodaDateTime(DateTime.class,
        asInts(Types.TIMESTAMP),
        (set, idx) -> useIfNonNull(set.getTimestamp(idx), t -> new DateTime(t.getTime())),
        (set, name) -> useIfNonNull(set.getTimestamp(name), t -> new DateTime(t.getTime()))) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setTimestamp(index, new Timestamp(((DateTime) value).getMillis()));
        }
    },
    forJodaInstant(Instant.class,
        asInts(Types.TIMESTAMP),
        (set, idx) -> useIfNonNull(set.getTimestamp(idx), t -> ofEpochMilli(t.getTime())),
        (set, name) -> useIfNonNull(set.getTimestamp(name), t -> ofEpochMilli(t.getTime()))) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setTimestamp(index, new Timestamp(((Instant) value).getMillis()));
        }
    },
    forJodaMutableDateTime(MutableDateTime.class,
        asInts(Types.TIMESTAMP),
        (set, idx) -> useIfNonNull(set.getTimestamp(idx), dt -> new MutableDateTime(dt.getTime())),
        (set, name) -> useIfNonNull(set.getTimestamp(name), dt -> new MutableDateTime(dt.getTime()))) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            MutableDateTime datetime = (MutableDateTime) value;
            stmt.setTimestamp(index, new Timestamp(datetime.getMillis()));
        }
    },
    forJodaYearMonth(YearMonth.class,
        asInts(Types.VARCHAR),
        (set, idx) -> useIfNonNull(set.getString(idx), YearMonth::parse),
        (set, name) -> useIfNonNull(set.getString(name), YearMonth::parse)) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setString(index, String.valueOf(value));
        }
    },
    forJodaMonthDay(MonthDay.class,
        asInts(Types.VARCHAR),
        (set, idx) -> useIfNonNull(set.getString(idx), MonthDay::parse),
        (set, name) -> useIfNonNull(set.getString(name), MonthDay::parse)) {
        @Override
        public void setParameterForNonNull(PreparedStatement stmt, int index, Object value) throws SQLException {
            stmt.setString(index, String.valueOf(value));
        }
    };

    private final ThrowingIntBiApplier<ResultSet, Object> indexedGetter;
    private final ThrowingBiApplier<ResultSet, String, Object> namedGetter;
    private final int[] compatibleTypes;
    public final int primaryJdbcType;
    public final Class<?> supportClass;

    JodaTypeHandlersEnum(
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

    public int[] getCompatibleTypes() { return Arrays.copyOf(compatibleTypes, compatibleTypes.length); }

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
