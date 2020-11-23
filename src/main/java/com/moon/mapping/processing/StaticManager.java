package com.moon.mapping.processing;

import org.joda.time.format.DateTimeFormat;

import javax.lang.model.element.Element;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

import static com.moon.mapping.processing.ElementUtils.getSimpleName;
import static com.moon.mapping.processing.StringUtils.isPrimitiveNumber;
import static com.moon.mapping.processing.StringUtils.isWrappedNumber;
import static javax.tools.Diagnostic.Kind.MANDATORY_WARNING;

/**
 * 静态变量遍历器
 *
 * @author benshaoye
 */
public class StaticManager {

    private final ImportManager importManager;

    private final AtomicInteger indexer = new AtomicInteger();
    /**
     * 静态变量名缓存
     */
    private final Map<String, String> varCached = new HashMap<>();
    /**
     * 静态字段语句
     */
    private final List<String> fields = new ArrayList<>();

    public StaticManager(ImportManager importManager) { this.importManager = importManager; }

    public String getStaticVar(String key) {
        return varCached.computeIfAbsent(key, k -> "VAR" + indexer.getAndIncrement());
    }

    private String computeStaticVariable(
        String classname, String declareVal, BiFunction<String, String, String> defaultPuller
    ) {
        final String trimmedVal = trimToEmpty(declareVal);
        if (StringUtils.isEmpty(trimmedVal)) {
            return null;
        }
        String key = getKey(classname, trimmedVal);
        String varName = varCached.get(key);
        if (varName != null) {
            return varName;
        } else {
            String script = defaultPuller.apply(classname, declareVal);
            if (script != null) {
                String resultVar = getStaticVar(key);
                script = Replacer.type0.replace(script, importManager.onImported(classname));
                script = Replacer.var.replace(script, resultVar);
                fields.add("private final static " + script);
                return resultVar;
            }
            return null;
        }
    }

    /**
     * 默认 boolean 值
     *
     * @param classname  boolean 类型（boolean 和 Boolean）
     * @param declareVal 只能为 true / false
     *
     * @return
     */
    public String onDefaultBoolean(String classname, String declareVal) {
        return computeStaticVariable(classname, declareVal, (type, value) -> {
            String booleanValue = Boolean.valueOf(value).toString();
            String t0 = "{type0} {var} = {value};";
            return Replacer.value.replace(t0, booleanValue);
        });
    }

    public String onDefaultNumber(String classname, String declareVal) {
        return computeStaticVariable(classname, declareVal, (type, value) -> {
            if (!(isPrimitiveNumber(type) || isWrappedNumber(type))) {
                return null;
            }
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            value = value.toUpperCase();
            if (value.endsWith("D") || value.endsWith("F") || value.endsWith("L")) {
                value = value.substring(value.length() - 1);
            }
            if (!DetectUtils.isDigit(value)) {
                Logger.printWarn("【已忽略默认值】非法 {} 默认值: {}.", type, value);
                return null;
            }
            if (DetectUtils.isTypeofAny(type, Float.class, Double.class, Long.class)) {
                value += ElementUtils.getSimpleName(type).charAt(0);
            }
            String t0 = "{type0} {var} = {value};";
            return Replacer.value.replace(t0, value);
        });
    }

    public String onDefaultBigInteger(String value) { return onDefault(value, BigInteger.class); }

    public String onDefaultBigDecimal(String value) { return onDefault(value, BigDecimal.class); }

    private <T> String onDefault(String value, Class<T> type) {
        value = trimToEmpty(value);
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        String key = getKey(type, value);
        String varName = varCached.get(key);
        if (varName != null) {
            return varName;
        }
        String valExp;
        switch (value) {
            case "0":
                valExp = " {type0}.ZERO;";
                break;
            case "1":
                valExp = " {type0}.ONE;";
                break;
            case "10":
                valExp = " {type0}.TEN;";
                break;
            default:
                try {
                    new BigDecimal(value);
                    valExp = " new {type0}(\"{value}\");";
                    break;
                } catch (Throwable t) {
                    Logger.warn("【已忽略默认值】非法 {} 默认值: {}.", type.getSimpleName(), value);
                    return null;
                }
        }
        varName = getStaticVar(key);
        String t0 = "{modifiers} {type0} {var} = " + valExp;
        t0 = Replacer.type0.replace(t0, importManager.onImported(type));
        t0 = Replacer.value.replace(t0, value);
        fields.add(then(t0, varName));
        return varName;
    }

    public String onString(String declareVal) {
        return computeStaticVariable(String.class.getCanonicalName(), declareVal, (type, value) -> {
            final int length = value.length();
            final char quote = '"';
            String fieldValue;
            if (length > 1 && value.charAt(0) == quote && value.charAt(length - 1) == quote) {
                fieldValue = value;
            } else {
                fieldValue = (quote + value + quote);
            }
            return Replacer.value.replace("{type0} {var} = {value};", fieldValue);
        });
    }

    public String onDateTimeFormatter(String format) {
        if (StringUtils.isEmpty(format)) {
            return null;
        }
        try {
            DateTimeFormatter.ofPattern(format);
        } catch (Exception e) {
            Logger.printWarn("【已忽略日期格式化】非法日期格式: {}.", format);
            return null;
        }
        String key = getKey(DateTimeFormatter.class, format);
        String varName = varCached.get(key);
        if (varName == null) {
            varName = getStaticVar(key);
            String declare = "{modifiers} {type0} {var} = {type0}.ofPattern(\"{format}\");";
            declare = Replacer.type0.replace(declare, importManager.onImported(DateTimeFormatter.class));
            declare = Replacer.format.replace(declare, format);
            fields.add(then(declare, varName));
        }
        return varName;
    }

    public String onJodaDateTimeFormat(String format) {
        if (!Imported.JODA_TIME || StringUtils.isEmpty(format)) {
            return null;
        }
        try {
            DateTimeFormat.forPattern(format);
        } catch (Exception e) {
            Logger.printWarn("【已忽略日期格式化】非法日期格式: {}.", format);
            return null;
        }
        String key = getJodaFormatterKey(format);
        String varName = varCached.get(key);
        if (varName == null) {
            varName = getStaticVar(key);
            String declare = "{modifiers} {type0} {var} = {type1}.forPattern(\"{format}\");";
            Class<?> formatter = org.joda.time.format.DateTimeFormatter.class;
            declare = Replacer.type0.replace(declare, importManager.onImported(formatter));
            declare = Replacer.type1.replace(declare, importManager.onImported(DateTimeFormat.class));
            declare = Replacer.format.replace(declare, format);
            fields.add(then(declare, varName));
        }
        return varName;
    }

    public String onEnumIndexed(String enumClassname, String index) {
        index = trimToEmpty(index);
        int idx;
        try {
            idx = Integer.parseInt(index);
        } catch (Exception e) {
            Logger.printWarn("【已忽略默认值】{} 不存在第 '{}' 个枚举值", enumClassname, index);
            return null;
        }
        String key = getKey(enumClassname, index);
        String varName = varCached.get(key);
        if (varName != null) {
            return varName;
        }
        Element enumConst = ElementUtils.findEnumIndexOf(enumClassname, idx);
        if (enumConst == null) {
            Logger.printWarn("【已忽略默认值】{} 不存在第 '{}' 个枚举项", enumClassname, index);
            return null;
        }
        String enumConstName = getSimpleName(enumConst);
        varName = getStaticVar(key);
        String declare = "{modifiers} {type0} {var}={type0}.{name};";
        declare = Replacer.type0.replace(declare, importManager.onImported(enumClassname));
        declare = Replacer.name.replace(declare, enumConstName);
        fields.add(then(declare, varName));
        return varName;
    }

    public String onEnumNamed(String enumClassname, String name) {
        name = trimToEmpty(name);
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        String key = getKey(enumClassname, name);
        String varName = varCached.get(key);
        if (varName != null) {
            return varName;
        }
        Element enumConst = ElementUtils.findEnumNameOf(enumClassname, name);
        if (enumConst == null) {
            Logger.printWarn("【已忽略默认值】{} 不存在枚举项: {}", enumClassname, name);
            return null;
        }
        varName = getStaticVar(key);
        String declare = "{modifiers} {type0} {var} = {type0}.{name};";
        declare = Replacer.type0.replace(declare, importManager.onImported(enumClassname));
        declare = Replacer.name.replace(declare, name);
        fields.add(then(declare, varName));
        return varName;
    }

    public String onEnumValues(String enumClassname) {
        String varName = varCached.get(enumClassname);
        if (varName == null) {
            varName = getStaticVar(enumClassname);
            String declare = "{modifiers} {type0}[] {var} = {type0}.values();";
            declare = Replacer.type0.replace(declare, importManager.onImported(enumClassname));
            fields.add(then(declare, varName));
        }
        return varName;
    }

    public String onDefaultChar(String value) {
        value = trimToEmpty(value);
        switch (value.length()) {
            case 0:
                return null;
            case 1:
                try {
                    char ch = value.charAt(0);
                    String key = getKey(char.class, value);
                    String varName = varCached.get(key);
                    if (varName != null) {
                        return varName;
                    }
                    varName = "'" + ch + "'";
                    varCached.put(key, varName);
                    return varName;
                } catch (Throwable ignored) {
                }
            default:
                Logger.printWarn("【已忽略默认值】非法 {} 默认值: {}", "char", value);
                return null;
        }
    }

    public String defaultNull() { return null; }

    @Override
    public String toString() { return String.join("", fields); }

    private static String then(String declare, String var) {
        declare = Replacer.modifiers.replace(declare, "private final static ");
        declare = Replacer.var.replace(declare, var);
        return declare;
    }

    private String getJodaFormatterKey(String key) {
        try {
            return getKey(DateTimeFormat.class, key);
        } catch (Throwable t) {
            return "org.joda.time.format.DateTimeFormat#" + key;
        }
    }

    private static String trimToEmpty(String value) { return (value == null ? "" : value).trim(); }

    private String getKey(Class<?> type, String key) { return getKey(type.getCanonicalName(), key); }

    private String getKey(String type, String key) { return type + ">" + key; }
}
