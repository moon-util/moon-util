package com.moon.processor.utils;

import java.beans.Introspector;
import java.util.Arrays;

/**
 * @author benshaoye
 */
public enum String2 {
    ;

    static String decapitalize(String name) { return Introspector.decapitalize(name); }

    static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        if (name.length() > 1 && Character.isUpperCase(name.charAt(1))) {
            return name;
        }
        char[] chars = name.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    public static String indent(int indent) {
        char[] chars = new char[indent];
        Arrays.fill(chars, ' ');
        return new String(chars);
    }

    public static StringBuilder newLine(StringBuilder builder) {
        return builder.append('\n');
    }

    public static StringBuilder newLine(StringBuilder builder, String indent) {
        return newLine(builder).append(indent);
    }

    public static boolean isBlank(String str) {
        if (isEmpty(str)) {
            return true;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(String str) { return !isBlank(str); }

    public static boolean isEmpty(String str) { return str == null || str.isEmpty(); }

    public static boolean isNotEmpty(String str) { return !isEmpty(str); }

    public static String replaceAll(String input, String search, String replacement) {
        if (isEmpty(input) || isEmpty(search) || replacement == null) {
            return input;
        }
        int index = input.indexOf(search);
        if (index == -1) {
            return input;
        }
        int capacity = input.length();
        if (replacement.length() > search.length()) {
            capacity += 16;
        }
        StringBuilder sb = new StringBuilder(capacity);
        int idx = 0;
        int searchLen = search.length();
        while (index >= 0) {
            sb.append(input, idx, index);
            sb.append(replacement);
            idx = index + searchLen;
            index = input.indexOf(search, idx);
        }
        sb.append(input, idx, input.length());
        return sb.toString();
    }

    private final static HolderGroup GROUP = Holder.of(Holder.type, Holder.name, Holder.field);

    public static String toConstField(String type, String name, String value) {
        // 这里实际应该是 {value}，为了重用 GROUP，就写成了 {field}
        String template = "private final static {type} {name} = {field};";
        return GROUP.on(template, type, name, value);
    }

    public static String toDeclareField(String field, String type) {
        String template = "private {type} {field};";
        return GROUP.on(template, type, "", field);
    }

    public static String toGetterMethod(String field, String type) {
        String prefix = "boolean".equals(type) ? Const2.IS : Const2.GET;
        return toGetterMethod(prefix + capitalize(field), field, type);
    }

    public static String toGetterMethod(String getterName, String field, String type) {
        String template = "public {type} {name}() { return this.{field}; }";
        return GROUP.on(template, type, getterName, field);
    }

    public static String toSetterMethod(String field, String type) {
        return toSetterMethod(Const2.SET + capitalize(field), field, type);
    }

    public static String toSetterMethod(String setterName, String field, String type) {
        String template = "public void {name}({type} {field}) { this.{field} = {field}; }";
        return GROUP.on(template, type, setterName, field);
    }
}
