package com.moon.processing.util;

import java.beans.Introspector;
import java.util.*;
import java.util.function.IntPredicate;

/**
 * @author benshaoye
 */
public enum String2 {
    ;

    public static String defaultIfBlank(String str, String defaultVal) {
        return isBlank(str) ? defaultVal : str;
    }

    public static String decapitalize(String name) { return Introspector.decapitalize(name); }

    public static String capitalize(String name) {
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

    public static String keyOf(String... keys) {
        return String.join(":", keys);
    }

    public static String formatTypes(String typeTemplate, Object... types) {
        if (types == null) {
            return typeTemplate;
        }
        Object[] typesStringify = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            Object type = types[i];
            if (type instanceof Class<?>) {
                typesStringify[i] = ((Class<?>) type).getCanonicalName();
            } else {
                typesStringify[i] = String.valueOf(type);
            }
        }
        return String2.format(typeTemplate, typesStringify);
    }

    public static String format(String template, Object... values) {
        return com.moon.accessor.util.String2.format(template, values);
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

    public static boolean isAny(String str, IntPredicate tester) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        for (int i = 0; i < length; i++) {
            if (tester.test(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

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

    public static String toGetterName(String field, String type) {
        return ("boolean".equals(type) ? Const2.IS : Const2.GET) + capitalize(field);
    }

    public static String toSetterName(String field) {
        return Const2.SET + capitalize(field);
    }

    public static String camelcaseToHyphen(String str, char hyphen, boolean continuousSplit) {
        return camelcaseToHyphen(str, hyphen, continuousSplit, true);
    }

    public static boolean isUpper(int ch) { return ch > 64 && ch < 91; }

    public static boolean isLower(int ch) { return ch > 96 && ch < 123; }

    public static boolean isLetter(int ch) { return isUpper(ch) || isLower(ch); }

    /**
     * 截取第一个单词
     *
     * @param string
     *
     * @return
     */
    public static String firstWord(String string) {
        char[] chars = string.trim().toCharArray();
        StringBuilder builder = new StringBuilder(8);
        Boolean firstUpper = null;
        for (char currChar : chars) {
            if (firstUpper == null) {
                if (isUpper(currChar)) {
                    firstUpper = Boolean.TRUE;
                } else if (isLower(currChar)) {
                    firstUpper = Boolean.FALSE;
                } else {
                    continue;
                }
                builder.append(currChar);
            } else if (!isLetter(currChar)) {
                break;
            } else {
                if (firstUpper && isUpper(currChar)) {
                    builder.append(currChar);
                } else if (!firstUpper && isLower(currChar)) {
                    builder.append(currChar);
                } else {
                    break;
                }
            }
        }
        return builder.toString();
    }

    public static String camelcaseToHyphen(String str, char hyphen, boolean continuousSplit, boolean lowerCaseAtPos) {
        final int len = str == null ? 0 : str.length();
        if (len == 0) { return str; }
        boolean prevIsUpper = false, thisIsUpper;
        StringBuilder res = new StringBuilder(len + 5);
        for (int i = 0; i < len; i++) {
            char ch = str.charAt(i);
            if (thisIsUpper = Character.isUpperCase(ch)) {
                ch = lowerCaseAtPos ? Character.toLowerCase(ch) : ch;
                if (i == 0) {
                    res.append(ch);
                } else if (prevIsUpper) {
                    if (continuousSplit) {
                        res.append(hyphen).append(ch);
                    } else if (Character.isLowerCase(str.charAt(i + 1))) {
                        res.append(hyphen).append(ch);
                    } else {
                        res.append(ch);
                    }
                } else {
                    res.append(hyphen).append(ch);
                }
            } else if (!Character.isWhitespace(ch)) {
                res.append(ch);
            }
            prevIsUpper = thisIsUpper;
        }
        return res.toString();
    }

    public static List<String> split(CharSequence str, char separator) {
        List<String> result = new ArrayList<>();
        int length = str == null ? 0 : str.length();
        if (length == 0) {
            return result;
        } else {
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < length; ++i) {
                char ch;
                if ((ch = str.charAt(i)) == separator) {
                    result.add(builder.toString());
                    builder.setLength(0);
                } else {
                    builder.append(ch);
                }
            }

            result.add(builder.toString());
            return result;
        }
    }

    public static String dotClass(Class<?> cls) { return cls == null ? null : dotClass(cls.getCanonicalName()); }

    public static String dotClass(String classname) { return classname == null ? null : (classname + ".class"); }

    public static String toGeneralizableType(String type) {
        switch (type) {
            case "byte":
                return "java.lang.Byte";
            case "short":
                return "java.lang.Short";
            case "int":
                return "java.lang.Integer";
            case "long":
                return "java.lang.Long";
            case "float":
                return "java.lang.Float";
            case "double":
                return "java.lang.Double";
            case "boolean":
                return "java.lang.Boolean";
            case "char":
                return "java.lang.Character";
            default:
                return type;
        }
    }

    public static String strWrapped(CharSequence value) { return "\"" + value + '"'; }

    public static int length(CharSequence sequence) {
        return sequence == null ? 0 : sequence.length();
    }
}
