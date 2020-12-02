package com.moon.mapping.processing;

import org.joda.time.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import static com.moon.mapping.processing.DetectUtils.*;
import static com.moon.mapping.processing.ElementUtils.getSimpleName;
import static com.moon.mapping.processing.StringUtils.*;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
final class MappingField {

    private final static Map<Predicate<String>, Class<? extends Mapper>> MAPPERS = new HashMap<>();

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
        for (Map.Entry<Predicate<String>, Class<? extends Mapper>> clsEntry : MAPPERS.entrySet()) {
            String setterType = getSetterType(manager);
            if (clsEntry.getKey().test(setterType)) {
                for (Mapper mapper : clsEntry.getValue().getEnumConstants()) {
                    if (mapper.support(manager)) {
                        return mapper.doMapping(manager);
                    }
                }
            }
        }
        return null;
    }

    private static String doMappingIfAssignable(Manager manager) {
        String setterType = getSetterType(manager);
        String getterType = getGetterType(manager);
        PropertyAttr attr = manager.getModel().getAttr();
        String fmt = attr.formatValue(), dft = attr.defaultValue();

        TransferManager transferManager = manager.getTransfer();
        TransferInfo info = null;
        if (dft != null) {
            String[] types = {getterType, String.class.getCanonicalName(), String.class.getCanonicalName()};
            info = transferManager.find(setterType, types);
        }
        if (info == null && fmt != null) {
            info = transferManager.find(setterType, getterType, String.class.getCanonicalName());
        }
        if (info == null) {
            info = transferManager.find(setterType, getterType);
        }
        if (info != null) {
            // TODO
        }

        if (fmt == null && dft == null && isSubtypeOf(getterType, setterType)) {
            return manager.getMapping().normalized(null, null);
        }
        return null;
    }

    private interface Mapper {

        boolean support(Manager manager);

        String doMapping(Manager manager);
    }

    private enum ToCalendar implements Mapper {
        fromString {
            @Override
            public boolean support(Manager manager) {
                return isGetterTypeOf(manager, String.class);
            }

            @Override
            public String doMapping(Manager manager) {
                MappingManager mapping = manager.getMapping();
                TransferManager transfer = manager.getTransfer();
                String returnType = Calendar.class.getCanonicalName();
                String fmt = manager.getFormatPatternVal(returnType, true);
                if (fmt == null) {
                    TransferInfo info = transfer.findInAll(returnType, String.class);
                    return mapping.normalized(info.toString(), null);
                } else {
                    TransferInfo info = transfer.findInAll(returnType, String.class, String.class);
                    String mapper = info.toString(null, strWrapped(fmt));
                    return mapping.normalized(mapper, null);
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
                TransferManager transfer = manager.getTransfer();
                TransferInfo info = transfer.findInAll(setterType, cast);
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
                TransferManager transfer = mgr.getTransfer();
                TransferInfo info = transfer.findInAll(Calendar.class, Number.class);
                return mapping.normalized(info.toString(), null);
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
                TransferManager transfer = mgr.getTransfer();
                TransferInfo info = transfer.findInAll(Calendar.class, Date.class);
                return mapping.normalized(info.toString(), null);
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
                TransferManager transfer = mgr.getTransfer();
                String getterType = getGetterType(mgr);
                String returnType = Calendar.class.getCanonicalName();
                TransferInfo info = transfer.findInAll(returnType, getterType);
                return mapping.normalized(info.toString(), null);
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
                TransferManager transfer = mgr.getTransfer();
                TransferInfo info = transfer.findInAll(Calendar.class, ReadableInstant.class);
                return mgr.getMapping().normalized(info.toString(), null);
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
                TransferManager transfer = mgr.getTransfer();
                TransferInfo info = transfer.findInAll(returnType, getterType);
                return mgr.getMapping().normalized(info.toString(), null);
            }
        }
    }

    private enum ToUtilDate implements Mapper {
        fromString {
            @Override
            public boolean support(Manager manager) {
                return isGetterTypeOf(manager, String.class);
            }

            @Override
            public String doMapping(Manager manager) {
                MappingManager mapping = manager.getMapping();
                TransferManager transfer = manager.getTransfer();
                String getterType = mapping.getGetterType();
                String setterType = mapping.getSetterType();
                String pattern = manager.getFormatPatternVal(getterType, true);
                if (pattern == null) {
                    TransferInfo info = transfer.findInAll(setterType, String.class);
                    return mapping.normalized(info.toString(), null);
                } else {
                    TransferInfo info = transfer.findInAll(setterType, String.class, String.class);
                    return mapping.normalized(info.toString(null, strWrapped(pattern)), null);
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
                TransferManager transfer = manager.getTransfer();
                TransferInfo info = transfer.findInAll(setterType, cast);
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
                TransferManager transfer = mgr.getTransfer();
                TransferInfo info = transfer.findInAll(setterType, Number.class);
                return mapping.normalized(info.toString(), null);
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
                TransferManager transfer = mgr.getTransfer();
                TransferInfo info = transfer.findInAll(setterType, Calendar.class);
                return mapping.normalized(info.toString(), null);
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
                TransferManager transfer = mgr.getTransfer();
                TransferInfo info = getBasicTransfer(mgr);
                return mapping.normalized(info.toString(), null);
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
                TransferManager transfer = mgr.getTransfer();
                String setterType = getSetterType(mgr);
                TransferInfo info = transfer.findInAll(setterType, ReadableInstant.class);
                return mgr.getMapping().normalized(info.toString(), null);
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
                TransferManager transfer = mgr.getTransfer();
                TransferInfo info = getBasicTransfer(mgr);
                return mgr.getMapping().normalized(info.toString(), null);
            }
        }
    }

    private enum ToJdk8TimeByString implements Mapper {
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
                    return mapping.normalized("{setterType}.parse({var})", null);
                } else {
                    String format = manager.staticForDateTimeFormatter(pattern);
                    String mapper = "{setterType}.from({format}.parse({var}))";
                    mapper = Replacer.format.replace(mapper, format);
                    return mapping.normalized(mapper, null);
                }
            }
        }
    }

    private enum ToJdk8Time implements Mapper {
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
                TransferManager transfer = manager.getTransfer();
                TransferInfo info = transfer.findInAll(setterType, byType);
                return mapping.normalized(info.toString(), null);
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
                return mapping.normalized(getBasicTransfer(manager).toString(), null);
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
                TransferManager transfer = manager.getTransfer();
                String setterType = getSetterType(manager);
                TransferInfo info = transfer.findInAll(setterType, Number.class);
                return mapping.normalized(info.toString(), null);
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
                TransferManager transfer = manager.getTransfer();
                String setterType = getSetterType(manager);
                TransferInfo info = transfer.findInAll(setterType, Date.class);
                return mapping.normalized(info.toString(), null);
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
                TransferManager transfer = manager.getTransfer();
                String setterType = getSetterType(manager);
                TransferInfo info = transfer.findInAll(setterType, Calendar.class);
                return mapping.normalized(info.toString(), null);
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
                TransferManager transfer = manager.getTransfer();
                String setterType = getSetterType(manager);
                TransferInfo info = transfer.findInAll(setterType, ReadableInstant.class);
                return mapping.normalized(info.toString(), null);
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
                TransferManager transfer = manager.getTransfer();
                TransferInfo info = getBasicTransfer(manager);
                return mapping.normalized(info.toString(), null);
            }
        }
    }

    private enum ToJodaTime implements Mapper {
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
                    return mapping.normalized(mapper, null);
                } else {
                    String fmt = manager.staticForJodaDateTimeFormatter(pattern);
                    String tx = "{setterType}.parse({var}, {format})";
                    String mapper = Replacer.format.replace(tx, fmt);
                    return mapping.normalized(mapper, null);
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
                return manager.getMapping().normalized(mapper, null);
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
                return manager.getMapping().normalized(mapper, null);
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
                return manager.getMapping().normalized(mapper, null);
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
                return manager.getMapping().normalized(mapper, null);
            }
        }
    }

    private enum ToBigInteger implements Mapper {
        fromBigInteger {
            @Override
            public boolean support(Manager manager) {
                return isGetterTypeOf(manager, BigInteger.class);
            }

            @Override
            public String doMapping(Manager manager) {
                String var = manager.staticForDefaultBigInteger();
                return manager.getMapping().normalized(null, var);
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
                return manager.getMapping().normalized(mapper, var);
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
                return manager.getMapping().normalized(mapper, var);
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
                return manager.getMapping().normalized(mapper, var);
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
                    return mapping.normalized("new {setterType}({var})", var);
                } else {
                    TransferManager transfer = manager.getTransfer();
                    TransferInfo info = transfer.findInAll(BigInteger.class, String.class, String.class);
                    return mapping.normalized(info.toString(null, strWrapped(pattern)), var);
                }
            }
        }
    }

    private enum ToBigDecimal implements Mapper {
        fromBigDecimal {
            @Override
            public boolean support(Manager manager) {
                return isGetterTypeOf(manager, BigDecimal.class);
            }

            @Override
            public String doMapping(Manager manager) {
                String var = manager.staticForDefaultBigDecimal();
                return manager.getMapping().normalized(null, var);
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
                return mapping.normalized("new {setterType}({var})", var);
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
                return manager.getMapping().normalized(mapper, var);
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
                return mapping.normalized("{setterType}.valueOf({var})", var);
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
                return mapping.normalized(mapper, var);
            }
        },
        fromNumber {
            @Override
            public boolean support(Manager manager) {
                return isSubtypeOf(getGetterType(manager), Number.class);
            }

            @Override
            public String doMapping(Manager manager) {
                TransferManager transfer = manager.getTransfer();
                TransferInfo info = transfer.findInAll(BigDecimal.class, Number.class);
                String var = manager.staticForDefaultBigDecimal();
                MappingManager mapping = manager.getMapping();
                return mapping.normalized(info.toString(), var);
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
                    return mapping.normalized("new {setterType}({var})", var);
                } else {
                    TransferManager transfer = manager.getTransfer();
                    TransferInfo info = transfer.findInAll(BigDecimal.class, String.class, String.class);
                    return mapping.normalized(info.toString(null, strWrapped(pattern)), var);
                }
            }
        }
    }

    private enum ToCharValue implements Mapper {
        fromPrimitiveChar {
            @Override
            public boolean support(Manager manager) {
                return isPrimitiveChar(getGetterType(manager));
            }

            @Override
            public String doMapping(Manager manager) {
                return manager.getMapping().normalized(null, null);
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
                return manager.getMapping().normalized(null, dftValue);
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
                return manager.getMapping().normalized(t0, null);
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
                return manager.getMapping().normalized(t0, null);
            }
        }
    }

    private enum ToBoolean implements Mapper {
        fromPrimitiveNumber {
            @Override
            public boolean support(Manager manager) {
                return isPrimitiveNumber(getGetterType(manager));
            }

            @Override
            public String doMapping(Manager manager) {
                return manager.getMapping().normalized("{var} != 0", null);
            }
        },
        fromPrimitiveBoolean {
            @Override
            public boolean support(Manager manager) {
                return isPrimitiveBoolean(getGetterType(manager));
            }

            @Override
            public String doMapping(Manager manager) {
                return manager.getMapping().normalized("{var}", null);
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
                return manager.getMapping().normalized(mapper, var);
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
                return manager.getMapping().normalized(mapper, var);
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
                return manager.getMapping().normalized("{var}", var);
            }
        }
    }

    private enum ToNumber implements Mapper {
        fromPrimitiveNumber {
            @Override
            public boolean support(Manager manager) {
                return isPrimitiveNumber(getGetterType(manager));
            }

            @Override
            public String doMapping(Manager manager) {
                return manager.getMapping().normalized(null, null);
            }
        },
        fromPrimitiveChar {
            @Override
            public boolean support(Manager manager) {
                return isPrimitiveChar(getGetterType(manager));
            }

            @Override
            public String doMapping(Manager manager) {
                return manager.getMapping().normalized("(int){var}", null);
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
                return manager.getMapping().normalized("{var}", dftValue);
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
                return manager.getMapping().normalized("{var}.ordinal()", dftValue);
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
                TransferManager transfer = manager.getTransfer();
                PropertyAttr attr = manager.getModel().getAttr();
                TransferInfo info = transfer.findInAll(Number.class, String.class, String.class);
                String mapper = info.toString(null, strWrapped(attr.formatValue()));
                final String dftValue = manager.staticForDefaultNumber(INT);
                return manager.getMapping().normalized(mapper, dftValue);
            }
        }
    }

    private enum DateToLong implements Mapper {
        fromUtilDate {
            @Override
            public boolean support(Manager manager) {
                return isGetterSubtypeOf(manager, Date.class);
            }

            @Override
            public String doMapping(Manager manager) {
                final String dftValue = manager.staticForDefaultNumber();
                MappingManager mapping = manager.getMapping();
                return mapping.normalized("{var}.getTime()", dftValue);
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
                return mapping.normalized("{var}.getTimeInMillis()", dftValue);
            }
        },
        fromJdk8Time {
            @Override
            public boolean support(Manager manager) {
                String getter = getGetterType(manager);
                return manager.getTransfer().findInAll(LONG, getter) != null;
            }

            @Override
            public String doMapping(Manager manager) {
                String setter = getSetterType(manager);
                String getter = getGetterType(manager);
                TransferInfo info = manager.getTransfer().findInAll(setter, getter);
                String dftValue = manager.staticForDefaultNumber();
                return manager.getMapping().normalized(info.toString(), dftValue);
            }
        },
        fromJodaTime {
            @Override
            public boolean support(Manager manager) {
                if (Imported.JODA_TIME) {
                    String getter = getGetterType(manager);
                    return manager.getTransfer().findInAll(LONG, getter) != null;
                }
                return false;
            }

            @Override
            public String doMapping(Manager manager) {
                String setter = getSetterType(manager);
                String getter = getGetterType(manager);
                TransferInfo info = manager.getTransfer().findInAll(setter, getter);
                String dftValue = manager.staticForDefaultNumber();
                return manager.getMapping().normalized(info.toString(), dftValue);
            }
        }
    }

    private enum DateToDouble implements Mapper {
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
                return mapping.normalized(mapper, dftValue);
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
                return mapping.normalized(mapper, dftValue);
            }
        },
        fromJdk8Time {
            @Override
            public boolean support(Manager manager) {
                String getter = getGetterType(manager);
                return manager.getTransfer().findInAll(DOUBLE, getter) != null;
            }

            @Override
            public String doMapping(Manager manager) {
                String setter = getSetterType(manager);
                String getter = getGetterType(manager);
                TransferInfo info = manager.getTransfer().findInAll(setter, getter);
                String dftValue = manager.staticForDefaultNumber();
                return manager.getMapping().normalized(info.toString(), dftValue);
            }
        },
        fromJodaTime {
            @Override
            public boolean support(Manager manager) {
                if (Imported.JODA_TIME) {
                    String getter = getGetterType(manager);
                    return manager.getTransfer().findInAll(DOUBLE, getter) != null;
                }
                return false;
            }

            @Override
            public String doMapping(Manager manager) {
                String setter = getSetterType(manager);
                String getter = getGetterType(manager);
                TransferInfo info = manager.getTransfer().findInAll(setter, getter);
                String dftValue = manager.staticForDefaultNumber();
                return manager.getMapping().normalized(info.toString(), dftValue);
            }
        }
    }

    private enum ToWrappedNumber implements Mapper {
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
                return mapping.normalized(mapper, null);
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
                return mapping.normalized(mapper, null);
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
                    return mapping.normalized(null, dftValue);
                } else {
                    String setterPrimitive = toPrimitiveType(setterWrapped);
                    String tx = "{var}.{type0}Value()";
                    tx = Replacer.type0.replace(tx, setterPrimitive);
                    return mapping.normalized(tx, dftValue);
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
                    return mapping.normalized(mapper, var);
                } else {
                    TransferManager transfer = manager.getTransfer();
                    TransferInfo info = transfer.findInAll(setterWrapped, String.class, String.class);
                    String mapper = info.toString(null, strWrapped(pattern));
                    return mapping.normalized(mapper, var);
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
                return mapping.normalized(t0, var);
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
                return mapping.normalized(t0, var);
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
                return mapping.normalized(t0, var);
            }
        }
    }

    private enum ToEnum implements Mapper {
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
                return manager.getMapping().normalized(mapper, dftValue);
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
                return manager.getMapping().normalized(mapper, dftValue);
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
                return manager.getMapping().normalized(mapper, dftValue);
            }
        },
    }

    private enum ToPrimitiveNumber implements Mapper {
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
                return manager.getMapping().normalized(mapper, null);
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
                return manager.getMapping().normalized(mapper, null);
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
                return manager.getMapping().normalized(mapper, dftValue);
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
                    return mapping.normalized(mapper, dftValue);
                } else {
                    TransferManager transfer = manager.getTransfer();
                    TransferInfo info = transfer.findInAll(Number.class, String.class, String.class);
                    String suffix = Replacer.type0.replace(".{type0}Value()", setter);
                    String mapper = info.toString(null, strWrapped(fmt)) + suffix;
                    return mapping.normalized(mapper, dftValue);
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
                return manager.getMapping().normalized(mapper, dft);
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
                return manager.getMapping().normalized(mapper, dft);
            }
        }
    }

    private enum ToString implements Mapper {
        fromString {
            @Override
            public boolean support(Manager manager) {
                return isGetterTypeOf(manager, String.class);
            }

            @Override
            public String doMapping(Manager manager) {
                final String dftValue = manager.staticForDefaultString();
                return manager.getMapping().normalized(null, dftValue);
            }
        },
        fromPrimitiveNumber {
            @Override
            public boolean support(Manager manager) {
                String type = getGetterType(manager);
                return isPrimitiveNumber(type);
            }

            @Override
            public String doMapping(Manager manager) {
                final MappingManager mapping = manager.getMapping();
                String formatVal = manager.getModel().getAttr().formatValue();
                if (formatVal == null) {
                    return mapping.normalized("{setterType}.valueOf({var})", null);
                } else {
                    String stringType = String.class.getCanonicalName();
                    boolean isAllowLong = isPrimitiveNotLt(LONG, mapping.getGetterType());
                    String targetType = isAllowLong ? LONG : DOUBLE;
                    TransferInfo tInfo = findFormatter(manager, stringType, targetType);
                    String mapper = tInfo.toString(null, strWrapped(formatVal));
                    return mapping.normalized(mapper, null);
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
                return manager.getMapping().normalized(mapper, null);
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
                return manager.getMapping().normalized(mapper, null);
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
                return manager.getMapping().normalized("{var}.name()", dftValue);
            }
        },
        fromNumber {
            @Override
            public boolean support(Manager manager) {
                return isGetterSubtypeOf(manager, Number.class);
            }

            @Override
            public String doMapping(Manager mgr) { return asMapping(mgr, Number.class); }
        },
        fromUtilDate {
            @Override
            public boolean support(Manager manager) {
                return isGetterSubtypeOf(manager, Date.class);
            }

            @Override
            public String doMapping(Manager mgr) { return asMapping(mgr, Date.class); }
        },
        fromCalendar {
            @Override
            public boolean support(Manager manager) {
                return isGetterSubtypeOf(manager, Calendar.class);
            }

            @Override
            public String doMapping(Manager mgr) { return asMapping(mgr, Calendar.class); }
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
                return ToString.onDateFormattable(manager, (mgr, pattern) -> {
                    String dftValue = mgr.staticForDefaultString();
                    String format = mgr.staticForJodaDateTimeFormatter(pattern);
                    String fmt = Replacer.format.replace("{format}.print({var})", format);
                    return mgr.getMapping().normalized(fmt, dftValue);
                });
            }
        },
        fromJdk8Time {
            @Override
            public boolean support(Manager manager) {
                return isGetterSubtypeOf(manager, TemporalAccessor.class);
            }

            @Override
            public String doMapping(Manager manager) {
                return ToString.onDateFormattable(manager, (mgr, pattern) -> {
                    String dftValue = mgr.staticForDefaultString();
                    String format = mgr.staticForDateTimeFormatter(pattern);
                    String fmt = Replacer.format.replace("{format}.format({var})", format);
                    return mgr.getMapping().normalized(fmt, dftValue);
                });
            }
        },
        fromObject {
            @Override
            public boolean support(Manager manager) { return true; }

            @Override
            public String doMapping(Manager manager) {
                final String dftValue = manager.staticForDefaultString();
                return manager.getMapping().normalized("{var}.toString()", dftValue);
            }
        };

        private static String asMapping(Manager mgr, Class<?> type) {
            return onDateFormattable(mgr, (m, pattern) -> {
                MappingManager mapping = m.getMapping();
                TransferManager transfer = m.getTransfer();
                String setterType = mapping.getSetterType();
                String getterType = type.getCanonicalName();
                String stringType = String.class.getCanonicalName();
                TransferInfo info = transfer.findInAll(setterType, getterType, stringType);
                String mapper = info.toString(null, strWrapped(pattern));
                return mapping.normalized(mapper, null);
            });
        }

        private static String onDateFormattable(Manager manager, BiFunction<Manager, String, String> formatter) {
            String type = getGetterType(manager);
            String pattern = manager.getFormatPatternVal(type, true);
            if (pattern == null) {
                return fromObject.doMapping(manager);
            } else {
                return formatter.apply(manager, pattern);
            }
        }
    }


    private static TransferInfo getBasicTransfer(Manager manager) {
        String setterType = getSetterType(manager);
        String getterType = getGetterType(manager);
        TransferManager transfer = manager.getTransfer();
        return transfer.findInAll(setterType, getterType);
    }

    private static TransferInfo findFormatter(Manager manager, String setterType, String getterType) {
        TransferManager transfer = manager.getTransfer();
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
