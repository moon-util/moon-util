package com.moon.mapping.processing;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static javax.tools.Diagnostic.Kind.MANDATORY_WARNING;

/**
 * @author benshaoye
 */
final class MapFieldFactory {

    private final static String LONG = "long";
    private final static String INT = "int";

    private boolean warned = false;

    public MapFieldFactory() {}

    public String doConvertField(Mappable fromProp, Mappable toProp, String fromName, String toName) {
        if (!fromProp.hasGetterMethod() || !toProp.hasSetterMethod()) {
            return "";
        }
        unWarned();
        String field;
        if (!fromProp.isGetterGenerify() && !toProp.isSetterGenerify()) {
            field = doConvertDeclaredField(fromProp, toProp, fromName, toName);
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
        } else if (fromProp.isSetterGenerify() && toProp.isGetterGenerify()) {
            final String setterTypeString = toProp.getSetterActualType();
            final String getterTypeString = fromProp.getGetterActualType();
            field = doConvertGenerify(fromProp, toProp, getterTypeString, setterTypeString);
        } else if (fromProp.isSetterGenerify()) {
            final String setterTypeString = toProp.getSetterActualType();
            final String getterTypeString = fromProp.getGetterDeclareType();
            field = doConvertGenerify(fromProp, toProp, getterTypeString, setterTypeString);
        } else if (toProp.isGetterGenerify()) {
            final String setterTypeString = toProp.getSetterDeclareType();
            final String getterTypeString = fromProp.getGetterActualType();
            field = doConvertGenerify(fromProp, toProp, getterTypeString, setterTypeString);
        } else {
            throw new IllegalStateException("This is impossible.");
        }
        return onDeclareCompleted(field, fromProp, toProp, fromName, toName);
    }

    private String doConvertGenerify(
        Mappable fromProp, Mappable toProp, String getterTypeString, String setterTypeString
    ) {
        Elements utils = EnvUtils.getUtils();
        TypeMirror setterType = utils.getTypeElement(setterTypeString).asType();
        TypeMirror getterType = utils.getTypeElement(getterTypeString).asType();
        if (EnvUtils.getTypes().isSubtype(getterType, setterType)) {
            String field = "{toName}.{setterName}({fromName}.{getterName}());";
            return Replacer.setterType.replace(field, setterTypeString);
        } else {
            return warningAndIgnored(fromProp, toProp);
        }
    }

    @SuppressWarnings("all")
    private String doConvertDeclaredField(Mappable fromProp, Mappable toProp, String fromName, String toName) {
        String t0 = null;
        final String setterDeclareType = toProp.getSetterDeclareType();
        final String getterDeclareType = fromProp.getGetterDeclareType();
        if (Objects.equals(setterDeclareType, getterDeclareType)) {
            t0 = "{toName}.{setterName}({fromName}.{getterName}());";
        }
        if (t0 == null) {
            t0 = declare2String(fromProp, toProp);
        }
        if (t0 == null) {
            t0 = declare2PrimitiveNumber(fromProp, toProp);
        }
        if (t0 == null) {
            t0 = declare2WrappedNumber(fromProp, toProp);
        }
        if (t0 == null) {
            t0 = declare2Boolean(fromProp, toProp);
        }
        if (t0 == null) {
            t0 = declare2Char(fromProp, toProp);
        }
        if (t0 == null) {
            t0 = declare2Date(fromProp, toProp);
        }
        if (t0 == null) {
            t0 = declare2BigInteger(fromProp, toProp);
        }
        if (t0 == null) {
            t0 = declare2BigDecimal(fromProp, toProp);
        }
        return onDeclareCompleted(t0, fromProp, toProp, fromName, toName);
    }

    private String declare2String(Mappable fromProp, Mappable toProp) {
        String t0;
        final String setterDeclareType = toProp.getSetterDeclareType();
        final String getterDeclareType = fromProp.getGetterDeclareType();
        if (isString(setterDeclareType)) {
            final String getterType = getterDeclareType;
            if (isString(getterType)) {
                t0 = "{toName}.{setterName}({fromName}.{getterName}());";
            } else if (StringUtils.isPrimitive(getterType)) {
                t0 = "{toName}.{setterName}(String.valueOf({fromName}.{getterName}()));";
            } else {
                t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}(null); }" +//
                    "else { {toName}.{setterName}({var}.toString()); }";
                t0 = Replacer.getterType.replace(t0, getterType);
            }
            return t0;
        } else {
            return null;
        }
    }

    private String declare2PrimitiveNumber(Mappable fromProp, Mappable toProp) {
        String t0;
        final String setterDeclareType = toProp.getSetterDeclareType();
        final String getterDeclareType = fromProp.getGetterDeclareType();
        if (StringUtils.isPrimitiveNumber(setterDeclareType)) {
            final String setterType = setterDeclareType;
            final String getterType = getterDeclareType;
            if (StringUtils.isPrimitiveNumber(getterType)) {
                if (isCompatible(setterType, getterType)) {
                    t0 = "{toName}.{setterName}({fromName}.{getterName}());";
                } else {
                    t0 = "{toName}.{setterName}(({setterType}){fromName}.{getterName}());";
                    t0 = Replacer.setterType.replace(t0, setterType);
                }
            } else if (StringUtils.isWrappedNumber(getterType)) {
                String getterWrapped = StringUtils.toWrappedType(setterType);
                t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                    "if ({var} != null) { {toName}.{setterName}({var}.{setterType}Value()); }";
                t0 = Replacer.getterType.replace(t0, getterWrapped);
                t0 = Replacer.setterType.replace(t0, setterType);
            } else if (StringUtils.isPrimitiveChar(getterType)) {
                if (isCompatible(setterType, INT)) {
                    t0 = "{toName}.{setterName}({fromName}.{getterName}());";
                } else {
                    t0 = "{toName}.{setterName}(({setterType}){fromName}.{getterName}());";
                    t0 = Replacer.setterType.replace(t0, setterType);
                }
            } else if (isString(getterType)) {
                t0 = "{toName}.{setterName}({type0}.parse{type1}({fromName}.{getterName}()));";
                String setterWrapped = StringUtils.toWrappedType(setterType);
                if (INT.equals(setterType)) {
                    t0 = Replacer.type0.replace(t0, setterWrapped);
                    t0 = Replacer.type1.replace(t0, "Int");
                } else {
                    t0 = Replacer.type0.replace(t0, setterWrapped);
                    t0 = Replacer.type1.replace(t0, setterWrapped);
                }
            } else if (isSubTypeof(getterType, Number.class)) {
                t0 = "Number {var} = {fromName}.{getterName}();" +//
                    "if ({var} != null) { {toName}.{setterName}({var}.{type0}Value()); }";
                t0 = Replacer.type0.replace(t0, setterType);
            } else if (LONG.equals(setterDeclareType)) {
                if (isSubTypeof(getterDeclareType, Date.class)) {
                    t0 = "java.util.Date {var} = {fromName}.{getterName}();" +//
                        "if ({var} != null) { {toName}.{setterName}({var}.getTime()); }";
                } else if (isSubTypeof(getterType, Calendar.class)) {
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

    private String declare2WrappedNumber(Mappable fromProp, Mappable toProp) {
        String t0;
        final String setterDeclareType = toProp.getSetterDeclareType();
        final String getterDeclareType = fromProp.getGetterDeclareType();
        if (StringUtils.isWrappedNumber(setterDeclareType)) {
            final String setterType = setterDeclareType;
            final String getterType = getterDeclareType;
            final String setterPrimitive = StringUtils.toPrimitiveType(setterType);
            if (StringUtils.isPrimitiveNumber(getterType)) {
                if (isCompatible(setterPrimitive, getterType)) {
                    t0 = "{toName}.{setterName}({fromName}.{getterName}());";
                } else {
                    t0 = "{toName}.{setterName}(({setterType}){fromName}.{getterName}());";
                    t0 = Replacer.setterType.replace(t0, setterPrimitive);
                }
            } else if (StringUtils.isWrappedNumber(getterType)) {
                if (Objects.equals(setterType, getterType)) {
                    t0 = "{toName}.{setterName}({fromName}.{getterName}());";
                } else {
                    t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                        "if ({var} == null) { {toName}.{setterName}(null); }" +//
                        "else { {toName}.{setterName}({var}.{type0}Value()); }";
                    t0 = Replacer.getterType.replace(t0, getterType);
                    t0 = Replacer.type0.replace(t0, setterPrimitive);
                }
            } else if (isString(getterType)) {
                t0 = "String {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}(null); }" +//
                    "else { {toName}.{setterName}({type0}.parse{type1}({var})); }";
                t0 = Replacer.type0.replace(t0, setterType);
                if (INT.equals(setterPrimitive)) {
                    t0 = Replacer.type1.replace(t0, "Int");
                } else {
                    t0 = Replacer.type1.replace(t0, setterType);
                }
            } else if (StringUtils.isPrimitiveChar(getterType)) {
                if (isCompatible(setterPrimitive, INT)) {
                    t0 = "{toName}.{setterName}({fromName}.{getterName}());";
                } else {
                    t0 = "{toName}.{setterName}(({type0}) {fromName}.{getterName}());";
                    t0 = Replacer.type0.replace(t0, setterPrimitive);
                }
            } else if (Character.class.getName().equals(getterType)) {
                t0 = "Character {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}(null); }" +//
                    "else { {toName}.{setterName}(({setterType}) {var}.charValue()); }";
                t0 = Replacer.setterType.replace(t0, setterPrimitive);
            } else if (isSubTypeof(getterType, Number.class)) {
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

    private String declare2Boolean(Mappable fromProp, Mappable toProp) {
        String t0;
        final String setterDeclareType = toProp.getSetterDeclareType();
        final String getterDeclareType = fromProp.getGetterDeclareType();
        if (StringUtils.isPrimitiveBoolean(setterDeclareType)) {
            final String getterType = getterDeclareType;
            if (isString(getterType)) {
                t0 = "{toName}.{setterName}(Boolean.parseBoolean({fromName}.{getterName}()));";
            } else if (StringUtils.isPrimitiveNumber(getterType)) {
                t0 = "{toName}.{setterName}({fromName}.getterName() != 0);";
            } else if (StringUtils.isWrappedNumber(getterType)) {
                t0 = "{toName}.{setterName}(Integer.valueOf(0).equals({fromName}.getterName()));";
            } else if (StringUtils.isPrimitiveBoolean(getterType)) {
                t0 = "{toName}.{setterName}({fromName}.{getterName}());";
            } else if (Boolean.class.getName().equals(getterType)) {
                t0 = "Boolean {var} = {fromName}.{getterName}();" +//
                    "if ({var} != null) { {toName}.{setterName}({var}); }";
            } else {
                t0 = warningAndIgnored(fromProp, toProp);
            }
        } else if (Boolean.class.getName().equals(setterDeclareType)) {
            final String getterType = getterDeclareType;
            if (isString(getterType)) {
                t0 = "{toName}.{setterName}(Boolean.parseBoolean({fromName}.{getterName}()));";
            } else if (StringUtils.isPrimitiveNumber(getterType)) {
                t0 = "{toName}.{setterName}({fromName}.getterName() != 0);";
            } else if (StringUtils.isWrappedNumber(getterType)) {
                t0 = "{toName}.{setterName}(Integer.valueOf(0).equals({fromName}.getterName()));";
            } else if (StringUtils.isPrimitiveBoolean(getterType) || Boolean.class.getName().equals(getterType)) {
                t0 = "{toName}.{setterName}({fromName}.{getterName}());";
            } else {
                t0 = warningAndIgnored(fromProp, toProp);
            }
        } else {
            return null;
        }
        return t0;
    }

    private String declare2Char(Mappable fromProp, Mappable toProp) {
        String t0;
        final String setterDeclareType = toProp.getSetterDeclareType();
        final String getterDeclareType = fromProp.getGetterDeclareType();
        if (StringUtils.isPrimitiveChar(setterDeclareType)) {
            final String getterType = getterDeclareType;
            if (StringUtils.isPrimitiveChar(getterType)) {
                t0 = "{toName}.{setterName}({fromName}.{getterName}());";
            } else if (Character.class.getName().equals(getterType)) {
                t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                    "if ({var} != null) { {toName}.{setterName}({var}); }";
                t0 = Replacer.getterType.replace(t0, getterType);
            } else if (StringUtils.isPrimitiveNumber(getterType)) {
                if (isCompatible(INT, getterType)) {
                    t0 = "{toName}.{setterName}(Character.forDigit({fromName}.{getterName}(), 10));";
                } else {
                    t0 = "{toName}.{setterName}(Character.forDigit((int){fromName}.{getterName}(), 10));";
                }
            } else if (StringUtils.isWrappedNumber(getterType)) {
                t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                    "if ({var} != null) { {toName}.{setterName}(Character.forDigit({var}.intValue(), 10)); }";
                t0 = Replacer.getterType.replace(t0, getterType);
            } else {
                t0 = warningAndIgnored(fromProp, toProp);
            }
        } else if (Character.class.getName().equals(setterDeclareType)) {
            final String getterType = getterDeclareType;
            if (StringUtils.isPrimitiveChar(getterType) || Character.class.getName().equals(getterType)) {
                t0 = "{toName}.{setterName}({fromName}.{getterName}());";
            } else if (StringUtils.isPrimitiveNumber(getterType)) {
                if (isCompatible(INT, getterType)) {
                    t0 = "{toName}.{setterName}(Character.forDigit({fromName}.{getterName}(), 10));";
                } else {
                    t0 = "{toName}.{setterName}(Character.forDigit((int){fromName}.{getterName}(), 10));";
                }
            } else if (StringUtils.isWrappedNumber(getterType)) {
                t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
                    "if ({var} != null) { {toName}.{setterName}(Character.forDigit({var}.intValue(), 10)); }" +//
                    "else { {toName}.{setterName}(null); }";
                t0 = Replacer.getterType.replace(t0, getterType);
            } else {
                t0 = warningAndIgnored(fromProp, toProp);
            }
        } else {
            return null;
        }
        return t0;
    }

    private String declare2BigDecimal(Mappable fromProp, Mappable toProp) {
        String t0;
        final String setterDeclareType = toProp.getSetterDeclareType();
        final String getterDeclareType = fromProp.getGetterDeclareType();
        if (isTypeof(setterDeclareType, BigDecimal.class)) {
            if (StringUtils.isPrimitiveNumber(getterDeclareType)) {
                t0 = "{toName}.{setterName}({setterType}.valueOf({fromName}.{getterName}()));";
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
            } else if (StringUtils.isWrappedNumber(getterDeclareType)) {
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

    private String declare2BigInteger(Mappable fromProp, Mappable toProp) {
        String t0;
        final String setterDeclareType = toProp.getSetterDeclareType();
        final String getterDeclareType = fromProp.getGetterDeclareType();
        if (isTypeof(setterDeclareType, BigInteger.class)) {
            if (StringUtils.isPrimitiveNumber(getterDeclareType)) {
                String getterPrimitive = StringUtils.toPrimitiveType(getterDeclareType);
                if (isCompatible(LONG, getterPrimitive)) {
                    t0 = "{toName}.{setterName}({setterType}.valueOf({fromName}.{getterName}()));";
                } else {
                    t0 = "{toName}.{setterName}({setterType}.valueOf((long){fromName}.{getterName}()));";
                }
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
            } else if (StringUtils.isWrappedNumber(getterDeclareType)) {
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

    private String declare2Date(Mappable fromProp, Mappable toProp) {
        String t0;
        final String setterDeclareType = toProp.getSetterDeclareType();
        final String getterDeclareType = fromProp.getGetterDeclareType();
        if (isTypeofAny(setterDeclareType, Date.class, java.sql.Date.class, Timestamp.class)) {
            if (StringUtils.isPrimitiveNumber(getterDeclareType)) {
                if (isCompatible(LONG, getterDeclareType)) {
                    t0 = "{toName}.{setterName}(new {setterType}({fromName}.{getterName}()));";
                } else {
                    t0 = "{toName}.{setterName}(new {setterType}((long){fromName}.{getterName}()));";
                }
            } else if (isSubTypeof(getterDeclareType, Number.class)) {
                t0 = "Number {var} = {fromName}.{getterName}();" +//
                    "if ({var} == null) { {toName}.{setterName}(null); }" +//
                    "else { {toName}.{setterName}(new {setterType}({var}.longValue())); }";
                t0 = Replacer.setterType.replace(t0, setterDeclareType);
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
                t0 = Replacer.var.replace(t0, nextVarname());
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

    private final String nextVarname() {
        return nextVarname(getIndexer());
    }

    private boolean isWarned() { return warned; }

    private void unWarned() { this.warned = false; }

    private final static String nextVarname(AtomicInteger indexer) {
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

    private static boolean isTypeofAny(String actual, Class... expected) {
        for (Class aClass : expected) {
            if (isTypeof(actual, aClass)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isTypeof(String actual, Class expected) {
        return expected.getCanonicalName().equals(actual);
    }

    private static boolean isSubTypeof(String actualType, Class superClass) {
        return isSubTypeof(actualType, superClass.getCanonicalName());
    }

    private static boolean isSubTypeof(String actualType, String superClass) {
        Elements utils = EnvUtils.getUtils();
        TypeMirror actual = utils.getTypeElement(actualType).asType();
        TypeMirror top = utils.getTypeElement(superClass).asType();
        return EnvUtils.getTypes().isSubtype(actual, top);
    }
}
