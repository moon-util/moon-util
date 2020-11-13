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
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

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
        final MappingModel model, StaticManager staticManager
    ) {
        unWarned();
        String field;
        if (!model.isGetterGeneric() && !model.isSetterGeneric()) {
            field = doConvertDeclaredField(model, staticManager);
            if (field == null) {
                final Elements utils = EnvUtils.getUtils();
                TypeMirror setterType = utils.getTypeElement(model.getSetterDeclareType()).asType();
                TypeMirror getterType = utils.getTypeElement(model.getGetterDeclareType()).asType();
                if (EnvUtils.getTypes().isSubtype(getterType, setterType)) {
                    field = "{toName}.{setterName}({fromName}.{getterName}());";
                }
            }
            if (field == null && !isWarned()) {
                field = warningAndIgnored(model);
            }
        } else if (model.isGetterGeneric() && model.isSetterGeneric()) {
            final String setterTypeString = model.getSetterActualType();
            final String getterTypeString = model.getGetterActualType();
            field = doConvertGenerify(model, getterTypeString, setterTypeString);
        } else if (model.isGetterGeneric()) {
            final String setterTypeString = model.getSetterDeclareType();
            final String getterTypeString = model.getGetterActualType();
            field = doConvertGenerify(model, getterTypeString, setterTypeString);
        } else if (model.isSetterGeneric()) {
            final String setterTypeString = model.getSetterActualType();
            final String getterTypeString = model.getGetterDeclareType();
            field = doConvertGenerify(model, getterTypeString, setterTypeString);
        } else {
            throw new IllegalStateException("This is impossible.");
        }
        return onDeclareCompleted(field, model);
    }

    private String doConvertGenerify(
        MappingModel model, String getterTypeString, String setterTypeString
    ) {
        Elements utils = EnvUtils.getUtils();
        TypeMirror setterType = utils.getTypeElement(setterTypeString).asType();
        TypeMirror getterType = utils.getTypeElement(getterTypeString).asType();
        if (EnvUtils.getTypes().isSubtype(getterType, setterType)) {
            String field = "{toName}.{setterName}(({setterType}){fromName}.{getterName}());";
            return Replacer.setterType.replace(field, setterTypeString);
        } else {
            return warningAndIgnored(model);
        }
    }

    @SuppressWarnings("all")
    private String doConvertDeclaredField(
        final MappingModel model, StaticManager staticManager
    ) {
        String t0 = null;
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        if (Objects.equals(setterDeclareType, getterDeclareType)) {
            t0 = "{toName}.{setterName}({fromName}.{getterName}());";
        }
        if (t0 == null) {
            t0 = declare2String(model, staticManager);
        }
        if (t0 == null) {
            t0 = declare2PrimitiveNumber(model, staticManager);
        }
        if (t0 == null) {
            t0 = declare2WrappedNumber(model, staticManager);
        }
        if (t0 == null) {
            t0 = declare2Enum(model, staticManager);
        }
        if (t0 == null) {
            t0 = declare2Boolean(model, staticManager);
        }
        if (t0 == null) {
            t0 = declare2Char(model, staticManager);
        }
        if (t0 == null) {
            t0 = declare2Date(model, staticManager);
        }
        if (t0 == null) {
            t0 = declare2JodaTime(model, staticManager);
        }
        if (t0 == null) {
            t0 = declare2BigInteger(model, staticManager);
        }
        if (t0 == null) {
            t0 = declare2BigDecimal(model, staticManager);
        }
        return onDeclareCompleted(t0, model);
    }

    private String forDefaultEnum(MappingModel model, StaticManager staticManager, String enumClassname) {
        PropertyAttr attr = model.getAttr();
        String dftValue = attr.defaultValue();
        if (DetectUtils.isDigit(dftValue)) {
            return staticManager.onEnumIndexed(enumClassname, dftValue);
        } else if (DetectUtils.isVar(dftValue)) {
            return staticManager.onEnumNamed(enumClassname, dftValue);
        } else {
            return staticManager.defaultNull();
        }
    }

    private String declare2Enum(MappingModel model, StaticManager staticManager) {
        String t0;
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        if (isEnum(setterDeclareType)) {
            if (isString(getterDeclareType)) {
                String dftValue = forDefaultEnum(model, staticManager, setterDeclareType);
                t0 = "String {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null || {var}.length() == 0) { {toName}.{setterName}({NULL}); }" +//
                    "else { {toName}.{setterName}({setterType}.valueOf({var})); }";
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
                t0 = Replacer.NULL.replace(t0, dftValue);
            } else if (isPrimitiveNumber(getterDeclareType)) {
                String enumValues = staticManager.onEnumValues(setterDeclareType);
                if (isCompatible(INT, getterDeclareType)) {
                    t0 = "{toName}.{setterName}({setterType}[{fromName}.{getterName}()]);";
                } else {
                    t0 = "{toName}.{setterName}({setterType}[(int){fromName}.{getterName}()]);";
                }
                t0 = Replacer.setterType.replace(t0, enumValues);
            } else if (isSubTypeof(getterDeclareType, Number.class)) {
                String enumValues = staticManager.onEnumValues(setterDeclareType);
                String dftValue = forDefaultEnum(model, staticManager, setterDeclareType);
                t0 = "Number {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}({NULL}); }" +//
                    "else { {toName}.{setterName}({setterType}[{var}.intValue()]); }";
                t0 = Replacer.setterType.replace(t0, enumValues);
                t0 = Replacer.NULL.replace(t0, dftValue);
            } else {
                t0 = warningAndIgnored(model);
            }
            return t0;
        }
        return null;
    }

    private String declare2String(final MappingModel model, StaticManager staticManager) {
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
                    t0 = "{toName}.{setterName}(String.valueOf({fromName}.{getterName}()));";
                } else {
                    t0 = "{toName}.{setterName}(new {type0}(\"{format}\").format({fromName}.{getterName}()));";
                    t0 = Replacer.type0.replaceTypeof(t0, DecimalFormat.class);
                    t0 = Replacer.format.replace(t0, attr.formatValue());
                }
            } else if (isEnum(getterDeclareType)) {
                // todo 默认值
                t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}(null); }" +//
                    "else { {toName}.{setterName}({var}.name()); }";
                t0 = Replacer.getterType.replace(t0, getterDeclareType);
            } else {
                // todo 默认值
                t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}(null); }" +//
                    "else { {toName}.{setterName}({var}.toString()); }";
                t0 = Replacer.getterType.replace(t0, getterDeclareType);
            }
            return t0;
        } else {
            return null;
        }
    }

    private String forDefaultNumber(MappingModel model, StaticManager staticManager, String classname) {
        PropertyAttr attr = model.getAttr();
        String defaultValue = attr.defaultValue();
        if (defaultValue != null) {
            return staticManager.onDefaultNumber(classname, defaultValue);
        }
        return staticManager.defaultNull();
    }

    private String declare2PrimitiveNumber(final MappingModel model, StaticManager staticManager) {
        String t0;
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        final String dftValue = forDefaultNumber(model, staticManager, setterDeclareType);
        if (isPrimitiveNumber(setterDeclareType)) {
            if (isPrimitiveNumber(getterDeclareType)) {
                if (isCompatible(setterDeclareType, getterDeclareType)) {
                    t0 = "{toName}.{setterName}({fromName}.{getterName}());";
                } else {
                    t0 = "{toName}.{setterName}(({setterType}){fromName}.{getterName}());";
                    t0 = Replacer.setterType.replace(t0, setterDeclareType);
                }
            } else if (isWrappedNumber(getterDeclareType)) {
                String setterWrapped = StringUtils.toWrappedType(setterDeclareType);
                if (isNullString(dftValue)) {
                    t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                        "if ({var} != null) { {toName}.{setterName}({var}.{setterType}Value()); }";
                    t0 = Replacer.getterType.replace(t0, setterWrapped);
                    t0 = Replacer.setterType.replace(t0, setterDeclareType);
                } else {
                    t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                        "if ({var} == null) { {toName}.{setterName}({NULL}); }" +//
                        "else { {toName}.{setterName}({var}.{setterType}Value()); }";
                    t0 = Replacer.NULL.replace(t0, dftValue);
                    t0 = Replacer.getterType.replace(t0, setterWrapped);
                    t0 = Replacer.setterType.replace(t0, setterDeclareType);
                }
            } else if (StringUtils.isPrimitiveChar(getterDeclareType)) {
                if (isCompatible(setterDeclareType, INT)) {
                    t0 = "{toName}.{setterName}({fromName}.{getterName}());";
                } else {
                    t0 = "{toName}.{setterName}(({setterType}){fromName}.{getterName}());";
                    t0 = Replacer.setterType.replace(t0, setterDeclareType);
                }
            } else if (isString(getterDeclareType)) {
                String setterWrapped = StringUtils.toWrappedType(setterDeclareType);
                if (isNullString(dftValue)) {
                    t0 = "String {var} = {fromName}.{getterName}();" +//
                        "if ({var} != null && {var}.length() > 0) {" +//
                        " {toName}.{setterName}({type0}.parse{type1}({var})); " +//
                        "}";
                } else {
                    t0 = "String {var} = {fromName}.{getterName}();" +//
                        "if ({var} == null || {var}.length() = 0) {" +//
                        "  {toName}.{setterName}({NULL});" +//
                        "} else { {toName}.{setterName}({type0}.parse{type1}({var})); }";
                    t0 = Replacer.NULL.replace(t0, dftValue);
                }
                t0 = Replacer.type0.replace(t0, setterWrapped);
                String type1 = INT.equals(setterDeclareType) ? "Int" : setterWrapped;
                t0 = Replacer.type1.replace(t0, type1);
            } else if (isSubTypeof(getterDeclareType, Number.class)) {
                if (isNullString(dftValue)) {
                    t0 = "Number {var} = {fromName}.{getterName}();" +//
                        "if ({var} != null) { {toName}.{setterName}({var}.{type0}Value()); }";
                } else {
                    t0 = "Number {var} = {fromName}.{getterName}();" +//
                        "if ({var} == null) { {toName}.{setterName}({NULL}); }" +//
                        "else { {toName}.{setterName}({var}.{type0}Value()); }";
                    t0 = Replacer.NULL.replace(t0, dftValue);
                }
                t0 = Replacer.type0.replace(t0, setterDeclareType);
            } else if (isEnum(getterDeclareType)) {
                if (isNullString(dftValue)) {
                    if (isCompatible(setterDeclareType, INT)) {
                        t0 = "Enum {var} = {fromName}.{getterName}();" +//
                            "if ({var} != null) { {toName}.{setterName}({var}.ordinal()); }";
                    } else {
                        t0 = "Enum {var} = {fromName}.{getterName}();" +//
                            "if ({var} != null) { {toName}.{setterName}(({setterType}){var}.ordinal()); }";
                        t0 = Replacer.setterType.replace(t0, setterDeclareType);
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
                        t0 = Replacer.setterType.replace(t0, setterDeclareType);
                    }
                    t0 = Replacer.NULL.replace(t0, dftValue);
                }
            } else if (LONG.equals(setterDeclareType)) {
                if (isNullString(dftValue)) {
                    if (isSubTypeof(getterDeclareType, Date.class)) {
                        t0 = "java.util.Date {var} = {fromName}.{getterName}();" +//
                            "if ({var} != null) { {toName}.{setterName}({var}.getTime()); }";
                    } else if (isSubTypeof(getterDeclareType, Calendar.class)) {
                        t0 = "java.util.Calendar {var} = {fromName}.{getterName}();" +//
                            "if ({var} != null) { {toName}.{setterName}({var}.getTimeInMillis()); }";
                    } else {
                        t0 = warningAndIgnored(model);
                    }
                } else {
                    if (isSubTypeof(getterDeclareType, Date.class)) {
                        t0 = "java.util.Date {var} = {fromName}.{getterName}();" +//
                            "if ({var} == null) { {toName}.{setterName}({NULL}); }" +//
                            "else { {toName}.{setterName}({var}.getTime()); }";
                        t0 = Replacer.NULL.replace(t0, dftValue);
                    } else if (isSubTypeof(getterDeclareType, Calendar.class)) {
                        t0 = "java.util.Calendar {var} = {fromName}.{getterName}();" +//
                            "if ({var} == null) { {toName}.{setterName}({NULL}); }" +//
                            "else { {toName}.{setterName}({var}.getTimeInMillis()); }";
                        t0 = Replacer.NULL.replace(t0, dftValue);
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

    private String declare2WrappedNumber(final MappingModel model, StaticManager staticManager) {
        String t0;
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        final String dftValue = forDefaultNumber(model, staticManager, setterDeclareType);
        if (isWrappedNumber(setterDeclareType)) {
            final String setterPrimitive = StringUtils.toPrimitiveType(setterDeclareType);
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
                    t0 = Replacer.getterType.replace(t0, getterDeclareType);
                    t0 = Replacer.type0.replace(t0, setterPrimitive);
                    t0 = Replacer.NULL.replace(t0, dftValue);
                }
            } else if (isString(getterDeclareType)) {
                t0 = "String {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null || {var}.length() == 0) { {toName}.{setterName}({NULL}); }" +//
                    "else { {toName}.{setterName}({type0}.parse{type1}({var})); }";
                t0 = Replacer.NULL.replace(t0, dftValue);
                t0 = Replacer.type0.replace(t0, setterDeclareType);
                if (INT.equals(setterPrimitive)) {
                    t0 = Replacer.type1.replace(t0, "Int");
                } else {
                    t0 = Replacer.type1.replace(t0, setterDeclareType);
                }
            } else if (isEnum(getterDeclareType)) {
                t0 = "Enum {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}({NULL}); }" +//
                    "else { {toName}.{setterName}({setterType}.valueOf({cast}{var}.ordinal())); }";
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
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
                t0 = "Character {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}({NULL}); }" +//
                    "else { {toName}.{setterName}(({setterType}) {var}.charValue()); }";
                t0 = Replacer.setterType.replace(t0, setterPrimitive);
                t0 = Replacer.NULL.replace(t0, dftValue);
            } else if (isSubTypeof(getterDeclareType, Number.class)) {
                t0 = "Number {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}({NULL}); }" +//
                    "else { {toName}.{setterName}({var}.{type0}Value()); }";
                t0 = Replacer.type0.replace(t0, setterPrimitive);
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
     * String -> boolean
     * byte|short|int|long|float|double -> boolean : number != 0
     * Byte|Short|Integer|Long|Float|Double -> boolean {@link Character#forDigit(int, int)}
     * Boolean -> boolean
     *
     * @param model
     *
     * @return
     */
    private String declare2Boolean(final MappingModel model, StaticManager staticManager) {
        String t0;
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        if (isPrimitiveBoolean(setterDeclareType)) {
            if (isString(getterDeclareType)) {
                t0 = "{toName}.{setterName}(Boolean.parseBoolean({fromName}.{getterName}()));";
            } else if (isPrimitiveNumber(getterDeclareType)) {
                t0 = "{toName}.{setterName}({fromName}.getterName() != 0);";
            } else if (isWrappedNumber(getterDeclareType)) {
                t0 = "{toName}.{setterName}(Integer.valueOf(0).equals({fromName}.getterName()));";
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
                t0 = "{toName}.{setterName}(Boolean.parseBoolean({fromName}.{getterName}()));";
            } else if (isPrimitiveNumber(getterDeclareType)) {
                t0 = "{toName}.{setterName}({fromName}.getterName() != 0);";
            } else if (isWrappedNumber(getterDeclareType)) {
                t0 = "{toName}.{setterName}(Integer.valueOf(0).equals({fromName}.getterName()));";
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
    private String declare2Char(final MappingModel model, StaticManager staticManager) {
        String t0;
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        if (StringUtils.isPrimitiveChar(setterDeclareType)) {
            if (StringUtils.isPrimitiveChar(getterDeclareType)) {
                t0 = "{toName}.{setterName}({fromName}.{getterName}());";
            } else if (Character.class.getName().equals(getterDeclareType)) {
                t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                    "if ({var} != null) { {toName}.{setterName}({var}); }";
                t0 = Replacer.getterType.replace(t0, getterDeclareType);
            } else if (isPrimitiveNumber(getterDeclareType)) {
                if (isCompatible(INT, getterDeclareType)) {
                    t0 = "{toName}.{setterName}(Character.forDigit({fromName}.{getterName}(), 10));";
                } else {
                    t0 = "{toName}.{setterName}(Character.forDigit((int){fromName}.{getterName}(), 10));";
                }
            } else if (isWrappedNumber(getterDeclareType)) {
                t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                    "if ({var} != null) { {toName}.{setterName}(Character.forDigit({var}.intValue(), 10)); }";
                t0 = Replacer.getterType.replace(t0, getterDeclareType);
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
                    "if ({var} != null) { {toName}.{setterName}(Character.forDigit({var}.intValue(), 10)); }" +//
                    "else { {toName}.{setterName}(null); }";
                t0 = Replacer.getterType.replace(t0, getterDeclareType);
            } else {
                t0 = warningAndIgnored(model);
            }
        } else {
            return null;
        }
        return t0;
    }

    private String forDefaultBigDecimal(MappingModel model, StaticManager staticManager, String classname) {
        PropertyAttr attr = model.getAttr();
        String defaultValue = attr.defaultValue();
        if (defaultValue != null) {
            return staticManager.onDefaultBigInteger(defaultValue);
        }
        return staticManager.defaultNull();
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
    private String declare2BigDecimal(final MappingModel model, StaticManager staticManager) {
        String t0;
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        final String dftValue = forDefaultBigDecimal(model, staticManager, setterDeclareType);
        if (isTypeof(setterDeclareType, BigDecimal.class)) {
            if (isPrimitiveNumber(getterDeclareType)) {
                t0 = "{toName}.{setterName}({setterType}.valueOf({fromName}.{getterName}()));";
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
            } else if (isWrappedNumber(getterDeclareType)) {
                String getterPrimitive = StringUtils.toPrimitiveType(getterDeclareType);
                t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}({NULL}); }" +//
                    "else { {toName}.{setterName}({setterType}.valueOf({fromName}.{getterName}().{type0}Value())); }";
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
                t0 = Replacer.getterType.replace(t0, getterDeclareType);
                t0 = Replacer.NULL.replace(t0, dftValue);
                if (isCompatible(LONG, getterPrimitive)) {
                    t0 = Replacer.type0.replace(t0, LONG);
                } else {
                    t0 = Replacer.type0.replace(t0, "double");
                }
            } else if (isString(getterDeclareType) || isTypeof(getterDeclareType, BigInteger.class)) {
                t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}({NULL}); }" +//
                    "else { {toName}.{setterName}(new {setterType}({var})); }";
                t0 = Replacer.NULL.replace(t0, dftValue);
                t0 = Replacer.getterType.replace(t0, getterDeclareType);
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
            } else if (StringUtils.isPrimitiveChar(getterDeclareType)) {
                t0 = "int {var} = {fromName}.{getterName}();" +//
                    "{toName}.{setterName}({setterType}.valueOf({var}));";
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
            } else if (isTypeof(setterDeclareType, Character.class)) {
                t0 = "Character {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}({NULL}); }" +//
                    "else { {toName}.{setterName}({setterType}.valueOf({var}.charValue())); }";
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
                t0 = Replacer.NULL.replace(t0, dftValue);
            } else {
                t0 = warningAndIgnored(model);
            }
            return t0;
        } else {
            return null;
        }
    }

    private String forDefaultBigInteger(MappingModel model, StaticManager staticManager, String classname) {
        PropertyAttr attr = model.getAttr();
        String defaultValue = attr.defaultValue();
        if (defaultValue != null) {
            return staticManager.onDefaultBigDecimal(defaultValue);
        }
        return staticManager.defaultNull();
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
    private String declare2BigInteger(final MappingModel model, StaticManager staticManager) {
        String t0;
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        final String dftValue = forDefaultBigInteger(model, staticManager, setterDeclareType);
        if (isTypeof(setterDeclareType, BigInteger.class)) {
            if (isPrimitiveNumber(getterDeclareType)) {
                String getterPrimitive = StringUtils.toPrimitiveType(getterDeclareType);
                if (isCompatible(LONG, getterPrimitive)) {
                    t0 = "{toName}.{setterName}({setterType}.valueOf({fromName}.{getterName}()));";
                } else {
                    t0 = "{toName}.{setterName}({setterType}.valueOf((long){fromName}.{getterName}()));";
                }
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
            } else if (isWrappedNumber(getterDeclareType)) {
                t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}({NULL}); }" +//
                    "else { {toName}.{setterName}({setterType}.valueOf({var}.longValue())); }";
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
                t0 = Replacer.getterType.replace(t0, getterDeclareType);
                t0 = Replacer.NULL.replace(t0, dftValue);
            } else if (isString(getterDeclareType)) {
                t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}({NULL}); }" +//
                    "else { {toName}.{setterName}(new {setterType}({var})); }";
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
                t0 = Replacer.getterType.replace(t0, getterDeclareType);
                t0 = Replacer.NULL.replace(t0, dftValue);
            } else if (StringUtils.isPrimitiveChar(getterDeclareType)) {
                t0 = "{toName}.{setterName}({setterType}.valueOf({fromName}.{getterName}()));";
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
            } else if (isTypeof(getterDeclareType, Character.class)) {
                t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}({NULL}); }" +//
                    "else { {toName}.{setterName}({setterType}.valueOf({var}.charValue())); }";
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
                t0 = Replacer.getterType.replace(t0, getterDeclareType);
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
    private String declare2JodaTime(final MappingModel model, StaticManager staticManager) {
        if (!DetectUtils.IMPORTED_JODA_TIME) {
            return null;
        }
        String t0;
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        if (isTypeofAny(setterDeclareType,
            MutableDateTime.class,
            DateTime.class,
            LocalDateTime.class,
            LocalDate.class,
            LocalTime.class,
            MutablePeriod.class,
            Duration.class,
            YearMonth.class,
            Instant.class,
            MonthDay.class)) {
            if (isPrimitiveNumber(getterDeclareType)) {
                if (isCompatible(LONG, getterDeclareType)) {
                    t0 = "{toName}.{setterName}(new {setterType}({fromName}.{getterName}()));";
                } else {
                    t0 = "{toName}.{setterName}(new {setterType}((long){fromName}.{getterName}()));";
                }
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
            } else if (isSubTypeof(getterDeclareType, Number.class)) {
                t0 = "Number {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}(null); }" +//
                    "else { {toName}.{setterName}(new {setterType}({var}.longValue())); }";
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
            } else {
                if (isTypeofAny(setterDeclareType,
                    MutableDateTime.class,
                    DateTime.class,
                    LocalDateTime.class,
                    LocalDate.class,
                    LocalTime.class)) {
                    if (isString(getterDeclareType)) {
                        String format = model.getAttr().formatValue();
                        String dftFormat = staticManager.onJodaDateTimeFormat(format);
                        if (isNullString(dftFormat)) {
                            t0 = "String {var} = {fromName}.{getterName}();" +//
                                "if ({var} == null) { {toName}.{setterName}(null); }" +//
                                "else { {toName}.{setterName}({var}); }";
                        } else {
                            t0 = "String {var} = {fromName}.{getterName}();" +//
                                "if ({var} == null) { {toName}.{setterName}(null); }" +//
                                "else { {toName}.{setterName}({format}.parse{name}({var})); }";
                            t0 = Replacer.format.replace(t0, dftFormat);
                            t0 = Replacer.name.replace(t0, ElementUtils.getSimpleName(setterDeclareType));
                        }
                    } else if (isSubTypeof(getterDeclareType, Date.class) || isTypeof(getterDeclareType,
                        Calendar.class)) {
                        t0 = "{type0} {var} = {fromName}.{getterName}();" +//
                            "if ({var} == null) { {toName}.{setterName}(null); }" +//
                            "else { {toName}.{setterName}(new {setterType}({var})); }";
                        t0 = Replacer.setterType.replace(t0, setterDeclareType);
                        t0 = Replacer.type0.replace(t0, getterDeclareType);
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

    private String declare2Jdk8Time(final MappingModel model, StaticManager ignored) {
        String t0;
        final String setterDeclareType = model.getSetterDeclareType();
        final String getterDeclareType = model.getGetterDeclareType();
        return null;
    }

    /** long -> Date, Timestamp, java.sql.Date, Calendar */
    private String declare2Date(final MappingModel model, StaticManager ignored) {
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
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
            } else if (isSubTypeof(getterDeclareType, Number.class)) {
                t0 = "Number {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}(null); }" +//
                    "else { {toName}.{setterName}(new {setterType}({var}.longValue())); }";
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
            } else if (isTypeof(getterDeclareType, Calendar.class)) {
                t0 = "java.util.Calendar {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}(null); }" +//
                    "else { {toName}.{setterName}({var}.getTime()); }";
            } else if (isString(getterDeclareType)) {
                String typedNewer;
                if (isTypeof(setterDeclareType, Date.class)) {
                    typedNewer = "{var0}";
                } else if (isTypeof(setterDeclareType, java.sql.Date.class)) {
                    typedNewer = "new java.sql.Date({var0}.getTime())";
                } else if (isTypeof(setterDeclareType, Timestamp.class)) {
                    typedNewer = "new java.sql.Timestamp({var0}.getTime())";
                } else if (isTypeof(setterDeclareType, Time.class)) {
                    typedNewer = "new java.sql.Time({var0}.getTime())";
                } else {
                    return warningAndIgnored(model);
                }
                String format = model.getAttr().formatValue();
                format = StringUtils.isBlank(format) ? "" : ('"' + format + '"');
                t0 = "String {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null || {var}.length() == 0) { {toName}.{setterName}(null); }" +//
                    "else {" +//
                    "  try {" +//
                    "    java.util.Date {var0} = new java.text.SimpleDateFormat({format}).parse({var});" +//
                    "    {toName}.{setterName}({type0});" +//
                    "  } catch (java.text.ParseException e) {" +//
                    "    throw new IllegalStateException(\"日期格式错误: \"+{var}, e);" +//
                    "  }" +//
                    "}";
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
                t0 = Replacer.format.replace(t0, format);
                t0 = Replacer.type0.replace(t0, typedNewer);
            } else if (DetectUtils.IMPORTED_JODA_TIME) {
                if (isSubTypeof(getterDeclareType, ReadableInstant.class)) {
                    t0 = "{type0} {var} = {fromName}.{getterName}();" +//
                        "if ({var0} == null) { {toName}.{setterName}(null); }" +//
                        "else { {toName}.{setterName}(new {type1}({var}.getMillis())); }";
                    t0 = Replacer.type0.replaceTypeof(t0, ReadableInstant.class);
                    t0 = Replacer.type1.replace(t0, setterDeclareType);
                } else if (isTypeofAny(getterDeclareType,
                    MutableDateTime.class,
                    LocalDateTime.class,
                    LocalDate.class)) {
                    String typedNewer;
                    if (isTypeof(setterDeclareType, Date.class)) {
                        typedNewer = "{var}.toDate()";
                    } else if (isTypeof(setterDeclareType, java.sql.Date.class)) {
                        typedNewer = "new java.sql.Date({var0}.toDate().getTime())";
                    } else if (isTypeof(setterDeclareType, Timestamp.class)) {
                        typedNewer = "new java.sql.Timestamp({var0}.toDate().getTime())";
                    } else if (isTypeof(setterDeclareType, Time.class)) {
                        typedNewer = "new java.sql.Time({var0}.toDate().getTime())";
                    } else {
                        return warningAndIgnored(model);
                    }
                    t0 = "{type0} {var} = {fromName}.{getterName}();" +//
                        "if ({var} == null) { {toName}.{setterName}(null); }" +//
                        "else { {toName}.{setterName}({type1}); }";
                    t0 = Replacer.type0.replace(t0, getterDeclareType);
                    t0 = Replacer.type1.replace(t0, typedNewer);
                } else {
                    t0 = warningAndIgnored(model);
                }
            } else {
                t0 = warningAndIgnored(model);
            }
            return t0;
        } else if (isTypeof(setterDeclareType, Calendar.class)) {
            if (isPrimitiveNumber(getterDeclareType)) {
                if (isCompatible(LONG, getterDeclareType)) {
                    t0 = "java.lang.Calendar {var} = java.lang.Calendar.getInstance();" +//
                        "{var}.setTimeInMillis({fromName}.{getterName}());" +//
                        "{toName}.{setterName}({var});";
                } else {
                    t0 = "java.lang.Calendar {var} = java.lang.Calendar.getInstance();" +//
                        "{var}.setTimeInMillis((long){fromName}.{getterName}());" +//
                        "{toName}.{setterName}({var});";
                }
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
            } else if (isSubTypeof(getterDeclareType, Number.class)) {
                t0 = "Number {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}(null); }" +//
                    "else { java.util.Calendar {var} = java.util.Calendar.getInstance();" +//
                    "    {var}.setTimeInMillis({var}.longValue());" +//
                    "{toName}.{setterName}({var});}";
            } else if (isString(getterDeclareType)) {
                String format = model.getAttr().formatValue();
                format = StringUtils.isBlank(format) ? "" : ('"' + format + '"');
                t0 = "String {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null || {var}.length() == 0) { {toName}.{setterName}(null); }" +//
                    "else {" +//
                    "  try {" +//
                    "    java.util.Date {var0} = new java.text.SimpleDateFormat({format}).parse({var});" +//
                    "    java.util.Calendar {var1} = java.util.Calendar.getInstance();" +//
                    "    {var1}.setTime({var0}); {toName}.{setterName}({var1});" +//
                    "  } catch (java.text.ParseException e) {" +//
                    "    throw new IllegalStateException(\"日期格式错误: \"+{var}, e);" +//
                    "  }" +//
                    "}";
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
                t0 = Replacer.format.replace(t0, format);
            } else if (isSubTypeof(getterDeclareType, Date.class)) {
                t0 = "Date {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}(null); }" +//
                    "else { java.util.Calendar {var0} = java.util.Calendar.getInstance();" +//
                    "{var0}.setTime({var}); {toName}.{getterName}({var0}); }";
            } else if (DetectUtils.IMPORTED_JODA_TIME) {
                if (isSubTypeof(getterDeclareType, ReadableInstant.class)) {
                    t0 = "{type0} {var} = {fromName}.{getterName}();" +//
                        "if ({var} == null) { {toName}.{setterName}(null); }" +//
                        "else { " +//
                        " java.util.Calendar {var1} = java.util.Calendar.getInstance();" +//
                        " {var1}.setTimeInMillis({var}.getMillis());" +//
                        " {toName}.{setterName}({var1});" +//
                        "}";
                    t0 = Replacer.type0.replaceTypeof(t0, ReadableInstant.class);
                } else if (isTypeofAny(getterDeclareType,
                    MutableDateTime.class,
                    LocalDateTime.class,
                    LocalDate.class)) {
                    t0 = "{type0} {var} = {fromName}.{getterName}();" +//
                        "if ({var} == null) { {toName}.{setterName}(null); }" +//
                        "else { java.util.Calendar {var1} = java.util.Calendar.getInstance();" +//
                        " {var1}.setTime({var}.toDate());" +//
                        " {toName}.{setterName}({var1});" +//
                        "}";
                    t0 = Replacer.type0.replace(t0, getterDeclareType);
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
        String fromType = ElementUtils.getSimpleName(model.getFromClassname());
        String toType = ElementUtils.getSimpleName(model.getToClassname());
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

    private static boolean isSubTypeof(String actualType, String superClass) {
        Elements utils = EnvUtils.getUtils();
        TypeMirror actual = utils.getTypeElement(actualType).asType();
        TypeMirror top = utils.getTypeElement(superClass).asType();
        return EnvUtils.getTypes().isSubtype(actual, top);
    }

    public static boolean isNullString(String dftVal) {
        return dftVal == null || StaticManager.NULL.equals(dftVal);
    }
}
