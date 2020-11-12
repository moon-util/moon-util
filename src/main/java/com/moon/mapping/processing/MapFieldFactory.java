package com.moon.mapping.processing;

import org.joda.time.*;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
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
        Mappable fromProp, Mappable toProp, String fromName, String toName, PropertyAttr attr
    ) {
        if (!fromProp.hasGetterMethod() || !toProp.hasSetterMethod()) {
            return "";
        }
        unWarned();
        String field;
        if (!fromProp.isGetterGenerify() && !toProp.isSetterGenerify()) {
            field = doConvertDeclaredField(fromProp, toProp, fromName, toName, attr);
            if (field == null) {
                final Elements utils = EnvUtils.getUtils();
                TypeMirror setterType = utils.getTypeElement(toProp.getSetterDeclareType()).asType();
                TypeMirror getterType = utils.getTypeElement(fromProp.getGetterDeclareType()).asType();
                if (EnvUtils.getTypes().isSubtype(getterType, setterType)) {
                    field = "{toName}.{setterName}({fromName}.{getterName}());";
                }
            }
            if (field == null && !isWarned()) {
                field = warningAndIgnored(fromProp, toProp);
            }
        } else if (fromProp.isGetterGenerify() && toProp.isSetterGenerify()) {
            final String setterTypeString = toProp.getSetterActualType();
            final String getterTypeString = fromProp.getGetterActualType();
            field = doConvertGenerify(fromProp, toProp, getterTypeString, setterTypeString, attr);
        } else if (fromProp.isGetterGenerify()) {
            final String setterTypeString = toProp.getSetterDeclareType();
            final String getterTypeString = fromProp.getGetterActualType();
            field = doConvertGenerify(fromProp, toProp, getterTypeString, setterTypeString, attr);
        } else if (toProp.isSetterGenerify()) {
            final String setterTypeString = toProp.getSetterActualType();
            final String getterTypeString = fromProp.getGetterDeclareType();
            field = doConvertGenerify(fromProp, toProp, getterTypeString, setterTypeString, attr);
        } else {
            throw new IllegalStateException("This is impossible.");
        }
        return onDeclareCompleted(field, fromProp, toProp, fromName, toName);
    }

    private String doConvertGenerify(
        Mappable fromProp, Mappable toProp, String getterTypeString, String setterTypeString, PropertyAttr attr
    ) {
        Elements utils = EnvUtils.getUtils();
        TypeMirror setterType = utils.getTypeElement(setterTypeString).asType();
        TypeMirror getterType = utils.getTypeElement(getterTypeString).asType();
        if (EnvUtils.getTypes().isSubtype(getterType, setterType)) {
            String field = "{toName}.{setterName}(({setterType}){fromName}.{getterName}());";
            return Replacer.setterType.replace(field, setterTypeString);
        } else {
            return warningAndIgnored(fromProp, toProp);
        }
    }

    @SuppressWarnings("all")
    private String doConvertDeclaredField(
        Mappable fromProp, Mappable toProp, String fromName, String toName, PropertyAttr attr
    ) {
        String t0 = null;
        final String setterDeclareType = toProp.getSetterDeclareType();
        final String getterDeclareType = fromProp.getGetterDeclareType();
        if (Objects.equals(setterDeclareType, getterDeclareType)) {
            t0 = "{toName}.{setterName}({fromName}.{getterName}());";
        }
        if (t0 == null) {
            t0 = declare2String(fromProp, toProp, attr);
        }
        if (t0 == null) {
            t0 = declare2PrimitiveNumber(fromProp, toProp, attr);
        }
        if (t0 == null) {
            t0 = declare2WrappedNumber(fromProp, toProp, attr);
        }
        if (t0 == null) {
            t0 = declare2Enum(fromProp, toProp, attr);
        }
        if (t0 == null) {
            t0 = declare2Boolean(fromProp, toProp, attr);
        }
        if (t0 == null) {
            t0 = declare2Char(fromProp, toProp, attr);
        }
        if (t0 == null) {
            t0 = declare2Date(fromProp, toProp, attr);
        }
        if (t0 == null) {
            t0 = declare2JodaTime(fromProp, toProp, attr);
        }
        if (t0 == null) {
            t0 = declare2BigInteger(fromProp, toProp, attr);
        }
        if (t0 == null) {
            t0 = declare2BigDecimal(fromProp, toProp, attr);
        }
        return onDeclareCompleted(t0, fromProp, toProp, fromName, toName);
    }

    private String declare2Enum(Mappable fromProp, Mappable toProp, PropertyAttr attr) {
        String t0;
        final String setterDeclareType = toProp.getSetterDeclareType();
        final String getterDeclareType = fromProp.getGetterDeclareType();
        if (isEnum(setterDeclareType)) {
            if (isString(getterDeclareType)) {
                t0 = "String {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null || {var}.length() == 0) { {toName}.{setterName}(null); }" +//
                    "else { {toName}.{setterName}({setterType}.valueOf({var})); }";
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
            } else if (isPrimitiveNumber(getterDeclareType)) {
                if (isCompatible(INT, getterDeclareType)) {
                    t0 = "{toName}.{setterName}({setterType}.values()[{fromName}.{getterName}()]);";
                } else {
                    t0 = "{toName}.{setterName}({setterType}.values()[(int){fromName}.{getterName}()]);";
                }
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
            } else if (isSubTypeof(getterDeclareType, Number.class)) {
                t0 = "Number {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}(null); }" +//
                    "else { {toName}.{setterName}({setterType}.values()[{var}.intValue()]); }";
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
            } else {
                t0 = warningAndIgnored(fromProp, toProp);
            }
            return t0;
        }
        return null;
    }

    private String declare2String(Mappable fromProp, Mappable toProp, PropertyAttr attr) {
        String t0;
        final String setterDeclareType = toProp.getSetterDeclareType();
        final String getterDeclareType = fromProp.getGetterDeclareType();
        if (isString(setterDeclareType)) {
            if (isString(getterDeclareType)) {
                t0 = "{toName}.{setterName}({fromName}.{getterName}());";
            } else if (StringUtils.isPrimitive(getterDeclareType)) {
                t0 = "{toName}.{setterName}(String.valueOf({fromName}.{getterName}()));";
            } else if (isEnum(getterDeclareType)) {
                t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}(null); }" +//
                    "else { {toName}.{setterName}({var}.name()); }";
                t0 = Replacer.getterType.replace(t0, getterDeclareType);
            } else {
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

    private String declare2PrimitiveNumber(Mappable fromProp, Mappable toProp, PropertyAttr attr) {
        String t0;
        final String setterDeclareType = toProp.getSetterDeclareType();
        final String getterDeclareType = fromProp.getGetterDeclareType();
        if (isPrimitiveNumber(setterDeclareType)) {
            if (isPrimitiveNumber(getterDeclareType)) {
                if (isCompatible(setterDeclareType, getterDeclareType)) {
                    t0 = "{toName}.{setterName}({fromName}.{getterName}());";
                } else {
                    t0 = "{toName}.{setterName}(({setterType}){fromName}.{getterName}());";
                    t0 = Replacer.setterType.replace(t0, setterDeclareType);
                }
            } else if (isWrappedNumber(getterDeclareType)) {
                String getterWrapped = StringUtils.toWrappedType(setterDeclareType);
                t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                    "if ({var} != null) { {toName}.{setterName}({var}.{setterType}Value()); }";
                t0 = Replacer.getterType.replace(t0, getterWrapped);
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
            } else if (StringUtils.isPrimitiveChar(getterDeclareType)) {
                if (isCompatible(setterDeclareType, INT)) {
                    t0 = "{toName}.{setterName}({fromName}.{getterName}());";
                } else {
                    t0 = "{toName}.{setterName}(({setterType}){fromName}.{getterName}());";
                    t0 = Replacer.setterType.replace(t0, setterDeclareType);
                }
            } else if (isString(getterDeclareType)) {
                String setterWrapped = StringUtils.toWrappedType(setterDeclareType);
                t0 = "String {var} = {fromName}.{getterName}();" +//
                    "if ({var} != null && {var}.length() > 0) { {toName}.{setterName}({type0}.parse{type1}({var})); }";
                t0 = Replacer.type0.replace(t0, setterWrapped);
                if (INT.equals(setterDeclareType)) {
                    t0 = Replacer.type1.replace(t0, "Int");
                } else {
                    t0 = Replacer.type1.replace(t0, setterWrapped);
                }
            } else if (isSubTypeof(getterDeclareType, Number.class)) {
                t0 = "Number {var} = {fromName}.{getterName}();" +//
                    "if ({var} != null) { {toName}.{setterName}({var}.{type0}Value()); }";
                t0 = Replacer.type0.replace(t0, setterDeclareType);
            } else if (isEnum(getterDeclareType)) {
                if (isCompatible(setterDeclareType, INT)) {
                    t0 = "Enum {var} = {fromName}.{getterName}();" +//
                        "if ({var} != null) { {toName}.{setterName}({var}.ordinal()); }";
                } else {
                    t0 = "Enum {var} = {fromName}.{getterName}();" +//
                        "if ({var} != null) { {toName}.{setterName}(({setterType}){var}.ordinal()); }";
                    t0 = Replacer.setterType.replace(t0, setterDeclareType);
                }
            } else if (LONG.equals(setterDeclareType)) {
                if (isSubTypeof(getterDeclareType, Date.class)) {
                    t0 = "java.util.Date {var} = {fromName}.{getterName}();" +//
                        "if ({var} != null) { {toName}.{setterName}({var}.getTime()); }";
                } else if (isSubTypeof(getterDeclareType, Calendar.class)) {
                    t0 = "java.util.Calendar {var} = {fromName}.{getterName}();" +//
                        "if ({var} != null) { {toName}.{setterName}({var}.getTimeInMillis()); }";
                } else {
                    t0 = warningAndIgnored(fromProp, toProp);
                }
            } else {
                t0 = warningAndIgnored(fromProp, toProp);
            }
            return t0;
        } else {
            return null;
        }
    }

    private String declare2WrappedNumber(Mappable fromProp, Mappable toProp, PropertyAttr attr) {
        String t0;
        final String setterDeclareType = toProp.getSetterDeclareType();
        final String getterDeclareType = fromProp.getGetterDeclareType();
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
                    t0 = "{toName}.{setterName}({fromName}.{getterName}());";
                } else {
                    t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                        "if ({var} == null) { {toName}.{setterName}(null); }" +//
                        "else { {toName}.{setterName}({var}.{type0}Value()); }";
                    t0 = Replacer.getterType.replace(t0, getterDeclareType);
                    t0 = Replacer.type0.replace(t0, setterPrimitive);
                }
            } else if (isString(getterDeclareType)) {
                t0 = "String {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null || {var}.length() == 0) { {toName}.{setterName}(null); }" +//
                    "else { {toName}.{setterName}({type0}.parse{type1}({var})); }";
                t0 = Replacer.type0.replace(t0, setterDeclareType);
                if (INT.equals(setterPrimitive)) {
                    t0 = Replacer.type1.replace(t0, "Int");
                } else {
                    t0 = Replacer.type1.replace(t0, setterDeclareType);
                }
            } else if (isEnum(getterDeclareType)) {
                if (isCompatible(setterDeclareType, INT)) {
                    t0 = "Enum {var} = {fromName}.{getterName}();" +//
                        "if ({var} == null) { {toName}.{setterName}(null); }" +//
                        "else { {toName}.{setterName}({var}.ordinal()); }";
                } else {
                    t0 = "Enum {var} = {fromName}.{getterName}();" +//
                        "if ({var} == null) { {toName}.{setterName}(null); }" +//
                        "else { {toName}.{setterName}({setterType}.valueOf({cast}{var}.ordinal())); }";
                    t0 = Replacer.setterType.replace(t0, setterDeclareType);
                    if (isCompatible(setterPrimitive, INT)) {
                        t0 = Replacer.cast.replace(t0, "");
                    } else {
                        t0 = Replacer.cast.replace(t0, "(" + setterPrimitive + ")");
                    }
                }
            } else if (StringUtils.isPrimitiveChar(getterDeclareType)) {
                if (isCompatible(setterPrimitive, INT)) {
                    t0 = "{toName}.{setterName}({fromName}.{getterName}());";
                } else {
                    t0 = "{toName}.{setterName}(({type0}) {fromName}.{getterName}());";
                    t0 = Replacer.type0.replace(t0, setterPrimitive);
                }
            } else if (isTypeof(getterDeclareType, Character.class)) {
                t0 = "Character {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}(null); }" +//
                    "else { {toName}.{setterName}(({setterType}) {var}.charValue()); }";
                t0 = Replacer.setterType.replace(t0, setterPrimitive);
            } else if (isSubTypeof(getterDeclareType, Number.class)) {
                t0 = "Number {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}(null); }" +//
                    "else { {toName}.{setterName}({var}.{type0}Value()); }";
                t0 = Replacer.type0.replace(t0, setterPrimitive);
            } else {
                t0 = warningAndIgnored(fromProp, toProp);
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
     * @param fromProp
     * @param toProp
     *
     * @return
     */
    private String declare2Boolean(Mappable fromProp, Mappable toProp, PropertyAttr attr) {
        String t0;
        final String setterDeclareType = toProp.getSetterDeclareType();
        final String getterDeclareType = fromProp.getGetterDeclareType();
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
                t0 = warningAndIgnored(fromProp, toProp);
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
                t0 = warningAndIgnored(fromProp, toProp);
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
     * @param fromProp
     * @param toProp
     *
     * @return
     */
    private String declare2Char(Mappable fromProp, Mappable toProp, PropertyAttr attr) {
        String t0;
        final String setterDeclareType = toProp.getSetterDeclareType();
        final String getterDeclareType = fromProp.getGetterDeclareType();
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
                t0 = warningAndIgnored(fromProp, toProp);
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
                t0 = warningAndIgnored(fromProp, toProp);
            }
        } else {
            return null;
        }
        return t0;
    }

    /**
     * byte、short、int、long、float、double -> {@link BigDecimal#valueOf(long)}, {@link BigDecimal#valueOf(double)}
     * Byte、Short、Integer、Long、Float、Double -> {@link BigDecimal#valueOf(long)}
     * <p>
     * String -> {@link BigDecimal#BigDecimal(String)}, {@link BigDecimal#BigDecimal(BigInteger)}
     * <p>
     * char|Character -> {@link BigDecimal#valueOf(long)}
     *
     * @param fromProp
     * @param toProp
     *
     * @return
     */
    private String declare2BigDecimal(Mappable fromProp, Mappable toProp, PropertyAttr attr) {
        String t0;
        final String setterDeclareType = toProp.getSetterDeclareType();
        final String getterDeclareType = fromProp.getGetterDeclareType();
        if (isTypeof(setterDeclareType, BigDecimal.class)) {
            if (isPrimitiveNumber(getterDeclareType)) {
                t0 = "{toName}.{setterName}({setterType}.valueOf({fromName}.{getterName}()));";
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
            } else if (isWrappedNumber(getterDeclareType)) {
                String getterPrimitive = StringUtils.toPrimitiveType(getterDeclareType);
                t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}(null); }" +//
                    "else { {toName}.{setterName}({setterType}.valueOf({fromName}.{getterName}().{type0}Value())); }";
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
                t0 = Replacer.getterType.replace(t0, getterDeclareType);
                if (isCompatible(LONG, getterPrimitive)) {
                    t0 = Replacer.type0.replace(t0, LONG);
                } else {
                    t0 = Replacer.type0.replace(t0, "double");
                }
            } else if (isString(getterDeclareType) || isTypeof(getterDeclareType, BigInteger.class)) {
                t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}(null); }" +//
                    "else { {toName}.{setterName}(new {setterType}({var})); }";
                t0 = Replacer.getterType.replace(t0, getterDeclareType);
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
            } else if (StringUtils.isPrimitiveChar(getterDeclareType)) {
                t0 = "int {var} = {fromName}.{getterName}();" +//
                    "{toName}.{setterName}({setterType}.valueOf({var}));";
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
            } else if (isTypeof(setterDeclareType, Character.class)) {
                t0 = "Character {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}(null); }" +//
                    "else { {toName}.{setterName}({setterType}.valueOf({var}.charValue())); }";
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
            } else {
                t0 = warningAndIgnored(fromProp, toProp);
            }
            return t0;
        } else {
            return null;
        }
    }

    /**
     * byte、short、int、long、float、double -> {@link BigInteger#valueOf(long)}
     * Byte、Short、Integer、Long、Float、Double -> {@link BigInteger#valueOf(long)}
     * String -> {@link BigInteger#BigInteger(String)}
     * char|Character -> {@link BigInteger#valueOf(long)}
     *
     * @param fromProp
     * @param toProp
     *
     * @return
     */
    private String declare2BigInteger(Mappable fromProp, Mappable toProp, PropertyAttr attr) {
        String t0;
        final String setterDeclareType = toProp.getSetterDeclareType();
        final String getterDeclareType = fromProp.getGetterDeclareType();
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
                    "if ({var} == null) { {toName}.{setterName}(null); }" +//
                    "else { {toName}.{setterName}({setterType}.valueOf({var}.longValue())); }";
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
                t0 = Replacer.getterType.replace(t0, getterDeclareType);
            } else if (isString(getterDeclareType)) {
                t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}(null); }" +//
                    "else { {toName}.{setterName}(new {setterType}({var})); }";
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
                t0 = Replacer.getterType.replace(t0, getterDeclareType);
            } else if (StringUtils.isPrimitiveChar(getterDeclareType)) {
                t0 = "{toName}.{setterName}({setterType}.valueOf({fromName}.{getterName}()));";
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
            } else if (isTypeof(getterDeclareType, Character.class)) {
                t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}(null); }" +//
                    "else { {toName}.{setterName}({setterType}.valueOf({var}.charValue())); }";
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
                t0 = Replacer.getterType.replace(t0, getterDeclareType);
            } else {
                t0 = warningAndIgnored(fromProp, toProp);
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
     * @param fromProp
     * @param toProp
     *
     * @return
     */
    private String declare2JodaTime(Mappable fromProp, Mappable toProp, PropertyAttr attr) {
        if (!DetectUtils.IMPORTED_JODA_TIME) {
            return null;
        }
        String t0;
        final String setterDeclareType = toProp.getSetterDeclareType();
        final String getterDeclareType = fromProp.getGetterDeclareType();
        if (isTypeofAny(setterDeclareType, MutablePeriod.class, MutableDateTime.class, DateTime.class,
            LocalDateTime.class, LocalDate.class, LocalTime.class, Duration.class, YearMonth.class, Instant.class,
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
                t0 = warningAndIgnored(fromProp, toProp);
            }
            return t0;
        }
        return null;
    }

    /** long -> Date, Timestamp, java.sql.Date, Calendar */
    private String declare2Date(Mappable fromProp, Mappable toProp, PropertyAttr attr) {
        String t0;
        final String setterDeclareType = toProp.getSetterDeclareType();
        final String getterDeclareType = fromProp.getGetterDeclareType();
        if (isTypeofAny(setterDeclareType, Date.class, java.sql.Date.class, Timestamp.class)) {
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
                t0 = warningAndIgnored(fromProp, toProp);
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
                    "else { java.lang.Calendar {var} = java.lang.Calendar.getInstance();" +//
                    "    {var}.setTimeInMillis({var}.longValue());" +//
                    "{toName}.{setterName}({var});}";
            } else {
                t0 = warningAndIgnored(fromProp, toProp);
            }
            return t0;
        } else {
            return null;
        }
    }

    private String onDeclareCompleted(String t0, Mappable fromProp, Mappable toProp, String fromName, String toName) {
        if (t0 != null) {
            t0 = Replacer.fromName.replace(t0, fromName);
            t0 = Replacer.toName.replace(t0, toName);
            t0 = Replacer.setterName.replace(t0, toProp.getSetterName());
            t0 = Replacer.getterName.replace(t0, fromProp.getGetterName());
            if (t0.contains("{var}")) {
                t0 = Replacer.var.replace(t0, nextVarName());
            }
        }
        return t0;
    }

    private String warningAndIgnored(Mappable from, Mappable to) {
        String fromType = ElementUtils.getSimpleName(from.getThisClassname());
        String toType = ElementUtils.getSimpleName(to.getThisClassname());
        Object[] values = {
            toType, to.getSetterName(), to.getSetterFinalType(),//
            fromType, from.getGetterName(), from.getGetterFinalType()
        };
        Logger.onLevel(MANDATORY_WARNING, () -> StringUtils.format(true, TEMPLATE, values));
        this.warned = true;
        return null;
    }

    private final static String TEMPLATE = "【已忽略】Setter方法'{}#{}({})'不兼容'{}#{}()' 返回值类型: {}";

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

    private static boolean isSubTypeof(String actualType, Class<?> superClass) {
        return isSubTypeof(actualType, superClass.getCanonicalName());
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
}
