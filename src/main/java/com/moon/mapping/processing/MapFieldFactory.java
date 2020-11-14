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
import java.text.DecimalFormat;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static com.moon.mapping.processing.ElementUtils.getSimpleName;
import static com.moon.mapping.processing.StringUtils.*;
import static javax.tools.Diagnostic.Kind.MANDATORY_WARNING;

/**
 * @author benshaoye
 */
final class MapFieldFactory {

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
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        if (isEnum(setterDeclareType)) {
            if (isString(getterDeclareType)) {
                String dftValue = forDefaultEnum(model, manager, setterDeclareType);
                t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null || {var}.length() == 0) { {toName}.{setterName}({NULL}); }" +//
                    "else { {toName}.{setterName}({setterType}.valueOf({var})); }";
                t0 = Replacer.getterType.replace(t0, manager.onImported(getterDeclareType));
                t0 = Replacer.setterType.replace(t0, manager.onImported(setterDeclareType));
                t0 = Replacer.NULL.replace(t0, dftValue);
            } else if (isPrimitiveNumber(getterDeclareType)) {
                String enumValues = manager.ofStatic().onEnumValues(setterDeclareType);
                if (isCompatible(INT, getterDeclareType)) {
                    t0 = "{toName}.{setterName}({setterType}[{fromName}.{getterName}()]);";
                } else {
                    t0 = "{toName}.{setterName}({setterType}[(int){fromName}.{getterName}()]);";
                }
                t0 = Replacer.setterType.replace(t0, enumValues);
            } else if (isSubTypeof(getterDeclareType, Number.class)) {
                String enumValues = manager.ofStatic().onEnumValues(setterDeclareType);
                String dftValue = forDefaultEnum(model, manager, setterDeclareType);
                t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}({NULL}); }" +//
                    "else { {toName}.{setterName}({setterType}[{var}.intValue()]); }";
                t0 = Replacer.getterType.replace(t0, manager.onImported(getterDeclareType));
                t0 = Replacer.setterType.replace(t0, enumValues);
                t0 = Replacer.NULL.replace(t0, dftValue);
            } else {
                t0 = warningAndIgnored(model);
            }
            return t0;
        }
        return null;
    }

    private String declare2String(final MappingModel model, Manager manager) {
        String t0;
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        if (isString(setterDeclareType)) {
            if (isString(getterDeclareType)) {
                // todo 默认值
                t0 = "{toName}.{setterName}({fromName}.{getterName}());";
            } else if (StringUtils.isPrimitive(getterDeclareType)) {
                PropertyAttr attr = model.getAttr();
                if (attr.formatValue() == null) {
                    t0 = "{toName}.{setterName}({type1}.valueOf({fromName}.{getterName}()));";
                    t0 = Replacer.type1.replace(t0, manager.onImported(String.class));
                } else {
                    t0 = "{toName}.{setterName}(new {type0}(\"{format}\").format({fromName}.{getterName}()));";
                    t0 = Replacer.type0.replace(t0, manager.onImported(DecimalFormat.class));
                    t0 = Replacer.type1.replace(t0, manager.onImported(String.class));
                    t0 = Replacer.format.replace(t0, attr.formatValue());
                }
            } else if (isEnum(getterDeclareType)) {
                // todo 默认值
                t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}(null); }" +//
                    "else { {toName}.{setterName}({var}.name()); }";
                t0 = Replacer.getterType.replace(t0, manager.onImported(getterDeclareType));
            } else {
                // todo 默认值
                t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}(null); }" +//
                    "else { {toName}.{setterName}({var}.toString()); }";
                t0 = Replacer.getterType.replace(t0, manager.onImported(getterDeclareType));
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
                // todo 字符串到数字格式化解析
                String setterWrapped = StringUtils.toWrappedType(manager.onImported(setterDeclareType));
                if (isNullString(dftValue)) {
                    t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                        "if ({var} != null && {var}.length() > 0) {" +//
                        " {toName}.{setterName}({type0}.parse{type1}({var})); " +//
                        "}";
                } else {
                    t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                        "if ({var} == null || {var}.length() = 0) {" +//
                        "  {toName}.{setterName}({NULL});" +//
                        "} else { {toName}.{setterName}({type0}.parse{type1}({var})); }";
                    t0 = Replacer.NULL.replace(t0, dftValue);
                }
                t0 = Replacer.getterType.replace(t0, manager.onImported(getterDeclareType));
                t0 = Replacer.type0.replace(t0, setterWrapped);
                String type1 = INT.equals(setterDeclareType) ? "Int" : setterWrapped;
                t0 = Replacer.type1.replace(t0, type1);
            } else if (isSubTypeof(getterDeclareType, Number.class)) {
                if (isNullString(dftValue)) {
                    t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                        "if ({var} != null) { {toName}.{setterName}({var}.{type0}Value()); }";
                } else {
                    t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                        "if ({var} == null) { {toName}.{setterName}({NULL}); }" +//
                        "else { {toName}.{setterName}({var}.{type0}Value()); }";
                    t0 = Replacer.NULL.replace(t0, dftValue);
                }
                t0 = Replacer.getterType.replace(t0, manager.onImported(getterDeclareType));
                t0 = Replacer.type0.replace(t0, manager.onImported(setterDeclareType));
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
            } else if (LONG.equals(setterDeclareType)) {
                if (isNullString(dftValue)) {
                    if (isSubTypeof(getterDeclareType, Date.class)) {
                        t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                            "if ({var} != null) { {toName}.{setterName}({var}.getTime()); }";
                        t0 = Replacer.getterType.replace(t0, manager.onImported(java.util.Date.class));
                    } else if (isSubTypeof(getterDeclareType, Calendar.class)) {
                        t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                            "if ({var} != null) { {toName}.{setterName}({var}.getTimeInMillis()); }";
                        t0 = Replacer.getterType.replace(t0, manager.onImported(java.util.Calendar.class));
                    } else if (DetectUtils.IMPORTED_JODA_TIME) {
                        if (isSubTypeof(getterDeclareType, ReadableInstant.class) ||
                            isTypeof(getterDeclareType, DateTime.class)) {
                            t0 = "{getterType} {var} = {fromName}.{getterName}();" +
                                "if ({var} != null) { {toName}.{setterName}({var}.getMillis()); }";
                            t0 = Replacer.getterType.replace(t0, manager.onImported(getterDeclareType));
                        } else if (isTypeofAny(getterDeclareType, LocalDateTime.class, LocalDate.class,
                            MutableDateTime.class)) {
                            t0 = "{getterType} {var} = {fromName}.{getterName}();" +
                                "if ({var} != null) { {toName}.{setterName}({var}.toDate().getTime()); }";
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
                        t0 = Replacer.getterType.replace(t0, manager.onImported(java.util.Date.class));
                    } else if (isSubTypeof(getterDeclareType, Calendar.class)) {
                        t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                            "if ({var} == null) { {toName}.{setterName}({NULL}); }" +//
                            "else { {toName}.{setterName}({var}.getTimeInMillis()); }";
                        t0 = Replacer.getterType.replace(t0, manager.onImported(java.util.Calendar.class));
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
                                //
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
            return t0;
        } else {
            return null;
        }
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
            if (isString(getterDeclareType)) {
                t0 = "{toName}.{setterName}({setterType}.parseBoolean({fromName}.{getterName}()));";
                t0 = Replacer.getterType.replace(t0, manager.onImported(setterDeclareType));
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
                t0 = Replacer.getterType.replace(t0, manager.onImported(setterDeclareType));
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
                if (isCompatible(INT, getterDeclareType)) {
                    t0 = "{toName}.{setterName}(Character.forDigit({fromName}.{getterName}(), 10));";
                } else {
                    t0 = "{toName}.{setterName}(Character.forDigit((int){fromName}.{getterName}(), 10));";
                }
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
                if (isCompatible(INT, getterDeclareType)) {
                    t0 = "{toName}.{setterName}(Character.forDigit({fromName}.{getterName}(), 10));";
                } else {
                    t0 = "{toName}.{setterName}(Character.forDigit((int){fromName}.{getterName}(), 10));";
                }
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
            return manager.ofStatic().onDefaultBigInteger(defaultValue);
        }
        return manager.ofStatic().defaultNull();
    }

    private String setNullOrElse(String getterType, String dftValue) {
        String t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
            "if ({var} == null) { {toName}.{setterName}({NULL}); }" +//
            "else { {MAPPINGS} }";
        return Replacer.getterType.replace(Replacer.NULL.replace(t0, dftValue), getterType);
    }

    private String emptyOrElse(String getterType, String dftValue) {
        String t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
            "if ({var} == null || {var}.length() == 0) { {toName}.{setterName}({NULL}); }" +//
            "else { {MAPPINGS} }";
        return Replacer.getterType.replace(Replacer.NULL.replace(t0, dftValue), getterType);
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
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        final String dftValue = forDefaultBigDecimal(model, manager, setterDeclareType);
        if (isTypeof(setterDeclareType, BigDecimal.class)) {
            if (isPrimitiveNumber(getterDeclareType)) {
                t0 = "{toName}.{setterName}({setterType}.valueOf({fromName}.{getterName}()));";
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
            } else if (isSubTypeof(getterDeclareType, Number.class)) {
                t0 = manager.ofConvert().onRefType(setterDeclareType, Number.class);
            } else if (isString(getterDeclareType) || isTypeof(getterDeclareType, BigInteger.class)) {
                t0 = manager.ofConvert().onRefType(setterDeclareType, getterDeclareType);
            } else if (StringUtils.isPrimitiveChar(getterDeclareType)) {
                t0 = "int {var} = {fromName}.{getterName}();" +//
                    "{toName}.{setterName}({setterType}.valueOf({var}));";
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
            } else if (isTypeof(setterDeclareType, Character.class)) {
                t0 = "Character {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}({NULL}); }" +//
                    "else { {toName}.{setterName}({setterType}.valueOf({var}.charValue())); }";
                t0 = Replacer.setterType.replace(t0, manager.onImported(setterDeclareType));
                t0 = Replacer.NULL.replace(t0, dftValue);
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
            return manager.ofStatic().onDefaultBigDecimal(defaultValue);
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
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        final String dftValue = forDefaultBigInteger(model, manager, setterDeclareType);
        if (isTypeof(setterDeclareType, BigInteger.class)) {
            if (isPrimitiveNumber(getterDeclareType)) {
                if (isCompatible(LONG, toPrimitiveType(getterDeclareType))) {
                    t0 = "{toName}.{setterName}({setterType}.valueOf({fromName}.{getterName}()));";
                } else {
                    t0 = "{toName}.{setterName}({setterType}.valueOf((long){fromName}.{getterName}()));";
                }
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
            } else if (isSubTypeof(getterDeclareType, Number.class)) {
                t0 = manager.ofConvert().onRefType(setterDeclareType, Number.class);
            } else if (isString(getterDeclareType)) {
                t0 = manager.ofConvert().onRefType(setterDeclareType, String.class);
            } else if (StringUtils.isPrimitiveChar(getterDeclareType)) {
                t0 = "{toName}.{setterName}({setterType}.valueOf({fromName}.{getterName}()));";
                t0 = Replacer.setterType.replace(t0, manager.onImported(setterDeclareType));
            } else if (isTypeof(getterDeclareType, Character.class)) {
                t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}({NULL}); }" +//
                    "else { {toName}.{setterName}({setterType}.valueOf({var}.charValue())); }";
                t0 = Replacer.setterType.replace(t0, manager.onImported(setterDeclareType));
                t0 = Replacer.getterType.replace(t0, manager.onImported(getterDeclareType));
                t0 = Replacer.NULL.replace(t0, dftValue);
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
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        if (isTypeofAny(setterDeclareType, MutableDateTime.class, DateTime.class, LocalDateTime.class, LocalDate.class,
            LocalTime.class, MutablePeriod.class, Duration.class, YearMonth.class, Instant.class, MonthDay.class)) {
            if (isPrimitiveNumber(getterDeclareType)) {
                if (isCompatible(LONG, getterDeclareType)) {
                    t0 = "{toName}.{setterName}(new {setterType}({fromName}.{getterName}()));";
                } else {
                    t0 = "{toName}.{setterName}(new {setterType}((long){fromName}.{getterName}()));";
                }
                t0 = Replacer.setterType.replace(t0, manager.onImported(setterDeclareType));
            } else if (isSubTypeof(getterDeclareType, Number.class)) {
                t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}(null); }" +//
                    "else { {toName}.{setterName}(new {setterType}({var}.longValue())); }";
                t0 = Replacer.getterType.replace(t0, manager.onImported(getterDeclareType));
                t0 = Replacer.setterType.replace(t0, manager.onImported(setterDeclareType));
            } else {
                if (isTypeofAny(setterDeclareType, MutableDateTime.class, DateTime.class, LocalDateTime.class,
                    LocalDate.class, LocalTime.class)) {
                    if (isString(getterDeclareType)) {
                        String format = model.getAttr().formatValue();
                        String dftFormat = manager.ofStatic().onJodaDateTimeFormat(format);
                        if (isNullString(dftFormat)) {
                            t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                                "if ({var} == null) { {toName}.{setterName}(null); }" +//
                                "else { {toName}.{setterName}({var}); }";
                            t0 = Replacer.getterType.replace(t0, manager.onImported(String.class));
                        } else {
                            t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                                "if ({var} == null) { {toName}.{setterName}(null); }" +//
                                "else { {toName}.{setterName}({format}.parse{name}({var})); }";
                            t0 = Replacer.getterType.replace(t0, manager.onImported(String.class));
                            t0 = Replacer.format.replace(t0, dftFormat);
                            t0 = Replacer.name.replace(t0, getSimpleName(setterDeclareType));
                        }
                    } else if (isSubTypeof(getterDeclareType, Date.class) ||
                        isTypeof(getterDeclareType, Calendar.class)) {
                        t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                            "if ({var} == null) { {toName}.{setterName}(null); }" +//
                            "else { {toName}.{setterName}(new {setterType}({var})); }";
                        t0 = Replacer.setterType.replace(t0, manager.onImported(setterDeclareType));
                        t0 = Replacer.getterType.replace(t0, manager.onImported(getterDeclareType));
                    } else {
                        t0 = warningAndIgnored(model);
                    }
                } else {
                    t0 = warningAndIgnored(model);
                }
            }
            return t0;
        }
        return null;
    }

    private String declare2Jdk8Time(final MappingModel model, Manager manager) {
        String t0;
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        if (isTypeofAny(setterDeclareType, java.time.LocalDateTime.class, java.time.ZonedDateTime.class,
            java.time.OffsetDateTime.class)) {
            if (isPrimitiveNumber(getterDeclareType)) {
                if (isCompatible(LONG, getterDeclareType)) {
                    t0 = "{toName}.{setterName}({setterType}.ofInstant({type0}.ofEpochMilli(" +//
                        "{fromName}.{getterName}()), {type1}.systemDefault()));";
                } else {
                    t0 = "{toName}.{setterName}({setterType}.ofInstant({type0}.ofEpochMilli(" +//
                        "(long){fromName}.{getterName}()), {type1}.systemDefault()));";
                }
                t0 = Replacer.setterType.replace(t0, manager.onImported(setterDeclareType));
                t0 = Replacer.type0.replace(t0, manager.onImported(java.time.Instant.class));
                t0 = Replacer.type1.replace(t0, manager.onImported(java.time.ZoneId.class));
            } else if (isPrimitiveChar(getterDeclareType)) {
                t0 = "{toName}.{setterName}({setterType}.ofInstant({type0}.ofEpochMilli(" +//
                    "{fromName}.{getterName}()), {type1}.systemDefault()));";
                t0 = Replacer.type0.replace(t0, manager.onImported(java.time.Instant.class));
                t0 = Replacer.type1.replace(t0, manager.onImported(java.time.ZoneId.class));
                t0 = Replacer.setterType.replace(t0, manager.onImported(setterDeclareType));
            } else if (isSubTypeof(getterDeclareType, Number.class)) {
                t0 = manager.ofConvert().onRefType(setterDeclareType, Number.class);
            } else if (isSubTypeof(getterDeclareType, Date.class)) {
                t0 = manager.ofConvert().onRefType(setterDeclareType, Date.class);
            } else if (isTypeof(getterDeclareType, Calendar.class)) {
                t0 = manager.ofConvert().onRefType(setterDeclareType, Calendar.class);
            } else if (isTypeof(getterDeclareType, java.time.Instant.class)) {
                t0 = manager.ofConvert().onRefType(setterDeclareType, java.time.Instant.class);
            } else if (isString(getterDeclareType)) {
                String format = manager.ofStatic().onDateTimeFormatter(model.getAttr().formatValue());
                if (isNullString(format)) {
                    t0 = manager.ofConvert().onRefType(setterDeclareType, String.class, "{setterType}.parse({var})");
                } else {
                    t0 = Replacer.format.replace("{setterType}.parse({var}, {format})", format);
                    t0 = manager.ofConvert().onRefType(setterDeclareType, String.class, t0);
                }
            } else if (DetectUtils.IMPORTED_JODA_TIME) {
                if (isSubTypeof(getterDeclareType, ReadableInstant.class)) {
                    t0 = manager.ofConvert().onRefType(setterDeclareType, ReadableInstant.class);
                } else if (isTypeofAny(getterDeclareType, LocalDateTime.class, LocalDate.class)) {
                    t0 = manager.ofConvert().onRefType(setterDeclareType, getterDeclareType);
                } else {
                    t0 = null;
                }
            } else {
                t0 = null;
            }
            if (t0 == null) {
                t0 = warningAndIgnored(model);
            }
        } else if (isTypeofAny(setterDeclareType, java.time.LocalDate.class, java.time.LocalTime.class)) {
            if (isTypeofAny(getterDeclareType, java.time.LocalDateTime.class, java.time.ZonedDateTime.class,
                java.time.OffsetDateTime.class)) {
                t0 = manager.ofConvert().onRefType(setterDeclareType, getterDeclareType);
            } else if (isPrimitiveNumber(getterDeclareType)) {
                if (isCompatible(LONG, getterDeclareType)) {
                    t0 = "{toName}.{setterName}({type1}.ofInstant({type0}.ofEpochMilli(" +//
                        "{fromName}.{getterName}()), {type2}.systemDefault()).to{simpleName}());";
                } else {
                    t0 = "{toName}.{setterName}({type1}.ofInstant({type0}.ofEpochMilli(" +//
                        "(long){fromName}.{getterName}()), {type2}.systemDefault()).to{simpleName}());";
                }
                t0 = Replacer.simpleName.replace(t0, ElementUtils.getSimpleName(setterDeclareType));
                t0 = Replacer.type2.replace(t0, manager.onImported(ZoneId.class));
                t0 = Replacer.type1.replace(t0, manager.onImported(java.time.LocalDateTime.class));
                t0 = Replacer.type0.replace(t0, manager.onImported(java.time.Instant.class));
            } else if (isPrimitiveChar(getterDeclareType)) {
                t0 = "{toName}.{setterName}({type1}.ofInstant({type0}.ofEpochMilli(" +//
                    "{fromName}.{getterName}()), {type2}.systemDefault()).to{simpleName}());";
                t0 = Replacer.type0.replace(t0, manager.onImported(java.time.Instant.class));
                t0 = Replacer.type1.replace(t0, manager.onImported(java.time.LocalDateTime.class));
                t0 = Replacer.type2.replace(t0, manager.onImported(ZoneId.class));
                t0 = Replacer.simpleName.replace(t0, ElementUtils.getSimpleName(setterDeclareType));
            } else if (isSubTypeof(getterDeclareType, Number.class)) {
                t0 = manager.ofConvert().onRefType(setterDeclareType, Number.class);
            } else if (isSubTypeof(getterDeclareType, Date.class)) {
                t0 = manager.ofConvert().onRefType(setterDeclareType, Date.class);
            } else if (isTypeof(getterDeclareType, Calendar.class)) {
                t0 = manager.ofConvert().onRefType(setterDeclareType, Calendar.class);
            } else if (isString(getterDeclareType)) {
                String format = manager.ofStatic().onDateTimeFormatter(model.getAttr().formatValue());
                if (isNullString(format)) {
                    t0 = manager.ofConvert().onRefType(setterDeclareType, String.class, "{setterType}.parse({var})");
                } else {
                    t0 = Replacer.format.replace("{setterType}.parse({var}, {format})", format);
                    t0 = manager.ofConvert().onRefType(setterDeclareType, String.class, t0);
                }
            } else if (DetectUtils.IMPORTED_JODA_TIME) {
                if (isSubTypeof(getterDeclareType, ReadableInstant.class)) {
                    t0 = manager.ofConvert().onRefType(setterDeclareType, ReadableInstant.class);
                } else if (isTypeofAny(getterDeclareType, LocalDateTime.class, LocalDate.class)) {
                    t0 = manager.ofConvert().onRefType(setterDeclareType, getterDeclareType);
                } else {
                    return warningAndIgnored(model);
                }
            } else {
                return warningAndIgnored(model);
            }
        } else {
            return warningAndIgnored(model);
        }
        return t0;
    }

    /** long -> Date, Timestamp, java.sql.Date, Calendar */
    private String declare2Date(final MappingModel model, Manager manager) {
        String t0;
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        if (isTypeofAny(setterDeclareType, Date.class, java.sql.Date.class, Timestamp.class, Time.class)) {
            if (isPrimitiveNumber(getterDeclareType)) {
                if (isCompatible(LONG, getterDeclareType)) {
                    t0 = "{toName}.{setterName}(new {setterType}({fromName}.{getterName}()));";
                } else {
                    t0 = "{toName}.{setterName}(new {setterType}((long){fromName}.{getterName}()));";
                }
                t0 = Replacer.setterType.replace(t0, manager.onImported(setterDeclareType));
            } else if (isSubTypeof(getterDeclareType, Number.class)) {
                t0 = manager.ofConvert().onRefType(setterDeclareType, Number.class);
            } else if (isTypeof(getterDeclareType, Calendar.class)) {
                t0 = manager.ofConvert().onRefType(setterDeclareType, Calendar.class);
            } else if (isTypeofJdk8Time(getterDeclareType)) {
                t0 = manager.ofConvert().onRefType(model);
            } else if (isString(getterDeclareType)) {
                t0 = manager.ofConvert().onRefType(setterDeclareType, String.class);
            } else if (DetectUtils.IMPORTED_JODA_TIME) {
                if (isSubTypeof(getterDeclareType, ReadableInstant.class)) {
                    t0 = manager.ofConvert().onRefType(setterDeclareType, ReadableInstant.class);
                } else if (isTypeofAny(getterDeclareType, LocalDateTime.class, LocalDate.class)) {
                    t0 = manager.ofConvert().onRefType(setterDeclareType, getterDeclareType);
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
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        if (isTypeof(setterDeclareType, Calendar.class)) {
            if (isPrimitiveNumber(getterDeclareType)) {
                if (isCompatible(LONG, getterDeclareType)) {
                    t0 = "{type1} {var} = {type1}.getInstance();" +//
                        "{var}.setTimeInMillis({fromName}.{getterName}());" +//
                        "{toName}.{setterName}({var});";
                } else {
                    t0 = "{type1} {var} = {type1}.getInstance();" +//
                        "{var}.setTimeInMillis((long){fromName}.{getterName}());" +//
                        "{toName}.{setterName}({var});";
                }
                t0 = Replacer.type1.replace(t0, manager.onImported(java.util.Calendar.class));
                t0 = Replacer.setterType.replace(t0, manager.onImported(setterDeclareType));
            } else if (isSubTypeof(getterDeclareType, Number.class)) {
                t0 = manager.ofConvert().onRefType(setterDeclareType, Number.class);
            } else if (isSubTypeof(getterDeclareType, Date.class)) {
                t0 = manager.ofConvert().onRefType(setterDeclareType, Date.class);
            } else if (isTypeofJdk8Time(getterDeclareType)) {
                t0 = manager.ofConvert().onRefType(model);
            } else if (isString(getterDeclareType)) {
                t0 = manager.ofConvert().onRefType(setterDeclareType, String.class);
            } else if (DetectUtils.IMPORTED_JODA_TIME) {
                if (isSubTypeof(getterDeclareType, ReadableInstant.class)) {
                    t0 = manager.ofConvert().onRefType(setterDeclareType, ReadableInstant.class);
                } else if (isTypeofAny(getterDeclareType, LocalDateTime.class, LocalDate.class)) {
                    t0 = manager.ofConvert().onRefType(setterDeclareType, getterDeclareType);
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

    private final static String TEMPLATE = "【已忽略】Setter方法 '{}#{}({})' 不兼容 '{}#{}()' 返回值类型: {}";

    private final AtomicInteger indexer = new AtomicInteger();

    private AtomicInteger getIndexer() { return indexer; }

    private boolean isWarned() { return warned; }

    private void unWarned() { this.warned = false; }

    private String nextVarName() {
        return nextVarName(getIndexer());
    }

    private static String nextVarName(AtomicInteger indexer) {
        return "var" + indexer.getAndIncrement();
    }

    private static boolean isCompatible(String thisType, String thatType) {
        String all = "byte,short,int,long,float,double";
        int thisIdx = all.indexOf(thisType);
        int thatIdx = all.indexOf(thatType);
        return thisIdx >= thatIdx;
    }

    private static boolean isString(String value) {
        return isTypeof(value, String.class);
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

    private static boolean isSubTypeof(String actualType, Class<?> superclass) {
        return isSubTypeof(actualType, superclass.getCanonicalName());
    }

    private static boolean isEnum(String value) {
        TypeElement elem = EnvUtils.getUtils().getTypeElement(value);
        return elem != null && elem.getKind() == ElementKind.ENUM;
    }

    private static boolean isTypeofJdk8Time(String type) {
        return isTypeofAny(type, java.time.LocalDateTime.class, java.time.ZonedDateTime.class,
            java.time.OffsetDateTime.class, java.time.Instant.class, java.time.LocalDate.class);
    }

    private static boolean isSubTypeof(String actualType, String superClass) {
        Elements utils = EnvUtils.getUtils();
        TypeElement elem1 = utils.getTypeElement(actualType);
        TypeElement elem2 = utils.getTypeElement(superClass);
        return elem1 != null && elem2 != null && EnvUtils.getTypes().isSubtype(elem1.asType(), elem2.asType());
    }

    public static boolean isNullString(String dftVal) {
        return dftVal == null || StaticManager.NULL.equals(dftVal);
    }
}
