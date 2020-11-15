package com.moon.mapping.processing;

import org.joda.time.format.DateTimeFormat;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static com.moon.mapping.processing.ElementUtils.getSimpleName;

/**
 * @author benshaoye
 */
public class StaticManager {

    static final String NULL = "null";

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

    public String onDefaultNumber(Class<?> type, String value) {
        return onDefaultNumber(type.getCanonicalName(), value);
    }

    public String onDefaultNumber(String type, String value) {
        value = (value == null ? "" : value).trim().toUpperCase();
        if (!(StringUtils.isPrimitiveNumber(type) || StringUtils.isWrappedNumber(type))) {
            return NULL;
        }
        if (StringUtils.isEmpty(value)) {
            return NULL;
        }
        if (!DetectUtils.isDigit(value)) {
            Logger.warn("【已忽略默认值】非法 {} 默认值: {}.", type, value);
            return NULL;
        }
        String key = getKey(type, value);
        String varName = varCached.get(key);
        if (varName != null) {
            return varName;
        }
        if (value.endsWith("D") || value.endsWith("F") || value.endsWith("L")) {
            // ignored
        } else if ("Double".equals(type) || Double.class.getCanonicalName().equals(type)) {
            value += "D";
        } else if ("Float".equals(type) || Float.class.getCanonicalName().equals(type)) {
            value += "F";
        } else if ("Long".equals(type) || Long.class.getCanonicalName().equals(type)) {
            value += "L";
        }
        varName = getStaticVar(key);
        String t0 = "{modifiers} {type0} {var} = {value};";
        t0 = Replacer.type0.replace(t0, importManager.onImported(type));
        t0 = Replacer.value.replace(t0, value);
        fields.add(then(t0, varName));
        return varName;
    }

    public String onDefaultBigInteger(String value) {
        return onDefault(value, BigInteger.class, BigInteger::new);
    }

    public String onDefaultBigDecimal(String value) {
        return onDefault(value, BigDecimal.class, BigDecimal::new);
    }

    private <T> String onDefault(String value, Class<T> type, Function<String, T> newer) {
        value = (value == null ? "" : value).trim();
        if (StringUtils.isEmpty(value)) {
            return NULL;
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
                    return NULL;
                }
        }
        varName = getStaticVar(key);
        String t0 = "{modifiers} {type0} {var} = " + valExp;
        t0 = Replacer.type0.replace(t0, importManager.onImported(type));
        t0 = Replacer.value.replace(t0, value);
        fields.add(then(t0, varName));
        return varName;
    }

    public String onString(String value) {
        if (StringUtils.isEmpty(value)) {
            return NULL;
        }
        String key = getKey(String.class, value);
        String varName = varCached.get(key);
        if (varName != null) {
            return varName;
        }
        char quote = '"';
        String fieldVal;
        varName = getStaticVar(key);
        int length = value.length();
        if (length > 1) {
            if (value.charAt(0) == quote && value.charAt(length - 1) == quote) {
                fieldVal = value;
            } else {
                fieldVal = (quote + value + quote);
            }
        } else {
            fieldVal = (quote + value + quote);
        }
        String t0 = "{modifiers} {type0} {var} = {value};";
        t0 = Replacer.type0.replace(t0, importManager.onImported(String.class));
        t0 = Replacer.value.replace(t0, fieldVal);
        fields.add(then(t0, varName));
        return varName;
    }

    public String onDateTimeFormatter(String format) {
        if (StringUtils.isEmpty(format)) {
            return NULL;
        }
        try {
            DateTimeFormatter.ofPattern(format);
        } catch (Exception e) {
            Logger.warn("【已忽略日期格式化】非法日期格式: {}.", format);
            return NULL;
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
        if (!DetectUtils.IMPORTED_JODA_TIME || StringUtils.isEmpty(format)) {
            return NULL;
        }
        try {
            DateTimeFormat.forPattern(format);
        } catch (Exception e) {
            Logger.warn("【已忽略日期格式化】非法日期格式: {}.", format);
            return NULL;
        }
        String key = getJodaFormatterKey(format);
        String varName = varCached.get(key);
        if (varName == null) {
            varName = getStaticVar(key);
            String declare = "{modifiers} {type0} {var} = {type0}.ofPattern(\"{format}\");";
            declare = Replacer.type0.replace(declare, importManager.onImported(DateTimeFormat.class));
            declare = Replacer.format.replace(declare, format);
            fields.add(then(declare, varName));
        }
        return varName;
    }

    public String onEnumIndexed(String enumClassname, String index) {
        int indexer = 0, idx;
        try {
            idx = Integer.parseInt(index.trim());
        } catch (Exception e) {
            Logger.warn("【已忽略默认值】{} 不存在第 '{}' 个枚举值", enumClassname, index);
            return NULL;
        }
        String key = enumClassname + "#" + idx;
        String varName = varCached.get(key);
        if (varName != null) {
            return varName;
        }
        TypeElement elem = EnvUtils.getUtils().getTypeElement(enumClassname);
        if (!DetectUtils.isEnum(elem)) {
            Logger.warn("【已忽略默认值】{} 不存在第 '{}' 个枚举值", enumClassname, index);
            return NULL;
        }
        String enumConstName = null;
        for (Element child : elem.getEnclosedElements()) {
            if (child.getKind() == ElementKind.ENUM_CONSTANT) {
                if (idx == indexer) {
                    enumConstName = getSimpleName(child);
                    break;
                } else {
                    indexer++;
                }
            }
        }
        if (enumConstName == null) {
            Logger.warn("【已忽略默认值】{} 不存在第 '{}' 个枚举值", enumClassname, index);
            return NULL;
        } else {
            varName = getStaticVar(key);
            String declare = "{modifiers} {type0} {var}={type0}.{name};";
            declare = Replacer.type0.replace(declare, importManager.onImported(enumClassname));
            declare = Replacer.name.replace(declare, enumConstName);
            declare = then(declare, varName);
            fields.add(declare);
        }
        return varName;
    }

    public String onEnumNamed(String enumClassname, String name) {
        name = (name == null ? "" : name).trim();
        if (StringUtils.isEmpty(name)) {
            return NULL;
        }
        String key = getKey(enumClassname, name);
        String varName = varCached.get(key);
        if (varName != null) {
            return varName;
        }

        TypeElement elem = EnvUtils.getUtils().getTypeElement(enumClassname);
        if (!DetectUtils.isEnum(elem)) {
            Logger.warn("【已忽略默认值】{} 不存在枚举项: {}", enumClassname, name);
            return NULL;
        }
        for (Element child : elem.getEnclosedElements()) {
            if (child.getKind() == ElementKind.ENUM_CONSTANT && Objects.equals(getSimpleName(child), name)) {
                varName = getStaticVar(key);
                String declare = "{modifiers} {type0} {var} = {type0}.{name};";
                declare = Replacer.type0.replace(declare, importManager.onImported(enumClassname));
                declare = Replacer.name.replace(declare, name);
                declare = then(declare, varName);
                fields.add(declare);
                return varName;
            }
        }
        Logger.warn("【已忽略默认值】{} 不存在枚举项: {}", enumClassname, name);
        return NULL;
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

    public String defaultNull() { return NULL; }

    public void addStaticField(String key, String field) {
        fields.add(Replacer.var.replace(field, getStaticVar(key)));
    }

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

    private String getKey(Class<?> type, String key) {
        return getKey(type.getCanonicalName(), key);
    }

    private String getKey(String type, String key) {
        return type + ">" + key;
    }
}
