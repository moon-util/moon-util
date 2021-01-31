package com.moon.accessor.param;

import com.moon.core.util.function.IntBiFunction;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Types;
import java.time.*;
import java.time.chrono.JapaneseDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * @author benshaoye
 */
public final class Parameters extends ArrayList<ParameterSetter> {

    public Parameters() { }

    public static Parameters of() { return new Parameters(); }

    private final int nextIdx() { return size() + 1; }

    public Parameters with(Object value) {
        return with(value, Types.JAVA_OBJECT, ObjectParameterSetter::new);
    }

    public Parameters with(ZonedDateTime value) {
        return with(value, Types.JAVA_OBJECT, ZonedDateTimeParameterSetter::new);
    }

    public Parameters with(OffsetDateTime value) {
        return with(value, Types.TIMESTAMP_WITH_TIMEZONE, OffsetTimestampParameterSetter::new);
    }

    public Parameters with(OffsetTime value) {
        return with(value, Types.TIME_WITH_TIMEZONE, OffsetTimeParameterSetter::new);
    }

    public Parameters with(LocalDateTime value) {
        return with(value, Types.TIMESTAMP, SqlTimestampParameterSetter::new);
    }

    public Parameters with(LocalTime value) {
        return with(value, Types.TIME, SqlTimeParameterSetter::new);
    }

    public Parameters with(LocalDate value) {
        return with(value, Types.DATE, SqlDateParameterSetter::new);
    }

    public Parameters with(Instant value) {
        return with(value, Types.TIMESTAMP, InstantParameterSetter::new);
    }

    public Parameters with(JapaneseDate value) {
        return with(value, Types.DATE, JapaneseDateParameterSetter::new);
    }

    public Parameters with(Calendar value) {
        add(value == null ? withNull(Types.TIMESTAMP) : new SqlTimestampParameterSetter(nextIdx(),
            value.getTimeInMillis()));
        return this;
    }

    public Parameters with(Date value) { return withSqlTimestamp(value); }

    public Parameters withSqlTimestamp(Date value) {
        return with(value, Types.TIMESTAMP, SqlTimestampParameterSetter::new);
    }

    public Parameters withSqlTime(Date value) {
        return with(value, Types.TIME, SqlTimeParameterSetter::new);
    }

    public Parameters withSqlDate(Date value) {
        return with(value, Types.DATE, SqlDateParameterSetter::new);
    }

    public Parameters withIndexedEnum(Enum<?> value) {
        return with(value, Types.INTEGER, EnumOrdinalParameterSetter::new);
    }

    public Parameters withNamedEnum(Enum<?> value) {
        return with(value, Types.VARCHAR, EnumNamedParameterSetter::new);
    }

    public Parameters with(BigDecimal value) {
        return with(value, Types.NUMERIC, BigDecimalParameterSetter::new);
    }

    public Parameters with(BigInteger value) {
        return with(value, Types.BIGINT, BigIntegerParameterSetter::new);
    }

    public Parameters with(String value) {
        return with(value, Types.VARCHAR, StringParameterSetter::new);
    }

    public Parameters with(Boolean value) {
        return with(value, Types.BOOLEAN, BooleanParameterSetter::new);
    }

    public Parameters with(Double value) {
        return with(value, Types.DOUBLE, DoubleParameterSetter::new);
    }

    public Parameters with(Float value) {
        return with(value, Types.REAL, FloatParameterSetter::new);
    }

    public Parameters with(Long value) {
        return with(value, Types.BIGINT, LongParameterSetter::new);
    }

    public Parameters with(Integer value) {
        return with(value, Types.INTEGER, IntParameterSetter::new);
    }

    public Parameters with(Short value) {
        return with(value, Types.SMALLINT, ShortParameterSetter::new);
    }

    public Parameters with(Byte value) {
        return with(value, Types.TINYINT, ByteParameterSetter::new);
    }

    public Parameters with(Blob value) {
        return with(value, Types.BLOB, BlobParameterSetter::new);
    }

    public Parameters with(Clob value) {
        return with(value, Types.CLOB, ClobParameterSetter::new);
    }

    private <T> Parameters with(T value, int type, IntBiFunction<T, ParameterSetter> getterApplier) {
        add(value == null ? withNull(type) : getterApplier.apply(nextIdx(), value));
        return this;
    }

    private ParameterSetter withNull(int type) {
        return new NullParameterSetter(nextIdx(), type);
    }
}
