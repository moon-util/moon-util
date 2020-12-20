package com.moon.data.jdbc.processing;

import com.moon.core.lang.StringUtil;

import javax.lang.model.element.Element;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.type.MirroredTypeException;
import java.beans.Introspector;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

/**
 * @author benshaoye
 */
abstract class StringUtils {

    private StringUtils() {}

    public static String getPackageName(Element elem) {
        return getQualifiedName(EnvUtils.getUtils().getPackageOf(elem));
    }

    public static String getQualifiedName(QualifiedNameable elem) {
        return elem.getQualifiedName().toString();
    }

    public static String getSimpleName(Element elem) {
        return elem.getSimpleName().toString();
    }

    public static String getSimpleName(String fullName) {
        int last = fullName.indexOf("<"), idx;
        if (last > 0) {
            idx = fullName.lastIndexOf('.', last);
        } else {
            idx = fullName.lastIndexOf('.');
            last = fullName.length();
        }
        return fullName.substring(Math.max(idx + 1, 0), last);
    }

    public static <T> String getAnnotatedClass(T t, Function<T, Class<?>> classGetter) {
        try {
            return classGetter.apply(t).getCanonicalName();
        } catch (MirroredTypeException mirrored) {
            return mirrored.getTypeMirror().toString();
        }
    }

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

    static String format(boolean appendIfOver, String template, Object... values) {
        if (values != null) {
            for (Object value : values) {
                if (template.contains("{}")) {
                    template = template.replaceFirst("\\{\\}", value == null ? "null" : value.toString());
                } else if (appendIfOver) {
                    template += ", " + value;
                } else {
                    break;
                }
            }
        }
        return template;
    }

    static boolean isNotAnyBlank(Collection<? extends CharSequence> sequences) {
        if (CollectUtils.isNotEmpty(sequences)) {
            for (CharSequence sequence : sequences) {
                if (sequence == null || StringUtils.isBlank(sequence.toString())) {
                    return false;
                }
            }
        }
        return true;
    }

    static boolean isNotBlank(String str) { return !isBlank(str); }

    static boolean isBlank(String str) {
        return StringUtil.isBlank(str);
        // if (str == null) {
        //     return true;
        // }
        // int strLen = str.length();
        // for (int i = 0; i < strLen; i++) {
        //     if (!Character.isWhitespace(str.charAt(i))) {
        //         return false;
        //     }
        // }
        // return strLen == 0;
    }

    private final static Replacer.Group GROUP = Replacer.of(Replacer.type, Replacer.name, Replacer.field);

    public static String toDeclareField(String fieldName, String type) {
        String template = "private {type} {field};";
        return GROUP.replace(template, type, "", fieldName);
    }

    public static String toGetterMethod(String field, String type) {
        String prefix = isPrimitiveBoolean(type) ? Const.IS : Const.GET;
        return toGetterMethod(prefix + capitalize(field), field, type);
    }

    public static String toGetterMethod(String getterName, String field, String type) {
        String template = "public {type} {name}() { return this.{field}; }";
        return GROUP.replace(template, type, getterName, field);
    }

    public static String toSetterMethod(String field, String type) {
        return toSetterMethod(Const.SET + capitalize(field), field, type);
    }

    public static String toSetterMethod(String setterName, String field, String type) {
        String template = "public void {name}({type} {field}) { this.{field} = {field}; }";
        return GROUP.replace(template, type, setterName, field);
    }

    static boolean isPrimitive(String type) {
        return isPrimitiveNumber(type) || isPrimitiveBoolean(type) || isPrimitiveChar(type);
    }

    static boolean isPrimitiveChar(String type) { return "char".equals(type); }

    static boolean isPrimitiveBoolean(String type) { return "boolean".equals(type); }

    static boolean isPrimitiveNumber(String type) {
        if (type == null) {
            return false;
        }
        switch (type) {
            case "byte":
            case "short":
            case "int":
            case "long":
            case "float":
            case "double":
                return true;
            default:
                return false;
        }
    }

    public static String indent(int count) {
        char[] chars = new char[count];
        Arrays.fill(chars, ' ');
        return new String(chars);
    }

    public static void addInlineComment(StringBuilder sb, int indent, String comment) {
        nextLine(sb).append(indent(indent)).append("/* ").append(comment).append(" */");
    }

    public static void addBlockComment(StringBuilder sb, int indent, String... comments) {
        String space = indent(indent);
        nextLine(sb);
        sb.append(space).append("/*");
        for (String comment : comments) {
            nextLine(sb);
            sb.append(space).append(" * ").append(comment);
        }
        nextLine(sb).append(space).append(" */");
    }

    public static StringBuilder nextLine(StringBuilder sb) {
        sb.append('\n');
        return sb;
    }

    public static StringBuilder newLine(StringBuilder sb) { return nextLine(sb); }

    public static boolean isEmpty(String str) { return (str == null || str.isEmpty()); }

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
}
