package com.moon.core.time.converter;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.Jdk8DateCodec;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.DateCodec;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.moon.core.time.DateUtil;
import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import static com.alibaba.fastjson.serializer.SerializeConfig.getGlobalInstance;

/**
 * @author benshaoye
 */
public class FastDateConverter extends BaseDateConverter {

    public static SerializeConfig getSerializeConfig() { return getSerializeConfig(getGlobalInstance()); }

    public static SerializeConfig getSerializeConfig(SerializeConfig config) { return config; }

    public static ParserConfig getParserConfig() { return getParserConfig(new ParserConfig()); }

    /**
     * fastjson web date converter
     *
     * @param config
     *
     * @return
     */
    public static ParserConfig getParserConfig(ParserConfig config) {
        config.putDeserializer(LocalDateTime.class, new FastDateTimeDeserializer());
        config.putDeserializer(LocalDate.class, new FastDateTimeDeserializer());
        config.putDeserializer(java.sql.Date.class, new FastSqlDateDeserializer());
        config.putDeserializer(Timestamp.class, new FastTimestampDeserializer());
        config.putDeserializer(Calendar.class, new FastCalendarDeserializer());
        config.putDeserializer(Time.class, new FastSqlTimeDeserializer());
        config.putDeserializer(Date.class, new FastDateDeserializer());
        if (DateUtil.isImportedJodaTime()) {
            config.putDeserializer(DateTime.class, new FastJodaDateTimeParser());
        }
        return config;
    }

    private static abstract class FastBaseDateDeserializer extends DateCodec {

        Object doParse(DefaultJSONParser parser, Type clazz, Object fieldName, Object val) {
            Object parsed = val instanceof CharSequence ? parseToDate(val.toString()) : null;
            return parsed == null ? super.cast(parser, clazz, fieldName, val) : parsed;
        }
    }

    private static class FastDateDeserializer extends FastBaseDateDeserializer {

        @Override
        public <T> T cast(
            DefaultJSONParser parser, Type clazz, Object fieldName, Object val
        ) {
            Object parsed = super.doParse(parser, clazz, fieldName, val);
            if (parsed instanceof Date) {
                return (T) parsed;
            } else if (parsed instanceof Calendar) {
                return (T) new Date(((Calendar) parsed).getTimeInMillis());
            } else if (parsed instanceof Long) {
                return (T) new Date((long) parsed);
            }
            return (T) parsed;
        }
    }

    private static class FastTimestampDeserializer extends FastBaseDateDeserializer {

        @Override
        public <T> T cast(
            DefaultJSONParser parser, Type clazz, Object fieldName, Object val
        ) {
            Object parsed = super.doParse(parser, clazz, fieldName, val);
            if (parsed instanceof Timestamp) {
                return (T) parsed;
            } else if (parsed instanceof Date) {
                return (T) new Timestamp(((Date) parsed).getTime());
            } else if (parsed instanceof Calendar) {
                return (T) new Timestamp(((Calendar) parsed).getTimeInMillis());
            } else if (parsed instanceof Long) {
                return (T) new Timestamp((long) parsed);
            }
            return (T) parsed;
        }
    }

    private static class FastSqlTimeDeserializer extends FastBaseDateDeserializer {

        @Override
        public <T> T cast(
            DefaultJSONParser parser, Type clazz, Object fieldName, Object val
        ) {
            Object parsed = super.doParse(parser, clazz, fieldName, val);
            if (parsed instanceof Time) {
                return (T) parsed;
            } else if (parsed instanceof Date) {
                return (T) new Time(((Date) parsed).getTime());
            } else if (parsed instanceof Calendar) {
                return (T) new Time(((Calendar) parsed).getTimeInMillis());
            } else if (parsed instanceof Long) {
                return (T) new Time((long) parsed);
            }
            return (T) parsed;
        }
    }

    private static class FastSqlDateDeserializer extends FastBaseDateDeserializer {

        @Override
        public <T> T cast(
            DefaultJSONParser parser, Type clazz, Object fieldName, Object val
        ) {
            Object parsed = super.doParse(parser, clazz, fieldName, val);
            if (parsed instanceof java.sql.Date) {
                return (T) parsed;
            } else if (parsed instanceof Date) {
                return (T) new java.sql.Date(((Date) parsed).getTime());
            } else if (parsed instanceof Calendar) {
                return (T) new java.sql.Date(((Calendar) parsed).getTimeInMillis());
            } else if (parsed instanceof Long) {
                return (T) new java.sql.Date((long) parsed);
            }
            return (T) parsed;
        }
    }

    private static class FastCalendarDeserializer extends FastBaseDateDeserializer {

        @Override
        public <T> T cast(
            DefaultJSONParser parser, Type clazz, Object fieldName, Object val
        ) {
            Object parsed = super.doParse(parser, clazz, fieldName, val);
            if (parsed instanceof Calendar) {
                return (T) parsed;
            } else if (parsed instanceof Date) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(((Date) parsed).getTime());
                return (T) calendar;
            } else if (parsed instanceof Long) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis((long) parsed);
                return (T) calendar;
            }
            return (T) parsed;
        }
    }

    private static class FastDateTimeDeserializer extends Jdk8DateCodec {

        @Override
        protected ZonedDateTime parseZonedDateTime(String text, DateTimeFormatter formatter) {
            if (formatter == null) {
                formatter = getFormatter(text);
            }
            return super.parseZonedDateTime(text, formatter);
        }

        @Override
        protected LocalDateTime parseDateTime(String text, DateTimeFormatter formatter) {
            if (formatter == null) {
                formatter = getFormatter(text);
            }
            return super.parseDateTime(text, formatter);
        }

        @Override
        protected LocalDate parseLocalDate(String text, String format, DateTimeFormatter formatter) {
            if (formatter == null) {
                formatter = getFormatter(text);
            }
            return super.parseLocalDate(text, format, formatter);
        }
    }

    private static class FastJodaDateTimeParser implements ObjectDeserializer {

        @Override
        public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
            JSONLexer lexer = parser.lexer;
            if (lexer.token() == JSONToken.NULL) {
                lexer.nextToken();
                return null;
            }
            if (lexer.token() == JSONToken.LITERAL_STRING) {
                String text = lexer.stringVal().trim();
                lexer.nextToken();
                if ("".equals(text)) {
                    return null;
                }
                return (T) DateTime.parse(text);
            }
            if (lexer.token() == JSONToken.LITERAL_INT) {
                long milli = lexer.longValue();
                return (T) new DateTime(milli);
            }
            return (T) new DateTime();
        }

        @Override
        public int getFastMatchToken() { return JSONToken.LITERAL_STRING; }
    }
}
