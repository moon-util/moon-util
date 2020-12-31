package com.moon.processor.manager;

import com.moon.processor.utils.*;
import org.joda.time.format.DateTimeFormat;

import javax.lang.model.element.Element;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

/**
 * @author benshaoye
 */
public class ConstManager {

    private final Importer importer;
    private final AtomicInteger indexer = new AtomicInteger();
    private final Map<String, String> varsCached = new LinkedHashMap<>();
    private final Map<String, String> scriptsMap = new LinkedHashMap<>();

    public ConstManager(Importer importer) { this.importer = importer; }

    private String onImported(String classname) { return importer.onImported(classname); }

    private String onImported(Class<?> classname) { return importer.onImported(classname); }

    private String nextVar() { return "V" + indexer.getAndIncrement(); }

    private String computeConst(Class<?> type, String value, BiFunction<String, String, String> puller) {
        return computeConst(type.getCanonicalName(), value, puller);
    }

    private String computeConst(String type, String value, BiFunction<String, String, String> puller) {
        String key = toKey(type, value);
        String var = varsCached.get(key);
        if (var == null) {
            String pulled = puller.apply(type, value);
            if (pulled != null) {
                var = nextVar();
                String declare = "private final static " + pulled;
                scriptsMap.put(var, Holder.var.on(declare, var));
                varsCached.put(key, var);
            } else {
                return null;
            }
        }
        return var;
    }

    final HolderGroup typeValue = Holder.of(Holder.type, Holder.value);

    public String[] getScripts() { return scriptsMap.values().toArray(new String[0]); }

    public String charOf(String type, String value) {
        if (String2.isEmpty(value)) {
            return null;
        }
        if (String2.isBlank(value)) {
            value = " ";
        } else {
            value = value.trim();
        }
        return computeConst(type, value, (t, v) -> {
            if (Test2.isBasicCharValue(t, v)) {
                String t0 = "{type} {var} = '{value}';";
                return typeValue.on(t0, onImported(t), v.substring(0, 1));
            } else {
                return ignored(t, v);
            }
        });
    }

    public String booleanOf(String type, String value) {
        if (String2.isBlank(value)) {
            return null;
        }
        return computeConst(type, value, (t, v) -> {
            String t0 = "{type} {var} = {value};";
            return typeValue.on(t0, onImported(t), Boolean.valueOf(v).toString());
        });
    }

    /**
     * @param type
     * @param value
     *
     * @return
     *
     * @see Byte#valueOf(String)
     * @see Short#valueOf(String)
     * @see Integer#valueOf(String)
     * @see Long#valueOf(String)
     * @see Float#valueOf(String)
     * @see Double#valueOf(String)
     * @see Byte#Byte(String)
     * @see Short#Short(String)
     * @see Integer#Integer(String)
     * @see Long#Long(String)
     * @see Float#Float(String)
     * @see Double#Double(String)
     */
    public String numberOf(String type, String value) {
        if (String2.isBlank(value)) {
            return null;
        }
        return computeConst(type, value, (t, v) -> {
            String t0 = "{type} {var} = {value};";
            if (Test2.isBasicNumberValue(t, v)) {
                return typeValue.on(t0, onImported(t), v);
            }
            String dbt = "java.lang.Double";
            if (Test2.isTypeof(t, Number.class) && Test2.isBasicNumberValue(dbt, v)) {
                return typeValue.on(t0, onImported(dbt), v);
            }
            return null;
        });
    }

    public String bigIntegerOf(String value) {
        return defaultMathNumber(BigInteger.class, value);
    }

    public String bigDecimalOf(String value) {
        return defaultMathNumber(BigDecimal.class, value);
    }

    public String jdk8DateTimeFormatter(String pattern) {
        if (String2.isBlank(pattern)) {
            return null;
        }
        return computeConst(DateTimeFormatter.class, pattern, (t, v) -> {
            if (Test2.isValid(v, DateTimeFormatter::ofPattern)) {
                String t0 = "{type} {var} = {type}.ofPattern(\"{value}\");";
                return typeValue.on(t0, onImported(t), v);
            }
            return null;
        });
    }

    public String jodaDateTimeFormatter(String pattern) {
        if (String2.isBlank(pattern)) {
            return null;
        }
        return computeConst(org.joda.time.format.DateTimeFormatter.class, pattern, (t, v) -> {
            if (Test2.isValid(v, DateTimeFormatter::ofPattern)) {
                String t0 = "{type} {var} = {type0}.ofPattern(\"{value}\");";
                t0 = typeValue.on(t0, onImported(t), v);
                return Holder.type0.on(t0, onImported(DateTimeFormat.class));
            }
            return null;
        });
    }

    public String enumOf(String type, String value) {
        if (String2.isBlank(value)) {
            return null;
        }
        value = value.trim();
        char first = value.charAt(0);
        if (first > 47 && first < 58) {
            return indexedEnumOf(type, value);
        } else {
            return namedEnumOf(type, value);
        }
    }

    public String indexedEnumOf(String type, String index) {
        if (String2.isBlank(index)) {
            return null;
        }
        return computeConst(type, index.trim(), (t, v) -> {
            if (Test2.isValid(v, Integer::valueOf)) {
                int idx = Integer.valueOf(v);
                Element enumConst = Element2.findEnumAt(t, idx);
                if (enumConst != null) {
                    String name = Element2.getSimpleName(enumConst);
                    String t0 = "{type} {var} = {type}.{value};";
                    return typeValue.on(t0, onImported(t), name);
                }
            }
            return ignored(t, v);
        });
    }

    public String namedEnumOf(String type, String name) {
        if (String2.isBlank(name)) {
            return null;
        }
        return computeConst(type, name.trim(), (t, v) -> {
            if (Test2.isValid(v, Integer::valueOf)) {
                Element enumConst = Element2.findEnumAs(t, v);
                if (enumConst != null) {
                    String value = Element2.getSimpleName(enumConst);
                    String t0 = "{type} {var} = {type}.{value};";
                    return typeValue.on(t0, onImported(t), value);
                }
            }
            return ignored(t, v);
        });
    }

    public String enumValuesOf(String type) {
        return computeConst(type, "<values>", (t, ignore) -> {
            String t0 = "{type} {var} = {type}.values();";
            return Holder.type.on(t0, onImported(t));
        });
    }

    public String stringOf(String value) {
        return computeConst(String.class, value, (t, v) -> {
            final int length = v.length();
            final char quote = '"';
            String t0 = "{type} {var} = {value};", quoted = v;
            if (!(length > 1 && v.charAt(0) == quote && v.charAt(length - 1) == quote)) {
                quoted = (quote + String2.replaceAll(v, "\"", "\\\"") + quote);
            }
            return typeValue.on(t0, onImported(t), quoted);
        });
    }

    public String defaultMathNumber(Class<?> mathType, String value) {
        if (String2.isBlank(value)) {
            return null;
        }
        return computeConst(mathType, value, (t, v) -> {
            if (v == null) {
                return null;
            }
            String trimmed = v.trim(), constant;
            switch (trimmed) {
                case "0":
                    constant = "{type}.ZERO;";
                    break;
                case "1":
                    constant = "{type}.ONE;";
                    break;
                case "10":
                    constant = "{type}.TEN;";
                    break;
                default:
                    boolean isBigInteger = mathType == BigInteger.class && Test2.isValid(v, BigInteger::new);
                    boolean isBigDecimal = mathType == BigDecimal.class && Test2.isValid(v, BigDecimal::new);
                    if (isBigInteger || isBigDecimal) {
                        constant = "new {type}({value});";
                        break;
                    } else {
                        return ignored(t, v);
                    }
            }
            return typeValue.on("{type} {var} = " + constant, onImported(t), v);
        });
    }

    private static String ignored(String type, String value) {
        Log2.warning("【已忽略默认值】{}: {}", type, value);
        return null;
    }

    private static String toKey(String... values) {
        return String.join(":", values);
    }
}
