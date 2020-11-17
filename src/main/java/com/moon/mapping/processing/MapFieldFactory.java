package com.moon.mapping.processing;

import org.joda.time.*;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.moon.mapping.processing.ElementUtils.getSimpleName;
import static com.moon.mapping.processing.StringUtils.*;
import static javax.tools.Diagnostic.Kind.MANDATORY_WARNING;

/**
 * @author benshaoye
 */
final class MapFieldFactory {

    private final static String DOUBLE = "double";
    private final static String LONG = "long";
    private final static String INT = "int";

    private boolean warned = false;

    public MapFieldFactory() {}

    public String doConvertField(
        final MappingModel model, Manager manager
    ) {
        unWarned();
        String field;
        if (!model.isGetterGeneric() && !model.isSetterGeneric()) {
            field = doConvertDeclaredField(model, manager);
            if (field == null && isSubtypeOf(model.getGetterDeclareType(), model.getSetterDeclareType())) {
                field = "{toName}.{setterName}({fromName}.{getterName}());";
            }
            if (field == null && !isWarned()) {
                field = warningAndIgnored(model);
            }
        } else if (model.isGetterGeneric() && model.isSetterGeneric()) {
            final String setterTypeString = model.getSetterActualType();
            final String getterTypeString = model.getGetterActualType();
            field = doConvertGenerify(manager, model, getterTypeString, setterTypeString);
        } else if (model.isGetterGeneric()) {
            final String setterTypeString = model.getSetterDeclareType();
            final String getterTypeString = model.getGetterActualType();
            field = doConvertGenerify(manager, model, getterTypeString, setterTypeString);
        } else if (model.isSetterGeneric()) {
            final String setterTypeString = model.getSetterActualType();
            final String getterTypeString = model.getGetterDeclareType();
            field = doConvertGenerify(manager, model, getterTypeString, setterTypeString);
        } else {
            throw new IllegalStateException("This is impossible.");
        }
        return onDeclareCompleted(field, model);
    }

    private String doConvertGenerify(
        Manager manager, MappingModel model, String getterTypeString, String setterTypeString
    ) {
        Elements utils = EnvUtils.getUtils();
        TypeMirror setterType = utils.getTypeElement(setterTypeString).asType();
        TypeMirror getterType = utils.getTypeElement(getterTypeString).asType();
        if (EnvUtils.getTypes().isSubtype(getterType, setterType)) {
            String field = "{toName}.{setterName}(({setterType}){fromName}.{getterName}());";
            return Replacer.setterType.replace(field, manager.onImported(setterTypeString));
        } else {
            return warningAndIgnored(model);
        }
    }

    @SuppressWarnings("all")
    private String doConvertDeclaredField(
        final MappingModel model, Manager manager
    ) {
        String t0 = declareOnDefault(model, manager);
        if (t0 == null) {
            t0 = declare2String(model, manager);
        }
        if (t0 == null) {
            t0 = declare2PrimitiveNumber(model, manager);
        }
        if (t0 == null) {
            t0 = declare2WrappedNumber(model, manager);
        }
        if (t0 == null) {
            t0 = declare2Enum(model, manager);
        }
        if (t0 == null) {
            t0 = declare2Boolean(model, manager);
        }
        if (t0 == null) {
            t0 = declare2Char(model, manager);
        }
        if (t0 == null) {
            t0 = declare2Date(model, manager);
        }
        if (t0 == null) {
            t0 = declare2Calendar(model, manager);
        }
        if (t0 == null) {
            t0 = declare2Jdk8Time(model, manager);
        }
        if (t0 == null) {
            t0 = declare2JodaTime(model, manager);
        }
        if (t0 == null) {
            t0 = declare2BigInteger(model, manager);
        }
        if (t0 == null) {
            t0 = declare2BigDecimal(model, manager);
        }
        return onDeclareCompleted(t0, model);
    }

    private String declareOnDefault(MappingModel model, Manager manager) {
        if (model.nonAnnotatedMapProperty()) {
            final String setterDeclareType = model.getSetterDeclareType();
            final String getterDeclareType = model.getGetterDeclareType();
            if (Objects.equals(setterDeclareType, getterDeclareType)//
                || isSubtypeOf(getterDeclareType, setterDeclareType)) {
                return "{toName}.{setterName}({fromName}.{getterName}());";
            }
        }
        return null;
    }

    private String forDefaultEnum(MappingModel model, Manager manager, String enumClassname) {
        PropertyAttr attr = model.getAttr();
        String dftValue = attr.defaultValue();
        if (DetectUtils.isDigit(dftValue)) {
            return manager.ofStatic().onEnumIndexed(enumClassname, dftValue);
        } else if (DetectUtils.isVar(dftValue)) {
            return manager.ofStatic().onEnumNamed(enumClassname, dftValue);
        } else {
            return manager.ofStatic().defaultNull();
        }
    }

    /**
     * 已支持：枚举默认值、枚举到字符串数字等默认类型转换、指定属性名
     * <p>
     * 不支持格式化
     *
     * @param model
     * @param manager
     *
     * @return
     */
    private String declare2Enum(MappingModel model, Manager manager) {
        final String setterDeclareType = model.getSetterDeclareType();
        if (!isEnum(setterDeclareType)) {
            return null;
        }
        String t0;
        final ConvertManager convert = manager.ofConvert();
        final String getterDeclareType = model.getGetterDeclareType();
        final String dftValue = forDefaultEnum(model, manager, setterDeclareType);
        if (isString(getterDeclareType)) {
            Supplier<String> mappings = () -> "{setterType}.valueOf({var})";
            t0 = convert.useMapping(dftValue, mappings, setterDeclareType, getterDeclareType);
        } else if (isPrimitiveNumber(getterDeclareType)) {
            String enumValues = manager.ofStatic().onEnumValues(setterDeclareType);
            t0 = "{toName}.{setterName}({setterType}[{cast}{fromName}.{getterName}()]);";
            t0 = Replacer.cast.replace(t0, isCompatible(INT, getterDeclareType) ? "" : "(int)");
            t0 = Replacer.setterType.replace(t0, enumValues);
        } else if (isSubtypeOf(getterDeclareType, Number.class)) {
            String enumValues = manager.ofStatic().onEnumValues(setterDeclareType);
            t0 = convert.useMapping(dftValue, () -> {
                String tx = "{name}[{var}.intValue()]";
                return Replacer.name.replace(tx, enumValues);
            }, "", getterDeclareType);
        } else {
            t0 = warningAndIgnored(model);
        }
        return t0;
    }

    /**
     * 所有数据类型到字符串的转换
     * 1. 基本数字类型：String.valueOf(..) + 格式化
     * 2. 数字包装类 + 格式化
     * 3. 枚举
     * 4. jdk8 日期类型 + 格式化
     * 5. Date、Calendar、sql.Date 日期类型 + 格式化
     * 6. Joda 日期类型格式化
     * 7. 其他对象到直接调用 Object.toString()
     *
     * @param model
     * @param manager
     *
     * @return
     */
    private String declare2String(final MappingModel model, Manager manager) {
        final String setterDeclareType = model.getSetterDeclareType();
        if (!isString(setterDeclareType)) {
            return null;
        }
        String t0;
        final PropertyAttr attr = model.getAttr();
        final ConvertManager convert = manager.ofConvert();
        final String getterDeclareType = model.getGetterDeclareType();
        final String formatVal = attr.formatValue();
        final String dftValue = manager.ofStatic().onString(attr.defaultValue());
        if (isString(getterDeclareType)) {
            if (isNullString(dftValue)) {
                t0 = "{toName}.{setterName}({fromName}.{getterName}());";
            } else {
                t0 = convert.onMapping(dftValue, "{var}", setterDeclareType, String.class);
            }
        } else if (StringUtils.isPrimitive(getterDeclareType)) {
            // getter 是基本数据类型，没有默认值，因为 get 不到 null
            if (formatVal == null) {
                t0 = "{toName}.{setterName}({type1}.valueOf({fromName}.{getterName}()));";
                t0 = Replacer.type1.replace(t0, manager.onImported(String.class));
            } else {
                t0 = convert.useMapping(dftValue, () -> {
                    String stringType = String.class.getCanonicalName();
                    String targetType = isCompatible(LONG, getterDeclareType) ? LONG : DOUBLE;
                    CallerInfo info = convert.find(stringType, targetType, stringType);
                    return info.toString("{fromName}.{getterName}()", strWrapped(formatVal));
                }, setterDeclareType, getterDeclareType);
            }
        } else if (isEnum(getterDeclareType)) {
            t0 = convert.onMapping(dftValue, "{var}.name()", setterDeclareType, getterDeclareType);
        } else if (isFormattable(formatVal, getterDeclareType, Number.class)) {
            t0 = useConvertAndDftValAndFormat(model, manager, dftValue, Number.class);
        } else if (isFormattable(formatVal, getterDeclareType, Date.class)) {
            t0 = useConvertAndDftValAndFormat(model, manager, dftValue, Date.class);
        } else if (isFormattable(formatVal, getterDeclareType, Calendar.class)) {
            t0 = useConvertAndDftValAndFormat(model, manager, dftValue, Calendar.class);
        } else if (isFormattable(formatVal, getterDeclareType, TemporalAccessor.class)) {
            String format = manager.ofStatic().onDateTimeFormatter(formatVal);
            String fmt = Replacer.format.replace("{format}.format({var})", format);
            t0 = convert.onMapping(dftValue, fmt, setterDeclareType, getterDeclareType);
        } else if (isJodaFormattable(formatVal, getterDeclareType)) {
            String format = manager.ofStatic().onJodaDateTimeFormat(formatVal);
            String fmt = Replacer.format.replace("{format}.print({var})", format);
            t0 = convert.onMapping(dftValue, fmt, setterDeclareType, getterDeclareType);
        } else {
            t0 = convert.onMapping(dftValue, "{var}.toString()", setterDeclareType, getterDeclareType);
        }
        return t0;
    }

    private static String useConvertAndDftValAndFormat(
        MappingModel model, Manager manager, String dftValue, Class<?> getterSuper
    ) {
        return manager.ofConvert().useConvert(dftValue, info -> {
            String fmt = strWrapped(model.getAttr().formatValue());
            return info.toString(null, fmt);
        }, model.getSetterDeclareType(), getterSuper, String.class);
    }

    private String forDefaultNumber(MappingModel model, Manager manager, String classname) {
        PropertyAttr attr = model.getAttr();
        String defaultValue = attr.defaultValue();
        if (defaultValue != null) {
            return manager.ofStatic().onDefaultNumber(classname, defaultValue);
        }
        return manager.ofStatic().defaultNull();
    }

    /**
     * 支持的数据转换：
     * 1. 基本数据类型之间的数据转换
     * 2. 包装类到基本数据之间的数据转换 + 默认值
     * 3，字符串到基本数据之间的转换 + 格式化 + 默认值
     * 4. Number 到基本数据之间的转换 + 默认值
     * 5. char 到数字之间的转换（不包括{@link Character}）
     * 6. 枚举到数字之间的转换，{@link Enum#ordinal()} + 默认值
     * 7. util Date 到 long/double 的转换 + 默认值
     * 8. jdk8 日期到 long/double 的转换 + 默认值
     * 9. joda 日期到 long/double 的转换 + 默认值
     *
     * @param model
     * @param manager
     *
     * @return
     */
    private String declare2PrimitiveNumber(final MappingModel model, Manager manager) {
        String t0;
        final ConvertManager convert = manager.ofConvert();
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        if (!isPrimitiveNumber(setterDeclareType)) {
            return null;
        }
        final String dftValue = forDefaultNumber(model, manager, setterDeclareType);
        if (isPrimitiveNumber(getterDeclareType)) {
            String cast = castTopAsPrimitive(setterDeclareType, getterDeclareType);
            t0 = "{toName}.{setterName}({cast}{fromName}.{getterName}());";
            t0 = Replacer.cast.replace(t0, cast);
        } else if (isWrappedNumber(getterDeclareType)) {
            t0 = convert.onMapping(dftValue, "{var}.{setterType}Value()", setterDeclareType, getterDeclareType);
        } else if (StringUtils.isPrimitiveChar(getterDeclareType)) {
            String cast = castTopAsPrimitive(setterDeclareType, INT);
            t0 = "{toName}.{setterName}({cast}{fromName}.{getterName}());";
            t0 = Replacer.cast.replace(t0, cast);
        } else if (isString(getterDeclareType)) {
            String format = model.getAttr().formatValue();
            String setterWrapped = StringUtils.toWrappedType(manager.onImported(setterDeclareType));
            if (isNullString(format)) {
                t0 = convert.useMapping(dftValue, () -> {
                    String tx = "{type0}.parse{type1}({var})";
                    tx = Replacer.type0.replace(tx, manager.onImported(setterWrapped));
                    String type1 = INT.equals(setterDeclareType) ? "Int" : setterWrapped;
                    return Replacer.type1.replace(tx, getSimpleName(type1));
                }, setterDeclareType, getterDeclareType);
            } else {
                t0 = convert.useConvert(dftValue, info -> {
                    String suffix = ".{type0}Value()";
                    suffix = Replacer.type0.replace(suffix, setterDeclareType);
                    return info.toString(null, strWrapped(format)) + suffix;
                }, Number.class.getCanonicalName(), String.class, String.class);
            }
        } else if (isSubtypeOf(getterDeclareType, Number.class)) {
            t0 = convert.onMapping(dftValue, "{var}.{setterType}Value()", setterDeclareType, getterDeclareType);
        } else if (isEnum(getterDeclareType)) {
            String cast = castTopAsPrimitive(setterDeclareType, INT);
            t0 = Replacer.cast.replace("{cast}{var}.ordinal()", cast);
            t0 = convert.onMapping(dftValue, t0, setterDeclareType, getterDeclareType);
        } else if (LONG.equals(setterDeclareType) || DOUBLE.equals(setterDeclareType)) {
            if (isSubtypeOf(getterDeclareType, Date.class)) {
                t0 = convert.useConvert(dftValue, STRINGIFY, setterDeclareType, Date.class);
            } else if (isSubtypeOf(getterDeclareType, Calendar.class)) {
                t0 = convert.useConvert(dftValue, STRINGIFY, setterDeclareType, Calendar.class);
            } else if (isTypeofAny(getterDeclareType, java.time.LocalDate.class, java.time.LocalDateTime.class,
                ZonedDateTime.class, OffsetDateTime.class, java.time.Instant.class)) {
                t0 = convert.useConvert(dftValue, STRINGIFY, setterDeclareType, getterDeclareType);
            } else if (DetectUtils.IMPORTED_JODA_TIME) {
                if (isSubtypeOf(getterDeclareType, ReadableInstant.class) ||
                    isTypeof(getterDeclareType, DateTime.class)) {
                    t0 = convert.useConvert(dftValue, STRINGIFY, LONG, ReadableInstant.class);
                } else if (isTypeofAny(getterDeclareType, LocalDateTime.class, LocalDate.class)) {
                    t0 = convert.useConvert(dftValue, STRINGIFY, LONG, getterDeclareType);
                } else {
                    t0 = warningAndIgnored(model);
                }
            } else {
                t0 = warningAndIgnored(model);
            }
        } else {
            t0 = warningAndIgnored(model);
        }
        return t0;
    }

    /**
     * 支持的数据转换：
     * 1. 基本数据类型之间的数据转换
     * 2. 包装类到基本数据之间的数据转换 + 默认值
     * 3，字符串到基本数据之间的转换 + 格式化 + 默认值
     * 4. Number 到基本数据之间的转换 + 默认值
     * 5. char 到数字之间的转换（不包括{@link Character}）
     * 6. 枚举到数字之间的转换，{@link Enum#ordinal()} + 默认值
     * 7. util Date 到 long/double 的转换 + 默认值
     * 8. jdk8 日期到 long/double 的转换 + 默认值
     * 9. joda 日期到 long/double 的转换 + 默认值
     *
     * @param model
     * @param manager
     *
     * @return
     */
    private String declare2WrappedNumber(final MappingModel model, Manager manager) {
        String t0;
        final PropertyAttr attr = model.getAttr();
        final ConvertManager convert = manager.ofConvert();
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        if (isWrappedNumber(setterDeclareType)) {
            final String dftValue = forDefaultNumber(model, manager, setterDeclareType);
            final String setterPrimitive = toPrimitiveType(setterDeclareType);
            if (isPrimitiveNumber(getterDeclareType)) {
                String cast = getterDeclareType.equals(setterPrimitive) ? "" : bracketed(setterPrimitive);
                t0 = Replacer.cast.replace("{toName}.{setterName}({cast}{fromName}.{getterName}());", cast);
            } else if (isWrappedNumber(getterDeclareType)) {
                if (Objects.equals(setterDeclareType, getterDeclareType)) {
                    if (isNullString(dftValue)) {
                        t0 = "{toName}.{setterName}({fromName}.{getterName}());";
                    } else {
                        t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                            "{toName}.{setterName}({var}==null?{NULL}:{var});";
                        t0 = Replacer.NULL.replace(t0, dftValue);
                    }
                } else {
                    t0 = convert.useMapping(dftValue, () -> {
                        String tx = "{var}.{type0}Value()";
                        return Replacer.type0.replace(tx, setterPrimitive);
                    }, setterDeclareType, getterDeclareType);
                }
            } else if (isString(getterDeclareType)) {
                if (attr.formatValue() == null) {
                    t0 = convert.useMapping(dftValue, () -> {
                        String tx = "{setterType}.parse{type1}({var})";
                        String type1 = INT.equals(setterPrimitive) ? "Int" : getSimpleName(setterDeclareType);
                        return Replacer.type1.replace(tx, type1);
                    }, setterDeclareType, getterDeclareType);
                } else {
                    t0 = convert.useConvert(dftValue, info -> {
                        String format = strWrapped(attr.formatValue());
                        return info.toString(null, format);
                    }, setterDeclareType, String.class, String.class);
                }
            } else if (isEnum(getterDeclareType)) {
                String cast = castTopAsPrimitive(setterPrimitive, INT);
                t0 = Replacer.cast.replace("{setterType}.valueOf({cast}{var}.ordinal())", cast);
                t0 = convert.onMapping(dftValue, t0, setterDeclareType, getterDeclareType);
            } else if (StringUtils.isPrimitiveChar(getterDeclareType)) {
                t0 = "{toName}.{setterName}({cast} {fromName}.{getterName}());";
                t0 = Replacer.cast.replace(t0, castTopAsPrimitive(setterPrimitive, INT));
            } else if (isTypeof(getterDeclareType, Character.class)) {
                String cast = castTopAsPrimitive(setterPrimitive, INT);
                String charVal = Replacer.cast.replace("{setterType}.valueOf({cast}{var}.charValue())", cast);
                t0 = convert.onMapping(dftValue, charVal, setterDeclareType, getterDeclareType);
            } else if (isSubtypeOf(getterDeclareType, Number.class)) {
                String mapper = Replacer.type0.replace("{var}.{type0}Value()", setterPrimitive);
                t0 = convert.onMapping(dftValue, mapper, "", getterDeclareType);
            } else if (Long.class.getCanonicalName().equals(setterDeclareType)) {
                if (isSubtypeOf(getterDeclareType, Date.class)) {
                    t0 = convert.onMapping(dftValue, "{var}.getTime()", "", getterDeclareType);
                } else if (isSubtypeOf(getterDeclareType, Calendar.class)) {
                    t0 = convert.onMapping(dftValue, "{var}.getTimeInMillis()", "", getterDeclareType);
                } else if (DetectUtils.IMPORTED_JODA_TIME) {
                    if (isSubtypeOf(getterDeclareType, ReadableInstant.class) ||
                        isTypeof(getterDeclareType, DateTime.class)) {
                        t0 = convert.onMapping(dftValue, "{var}.getMillis()", "", getterDeclareType);
                    } else if (isTypeofAny(getterDeclareType, LocalDateTime.class, LocalDate.class,
                        MutableDateTime.class)) {
                        t0 = convert.onMapping(dftValue, "{var}.toDate().getTime()", "", getterDeclareType);
                    } else {
                        t0 = warningAndIgnored(model);
                    }
                } else {
                    t0 = warningAndIgnored(model);
                }
            } else {
                t0 = warningAndIgnored(model);
            }
        } else if (isTypeof(setterDeclareType, Number.class)) {
            final String dftValue = forDefaultNumber(model, manager, setterDeclareType);
            if (isSubtypeOf(getterDeclareType, Number.class) ||
                isPrimitiveNumber(getterDeclareType) ||
                isPrimitiveChar(getterDeclareType)) {
                t0 = "{toName}.{setterName}({fromName}.{getterName}());";
            } else if (isEnum(getterDeclareType)) {
                t0 = convert.onMapping(dftValue, "{var}.ordinal()", "", getterDeclareType);
            } else if (isString(getterDeclareType) && attr.formatValue() != null) {
                t0 = convert.useConvert(dftValue, info -> {
                    String format = strWrapped(attr.formatValue());
                    return info.toString(null, format);
                }, Number.class.getCanonicalName(), String.class, String.class);
            } else {
                t0 = warningAndIgnored(model);
            }
        } else {
            return null;
        }
        return t0;
    }

    /**
     * String -> boolean
     * byte|short|int|long|float|double -> boolean : number != 0
     * Byte|Short|Integer|Long|Float|Double -> boolean {@link Character#forDigit(int, int)}
     * Boolean -> boolean
     *
     * @param model
     *
     * @return
     */
    private String declare2Boolean(final MappingModel model, Manager manager) {
        String t0;
        final PropertyAttr attr = model.getAttr();
        final String defaultValue = attr.defaultValue();
        final StaticManager staticManager = manager.ofStatic();
        final ConvertManager convert = manager.ofConvert();
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        String setterWrappedType = StringUtils.toWrappedType(setterDeclareType);
        if (isPrimitiveBoolean(setterDeclareType)) {
            final String dftValue = staticManager.onDefaultBoolean(setterDeclareType, defaultValue);
            if (isString(getterDeclareType)) {
                if (isNullString(dftValue)) {
                    t0 = "{toName}.{setterName}({setterType}.parseBoolean({fromName}.{getterName}()));";
                    t0 = Replacer.setterType.replace(t0, manager.onImported(setterWrappedType));
                } else {
                    String tx = "{setterType}.parseBoolean({var})";
                    t0 = convert.onMapping(dftValue, tx, setterDeclareType, getterDeclareType);
                }
            } else if (isPrimitiveNumber(getterDeclareType)) {
                t0 = "{toName}.{setterName}({fromName}.{getterName}() != 0);";
            } else if (isWrappedNumber(getterDeclareType)) {
                String staticVar = staticManager.onDefaultNumber(getterDeclareType, "0");
                if (isNullString(dftValue)) {
                    t0 = "{toName}.{setterName}({name}.equals({fromName}.{getterName}()));";
                    t0 = Replacer.name.replace(t0, staticVar);
                } else {
                    t0 = convert.useMapping(dftValue, () -> {
                        String tx = "{name}.equals({fromName}.{getterName}())";
                        return Replacer.name.replace(tx, staticVar);
                    }, setterDeclareType, getterDeclareType);
                }
            } else if (isPrimitiveBoolean(getterDeclareType)) {
                t0 = "{toName}.{setterName}({fromName}.{getterName}());";
            } else if (Boolean.class.getName().equals(getterDeclareType)) {
                t0 = convert.onMapping(dftValue, "{var}", setterDeclareType, setterWrappedType);
            } else {
                t0 = warningAndIgnored(model);
            }
        } else if (Boolean.class.getName().equals(setterDeclareType)) {
            final String dftValue = staticManager.onDefaultBoolean(setterDeclareType, defaultValue);
            if (isString(getterDeclareType)) {
                if (isNullString(dftValue)) {
                    t0 = "{toName}.{setterName}({setterType}.parseBoolean({fromName}.{getterName}()));";
                    t0 = Replacer.setterType.replace(t0, manager.onImported(setterWrappedType));
                } else {
                    String tx = "{setterType}.parseBoolean({var})";
                    t0 = convert.onMapping(dftValue, tx, setterDeclareType, getterDeclareType);
                }
            } else if (isPrimitiveNumber(getterDeclareType)) {
                t0 = "{toName}.{setterName}({fromName}.{getterName}() != 0);";
            } else if (isWrappedNumber(getterDeclareType)) {
                String staticVar = staticManager.onDefaultNumber(getterDeclareType, "0");
                if (isNullString(dftValue)) {
                    t0 = "{toName}.{setterName}({name}.equals({fromName}.{getterName}()));";
                    t0 = Replacer.name.replace(t0, staticVar);
                } else {
                    t0 = convert.useMapping(dftValue, () -> {
                        String tx = "{name}.equals({fromName}.{getterName}())";
                        return Replacer.name.replace(tx, staticVar);
                    }, setterDeclareType, getterDeclareType);
                }
            } else if (isPrimitiveBoolean(getterDeclareType)) {
                t0 = "{toName}.{setterName}({fromName}.{getterName}());";
            } else if (isTypeof(getterDeclareType, Boolean.class)) {
                t0 = convert.onMapping(dftValue, "{var}", setterDeclareType, setterWrappedType);
            } else {
                t0 = warningAndIgnored(model);
            }
        } else {
            return null;
        }
        return t0;
    }

    /**
     * char|Character -> char
     * byte|short|int|long|float|double -> char {@link Character#forDigit(int, int)}
     * Byte|Short|Integer|Long|Float|Double -> char {@link Character#forDigit(int, int)}
     * <p>
     * char|Character -> Character
     * byte|short|int|long|float|double -> Character {@link Character#forDigit(int, int)}
     * Byte|Short|Integer|Long|Float|Double -> Character {@link Character#forDigit(int, int)}
     *
     * @param model
     *
     * @return
     */
    private String declare2Char(final MappingModel model, Manager manager) {
        String t0;
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        if (StringUtils.isPrimitiveChar(setterDeclareType)) {
            if (StringUtils.isPrimitiveChar(getterDeclareType)) {
                t0 = "{toName}.{setterName}({fromName}.{getterName}());";
            } else if (Character.class.getName().equals(getterDeclareType)) {
                t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                    "if ({var} != null) { {toName}.{setterName}({var}); }";
                t0 = Replacer.getterType.replace(t0, manager.onImported(getterDeclareType));
            } else if (isPrimitiveNumber(getterDeclareType)) {
                t0 = "{toName}.{setterName}(Character.forDigit({cast}{fromName}.{getterName}(), 10));";
                t0 = Replacer.cast.replace(t0, isCompatible(INT, getterDeclareType) ? "" : "(int)");
            } else if (isWrappedNumber(getterDeclareType)) {
                t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                    "if ({var} != null) { {toName}.{setterName}(Character.forDigit({var}.intValue(), 10)); }";
                t0 = Replacer.getterType.replace(t0, manager.onImported(getterDeclareType));
            } else {
                t0 = warningAndIgnored(model);
            }
        } else if (Character.class.getName().equals(setterDeclareType)) {
            if (StringUtils.isPrimitiveChar(getterDeclareType) || Character.class.getName().equals(getterDeclareType)) {
                t0 = "{toName}.{setterName}({fromName}.{getterName}());";
            } else if (isPrimitiveNumber(getterDeclareType)) {
                t0 = "{toName}.{setterName}(Character.forDigit({cast}{fromName}.{getterName}(), 10));";
                t0 = Replacer.cast.replace(t0, isCompatible(INT, getterDeclareType) ? "" : "(int)");
            } else if (isWrappedNumber(getterDeclareType)) {
                t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}(null); }" +//
                    "else { {toName}.{setterName}(Character.forDigit({var}.intValue(), 10)); }";
                t0 = Replacer.getterType.replace(t0, manager.onImported(getterDeclareType));
            } else {
                t0 = warningAndIgnored(model);
            }
        } else {
            return null;
        }
        return t0;
    }

    private String forDefaultBigDecimal(MappingModel model, Manager manager, String classname) {
        PropertyAttr attr = model.getAttr();
        String defaultValue = attr.defaultValue();
        if (defaultValue != null) {
            return manager.ofStatic().onDefaultBigDecimal(defaultValue);
        }
        return manager.ofStatic().defaultNull();
    }

    /**
     * byte、short、int、long、float、double -> {@link BigDecimal#valueOf(long)}, {@link BigDecimal#valueOf(double)}
     * Byte、Short、Integer、Long、Float、Double -> {@link BigDecimal#valueOf(long)}
     * <p>
     * String -> {@link BigDecimal#BigDecimal(String)}, {@link BigDecimal#BigDecimal(BigInteger)}
     * <p>
     * char|Character -> {@link BigDecimal#valueOf(long)}
     *
     * @param model
     *
     * @return
     */
    private String declare2BigDecimal(final MappingModel model, Manager manager) {
        String t0;
        final ConvertManager convert = manager.ofConvert();
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        if (!isTypeof(setterDeclareType, BigDecimal.class)) {
            return null;
        }
        final String dftValue = forDefaultBigDecimal(model, manager, setterDeclareType);
        if (isPrimitiveNumber(getterDeclareType)) {
            t0 = "{toName}.{setterName}({setterType}.valueOf({fromName}.{getterName}()));";
            t0 = Replacer.setterType.replace(t0, setterDeclareType);
        } else if (isTypeofAny(getterDeclareType, Double.class, Float.class)) {
            t0 = convert.useConvert(dftValue, STRINGIFY, setterDeclareType, getterDeclareType);
        } else if (isTypeof(getterDeclareType, BigInteger.class)) {
            t0 = convert.useConvert(dftValue, STRINGIFY, setterDeclareType, BigInteger.class);
        } else if (isSubtypeOf(getterDeclareType, Number.class)) {
            t0 = convert.useConvert(dftValue, STRINGIFY, setterDeclareType, Number.class);
        } else if (isEnum(getterDeclareType)) {
            String mapper = "{setterType}.valueOf({var}.ordinal())";
            t0 = convert.onMapping(dftValue, mapper, setterDeclareType, getterDeclareType);
        } else if (isString(getterDeclareType)) {
            String format = model.getAttr().formatValue();
            if (format == null) {
                t0 = convert.useConvert(dftValue, STRINGIFY, setterDeclareType, String.class);
            } else {
                Function<CallerInfo, String> mapper = info -> info.toString(null, strWrapped(format));
                t0 = convert.useConvert(dftValue, mapper, setterDeclareType, String.class, String.class);
            }
        } else if (StringUtils.isPrimitiveChar(getterDeclareType)) {
            t0 = "{toName}.{setterName}({setterType}.valueOf({type0}.valueOf({fromName}.{getterName}())));";
            t0 = Replacer.setterType.replace(t0, setterDeclareType);
        } else if (isTypeof(getterDeclareType, Character.class)) {
            t0 = convert.useMapping(dftValue, () -> {
                return "new {setterType}({var}.toString())";
            }, setterDeclareType);
        } else {
            t0 = warningAndIgnored(model);
        }
        return t0;
    }

    private String forDefaultBigInteger(MappingModel model, Manager manager, String classname) {
        PropertyAttr attr = model.getAttr();
        String defaultValue = attr.defaultValue();
        if (defaultValue != null) {
            return manager.ofStatic().onDefaultBigInteger(defaultValue);
        }
        return manager.ofStatic().defaultNull();
    }

    /**
     * byte、short、int、long、float、double -> {@link BigInteger#valueOf(long)}
     * Byte、Short、Integer、Long、Float、Double -> {@link BigInteger#valueOf(long)}
     * String -> {@link BigInteger#BigInteger(String)}
     * char|Character -> {@link BigInteger#valueOf(long)}
     *
     * @param model
     *
     * @return
     */
    private String declare2BigInteger(final MappingModel model, Manager manager) {
        String t0;
        final ConvertManager convert = manager.ofConvert();
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        if (!isTypeof(setterDeclareType, BigInteger.class)) {
            return null;
        }
        final String formatVal = model.getAttr().formatValue();
        final String dftValue = forDefaultBigInteger(model, manager, setterDeclareType);
        if (isPrimitiveNumber(getterDeclareType)) {
            t0 = "{toName}.{setterName}({setterType}.valueOf({cast}{fromName}.{getterName}()));";
            String cast = isCompatible(LONG, toPrimitiveType(getterDeclareType)) ? "" : "(long)";
            t0 = Replacer.cast.replace(t0, cast);
            t0 = Replacer.setterType.replace(t0, setterDeclareType);
        } else if (StringUtils.isPrimitiveChar(getterDeclareType)) {
            t0 = convert.useMapping(dftValue, () -> {
                String tx = "{setterType}.valueOf({type0}.valueOf({var}))";
                return Replacer.type0.replace(tx, manager.onImported(String.class));
            }, setterDeclareType);
        } else if (isSubtypeOf(getterDeclareType, Number.class)) {
            t0 = convert.useConvert(dftValue, STRINGIFY, setterDeclareType, Number.class);
        } else if (isEnum(getterDeclareType)) {
            String mapper = "{setterType}.valueOf({var}.ordinal())";
            t0 = convert.onMapping(dftValue, mapper, setterDeclareType, getterDeclareType);
        } else if (isString(getterDeclareType)) {
            if (formatVal == null) {
                t0 = convert.useConvert(dftValue, STRINGIFY, setterDeclareType, String.class);
            } else {
                Function<CallerInfo, String> mapper = info -> info.toString(null, strWrapped(formatVal));
                t0 = convert.useConvert(dftValue, mapper, setterDeclareType, String.class, String.class);
            }
        } else if (isTypeof(getterDeclareType, Character.class)) {
            t0 = convert.useMapping(dftValue, () -> {
                return "new {setterType}({var}.toString())";
            }, setterDeclareType);
        } else {
            t0 = warningAndIgnored(model);
        }
        return t0;
    }

    /**
     * byte|short|int|long|float|double
     * Byte|Short|Integer|Long|Float|Double
     * <p>
     * {@link LocalDateTime#LocalDateTime(long)}
     * {@link LocalDate#LocalDate(long)}
     * {@link LocalTime#LocalTime(long)}
     * {@link Duration#Duration(long)}
     * {@link YearMonth#YearMonth(long)}
     * {@link MonthDay#MonthDay(long)}
     * {@link DateTime#DateTime(long)}
     * {@link Instant#Instant(long)}
     * {@link MutableDateTime#MutableDateTime(long)}
     * {@link MutablePeriod#MutablePeriod(long)}
     *
     * @param model
     *
     * @return
     */
    private String declare2JodaTime(final MappingModel model, Manager manager) {
        if (!DetectUtils.IMPORTED_JODA_TIME) {
            return null;
        }
        String t0;
        final ConvertManager convert = manager.ofConvert();
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        if (isTypeofAny(setterDeclareType, MutableDateTime.class, DateTime.class, LocalDateTime.class, LocalDate.class,
            LocalTime.class, MutablePeriod.class, Duration.class, YearMonth.class, Instant.class, MonthDay.class)) {
            if (isPrimitiveNumber(getterDeclareType)) {
                String cast = isCompatible(LONG, getterDeclareType) ? "" : "(long)";
                t0 = "{toName}.{setterName}(new {setterType}({cast}{fromName}.{getterName}()));";
                t0 = Replacer.cast.replace(t0, cast);
                t0 = Replacer.setterType.replace(t0, manager.onImported(setterDeclareType));
            } else if (isSubtypeOf(getterDeclareType, Number.class)) {
                t0 = convert
                    .onMapping(null, "new {setterType}({var}.longValue())", setterDeclareType, getterDeclareType);
            } else if (isTypeof(setterDeclareType, Duration.class)) {
                if (isString(getterDeclareType)) {
                    t0 = convert.onMapping(null, "{setterType}.parse({var})", setterDeclareType, getterDeclareType);
                } else {
                    t0 = warningAndIgnored(model);
                }
            } else if (isTypeofAny(setterDeclareType, MutableDateTime.class, DateTime.class, LocalDateTime.class,
                LocalDate.class, LocalTime.class, YearMonth.class, MonthDay.class, Instant.class)) {
                if (isString(getterDeclareType)) {
                    String format = model.getAttr().formatValue();
                    String dftFormat = manager.ofStatic().onJodaDateTimeFormat(format);
                    if (isNullString(dftFormat)) {
                        t0 = convert.onMapping(null, "{setterType}.parse({var})", setterDeclareType, String.class);
                    } else {
                        t0 = convert.useMapping(null, () -> {
                            String tx = "{setterType}.parse({var}, {format})";
                            return Replacer.format.replace(tx, dftFormat);
                        }, setterDeclareType, String.class);
                    }
                } else if (isSubtypeOf(getterDeclareType, Date.class) || isTypeof(getterDeclareType, Calendar.class)) {
                    t0 = convert.onMapping(null, "new {setterType}({var})", setterDeclareType, getterDeclareType);
                } else {
                    t0 = warningAndIgnored(model);
                }
            } else {
                t0 = warningAndIgnored(model);
            }
            return t0;
        }
        return null;
    }

    private String declare2Jdk8Time(final MappingModel model, Manager manager) {
        final ConvertManager convert = manager.ofConvert();
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        if (isString(getterDeclareType) && isSubtypeOf(setterDeclareType, TemporalAccessor.class)) {
            String format = manager.ofStatic().onDateTimeFormatter(model.getAttr().formatValue());
            if (isNullString(format)) {
                return convert.onMapping(null, "{setterType}.parse({var})", setterDeclareType, getterDeclareType);
            } else {
                return convert.useMapping(null, () -> {
                    String fmt = "{setterType}.from({format}.parse({var}))";
                    return Replacer.format.replace(fmt, format);
                }, setterDeclareType, String.class);
            }
        }
        if (!isTypeofJdk8DateTime(setterDeclareType)) {
            return null;
        }
        String t0;
        if (isPrimitiveNumber(getterDeclareType)) {
            String getterType = isCompatible(LONG, getterDeclareType) ? LONG : DOUBLE;
            t0 = convert
                .useConvert(null, info -> info.toString("{fromName}.{getterName}()"), setterDeclareType, getterType);
        } else if (isSubtypeOf(getterDeclareType, Number.class)) {
            t0 = convert.onConvertSimple(setterDeclareType, Number.class);
        } else if (isSubtypeOf(getterDeclareType, Date.class)) {
            t0 = convert.onConvertSimple(setterDeclareType, Date.class);
        } else if (isTypeof(getterDeclareType, Calendar.class)) {
            t0 = convert.onConvertSimple(setterDeclareType, Calendar.class);
        } else if (DetectUtils.IMPORTED_JODA_TIME) {
            if (isSubtypeOf(getterDeclareType, ReadableInstant.class)) {
                t0 = convert.onConvertSimple(setterDeclareType, ReadableInstant.class);
            } else if (isTypeofAny(getterDeclareType, LocalDateTime.class, LocalDate.class)) {
                t0 = convert.onConvertSimple(setterDeclareType, getterDeclareType);
            } else {
                t0 = null;
            }
        } else {
            t0 = null;
        }
        if (t0 == null) {
            t0 = convert.onConvertSimple(setterDeclareType, getterDeclareType);
        }
        return t0 == null ? warningAndIgnored(model) : t0;
    }

    /** long -> Date, Timestamp, java.sql.Date, Calendar */
    private String declare2Date(final MappingModel model, Manager manager) {
        String t0;
        final ConvertManager convert = manager.ofConvert();
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        if (isTypeofAny(setterDeclareType, Date.class, java.sql.Date.class, Timestamp.class, Time.class)) {
            if (isPrimitiveNumber(getterDeclareType)) {
                String getterType = isCompatible(LONG, getterDeclareType) ? LONG : DOUBLE;
                t0 = convert.useConvert(null, info -> {
                    return info.toString("{fromName}.{getterName}()");
                }, setterDeclareType, getterType);
            } else if (isSubtypeOf(getterDeclareType, Number.class)) {
                t0 = convert.onConvertSimple(setterDeclareType, Number.class);
            } else if (isTypeof(getterDeclareType, Calendar.class)) {
                t0 = convert.onConvertSimple(setterDeclareType, Calendar.class);
            } else if (isTypeofJdk8Date(getterDeclareType)) {
                t0 = convert.onConvertSimple(setterDeclareType, getterDeclareType);
            } else if (isString(getterDeclareType)) {
                final String format = model.getAttr().formatValue();
                if (isNullString(format)) {
                    t0 = convert.onConvertSimple(setterDeclareType, String.class);
                } else {
                    t0 = convert.useConvert(null, info -> {
                        return info.toString(null, strWrapped(format));
                    }, setterDeclareType, String.class, String.class);
                }
            } else {
                t0 = onImportedJodaTime(model, convert);
            }
            return t0;
        } else {
            return null;
        }
    }

    private String declare2Calendar(final MappingModel model, Manager manager) {
        String t0;
        final ConvertManager convert = manager.ofConvert();
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        if (isTypeof(setterDeclareType, Calendar.class)) {
            if (isPrimitiveNumber(getterDeclareType)) {
                String getterType = isPrimitiveSubtypeOf(getterDeclareType, LONG) ? LONG : DOUBLE;
                t0 = convert.useConvert(null, info -> {
                    return info.toString("{fromName}.{getterName}()");
                }, setterDeclareType, getterType);
            } else if (isSubtypeOf(getterDeclareType, Number.class)) {
                t0 = convert.onConvertSimple(setterDeclareType, Number.class);
            } else if (isSubtypeOf(getterDeclareType, Date.class)) {
                t0 = convert.onConvertSimple(setterDeclareType, Date.class);
            } else if (isTypeofJdk8Date(getterDeclareType)) {
                t0 = convert.onConvertSimple(setterDeclareType, getterDeclareType);
            } else if (isString(getterDeclareType)) {
                String format = model.getAttr().formatValue();
                if (isNullString(format)) {
                    t0 = convert.onConvertSimple(setterDeclareType, String.class);
                } else {
                    t0 = convert.useConvert(null, info -> {
                        return info.toString(null, strWrapped(format));
                    }, setterDeclareType, String.class, String.class);
                }
            } else {
                t0 = onImportedJodaTime(model, convert);
            }
            return t0;
        }
        return null;
    }

    private String onImportedJodaTime(MappingModel model, ConvertManager convert) {
        String t0, setterDeclareType = model.getSetterDeclareType(), getterDeclareType = model.getGetterDeclareType();
        if (DetectUtils.IMPORTED_JODA_TIME) {
            if (isSubtypeOf(getterDeclareType, ReadableInstant.class)) {
                t0 = convert.useConvert(null, STRINGIFY, setterDeclareType, ReadableInstant.class);
            } else if (isTypeofAny(getterDeclareType, LocalDateTime.class, LocalDate.class)) {
                t0 = convert.useConvert(null, STRINGIFY, setterDeclareType, getterDeclareType);
            } else {
                t0 = warningAndIgnored(model);
            }
        } else {
            t0 = warningAndIgnored(model);
        }
        return t0;
    }

    private String onDeclareCompleted(String t0, MappingModel model) {
        if (t0 != null) {
            t0 = Replacer.fromName.replace(t0, model.getFromName());
            t0 = Replacer.toName.replace(t0, model.getToName());
            t0 = Replacer.setterName.replace(t0, model.getSetterName());
            t0 = Replacer.getterName.replace(t0, model.getGetterName());
            if (t0.contains("{var}")) {
                t0 = Replacer.var.replace(t0, nextVarName());
            }
            if (t0.contains("{var0}")) {
                t0 = Replacer.var0.replace(t0, nextVarName());
            }
            if (t0.contains("{var1}")) {
                t0 = Replacer.var1.replace(t0, nextVarName());
            }
        }
        return t0;
    }

    private String warningAndIgnored(final MappingModel model) {
        String fromType = getSimpleName(model.getFromClassname());
        String toType = getSimpleName(model.getToClassname());
        Object[] values = {
            toType, model.getSetterName(), model.getSetterFinalType(),//
            fromType, model.getGetterName(), model.getGetterFinalType()
        };
        Logger.onLevel(MANDATORY_WARNING, () -> StringUtils.format(true, TEMPLATE, values));
        this.warned = true;
        return null;
    }

    private final static String TEMPLATE = "【已忽略】'{}.{}({})' 不兼容 '{}.{}()' 返回值类型: {}";

    private final AtomicInteger indexer = new AtomicInteger();

    private AtomicInteger getIndexer() { return indexer; }

    private boolean isWarned() { return warned; }

    private void unWarned() { this.warned = false; }

    private String nextVarName() { return nextVarName(getIndexer()); }

    private static String nextVarName(AtomicInteger indexer) { return "var" + indexer.getAndIncrement(); }

    private static String castTopAsPrimitive(String thisPrimitive, String topPrimitive) {
        return isCompatible(thisPrimitive, topPrimitive) ? "" : bracketed(thisPrimitive);
    }

    private static boolean isPrimitiveSubtypeOf(String thisType, String thatType) {
        return isCompatible(thatType, thisType);
    }

    private static boolean isCompatible(String thisType, String thatType) {
        String all = "byte,short,int,long,float,double";
        int thisIdx = all.indexOf(thisType);
        int thatIdx = all.indexOf(thatType);
        return thisIdx >= thatIdx;
    }

    private final Function<CallerInfo, String> STRINGIFY = Object::toString;

    private static boolean isString(String value) { return isTypeof(value, String.class); }

    private static boolean isEnum(String value) {
        TypeElement elem = EnvUtils.getUtils().getTypeElement(value);
        return elem != null && elem.getKind() == ElementKind.ENUM;
    }

    private static boolean isTypeofAny(String actual, Class<?>... expected) {
        for (Class<?> aClass : expected) {
            if (isTypeof(actual, aClass)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isTypeof(String actual, Class<?> expected) {
        return expected.getCanonicalName().equals(actual);
    }

    private static boolean isTypeofJdk8Date(String type) {
        return isTypeofAny(type, java.time.LocalDateTime.class, java.time.ZonedDateTime.class,
            java.time.OffsetDateTime.class, java.time.Instant.class, java.time.LocalDate.class);
    }

    private static boolean isTypeofJdk8DateTime(String type) {
        return isTypeofJdk8Date(type) || isTypeof(type, java.time.LocalTime.class);
    }

    private static boolean isSubtypeOf(String actualType, Class<?> superclass) {
        return isSubtypeOf(actualType, superclass.getCanonicalName());
    }

    private static boolean isSubtypeOfAny(String actualType, Class<?>... superclasses) {
        if (superclasses != null) {
            for (Class<?> superclass : superclasses) {
                if (isSubtypeOf(actualType, superclass)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isSubtypeOf(String actualType, String superClass) {
        Elements utils = EnvUtils.getUtils();
        TypeElement elem1 = utils.getTypeElement(actualType);
        TypeElement elem2 = utils.getTypeElement(superClass);
        return elem1 != null && elem2 != null && EnvUtils.getTypes().isSubtype(elem1.asType(), elem2.asType());
    }

    private static boolean isFormattable(String fmt, String actualType, Class<?>... superclasses) {
        return !isNullString(fmt) && isSubtypeOfAny(actualType, superclasses);
    }

    private static boolean isJodaFormattable(String fmt, String type) {
        return DetectUtils.IMPORTED_JODA_TIME//
            && isFormattable(fmt, type, ReadableInstant.class, ReadablePartial.class);
    }

    private static boolean isNullString(String dftVal) { return dftVal == null || StaticManager.NULL.equals(dftVal); }

    private static String strWrapped(String str) { return '"' + str + '"'; }

    private static String bracketed(String str) { return "(" + str + ")"; }
}
