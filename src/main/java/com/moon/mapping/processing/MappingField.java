package com.moon.mapping.processing;

import org.joda.time.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import static com.moon.mapping.processing.DetectUtils.*;
import static com.moon.mapping.processing.ElemUtils.getSimpleName;
import static com.moon.mapping.processing.StringUtils.*;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
final class MappingField {

    private final static Map<Predicate<String>, Class<? extends MappingBuilder>> MAPPERS = new HashMap<>();

    private final static String DOUBLE = "double";
    private final static String LONG = "long";
    private final static String INT = "int";

    static {
        MAPPERS.put(DetectUtils::isEnum, ToEnum.class);

        MAPPERS.put(MappingField::isJdk8Date, ToJdk8Time.class);
        MAPPERS.put(MappingField::isJodaSpecialDate, ToJodaTime.class);

        MAPPERS.put(StringUtils::isWrappedNumber, ToWrappedNumber.class);
        MAPPERS.put(StringUtils::isPrimitiveNumber, ToPrimitiveNumber.class);

        MAPPERS.put(type -> isTypeof(type, String.class), ToString.class);
        MAPPERS.put(type -> isTypeof(type, Number.class), ToNumber.class);
        MAPPERS.put(type -> isTypeof(type, Calendar.class), ToCalendar.class);
        MAPPERS.put(type -> isTypeof(type, BigInteger.class), ToBigInteger.class);
        MAPPERS.put(type -> isTypeof(type, BigDecimal.class), ToBigDecimal.class);

        MAPPERS.put(type -> isSubtypeOf(type, Date.class), ToUtilDate.class);
        MAPPERS.put(type -> isSubtypeOf(type, Calendar.class), ToCalendar.class);
        MAPPERS.put(type -> isSubtypeOf(type, TemporalAccessor.class), ToJdk8TimeByString.class);

        MAPPERS.put(type -> isTypeofAny(type, long.class, Long.class), DateToLong.class);
        MAPPERS.put(type -> isTypeofAny(type, double.class, Double.class), DateToDouble.class);
        MAPPERS.put(type -> isTypeofAny(type, char.class, Character.class), ToCharValue.class);
        MAPPERS.put(type -> isTypeofAny(type, boolean.class, Boolean.class), ToBoolean.class);
    }

    public static String doMapping(Manager manager) {
        String mapping = doMappingIfAssignable(manager);
        if (mapping != null) {
            return mapping;
        }
        for (Map.Entry<Predicate<String>, Class<? extends MappingBuilder>> clsEntry : MAPPERS.entrySet()) {
            String setterType = getSetterType(manager);
            if (clsEntry.getKey().test(setterType)) {
                for (MappingBuilder builder : clsEntry.getValue().getEnumConstants()) {
                    if (builder.support(manager)) {
                        return builder.doMapping(manager);
                    }
                }
            }
        }
        return null;
    }

    private static String doMappingIfAssignable(Manager manager) {
        // 转换优先级顺序: setter 转换器、getter 转换器、内置默认转换器
        String template = "{toName}.{setterName}({fromName}.{getterName}());";
        String convertMethod = manager.findConverterMethod();
        if (convertMethod != null) {
            return Replacer.setterName.replace(template, convertMethod);
        }
        String provideMethod = manager.findProviderMethod();
        if (provideMethod != null) {
            return Replacer.getterName.replace(template, provideMethod);
        }

        String setterType = getSetterType(manager);
        String getterType = getGetterType(manager);
        PropertyModel model = manager.getModel();
        PropertyAttr attr = model.getAttr();
        String fmt = attr.formatValue(), dft = attr.defaultValue();

        if (fmt == null && dft == null && isSubtypeOf(getterType, setterType)) {
            return manager.getMapping().doMap(null, null);
        }
        return null;
    }

    private enum ToCalendar implements MappingBuilder {
        fromString {
            @Override
            public boolean support(Manager manager) {
                return isGetterTypeOf(manager, String.class);
            }

            @Override
            public String doMapping(Manager manager) {
                MappingManager mapping = manager.getMapping();
                CallerManager transfer = manager.getCaller();
                String returnType = Calendar.class.getCanonicalName();
                String fmt = manager.getFormatPatternVal(returnType, true);
                if (fmt == null) {
                    CallerInfo info = transfer.findInAll(returnType, String.class);
                    return mapping.doMap(info.toString(), null);
                } else {
                    CallerInfo info = transfer.findInAll(returnType, String.class, String.class);
                    String mapper = info.toString(null, strWrapped(fmt));
                    return mapping.doMap(mapper, asNull(manager, null));
                }
            }
        },
        fromPrimitiveNumber {
            @Override
            public boolean support(Manager manager) {
                return isPrimitiveNumber(getGetterType(manager));
            }

            @Override
            public String doMapping(Manager manager) {
                MappingManager mapping = manager.getMapping();
                String getterType = mapping.getGetterType();
                String setterType = mapping.getSetterType();
                String cast = isPrimitiveLt(getterType, LONG) ? LONG : DOUBLE;
                CallerManager transfer = manager.getCaller();
                CallerInfo info = transfer.findInAll(setterType, cast);
                boolean mandatory = manager.getModel().isSetterGeneric();
                return mapping.get(setterType, cast, info.toString(), null, mandatory);
            }
        },
        fromNumber {
            @Override
            public boolean support(Manager mgr) {
                return isGetterSubtypeOf(mgr, Number.class);
            }

            @Override
            public String doMapping(Manager mgr) {
                MappingManager mapping = mgr.getMapping();
                CallerManager transfer = mgr.getCaller();
                CallerInfo info = transfer.findInAll(Calendar.class, Number.class);
                return mapping.doMap(info.toString(), asNull(mgr, null));
            }
        },
        fromUtilDate {
            @Override
            public boolean support(Manager mgr) {
                return isGetterSubtypeOf(mgr, Date.class);
            }

            @Override
            public String doMapping(Manager mgr) {
                MappingManager mapping = mgr.getMapping();
                CallerManager transfer = mgr.getCaller();
                CallerInfo info = transfer.findInAll(Calendar.class, Date.class);
                return mapping.doMap(info.toString(), asNull(mgr, null));
            }
        },
        fromJdk8Date {
            @Override
            public boolean support(Manager manager) {
                return onGetterType(manager, MappingField::isJdk8Date);
            }

            @Override
            public String doMapping(Manager mgr) {
                MappingManager mapping = mgr.getMapping();
                CallerManager transfer = mgr.getCaller();
                String getterType = getGetterType(mgr);
                String returnType = Calendar.class.getCanonicalName();
                CallerInfo info = transfer.findInAll(returnType, getterType);
                return mapping.doMap(info.toString(), asNull(mgr, null));
            }
        },
        fromJodaReadableInstant {
            @Override
            public boolean support(Manager manager) {
                if (Imported.JODA_TIME) {
                    return isGetterSubtypeOf(manager, ReadableInstant.class);
                }
                return false;
            }

            @Override
            public String doMapping(Manager mgr) {
                CallerManager transfer = mgr.getCaller();
                CallerInfo info = transfer.findInAll(Calendar.class, ReadableInstant.class);
                return mgr.getMapping().doMap(info.toString(), asNull(mgr, null));
            }
        },
        fromJodaDate {
            @Override
            public boolean support(Manager manager) {
                if (Imported.JODA_TIME) {
                    String getterType = getGetterType(manager);
                    return isTypeofAny(getterType, LocalDateTime.class, LocalDate.class);
                }
                return false;
            }

            @Override
            public String doMapping(Manager mgr) {
                MappingManager mapping = mgr.getMapping();
                String getterType = mapping.getGetterType();
                String returnType = Calendar.class.getCanonicalName();
                CallerManager transfer = mgr.getCaller();
                CallerInfo info = transfer.findInAll(returnType, getterType);
                return mgr.getMapping().doMap(info.toString(), asNull(mgr, null));
            }
        }
    }

    private enum ToUtilDate implements MappingBuilder {
        fromString {
            @Override
            public boolean support(Manager manager) {
                return isGetterTypeOf(manager, String.class);
            }

            @Override
            public String doMapping(Manager manager) {
                MappingManager mapping = manager.getMapping();
                CallerManager transfer = manager.getCaller();
                String getterType = mapping.getGetterType();
                String setterType = mapping.getSetterType();
                String pattern = manager.getFormatPatternVal(getterType, true);
                if (pattern == null) {
                    CallerInfo info = transfer.findInAll(setterType, String.class);
                    return mapping.doMap(info.toString(), null);
                } else {
                    CallerInfo info = transfer.findInAll(setterType, String.class, String.class);
                    return mapping.doMap(info.toString(null, strWrapped(pattern)), asNull(manager, null));
                }
            }
        },
        fromPrimitiveNumber {
            @Override
            public boolean support(Manager manager) {
                return isPrimitiveNumber(getGetterType(manager));
            }

            @Override
            public String doMapping(Manager manager) {
                MappingManager mapping = manager.getMapping();
                String getterType = mapping.getGetterType();
                String setterType = mapping.getSetterType();
                String cast = isPrimitiveLt(getterType, LONG) ? LONG : DOUBLE;
                CallerManager transfer = manager.getCaller();
                CallerInfo info = transfer.findInAll(setterType, cast);
                boolean mandatory = manager.getModel().isSetterGeneric();
                return mapping.get(setterType, cast, info.toString(), null, mandatory);
            }
        },
        fromNumber {
            @Override
            public boolean support(Manager mgr) {
                return isGetterSubtypeOf(mgr, Number.class);
            }

            @Override
            public String doMapping(Manager mgr) {
                MappingManager mapping = mgr.getMapping();
                String setterType = mapping.getSetterType();
                CallerManager transfer = mgr.getCaller();
                CallerInfo info = transfer.findInAll(setterType, Number.class);
                return mapping.doMap(info.toString(), asNull(mgr, null));
            }
        },
        fromCalendar {
            @Override
            public boolean support(Manager mgr) {
                return isGetterSubtypeOf(mgr, Calendar.class);
            }

            @Override
            public String doMapping(Manager mgr) {
                MappingManager mapping = mgr.getMapping();
                String setterType = mapping.getSetterType();
                CallerManager transfer = mgr.getCaller();
                CallerInfo info = transfer.findInAll(setterType, Calendar.class);
                return mapping.doMap(info.toString(), asNull(mgr, null));
            }
        },
        fromJdk8Date {
            @Override
            public boolean support(Manager manager) {
                return onGetterType(manager, MappingField::isJdk8Date);
            }

            @Override
            public String doMapping(Manager mgr) {
                MappingManager mapping = mgr.getMapping();
                CallerManager transfer = mgr.getCaller();
                CallerInfo info = getBasicTransfer(mgr);
                return mapping.doMap(info.toString(), asNull(mgr, null));
            }
        },
        fromJodaReadableInstant {
            @Override
            public boolean support(Manager manager) {
                if (Imported.JODA_TIME) {
                    return isGetterSubtypeOf(manager, ReadableInstant.class);
                }
                return false;
            }

            @Override
            public String doMapping(Manager mgr) {
                CallerManager transfer = mgr.getCaller();
                String setterType = getSetterType(mgr);
                CallerInfo info = transfer.findInAll(setterType, ReadableInstant.class);
                return mgr.getMapping().doMap(info.toString(), asNull(mgr, null));
            }
        },
        fromJodaDate {
            @Override
            public boolean support(Manager manager) {
                if (Imported.JODA_TIME) {
                    String getterType = getGetterType(manager);
                    return isTypeofAny(getterType, LocalDateTime.class, LocalDate.class);
                }
                return false;
            }

            @Override
            public String doMapping(Manager mgr) {
                MappingManager mapping = mgr.getMapping();
                CallerManager transfer = mgr.getCaller();
                CallerInfo info = getBasicTransfer(mgr);
                return mgr.getMapping().doMap(info.toString(), asNull(mgr, null));
            }
        }
    }

    private enum ToJdk8TimeByString implements MappingBuilder {
        fromString {
            @Override
            public boolean support(Manager manager) {
                return isGetterTypeOf(manager, String.class);
            }

            @Override
            public String doMapping(Manager manager) {
                MappingManager mapping = manager.getMapping();
                String getterType = getGetterType(manager);
                String pattern = manager.getFormatPatternVal(getterType, true);
                if (pattern == null) {
                    return mapping.doMap("{setterType}.parse({var})", asNull(manager, null));
                } else {
                    String format = manager.staticForDateTimeFormatter(pattern);
                    String mapper = "{setterType}.from({format}.parse({var}))";
                    mapper = Replacer.format.replace(mapper, format);
                    return mapping.doMap(mapper, asNull(manager, null));
                }
            }
        }
    }

    private enum ToJdk8Time implements MappingBuilder {
        fromPrimitiveNumber {
            @Override
            public boolean support(Manager manager) {
                return isPrimitiveNumber(getGetterType(manager));
            }

            @Override
            public String doMapping(Manager manager) {
                MappingManager mapping = manager.getMapping();
                String setterType = getSetterType(manager);
                String getterType = getGetterType(manager);
                String byType = isPrimitiveGt(LONG, getterType) ? LONG : DOUBLE;
                CallerManager transfer = manager.getCaller();
                CallerInfo info = transfer.findInAll(setterType, byType);
                return mapping.doMap(info.toString(), null);
            }
        },
        fromJdk8Date {
            @Override
            public boolean support(Manager manager) {
                return getBasicTransfer(manager) != null;
            }

            @Override
            public String doMapping(Manager manager) {
                MappingManager mapping = manager.getMapping();
                return mapping.doMap(getBasicTransfer(manager).toString(), asNull(manager, null));
            }
        },
        fromNumber {
            @Override
            public boolean support(Manager manager) {
                return isSubtypeOf(getGetterType(manager), Number.class);
            }

            @Override
            public String doMapping(Manager manager) {
                MappingManager mapping = manager.getMapping();
                CallerManager transfer = manager.getCaller();
                String setterType = getSetterType(manager);
                CallerInfo info = transfer.findInAll(setterType, Number.class);
                return mapping.doMap(info.toString(), asNull(manager, null));
            }
        },
        fromUtilDate {
            @Override
            public boolean support(Manager manager) {
                return isSubtypeOf(getGetterType(manager), Date.class);
            }

            @Override
            public String doMapping(Manager manager) {
                MappingManager mapping = manager.getMapping();
                CallerManager transfer = manager.getCaller();
                String setterType = getSetterType(manager);
                CallerInfo info = transfer.findInAll(setterType, Date.class);
                return mapping.doMap(info.toString(), asNull(manager, null));
            }
        },
        fromCalendar {
            @Override
            public boolean support(Manager manager) {
                return isSubtypeOf(getGetterType(manager), Calendar.class);
            }

            @Override
            public String doMapping(Manager manager) {
                MappingManager mapping = manager.getMapping();
                CallerManager transfer = manager.getCaller();
                String setterType = getSetterType(manager);
                CallerInfo info = transfer.findInAll(setterType, Calendar.class);
                return mapping.doMap(info.toString(), asNull(manager, null));
            }
        },
        fromJodaReadableInstant {
            @Override
            public boolean support(Manager manager) {
                return Imported.JODA_TIME && isSubtypeOf(getGetterType(manager), ReadableInstant.class);
            }

            @Override
            public String doMapping(Manager manager) {
                MappingManager mapping = manager.getMapping();
                CallerManager transfer = manager.getCaller();
                String setterType = getSetterType(manager);
                CallerInfo info = transfer.findInAll(setterType, ReadableInstant.class);
                return mapping.doMap(info.toString(), asNull(manager, null));
            }
        },
        fromJodaDate {
            @Override
            public boolean support(Manager manager) {
                return Imported.JODA_TIME && isTypeofAny(getGetterType(manager), LocalDateTime.class, LocalDate.class);
            }

            @Override
            public String doMapping(Manager manager) {
                MappingManager mapping = manager.getMapping();
                CallerManager transfer = manager.getCaller();
                CallerInfo info = getBasicTransfer(manager);
                return mapping.doMap(info.toString(), asNull(manager, null));
            }
        }
    }

    private enum ToJodaTime implements MappingBuilder {
        fromString {
            @Override
            public boolean support(Manager manager) {
                return isGetterTypeOf(manager, String.class);
            }

            @Override
            public String doMapping(Manager manager) {
                MappingManager mapping = manager.getMapping();
                String getterType = getGetterType(manager);
                String pattern = manager.getFormatPatternVal(getterType, true);
                if (pattern == null) {
                    String mapper = "{setterType}.parse({var})";
                    return mapping.doMap(mapper, asNull(manager, null));
                } else {
                    String fmt = manager.staticForJodaDateTimeFormatter(pattern);
                    String tx = "{setterType}.parse({var}, {format})";
                    String mapper = Replacer.format.replace(tx, fmt);
                    return mapping.doMap(mapper, asNull(manager, null));
                }
            }
        },
        fromNumber {
            @Override
            public boolean support(Manager manager) {
                return isSubtypeOf(getGetterType(manager), Number.class);
            }

            @Override
            public String doMapping(Manager manager) {
                String mapper = "new {setterType}({var}.longValue())";
                return manager.getMapping().doMap(mapper, asNull(manager, null));
            }
        },
        fromPrimitiveNumber {
            @Override
            public boolean support(Manager manager) {
                return isPrimitiveNumber(getGetterType(manager));
            }

            @Override
            public String doMapping(Manager manager) {
                String getterType = getGetterType(manager);
                String cast = isPrimitiveGt(getterType, LONG) ? bracketed(LONG) : "";
                String mapper = "new {setterType}({cast}{var})";
                mapper = Replacer.cast.replace(mapper, cast);
                return manager.getMapping().doMap(mapper, null);
            }
        },
        fromUtilDate {
            @Override
            public boolean support(Manager manager) {
                return isSubtypeOf(getGetterType(manager), Date.class);
            }

            @Override
            public String doMapping(Manager manager) {
                String mapper = "new {setterType}({var})";
                return manager.getMapping().doMap(mapper, asNull(manager, null));
            }
        },
        fromCalendar {
            @Override
            public boolean support(Manager manager) {
                return isSubtypeOf(getGetterType(manager), Calendar.class);
            }

            @Override
            public String doMapping(Manager manager) {
                String mapper = "new {setterType}({var}.getTime())";
                return manager.getMapping().doMap(mapper, asNull(manager, null));
            }
        }
    }

    private enum ToBigInteger implements MappingBuilder {
        fromBigInteger {
            @Override
            public boolean support(Manager manager) {
                return isGetterTypeOf(manager, BigInteger.class);
            }

            @Override
            public String doMapping(Manager manager) {
                String var = manager.staticForDefaultBigInteger();
                return manager.getMapping().doMap(null, asNull(manager, var));
            }
        },
        fromPrimitiveNumber {
            @Override
            public boolean support(Manager manager) {
                return isPrimitiveNumber(getGetterType(manager));
            }

            @Override
            public String doMapping(Manager manager) {
                String getterType = getGetterType(manager);
                String var = manager.staticForDefaultBigInteger();
                String cast = isPrimitiveGt(getterType, LONG) ? bracketed(LONG) : "";
                String mapper = "{setterType}.valueOf({cast}{var})";
                mapper = Replacer.cast.replace(mapper, cast);
                return manager.getMapping().doMap(mapper, var);
            }
        },
        fromEnum {
            @Override
            public boolean support(Manager manager) {
                return isEnum(getGetterType(manager));
            }

            @Override
            public String doMapping(Manager manager) {
                String var = manager.staticForDefaultBigInteger();
                String mapper = "{setterType}.valueOf({var}.ordinal())";
                return manager.getMapping().doMap(mapper, asNull(manager, var));
            }
        },
        fromNumber {
            @Override
            public boolean support(Manager manager) {
                return isSubtypeOf(getGetterType(manager), Number.class);
            }

            @Override
            public String doMapping(Manager manager) {
                String var = manager.staticForDefaultBigInteger();
                String mapper = "{setterType}.valueOf({var}.longValue())";
                return manager.getMapping().doMap(mapper, asNull(manager, var));
            }
        },
        fromString {
            @Override
            public boolean support(Manager manager) {
                return isGetterTypeOf(manager, String.class);
            }

            @Override
            public String doMapping(Manager manager) {
                String getterType = getGetterType(manager);
                String pattern = manager.getFormatPatternVal(getterType, false);
                String var = manager.staticForDefaultBigInteger();
                MappingManager mapping = manager.getMapping();
                if (pattern == null) {
                    return mapping.doMap("new {setterType}({var})", asNull(manager, var));
                } else {
                    CallerManager transfer = manager.getCaller();
                    CallerInfo info = transfer.findInAll(BigInteger.class, String.class, String.class);
                    return mapping.doMap(info.toString(null, strWrapped(pattern)), asNull(manager, var));
                }
            }
        }
    }

    private enum ToBigDecimal implements MappingBuilder {
        fromBigDecimal {
            @Override
            public boolean support(Manager manager) {
                return isGetterTypeOf(manager, BigDecimal.class);
            }

            @Override
            public String doMapping(Manager manager) {
                String var = manager.staticForDefaultBigDecimal();
                return manager.getMapping().doMap(null, var);
            }
        },
        fromBigInteger {
            @Override
            public boolean support(Manager manager) {
                return isGetterTypeOf(manager, BigInteger.class);
            }

            @Override
            public String doMapping(Manager manager) {
                MappingManager mapping = manager.getMapping();
                String var = manager.staticForDefaultBigDecimal();
                return mapping.doMap("new {setterType}({var})", asNull(manager, var));
            }
        },
        fromEnum {
            @Override
            public boolean support(Manager manager) {
                return isEnum(getGetterType(manager));
            }

            @Override
            public String doMapping(Manager manager) {
                String var = manager.staticForDefaultBigDecimal();
                String mapper = "{setterType}.valueOf({var}.ordinal())";
                return manager.getMapping().doMap(mapper, asNull(manager, var));
            }
        },
        fromPrimitiveNumber {
            @Override
            public boolean support(Manager manager) {
                return isPrimitiveNumber(getGetterType(manager));
            }

            @Override
            public String doMapping(Manager manager) {
                MappingManager mapping = manager.getMapping();
                String var = manager.staticForDefaultBigDecimal();
                return mapping.doMap("{setterType}.valueOf({var})", var);
            }
        },
        fromWrappedNumber {
            @Override
            public boolean support(Manager manager) {
                return isWrappedNumber(getGetterType(manager));
            }

            @Override
            public String doMapping(Manager manager) {
                String getterType = getGetterType(manager);
                String mapper;
                if (isTypeofAny(getterType, Float.class, Double.class)) {
                    mapper = "{setterType}.valueOf({var}.doubleValue())";
                } else {
                    mapper = "{setterType}.valueOf({var}.longValue())";
                }
                String var = manager.staticForDefaultBigDecimal();
                MappingManager mapping = manager.getMapping();
                return mapping.doMap(mapper, asNull(manager, var));
            }
        },
        fromNumber {
            @Override
            public boolean support(Manager manager) {
                return isSubtypeOf(getGetterType(manager), Number.class);
            }

            @Override
            public String doMapping(Manager manager) {
                CallerManager transfer = manager.getCaller();
                CallerInfo info = transfer.findInAll(BigDecimal.class, Number.class);
                String var = manager.staticForDefaultBigDecimal();
                MappingManager mapping = manager.getMapping();
                return mapping.doMap(info.toString(), asNull(manager, var));
            }
        },
        fromString {
            @Override
            public boolean support(Manager manager) {
                return isGetterTypeOf(manager, String.class);
            }

            @Override
            public String doMapping(Manager manager) {
                String getterType = getGetterType(manager);
                String pattern = manager.getFormatPatternVal(getterType, false);
                String var = manager.staticForDefaultBigDecimal();
                MappingManager mapping = manager.getMapping();
                if (pattern == null) {
                    return mapping.doMap("new {setterType}({var})", asNull(manager, var));
                } else {
                    CallerManager transfer = manager.getCaller();
                    CallerInfo info = transfer.findInAll(BigDecimal.class, String.class, String.class);
                    return mapping.doMap(info.toString(null, strWrapped(pattern)), asNull(manager, var));
                }
            }
        }
    }

    private enum ToCharValue implements MappingBuilder {
        fromPrimitiveChar {
            @Override
            public boolean support(Manager manager) {
                return isPrimitiveChar(getGetterType(manager));
            }

            @Override
            public String doMapping(Manager manager) {
                return manager.getMapping().doMap(null, null);
            }
        },
        fromCharacter {
            @Override
            public boolean support(Manager manager) {
                return isTypeof(getGetterType(manager), Character.class);
            }

            @Override
            public String doMapping(Manager manager) {
                final String dftValue = manager.staticForDefaultChar();
                return manager.getMapping().doMap(null, dftValue);
            }
        },
        fromPrimitiveNumber {
            @Override
            public boolean support(Manager manager) {
                return isPrimitiveNumber(getGetterType(manager));
            }

            @Override
            public String doMapping(Manager manager) {
                String getterType = getGetterType(manager);
                String t0 = "{type0}.forDigit({cast}{var}, 10)";
                String cast = isPrimitiveGt(INT, getterType) ? "" : "(int)";
                String type0 = manager.onImported(Character.class);
                t0 = Replacer.cast.replace(t0, cast);
                t0 = Replacer.type0.replace(t0, type0);
                return manager.getMapping().doMap(t0, null);
            }
        },
        fromNumber {
            @Override
            public boolean support(Manager manager) {
                return isSubtypeOf(getGetterType(manager), Number.class);
            }

            @Override
            public String doMapping(Manager manager) {
                String t0 = "{type0}.forDigit({var}.intValue(), 10)";
                String type0 = manager.onImported(Character.class);
                t0 = Replacer.type0.replace(t0, type0);
                return manager.getMapping().doMap(t0, null);
            }
        }
    }

    private enum ToBoolean implements MappingBuilder {
        fromPrimitiveNumber {
            @Override
            public boolean support(Manager manager) {
                return isPrimitiveNumber(getGetterType(manager));
            }

            @Override
            public String doMapping(Manager manager) {
                return manager.getMapping().doMap("{var} != 0", null);
            }
        },
        fromPrimitiveBoolean {
            @Override
            public boolean support(Manager manager) {
                return isPrimitiveBoolean(getGetterType(manager));
            }

            @Override
            public String doMapping(Manager manager) {
                return manager.getMapping().doMap("{var}", null);
            }
        },
        fromString {
            @Override
            public boolean support(Manager manager) {
                return isGetterTypeOf(manager, String.class);
            }

            @Override
            public String doMapping(Manager manager) {
                String booleanWrapped = Boolean.class.getCanonicalName();
                String bool = manager.onImported(booleanWrapped);
                String var = manager.staticForDefaultBoolean();
                String mapper = "{type0}.parseBoolean({var})";
                mapper = Replacer.type0.replace(mapper, bool);
                return manager.getMapping().doMap(mapper, var);
            }
        },
        fromWrappedNumber {
            @Override
            public boolean support(Manager manager) {
                return isWrappedNumber(getGetterType(manager));
            }

            @Override
            public String doMapping(Manager manager) {
                String getterType = getGetterType(manager);
                String staticVar = manager.staticForDefaultNumberValueOf(getterType, "0");
                String mapper = "{name}.equals({var})";
                String var = manager.staticForDefaultBoolean();
                mapper = Replacer.name.replace(mapper, staticVar);
                return manager.getMapping().doMap(mapper, var);
            }
        },
        fromBoolean {
            @Override
            public boolean support(Manager manager) {
                return isGetterTypeOf(manager, Boolean.class);
            }

            @Override
            public String doMapping(Manager manager) {
                String var = manager.staticForDefaultBoolean();
                return manager.getMapping().doMap("{var}", var);
            }
        }
    }

    private enum ToNumber implements MappingBuilder {
        fromPrimitiveNumber {
            @Override
            public boolean support(Manager manager) {
                return isPrimitiveNumber(getGetterType(manager));
            }

            @Override
            public String doMapping(Manager manager) {
                return manager.getMapping().doMap(null, null);
            }
        },
        fromPrimitiveChar {
            @Override
            public boolean support(Manager manager) {
                return isPrimitiveChar(getGetterType(manager));
            }

            @Override
            public String doMapping(Manager manager) {
                return manager.getMapping().doMap("(int){var}", null);
            }
        },
        fromNumber {
            @Override
            public boolean support(Manager manager) {
                return isSubtypeOf(getGetterType(manager), Number.class);
            }

            @Override
            public String doMapping(Manager manager) {
                String dftValue = manager.staticForDefaultNumber(INT);
                return manager.getMapping().doMap("{var}", dftValue);
            }
        },
        fromEnum {
            @Override
            public boolean support(Manager manager) {
                return isEnum(getGetterType(manager));
            }

            @Override
            public String doMapping(Manager manager) {
                String dftValue = manager.staticForDefaultNumber(INT);
                return manager.getMapping().doMap("{var}.ordinal()", asNull(manager, dftValue));
            }
        },
        fromString {
            @Override
            public boolean support(Manager manager) {
                PropertyAttr attr = manager.getModel().getAttr();
                boolean hasFormat = attr.formatValue() != null;
                return hasFormat && isTypeof(getGetterType(manager), String.class);
            }

            @Override
            public String doMapping(Manager manager) {
                CallerManager transfer = manager.getCaller();
                PropertyAttr attr = manager.getModel().getAttr();
                CallerInfo info = transfer.findInAll(Number.class, String.class, String.class);
                String mapper = info.toString(null, strWrapped(attr.formatValue()));
                final String dftValue = manager.staticForDefaultNumber(INT);
                return manager.getMapping().doMap(mapper, asNull(manager, dftValue));
            }
        }
    }

    private enum DateToLong implements MappingBuilder {
        fromUtilDate {
            @Override
            public boolean support(Manager manager) {
                return isGetterSubtypeOf(manager, Date.class);
            }

            @Override
            public String doMapping(Manager manager) {
                final String dftValue = manager.staticForDefaultNumber();
                MappingManager mapping = manager.getMapping();
                return mapping.doMap("{var}.getTime()", asNull(manager, dftValue));
            }
        },
        fromCalendar {
            @Override
            public boolean support(Manager manager) {
                return isGetterSubtypeOf(manager, Calendar.class);
            }

            @Override
            public String doMapping(Manager manager) {
                final String dftValue = manager.staticForDefaultNumber();
                MappingManager mapping = manager.getMapping();
                return mapping.doMap("{var}.getTimeInMillis()", asNull(manager, dftValue));
            }
        },
        fromJdk8Time {
            @Override
            public boolean support(Manager manager) {
                String getter = getGetterType(manager);
                return manager.getCaller().findInAll(LONG, getter) != null;
            }

            @Override
            public String doMapping(Manager manager) {
                String setter = getSetterType(manager);
                String getter = getGetterType(manager);
                CallerInfo info = manager.getCaller().findInAll(setter, getter);
                String dftValue = manager.staticForDefaultNumber();
                return manager.getMapping().doMap(info.toString(), asNull(manager, dftValue));
            }
        },
        fromJodaTime {
            @Override
            public boolean support(Manager manager) {
                if (Imported.JODA_TIME) {
                    String getter = getGetterType(manager);
                    return manager.getCaller().findInAll(LONG, getter) != null;
                }
                return false;
            }

            @Override
            public String doMapping(Manager manager) {
                String setter = getSetterType(manager);
                String getter = getGetterType(manager);
                CallerInfo info = manager.getCaller().findInAll(setter, getter);
                String dftValue = manager.staticForDefaultNumber();
                return manager.getMapping().doMap(info.toString(), asNull(manager, dftValue));
            }
        }
    }

    private enum DateToDouble implements MappingBuilder {
        fromUtilDate {
            @Override
            public boolean support(Manager manager) {
                return isGetterSubtypeOf(manager, Date.class);
            }

            @Override
            public String doMapping(Manager manager) {
                final String dftValue = manager.staticForDefaultNumber();
                MappingManager mapping = manager.getMapping();
                String cast = isTypeof(getSetterType(manager), Double.class) ? "(double)" : "";
                String mapper = Replacer.cast.replace("{cast}{var}.getTime()", cast);
                return mapping.doMap(mapper, asNull(manager, dftValue));
            }
        },
        fromCalendar {
            @Override
            public boolean support(Manager manager) {
                return isGetterSubtypeOf(manager, Calendar.class);
            }

            @Override
            public String doMapping(Manager manager) {
                final String dftValue = manager.staticForDefaultNumber();
                MappingManager mapping = manager.getMapping();
                String cast = isTypeof(getSetterType(manager), Double.class) ? "(double)" : "";
                String mapper = Replacer.cast.replace("{cast}{var}.getTimeInMillis()", cast);
                return mapping.doMap(mapper, asNull(manager, dftValue));
            }
        },
        fromJdk8Time {
            @Override
            public boolean support(Manager manager) {
                String getter = getGetterType(manager);
                return manager.getCaller().findInAll(DOUBLE, getter) != null;
            }

            @Override
            public String doMapping(Manager manager) {
                String setter = getSetterType(manager);
                String getter = getGetterType(manager);
                CallerInfo info = manager.getCaller().findInAll(setter, getter);
                String dftValue = manager.staticForDefaultNumber();
                return manager.getMapping().doMap(info.toString(), asNull(manager, dftValue));
            }
        },
        fromJodaTime {
            @Override
            public boolean support(Manager manager) {
                if (Imported.JODA_TIME) {
                    String getter = getGetterType(manager);
                    return manager.getCaller().findInAll(DOUBLE, getter) != null;
                }
                return false;
            }

            @Override
            public String doMapping(Manager manager) {
                String setter = getSetterType(manager);
                String getter = getGetterType(manager);
                CallerInfo info = manager.getCaller().findInAll(setter, getter);
                String dftValue = manager.staticForDefaultNumber();
                return manager.getMapping().doMap(info.toString(), asNull(manager, dftValue));
            }
        }
    }

    private enum ToWrappedNumber implements MappingBuilder {
        fromPrimitiveNumber {
            @Override
            public boolean support(Manager manager) {
                return isPrimitiveNumber(getGetterType(manager));
            }

            @Override
            public String doMapping(Manager manager) {
                MappingManager mapping = manager.getMapping();
                String setterWrapped = getSetterType(manager);
                String getterWrapped = getGetterType(manager);
                String setterPrimitive = toPrimitiveType(setterWrapped);
                String cast = getterWrapped.equals(setterPrimitive) ? "" : bracketed(setterPrimitive);
                String mapper = Replacer.cast.replace("{cast}{var}", cast);
                return mapping.doMap(mapper, null);
            }
        },
        fromPrimitiveChar {
            @Override
            public boolean support(Manager manager) {
                return isPrimitiveChar(getGetterType(manager));
            }

            @Override
            public String doMapping(Manager manager) {
                MappingManager mapping = manager.getMapping();
                String setterWrapped = getSetterType(manager);
                String setterPrimitive = toPrimitiveType(setterWrapped);
                String cast = bracketed(setterPrimitive);
                String mapper = Replacer.cast.replace("{cast}{var}", cast);
                return mapping.doMap(mapper, null);
            }
        },
        fromWrappedNumber {
            @Override
            public boolean support(Manager manager) {
                return isWrappedNumber(getGetterType(manager));
            }

            @Override
            public String doMapping(Manager manager) {
                MappingManager mapping = manager.getMapping();
                String setterWrapped = getSetterType(manager);
                String getterWrapped = getGetterType(manager);
                String dftValue = manager.staticForDefaultNumber();
                if (Objects.equals(setterWrapped, getterWrapped)) {
                    return mapping.doMap(null, dftValue);
                } else {
                    String setterPrimitive = toPrimitiveType(setterWrapped);
                    String tx = "{var}.{type0}Value()";
                    tx = Replacer.type0.replace(tx, setterPrimitive);
                    return mapping.doMap(tx, asNull(manager, dftValue));
                }
            }
        },
        fromString {
            @Override
            public boolean support(Manager manager) {
                return isGetterTypeOf(manager, String.class);
            }

            @Override
            public String doMapping(Manager manager) {
                MappingManager mapping = manager.getMapping();
                String setterWrapped = getSetterType(manager);
                String var = manager.staticForDefaultNumber();
                String setterPrimitive = toPrimitiveType(setterWrapped);
                String pattern = manager.getFormatPatternVal(setterWrapped, false);
                if (pattern == null) {
                    String name = INT.equals(setterPrimitive) ? "Int" : getSimpleName(setterWrapped);
                    String mapper = Replacer.name.replace("{setterType}.parse{name}({var})", name);
                    return mapping.doMap(mapper, asNull(manager, var));
                } else {
                    CallerManager transfer = manager.getCaller();
                    CallerInfo info = transfer.findInAll(setterWrapped, String.class, String.class);
                    String mapper = info.toString(null, strWrapped(pattern));
                    return mapping.doMap(mapper, asNull(manager, var));
                }
            }
        },
        fromEnum {
            @Override
            public boolean support(Manager manager) {
                return isEnum(getGetterType(manager));
            }

            @Override
            public String doMapping(Manager manager) {
                MappingManager mapping = manager.getMapping();
                String setterWrapped = getSetterType(manager);
                String var = manager.staticForDefaultNumber();
                String setterPrimitive = toPrimitiveType(setterWrapped);
                String cast = isPrimitiveLt(setterPrimitive, INT) ? bracketed(setterPrimitive) : "";
                String t0 = "{setterType}.valueOf({cast}{var}.ordinal())";
                t0 = Replacer.cast.replace(t0, cast);
                return mapping.doMap(t0, asNull(manager, var));
            }
        },
        fromCharacter {
            @Override
            public boolean support(Manager manager) {
                return isTypeof(getGetterType(manager), Character.class);
            }

            @Override
            public String doMapping(Manager manager) {
                MappingManager mapping = manager.getMapping();
                String setterWrapped = getSetterType(manager);
                String var = manager.staticForDefaultNumber();
                String setterPrimitive = toPrimitiveType(setterWrapped);
                String cast = isPrimitiveGt(setterPrimitive, INT) ? bracketed(INT) : "";
                String t0 = "{setterType}.valueOf({cast}{var}.charValue())";
                t0 = Replacer.cast.replace(t0, cast);
                return mapping.doMap(t0, asNull(manager, var));
            }
        },
        fromNumber {
            @Override
            public boolean support(Manager manager) {
                return isSubtypeOf(getGetterType(manager), Number.class);
            }

            @Override
            public String doMapping(Manager manager) {
                MappingManager mapping = manager.getMapping();
                String setterWrapped = getSetterType(manager);
                String var = manager.staticForDefaultNumber();
                String setterPrimitive = toPrimitiveType(setterWrapped);
                String t0 = "{var}.{type0}Value()";
                t0 = Replacer.type0.replace(t0, setterPrimitive);
                return mapping.doMap(t0, asNull(manager, var));
            }
        }
    }

    private enum ToEnum implements MappingBuilder {
        fromPrimitiveNumber {
            @Override
            public boolean support(Manager manager) {
                return isPrimitiveNumber(getGetterType(manager));
            }

            @Override
            public String doMapping(Manager manager) {
                String getterType = getGetterType(manager);
                String cast = isPrimitiveGt(getterType, INT) ? "(int)" : "";
                String dftValue = manager.staticForDefaultEnumValue();
                String values = manager.staticForEnumValues();
                String mapper = "{value}[{cast}{var}]";
                mapper = Replacer.cast.replace(mapper, cast);
                mapper = Replacer.value.replace(mapper, values);
                return manager.getMapping().doMap(mapper, dftValue);
            }
        },
        fromNumber {
            @Override
            public boolean support(Manager manager) {
                return isSubtypeOf(getGetterType(manager), Number.class);
            }

            @Override
            public String doMapping(Manager manager) {
                String dftValue = manager.staticForDefaultEnumValue();
                String values = manager.staticForEnumValues();
                String mapper = "{value}[{var}.intValue()]";
                mapper = Replacer.value.replace(mapper, values);
                return manager.getMapping().doMap(mapper, asNull(manager, dftValue));
            }
        },
        fromString {
            @Override
            public boolean support(Manager manager) {
                return isTypeof(getGetterType(manager), String.class);
            }

            @Override
            public String doMapping(Manager manager) {
                String dftValue = manager.staticForDefaultEnumValue();
                String mapper = "{setterType}.valueOf({var})";
                return manager.getMapping().doMap(mapper, asNull(manager, dftValue));
            }
        },
    }

    private enum ToPrimitiveNumber implements MappingBuilder {
        fromPrimitiveNumber {
            @Override
            public boolean support(Manager manager) {
                return isPrimitiveNumber(manager.getMapping().getGetterType());
            }

            @Override
            public String doMapping(Manager manager) {
                String setter = getSetterType(manager);
                String getter = getGetterType(manager);
                String cast = isPrimitiveLt(setter, getter) ? bracketed(setter) : "";
                String mapper = Replacer.cast.replace("{cast}{var}", cast);
                return manager.getMapping().doMap(mapper, null);
            }
        },
        fromPrimitiveChar {
            @Override
            public boolean support(Manager manager) {
                return isGetterTypeOf(manager, "char");
            }

            @Override
            public String doMapping(Manager manager) {
                String setter = getSetterType(manager);
                String cast = isPrimitiveLt(setter, INT) ? bracketed(setter) : "";
                String mapper = Replacer.cast.replace("{cast}{var}", cast);
                return manager.getMapping().doMap(mapper, null);
            }
        },
        fromNumber {
            @Override
            public boolean support(Manager manager) {
                return isGetterSubtypeOf(manager, Number.class);
            }

            @Override
            public String doMapping(Manager manager) {
                String mapper = "{var}.{setterType}Value()";
                String dftValue = manager.staticForDefaultNumber();
                return manager.getMapping().doMap(mapper, dftValue);
            }
        },
        fromString {
            @Override
            public boolean support(Manager manager) {
                return isGetterTypeOf(manager, String.class);
            }

            @Override
            public String doMapping(Manager manager) {
                MappingManager mapping = manager.getMapping();
                String setter = getSetterType(manager);
                String dftValue = manager.staticForDefaultNumber();
                String fmt = manager.getFormatPatternVal();
                if (fmt == null) {
                    String wrappedType = toWrappedType(setter);
                    String name = INT.equals(setter) ? "Int" : getSimpleName(wrappedType);
                    String mapper = Replacer.name.replace("{setterType}.parse{name}({var})", name);
                    mapper = Replacer.setterType.replace(mapper, manager.onImported(wrappedType));
                    return mapping.doMap(mapper, dftValue);
                } else {
                    CallerManager transfer = manager.getCaller();
                    CallerInfo info = transfer.findInAll(Number.class, String.class, String.class);
                    String suffix = Replacer.type0.replace(".{type0}Value()", setter);
                    String mapper = info.toString(null, strWrapped(fmt)) + suffix;
                    return mapping.doMap(mapper, dftValue);
                }
            }
        },
        fromEnum {
            @Override
            public boolean support(Manager manager) {
                return isEnum(getGetterType(manager));
            }

            @Override
            public String doMapping(Manager manager) {
                String setter = getSetterType(manager);
                String dft = manager.staticForDefaultNumber();
                String cast = isPrimitiveLt(setter, INT) ? bracketed(setter) : "";
                String mapper = cast + "{var}.ordinal()";
                return manager.getMapping().doMap(mapper, dft);
            }
        },
        fromCharacter {
            @Override
            public boolean support(Manager manager) {
                return isGetterTypeOf(manager, Character.class);
            }

            @Override
            public String doMapping(Manager manager) {
                String setter = getSetterType(manager);
                String dft = manager.staticForDefaultNumber();
                String cast = isPrimitiveLt(setter, INT) ? bracketed(setter) : "";
                String mapper = cast + "{var}.charValue()";
                return manager.getMapping().doMap(mapper, dft);
            }
        }
    }

    private enum ToString implements MappingBuilder {
        fromString {
            @Override
            public boolean support(Manager manager) {
                return isGetterTypeOf(manager, String.class);
            }

            @Override
            public String doMapping(Manager manager) {
                final String dftValue = manager.staticForDefaultString();
                return manager.getMapping().doMap(null, dftValue);
            }
        },
        fromPrimitiveNumber {
            @Override
            public boolean support(Manager manager) {
                return isPrimitiveNumber(getGetterType(manager));
            }

            @Override
            public String doMapping(Manager manager) {
                final MappingManager mapping = manager.getMapping();
                String formatVal = manager.getModel().getAttr().formatValue();
                if (formatVal == null) {
                    return mapping.doMap("{setterType}.valueOf({var})", null);
                } else {
                    String stringType = String.class.getCanonicalName();
                    boolean isAllowLong = isPrimitiveNotLt(LONG, mapping.getGetterType());
                    String targetType = isAllowLong ? LONG : DOUBLE;
                    CallerInfo tInfo = findFormatter(manager, stringType, targetType);
                    String mapper = tInfo.toString(null, strWrapped(formatVal));
                    return mapping.doMap(mapper, null);
                }
            }
        },
        fromPrimitiveBoolean {
            @Override
            public boolean support(Manager manager) {
                return isPrimitiveBoolean(getGetterType(manager));
            }

            @Override
            public String doMapping(Manager manager) {
                String mapper = "{setterType}.valueOf({var})";
                return manager.getMapping().doMap(mapper, null);
            }
        },
        fromPrimitiveChar {
            @Override
            public boolean support(Manager manager) {
                return isPrimitiveChar(getGetterType(manager));
            }

            @Override
            public String doMapping(Manager manager) {
                String mapper = "{setterType}.valueOf({var})";
                return manager.getMapping().doMap(mapper, null);
            }
        },
        fromEnum {
            @Override
            public boolean support(Manager manager) {
                return isGetterSubtypeOf(manager, Enum.class);
            }

            @Override
            public String doMapping(Manager manager) {
                final String dftValue = manager.staticForDefaultString();
                return manager.getMapping().doMap("{var}.name()", asNull(manager, dftValue));
            }
        },
        fromNumber {
            @Override
            public boolean support(Manager manager) {
                return isGetterSubtypeOf(manager, Number.class);
            }

            @Override
            public String doMapping(Manager mgr) { return asMapping(mgr, Number.class, false); }
        },
        fromUtilDate {
            @Override
            public boolean support(Manager manager) {
                return isGetterSubtypeOf(manager, Date.class);
            }

            @Override
            public String doMapping(Manager mgr) { return asMapping(mgr, Date.class, true); }
        },
        fromCalendar {
            @Override
            public boolean support(Manager manager) {
                return isGetterSubtypeOf(manager, Calendar.class);
            }

            @Override
            public String doMapping(Manager mgr) { return asMapping(mgr, Calendar.class, true); }
        },
        fromJodaTime {
            @SuppressWarnings("all")
            @Override
            public boolean support(Manager manager) {
                return Imported.JODA_TIME//
                    && (isGetterSubtypeOf(manager, ReadableInstant.class)//
                    || isGetterSubtypeOf(manager, ReadablePartial.class));
            }

            @Override
            public String doMapping(Manager manager) {
                return onFormattable(manager, (mgr, pattern) -> {
                    String dftValue = mgr.staticForDefaultString();
                    String format = mgr.staticForJodaDateTimeFormatter(pattern);
                    String fmt = Replacer.format.replace("{format}.print({var})", format);
                    return mgr.getMapping().doMap(fmt, asNull(manager, dftValue));
                }, true);
            }
        },
        fromJdk8Time {
            @Override
            public boolean support(Manager manager) {
                return isGetterSubtypeOf(manager, TemporalAccessor.class);
            }

            @Override
            public String doMapping(Manager manager) {
                return onFormattable(manager, (mgr, pattern) -> {
                    String dftValue = mgr.staticForDefaultString();
                    String format = mgr.staticForDateTimeFormatter(pattern);
                    String fmt = Replacer.format.replace("{format}.format({var})", format);
                    return mgr.getMapping().doMap(fmt, asNull(manager, dftValue));
                }, true);
            }
        },
        fromObject {
            @Override
            public boolean support(Manager manager) { return true; }

            @Override
            public String doMapping(Manager manager) {
                final String dftValue = manager.staticForDefaultString();
                return manager.getMapping().doMap("{var}.toString()", asNull(manager, dftValue));
            }
        };

        private static String asMapping(Manager mgr, Class<?> type, boolean isDateType) {
            return onFormattable(mgr, (m, pattern) -> {
                MappingManager mapping = m.getMapping();
                CallerManager transfer = m.getCaller();
                String setterType = mapping.getSetterType();
                String getterType = type.getCanonicalName();
                String stringType = String.class.getCanonicalName();
                CallerInfo info = transfer.findInAll(setterType, getterType, stringType);
                String mapper = info.toString(null, strWrapped(pattern));
                return mapping.doMap(mapper, asNull(mgr, null));
            }, isDateType);
        }

        private static String onFormattable(
            Manager manager, BiFunction<Manager, String, String> formatter, boolean isDateType
        ) {
            String type = getGetterType(manager);
            String pattern = manager.getFormatPatternVal(type, isDateType);
            if (pattern == null) {
                return fromObject.doMapping(manager);
            } else {
                return formatter.apply(manager, pattern);
            }
        }
    }

    /**
     * null 值默认值，调用条件如下:
     * <p>
     * 1. 存在可能导致空指针异常的数据转换(就单独一个 {var} 就不用了)
     * 2. 转换双向均不是基本数据类型
     *
     * @param dftValue
     *
     * @return
     */
    private static String asNull(Manager mgr, String dftValue) {
        String getterType = getGetterType(mgr);
        String setterType = getSetterType(mgr);
        if (isPrimitive(getterType)) {
            return null;
        } else if (isPrimitive(setterType)) {
            return dftValue;
        }
        return (dftValue == null ? "null" : dftValue);
    }

    private static CallerInfo getBasicTransfer(Manager manager) {
        String setterType = getSetterType(manager);
        String getterType = getGetterType(manager);
        CallerManager transfer = manager.getCaller();
        return transfer.findInAll(setterType, getterType);
    }

    private static CallerInfo findFormatter(Manager manager, String setterType, String getterType) {
        CallerManager transfer = manager.getCaller();
        String stringType = String.class.getCanonicalName();
        return transfer.findInAll(setterType, getterType, stringType);
    }

    private static boolean isGetterTypeOf(Manager manager, Class<?> type) {
        return isTypeof(getGetterType(manager), type);
    }

    private static boolean isGetterTypeOf(Manager manager, String type) {
        return isTypeof(getGetterType(manager), type);
    }

    private static boolean isGetterSubtypeOf(Manager manager, Class<?> type) {
        return isSubtypeOf(getGetterType(manager), type);
    }

    private static boolean onGetterType(Manager manager, Predicate<String> tester) {
        return tester.test(getGetterType(manager));
    }

    private static boolean isJodaSpecialDate(String type) {
        return Imported.JODA_TIME &&
            isTypeofAny(type, MutableDateTime.class, DateTime.class, LocalDateTime.class, LocalDate.class,
                LocalTime.class, YearMonth.class, Instant.class, MonthDay.class);
    }

    private static boolean isJdk8Date(String type) {
        return isTypeofAny(type, java.time.LocalDateTime.class, java.time.ZonedDateTime.class,
            java.time.OffsetDateTime.class, java.time.Instant.class, java.time.LocalDate.class,
            java.time.LocalTime.class);
    }

    private static String getGetterType(Manager manager) {
        return manager.getMapping().getGetterType();
    }

    private static String getSetterType(Manager manager) {
        return manager.getMapping().getSetterType();
    }
}
