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
            if (field == null && isSubTypeof(model.getGetterDeclareType(), model.getSetterDeclareType())) {
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
        String t0 = null;
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        if (Objects.equals(setterDeclareType, getterDeclareType)) {
            t0 = "{toName}.{setterName}({fromName}.{getterName}());";
        }
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

    private String declare2Enum(MappingModel model, Manager manager) {
        String t0;
        final ConvertManager convert = manager.ofConvert();
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        final String dftValue = forDefaultEnum(model, manager, setterDeclareType);
        if (isEnum(setterDeclareType)) {
            if (isString(getterDeclareType)) {
                Supplier<String> mappings = () -> "{setterType}.valueOf({var})";
                t0 = convert.useMapping(dftValue, mappings, setterDeclareType, getterDeclareType);
            } else if (isPrimitiveNumber(getterDeclareType)) {
                String enumValues = manager.ofStatic().onEnumValues(setterDeclareType);
                t0 = "{toName}.{setterName}({setterType}[{cast}{fromName}.{getterName}()]);";
                t0 = Replacer.cast.replace(t0, isCompatible(INT, getterDeclareType) ? "" : "(int)");
                t0 = Replacer.setterType.replace(t0, enumValues);
            } else if (isSubTypeof(getterDeclareType, Number.class)) {
                String enumValues = manager.ofStatic().onEnumValues(setterDeclareType);
                t0 = convert.useMapping(dftValue, () -> {
                    String tx = "{setterType}[{var}.intValue()]";
                    return Replacer.setterType.replace(tx, enumValues);
                }, "", getterDeclareType);
            } else {
                t0 = warningAndIgnored(model);
            }
            return t0;
        }
        return null;
    }

    private String declare2String(final MappingModel model, Manager manager) {
        String t0;
        final PropertyAttr attr = model.getAttr();
        final ConvertManager convert = manager.ofConvert();
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        final String dftValue = manager.ofStatic().onString(attr.defaultValue());
        if (isString(setterDeclareType)) {
            if (isString(getterDeclareType)) {
                // todo 默认值
                t0 = "{toName}.{setterName}({fromName}.{getterName}());";
            } else if (StringUtils.isPrimitive(getterDeclareType)) {
                // todo 默认值
                if (attr.formatValue() == null) {
                    t0 = "{toName}.{setterName}({type1}.valueOf({fromName}.{getterName}()));";
                    t0 = Replacer.type1.replace(t0, manager.onImported(String.class));
                } else {
                    t0 = convert.useMapping(dftValue, () -> {
                        String stringType = String.class.getCanonicalName();
                        String targetType = isCompatible(LONG, getterDeclareType) ? LONG : DOUBLE;
                        CallerInfo info = convert.find(stringType, targetType, stringType);
                        return info.toString("{fromName}.{getterName}()", strWrapped(attr.formatValue()));
                    }, setterDeclareType, getterDeclareType);
                }
            } else if (isSubTypeof(getterDeclareType, Number.class)) {
                // todo 默认值
                String format = model.getAttr().formatValue();
                if (format == null) {
                    t0 = convert.useMapping(null, () -> "{var}.toString()", setterDeclareType, Number.class);
                } else {
                    t0 = convert.useConvert(null, info -> {
                        return info.toString(null, strWrapped(format));
                    }, setterDeclareType, Number.class, String.class);
                }
            } else if (isEnum(getterDeclareType)) {
                t0 = convert.useMapping(dftValue, () -> "{var}.name()", setterDeclareType, getterDeclareType);
            } else if (isTypeofJdk8Date(getterDeclareType)) {
                String format = manager.ofStatic().onDateTimeFormatter(model.getAttr().formatValue());
                if (isNullString(format)) {
                    t0 = convert.useMapping(dftValue, () -> "{var}.toString()", setterDeclareType, getterDeclareType);
                } else {
                    String fmt = Replacer.format.replace("{format}.format({var})", format);
                    t0 = convert.useMapping(dftValue, () -> fmt, setterDeclareType, getterDeclareType);
                }
            } else if (isSubTypeof(getterDeclareType, Date.class) && !isNullString(attr.formatValue())) {
                t0 = convert.useConvert(dftValue, info -> info.toString(null, strWrapped(attr.formatValue())),
                    setterDeclareType, Date.class, String.class);
            } else if (isSubTypeof(getterDeclareType, Calendar.class) && !isNullString(attr.formatValue())) {
                t0 = convert.useConvert(dftValue, info -> info.toString(null, strWrapped(attr.formatValue())),
                    setterDeclareType, Calendar.class, String.class);
            } else {
                // todo 默认值
                t0 = convert.useMapping(dftValue, () -> "{var}.toString()", "", getterDeclareType);
            }
            return t0;
        } else {
            return null;
        }
    }

    private String forDefaultNumber(MappingModel model, Manager manager, String classname) {
        PropertyAttr attr = model.getAttr();
        String defaultValue = attr.defaultValue();
        if (defaultValue != null) {
            return manager.ofStatic().onDefaultNumber(classname, defaultValue);
        }
        return manager.ofStatic().defaultNull();
    }

    private String declare2PrimitiveNumber(final MappingModel model, Manager manager) {
        String t0;
        final ConvertManager convert = manager.ofConvert();
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        final String dftValue = forDefaultNumber(model, manager, setterDeclareType);
        if (isPrimitiveNumber(setterDeclareType)) {
            if (isPrimitiveNumber(getterDeclareType)) {
                if (isCompatible(setterDeclareType, getterDeclareType)) {
                    t0 = "{toName}.{setterName}({fromName}.{getterName}());";
                } else {
                    t0 = "{toName}.{setterName}(({setterType}){fromName}.{getterName}());";
                    t0 = Replacer.setterType.replace(t0, setterDeclareType);
                }
            } else if (isWrappedNumber(getterDeclareType)) {
                if (isNullString(dftValue)) {
                    t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                        "if ({var} != null) { {toName}.{setterName}({var}.{setterType}Value()); }";
                } else {
                    t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                        "if ({var} == null) { {toName}.{setterName}({NULL}); }" +//
                        "else { {toName}.{setterName}({var}.{setterType}Value()); }";
                    t0 = Replacer.NULL.replace(t0, dftValue);
                }
                t0 = Replacer.getterType.replace(t0, manager.onImported(getterDeclareType));
                t0 = Replacer.setterType.replace(t0, manager.onImported(setterDeclareType));
            } else if (StringUtils.isPrimitiveChar(getterDeclareType)) {
                if (isCompatible(setterDeclareType, INT)) {
                    t0 = "{toName}.{setterName}({fromName}.{getterName}());";
                } else {
                    t0 = "{toName}.{setterName}(({setterType}){fromName}.{getterName}());";
                    t0 = Replacer.setterType.replace(t0, manager.onImported(setterDeclareType));
                }
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
            } else if (isSubTypeof(getterDeclareType, Number.class)) {
                t0 = convert
                    .useMapping(dftValue, () -> "{var}.{setterType}Value()", setterDeclareType, getterDeclareType);
            } else if (isEnum(getterDeclareType)) {
                if (isNullString(dftValue)) {
                    if (isCompatible(setterDeclareType, INT)) {
                        t0 = "Enum {var} = {fromName}.{getterName}();" +//
                            "if ({var} != null) { {toName}.{setterName}({var}.ordinal()); }";
                    } else {
                        t0 = "Enum {var} = {fromName}.{getterName}();" +//
                            "if ({var} != null) { {toName}.{setterName}(({setterType}){var}.ordinal()); }";
                        t0 = Replacer.setterType.replace(t0, manager.onImported(setterDeclareType));
                    }
                } else {
                    if (isCompatible(setterDeclareType, INT)) {
                        t0 = "Enum {var} = {fromName}.{getterName}();" +//
                            "if ({var} == null) { {toName}.{setterName}({NULL}); }" +//
                            "else { {toName}.{setterName}({var}.ordinal()); }";
                    } else {
                        t0 = "Enum {var} = {fromName}.{getterName}();" +//
                            "if ({var} == null) { {toName}.{setterName}({NULL}); }" +//
                            "else { {toName}.{setterName}(({setterType}){var}.ordinal()); }";
                        t0 = Replacer.setterType.replace(t0, manager.onImported(setterDeclareType));
                    }
                    t0 = Replacer.NULL.replace(t0, dftValue);
                }
            } else if (LONG.equals(setterDeclareType) || DOUBLE.equals(setterDeclareType)) {
                if (isSubTypeof(getterDeclareType, Date.class)) {
                    t0 = convert.useConvert(dftValue, STRINGIFY, setterDeclareType, Date.class);
                } else if (isSubTypeof(getterDeclareType, Calendar.class)) {
                    t0 = convert.useConvert(dftValue, STRINGIFY, setterDeclareType, Calendar.class);
                } else if (isTypeofAny(getterDeclareType, java.time.LocalDate.class, java.time.LocalDateTime.class,
                    ZonedDateTime.class, OffsetDateTime.class, java.time.Instant.class)) {
                    t0 = convert.useConvert(dftValue, STRINGIFY, setterDeclareType, getterDeclareType);
                } else if (DetectUtils.IMPORTED_JODA_TIME) {
                    if (isSubTypeof(getterDeclareType, ReadableInstant.class) ||
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
        } else {
            return null;
        }
    }

    private String declare2WrappedNumber(final MappingModel model, Manager manager) {
        String t0;
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        final String dftValue = forDefaultNumber(model, manager, setterDeclareType);
        if (isWrappedNumber(setterDeclareType)) {
            final String setterPrimitive = toPrimitiveType(setterDeclareType);
            if (isPrimitiveNumber(getterDeclareType)) {
                if (isCompatible(setterPrimitive, getterDeclareType)) {
                    t0 = "{toName}.{setterName}({fromName}.{getterName}());";
                } else {
                    t0 = "{toName}.{setterName}(({setterType}){fromName}.{getterName}());";
                    t0 = Replacer.setterType.replace(t0, setterPrimitive);
                }
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
                    t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                        "if ({var} == null) { {toName}.{setterName}({NULL}); }" +//
                        "else { {toName}.{setterName}({var}.{type0}Value()); }";
                    t0 = Replacer.getterType.replace(t0, manager.onImported(getterDeclareType));
                    t0 = Replacer.type0.replace(t0, setterPrimitive);
                    t0 = Replacer.NULL.replace(t0, dftValue);
                }
            } else if (isString(getterDeclareType)) {
                // todo 字符串到数字格式化解析
                t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null || {var}.length() == 0) { {toName}.{setterName}({NULL}); }" +//
                    "else { {toName}.{setterName}({type0}.parse{type1}({var})); }";
                t0 = Replacer.NULL.replace(t0, dftValue);
                t0 = Replacer.type0.replace(t0, manager.onImported(setterDeclareType));
                t0 = Replacer.getterType.replace(t0, manager.onImported(getterDeclareType));
                if (INT.equals(setterPrimitive)) {
                    t0 = Replacer.type1.replace(t0, "Int");
                } else {
                    String type1 = getSimpleName(setterDeclareType);
                    t0 = Replacer.type1.replace(t0, type1);
                }
            } else if (isEnum(getterDeclareType)) {
                t0 = "Enum {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}({NULL}); }" +//
                    "else { {toName}.{setterName}({setterType}.valueOf({cast}{var}.ordinal())); }";
                t0 = Replacer.setterType.replace(t0, manager.onImported(setterDeclareType));
                if (isCompatible(setterPrimitive, INT)) {
                    t0 = Replacer.cast.replace(t0, "");
                } else {
                    t0 = Replacer.cast.replace(t0, "(" + setterPrimitive + ")");
                }
                t0 = Replacer.NULL.replace(t0, dftValue);
            } else if (StringUtils.isPrimitiveChar(getterDeclareType)) {
                if (isCompatible(setterPrimitive, INT)) {
                    t0 = "{toName}.{setterName}({fromName}.{getterName}());";
                } else {
                    t0 = "{toName}.{setterName}(({type0}) {fromName}.{getterName}());";
                    t0 = Replacer.type0.replace(t0, setterPrimitive);
                }
            } else if (isTypeof(getterDeclareType, Character.class)) {
                t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}({NULL}); }" +//
                    "else { {toName}.{setterName}(({setterType}) {var}.charValue()); }";
                t0 = Replacer.getterType.replace(t0, manager.onImported(setterPrimitive));
                t0 = Replacer.setterType.replace(t0, setterPrimitive);
                t0 = Replacer.NULL.replace(t0, dftValue);
            } else if (isSubTypeof(getterDeclareType, Number.class)) {
                t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}({NULL}); }" +//
                    "else { {toName}.{setterName}({var}.{type0}Value()); }";
                t0 = Replacer.getterType.replace(t0, manager.onImported(Number.class));
                t0 = Replacer.type0.replace(t0, setterPrimitive);
                t0 = Replacer.NULL.replace(t0, dftValue);
            } else if (Long.class.getCanonicalName().equals(setterDeclareType)) {
                if (isNullString(dftValue)) {
                    if (isSubTypeof(getterDeclareType, Date.class)) {
                        t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                            "if ({var} == null) { {toName}.{setterName}(null); }" +//
                            "else { {toName}.{setterName}({var}.getTime()); }";
                        t0 = Replacer.getterType.replace(t0, manager.onImported(Date.class));
                    } else if (isSubTypeof(getterDeclareType, Calendar.class)) {
                        t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                            "if ({var} == null) { {toName}.{setterName}(null); }" +//
                            "else { {toName}.{setterName}({var}.getTimeInMillis()); }";
                        t0 = Replacer.getterType.replace(t0, manager.onImported(Calendar.class));
                    } else if (DetectUtils.IMPORTED_JODA_TIME) {
                        if (isSubTypeof(getterDeclareType, ReadableInstant.class) ||
                            isTypeof(getterDeclareType, DateTime.class)) {
                            t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                                "if ({var} == null) { {toName}.{setterName}(null); }" +//
                                "else { {toName}.{setterName}({var}.getMillis()); }";
                            t0 = Replacer.getterType.replace(t0, manager.onImported(getterDeclareType));
                        } else if (isTypeofAny(getterDeclareType, LocalDateTime.class, LocalDate.class,
                            MutableDateTime.class)) {
                            t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                                "if ({var} == null) { {toName}.{setterName}(null); }" +//
                                "else { {toName}.{setterName}({var}.toDate().getTime()); }";
                            t0 = Replacer.getterType.replace(t0, manager.onImported(getterDeclareType));
                        } else {
                            t0 = warningAndIgnored(model);
                        }
                    } else {
                        t0 = warningAndIgnored(model);
                    }
                } else {
                    if (isSubTypeof(getterDeclareType, Date.class)) {
                        t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                            "if ({var} == null) { {toName}.{setterName}({NULL}); }" +//
                            "else { {toName}.{setterName}({var}.getTime()); }";
                        t0 = Replacer.NULL.replace(t0, dftValue);
                        t0 = Replacer.getterType.replace(t0, manager.onImported(Date.class));
                    } else if (isSubTypeof(getterDeclareType, Calendar.class)) {
                        t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                            "if ({var} == null) { {toName}.{setterName}({NULL}); }" +//
                            "else { {toName}.{setterName}({var}.getTimeInMillis()); }";
                        t0 = Replacer.getterType.replace(t0, manager.onImported(Calendar.class));
                        t0 = Replacer.NULL.replace(t0, dftValue);
                    } else if (DetectUtils.IMPORTED_JODA_TIME) {
                        if (isSubTypeof(getterDeclareType, ReadableInstant.class) ||
                            isTypeof(getterDeclareType, DateTime.class)) {
                            t0 = "{getterType} {var} = {fromName}.{getterName}();" +
                                "if ({var} != null) { {toName}.{setterName}({NULL}); }" +
                                "else { {toName}.{setterName}({var}.getMillis()); }";
                            t0 = Replacer.getterType.replace(t0, manager.onImported(getterDeclareType));
                        } else if (isTypeofAny(getterDeclareType, LocalDateTime.class, LocalDate.class,
                            MutableDateTime.class)) {
                            t0 = "{getterType} {var} = {fromName}.{getterName}();" +
                                "if ({var} == null) { {toName}.{setterName}({NULL}); }" +
                                "else { {toName}.{setterName}({var}.toDate().getTime()); }";
                            t0 = Replacer.getterType.replace(t0, manager.onImported(getterDeclareType));
                        } else {
                            t0 = warningAndIgnored(model);
                        }
                    } else {
                        t0 = warningAndIgnored(model);
                    }
                }
            } else {
                t0 = warningAndIgnored(model);
            }
        } else if (isTypeof(setterDeclareType, Number.class)) {
            if (isSubTypeof(getterDeclareType, Number.class) ||
                isPrimitiveNumber(getterDeclareType) ||
                isPrimitiveChar(getterDeclareType)) {
                t0 = "{toName}.{setterName}({fromName}.{getterName}());";
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
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        if (isPrimitiveBoolean(setterDeclareType)) {
            String setterWrappedType = StringUtils.toWrappedType(setterDeclareType);
            if (isString(getterDeclareType)) {
                t0 = "{toName}.{setterName}({setterType}.parseBoolean({fromName}.{getterName}()));";
                t0 = Replacer.setterType.replace(t0, manager.onImported(setterWrappedType));
            } else if (isPrimitiveNumber(getterDeclareType)) {
                t0 = "{toName}.{setterName}({fromName}.{getterName}() != 0);";
            } else if (isWrappedNumber(getterDeclareType)) {
                String staticVar = manager.ofStatic().onDefaultNumber(Integer.class, "0");
                t0 = "{toName}.{setterName}({name}.equals({fromName}.getterName()));";
                t0 = Replacer.name.replace(t0, staticVar);
            } else if (isPrimitiveBoolean(getterDeclareType)) {
                t0 = "{toName}.{setterName}({fromName}.{getterName}());";
            } else if (Boolean.class.getName().equals(getterDeclareType)) {
                t0 = "Boolean {var} = {fromName}.{getterName}();" +//
                    "if ({var} != null) { {toName}.{setterName}({var}); }";
            } else {
                t0 = warningAndIgnored(model);
            }
        } else if (Boolean.class.getName().equals(setterDeclareType)) {
            if (isString(getterDeclareType)) {
                t0 = "{toName}.{setterName}({setterType}.parseBoolean({fromName}.{getterName}()));";
                t0 = Replacer.setterType.replace(t0, manager.onImported(setterDeclareType));
            } else if (isPrimitiveNumber(getterDeclareType)) {
                t0 = "{toName}.{setterName}({fromName}.getterName() != 0);";
            } else if (isWrappedNumber(getterDeclareType)) {
                String staticVar = manager.ofStatic().onDefaultNumber(Integer.class, "0");
                t0 = "{toName}.{setterName}({name}.equals({fromName}.getterName()));";
                t0 = Replacer.name.replace(t0, staticVar);
            } else if (isPrimitiveBoolean(getterDeclareType) || isTypeof(getterDeclareType, Boolean.class)) {
                t0 = "{toName}.{setterName}({fromName}.{getterName}());";
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
        final String dftValue = forDefaultBigDecimal(model, manager, setterDeclareType);
        if (isTypeof(setterDeclareType, BigDecimal.class)) {
            if (isPrimitiveNumber(getterDeclareType)) {
                t0 = "{toName}.{setterName}({setterType}.valueOf({fromName}.{getterName}()));";
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
            } else if (isTypeofAny(getterDeclareType, Double.class, Float.class)) {
                t0 = convert.useConvert(dftValue, STRINGIFY, setterDeclareType, getterDeclareType);
            } else if (isTypeof(getterDeclareType, BigInteger.class)) {
                t0 = convert.useConvert(dftValue, STRINGIFY, setterDeclareType, BigInteger.class);
            } else if (isSubTypeof(getterDeclareType, Number.class)) {
                t0 = convert.useConvert(dftValue, STRINGIFY, setterDeclareType, Number.class);
            } else if (isString(getterDeclareType)) {
                // TODO format & defaultValue(返回值应判断 null 和 str.length() == 0)
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
        } else {
            return null;
        }
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
        final String dftValue = forDefaultBigInteger(model, manager, setterDeclareType);
        if (isTypeof(setterDeclareType, BigInteger.class)) {
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
            } else if (isSubTypeof(getterDeclareType, Number.class)) {
                t0 = convert.useConvert(dftValue, STRINGIFY, setterDeclareType, Number.class);
            } else if (isString(getterDeclareType)) {
                String format = model.getAttr().formatValue();
                if (format == null) {
                    t0 = convert.useConvert(dftValue, STRINGIFY, setterDeclareType, String.class);
                } else {
                    Function<CallerInfo, String> mapper = info -> info.toString(null, strWrapped(format));
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
        } else {
            return null;
        }
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
            } else if (isSubTypeof(getterDeclareType, Number.class)) {
                t0 = convert.useMapping(null, () -> "new {setterType}({var}.longValue())", setterDeclareType,
                    getterDeclareType);
            } else if (isTypeof(setterDeclareType, Duration.class)) {
                if (isString(getterDeclareType)) {
                    t0 = convert.useMapping(null, () -> "{setterType}.parse({var})", setterDeclareType, String.class);
                } else {
                    t0 = warningAndIgnored(model);
                }
            } else if (isTypeofAny(setterDeclareType, MutableDateTime.class, DateTime.class, LocalDateTime.class,
                LocalDate.class, LocalTime.class, YearMonth.class, MonthDay.class, Instant.class)) {
                if (isString(getterDeclareType)) {
                    String format = model.getAttr().formatValue();
                    String dftFormat = manager.ofStatic().onJodaDateTimeFormat(format);
                    if (isNullString(dftFormat)) {
                        Supplier<String> parse = () -> "{setterType}.parse({var})";
                        t0 = convert.useMapping(null, parse, setterDeclareType, String.class);
                    } else {
                        t0 = convert.useMapping(null, () -> {
                            String tx = "{setterType}.parse({var}, {format})";
                            return Replacer.format.replace(tx, dftFormat);
                        }, setterDeclareType, String.class);
                    }
                } else if (isSubTypeof(getterDeclareType, Date.class) || isTypeof(getterDeclareType, Calendar.class)) {
                    t0 = convert
                        .useMapping(null, () -> "new {setterType}({var})", setterDeclareType, getterDeclareType);
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
        String t0;
        final ConvertManager convert = manager.ofConvert();
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        if (isTypeofJdk8DateTime(setterDeclareType)) {
            if (isPrimitiveNumber(getterDeclareType)) {
                String getterType = isCompatible(LONG, getterDeclareType) ? LONG : DOUBLE;
                t0 = convert.useConvert(null, info -> info.toString("{fromName}.{getterName}()"), setterDeclareType,
                    getterType);
            } else if (isString(getterDeclareType)) {
                String format = manager.ofStatic().onDateTimeFormatter(model.getAttr().formatValue());
                if (isNullString(format)) {
                    t0 = convert.useMapping(null, () -> "{setterType}.parse({var})", setterDeclareType, String.class);
                } else if (isTypeof(setterDeclareType, java.time.Instant.class)) {
                    t0 = convert.useMapping(null, () -> {
                        String tx = "{type0}.parse({var}, {format}).toInstant()";
                        tx = Replacer.format.replace(tx, format);
                        String type0 = manager.onImported(ZonedDateTime.class);
                        return Replacer.type0.replace(tx, type0);
                    }, java.time.Instant.class.getCanonicalName(), getterDeclareType);
                } else {
                    t0 = convert.useMapping(null, () -> {
                        String tx = "{setterType}.parse({var}, {format})";
                        return Replacer.format.replace(tx, format);
                    }, setterDeclareType, String.class);
                }
            } else if (isSubTypeof(getterDeclareType, Number.class)) {
                t0 = convert.useConvert(null, STRINGIFY, setterDeclareType, Number.class);
            } else if (isSubTypeof(getterDeclareType, Date.class)) {
                t0 = convert.useConvert(null, STRINGIFY, setterDeclareType, Date.class);
            } else if (isTypeof(getterDeclareType, Calendar.class)) {
                t0 = convert.useConvert(null, STRINGIFY, setterDeclareType, Calendar.class);
            } else if (DetectUtils.IMPORTED_JODA_TIME) {
                if (isSubTypeof(getterDeclareType, ReadableInstant.class)) {
                    t0 = convert.useConvert(null, STRINGIFY, setterDeclareType, ReadableInstant.class);
                } else if (isTypeofAny(getterDeclareType, LocalDateTime.class, LocalDate.class)) {
                    t0 = convert.useConvert(null, STRINGIFY, setterDeclareType, getterDeclareType);
                } else {
                    t0 = null;
                }
            } else {
                t0 = null;
            }
            if (t0 == null) {
                t0 = convert.useConvert(null, STRINGIFY, setterDeclareType, getterDeclareType);
            }
            if (getterDeclareType == null) {
                t0 = warningAndIgnored(model);
            }
        } else {
            t0 = null;
        }
        return t0;
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
            } else if (isSubTypeof(getterDeclareType, Number.class)) {
                t0 = convert.useConvert(null, STRINGIFY, setterDeclareType, Number.class);
            } else if (isTypeof(getterDeclareType, Calendar.class)) {
                t0 = convert.useConvert(null, STRINGIFY, setterDeclareType, Calendar.class);
            } else if (isTypeofJdk8Date(getterDeclareType)) {
                t0 = convert.useConvert(null, STRINGIFY, setterDeclareType, getterDeclareType);
            } else if (isString(getterDeclareType)) {
                final String format = model.getAttr().formatValue();
                if (isNullString(format)) {
                    t0 = convert.useConvert(null, STRINGIFY, setterDeclareType, String.class);
                } else {
                    t0 = convert.useConvert(null, info -> {
                        return info.toString(null, strWrapped(format));
                    }, setterDeclareType, String.class, String.class);
                }
            } else if (DetectUtils.IMPORTED_JODA_TIME) {
                if (isSubTypeof(getterDeclareType, ReadableInstant.class)) {
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
                String getterType = isCompatible(LONG, getterDeclareType) ? LONG : DOUBLE;
                t0 = convert.useConvert(null, info -> info.toString("{fromName}.{getterName}()"), setterDeclareType,
                    getterType);
            } else if (isSubTypeof(getterDeclareType, Number.class)) {
                t0 = convert.useConvert(null, STRINGIFY, setterDeclareType, Number.class);
            } else if (isSubTypeof(getterDeclareType, Date.class)) {
                t0 = convert.useConvert(null, STRINGIFY, setterDeclareType, Date.class);
            } else if (isTypeofJdk8Date(getterDeclareType)) {
                t0 = convert.useConvert(null, STRINGIFY, setterDeclareType, getterDeclareType);
            } else if (isString(getterDeclareType)) {
                String format = model.getAttr().formatValue();
                if (isNullString(format)) {
                    t0 = convert.useConvert(null, STRINGIFY, setterDeclareType, String.class);
                } else {
                    t0 = convert.useConvert(null, info -> {
                        return info.toString(null, strWrapped(format));
                    }, setterDeclareType, String.class, String.class);
                }
            } else if (DetectUtils.IMPORTED_JODA_TIME) {
                if (isSubTypeof(getterDeclareType, ReadableInstant.class)) {
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
        return null;
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

    private static boolean isSubTypeof(String actualType, Class<?> superclass) {
        return isSubTypeof(actualType, superclass.getCanonicalName());
    }

    private static boolean isSubTypeof(String actualType, String superClass) {
        Elements utils = EnvUtils.getUtils();
        TypeElement elem1 = utils.getTypeElement(actualType);
        TypeElement elem2 = utils.getTypeElement(superClass);
        return elem1 != null && elem2 != null && EnvUtils.getTypes().isSubtype(elem1.asType(), elem2.asType());
    }

    private static boolean isNullString(String dftVal) { return dftVal == null || StaticManager.NULL.equals(dftVal); }

    private static String strWrapped(String str) { return '"' + str + '"'; }
}
