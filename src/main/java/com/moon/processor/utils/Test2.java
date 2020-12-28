package com.moon.processor.utils;

import com.moon.processor.Args;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.moon.processor.utils.Environment2.getTypes;
import static javax.lang.model.element.ElementKind.*;

/**
 * @author benshaoye
 */
public enum Test2 {
    ;
    private final static String GET = "get", SET = "set", IS = "is", GET_CLASS = "getClass";

    public static boolean isEnum(String value) {
        return value != null && isEnum(Environment2.getUtils().getTypeElement(value));
    }

    public static boolean isPublic(Element elem) { return isAny(elem, Modifier.PUBLIC); }

    public static boolean isMember(Element elem) { return isNotAny(elem, Modifier.STATIC); }

    public static boolean isMethod(Element elem) {
        return elem instanceof ExecutableElement && isElemKind(elem, METHOD);
    }

    public static boolean isEnum(Element elem) { return isElemKind(elem, ENUM); }

    public static boolean isNotEnum(Element elem) { return !isEnum(elem); }

    public static boolean isAbstractClass(TypeElement type) {
        return isElemKind(type, INTERFACE) || isAny(type, Modifier.ABSTRACT);
    }

    public static boolean isField(Element elem) { return elem instanceof VariableElement && isElemKind(elem, FIELD); }

    public static boolean isDigit(String str) {
        if (String2.isEmpty(str)) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isVar(String str) {
        if (String2.isEmpty(str)) {
            return false;
        }
        char value = str.trim().charAt(0);
        return Character.isLetter(value) || value == '_' || value == '$';
    }

    public static boolean isAny(Element elem, Modifier modifier, Modifier... modifiers) {
        Set<Modifier> modifierSet = elem.getModifiers();
        boolean contains = modifierSet.contains(modifier);
        if (contains) {
            return true;
        } else if (modifiers != null) {
            for (Modifier m : modifiers) {
                if (modifierSet.contains(m)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isNotAny(Element elem, Modifier modifier, Modifier... modifiers) {
        return !isAny(elem, modifier, modifiers);
    }

    public static boolean isAnyNull(Object... values) {
        for (Object value : values) {
            if (value == null) {
                return true;
            }
        }
        return false;
    }

    public static boolean isTypeKind(TypeMirror elem, TypeKind kind) { return elem != null && elem.getKind() == kind; }

    public static boolean isElemKind(Element elem, ElementKind kind) { return elem != null && elem.getKind() == kind; }

    public static boolean isMappableElement(Element elem) { return elem != null; }

    public static boolean isConstructor(Element elem) { return isElemKind(elem, CONSTRUCTOR); }

    public static boolean isPackage(Element elem) { return isElemKind(elem, PACKAGE); }

    public static boolean isMemberField(Element elem) { return isField(elem) && isMember(elem); }

    static boolean isPublicMemberMethod(Element elem) {
        return isMethod(elem) && isMember(elem) && isPublic(elem);
    }

    static boolean isSetterMethod(Element elem) {
        if (isPublicMemberMethod(elem)) {
            ExecutableElement exe = (ExecutableElement) elem;
            String name = exe.getSimpleName().toString();
            boolean maybeSet = name.length() > 3 && name.startsWith(SET);
            maybeSet = maybeSet && exe.getParameters().size() == 1;
            maybeSet = maybeSet && isTypeKind(exe.getReturnType(), TypeKind.VOID);
            return maybeSet;
        }
        return false;
    }

    static boolean isGetterMethod(Element elem) {
        if (isPublicMemberMethod(elem)) {
            ExecutableElement exe = (ExecutableElement) elem;
            String name = exe.getSimpleName().toString();
            boolean maybeGet = exe.getParameters().isEmpty();
            if (name.startsWith(GET)) {
                return maybeGet && name.length() > 3 && !name.equals(GET_CLASS);
            } else if (name.startsWith(IS)) {
                return maybeGet && name.length() > 2 && isTypeKind(exe.getReturnType(), TypeKind.BOOLEAN);
            }
        }
        return false;
    }

    public static boolean isTypeofAny(String actual, String... expected) {
        for (String aClass : expected) {
            if (isTypeof(actual, aClass)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isTypeofAny(String actual, Class<?>... expected) {
        for (Class<?> aClass : expected) {
            if (isTypeof(actual, aClass)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isTypeof(String actual, Class<?> expected) {
        return isTypeof(expected.getCanonicalName(), actual);
    }

    public static boolean isTypeof(String actual, String expected) {
        return expected.equals(actual);
    }

    public static boolean isSubtypeOf(String actualType, Class<?> superclass) {
        return isSubtypeOf(actualType, superclass.getCanonicalName());
    }

    public static boolean isSubtypeOf(String actualType, String superClass) {
        if (actualType == null || superClass == null) {
            return false;
        }
        Elements utils = Environment2.getUtils();
        return isSubtypeOf(utils.getTypeElement(actualType), utils.getTypeElement(superClass));
    }

    public static boolean isSubtypeOf(TypeElement elem1, TypeElement elem2) {
        return elem1 != null && elem2 != null && getTypes().isSubtype(elem1.asType(), elem2.asType());
    }

    final static Args<Collection<String>, Class<?>> WITHER = (c, cs) -> Arrays.stream(cs)
        .map(Class::getCanonicalName)
        .forEach(c::add);
    private final static Set<String> PRIMITIVE_NUMS = new HashSet<>();
    private final static Set<String> BASIC_TYPES = new HashSet<>();

    static {
        WITHER.with(PRIMITIVE_NUMS, byte.class, short.class, int.class, long.class, float.class, double.class);
        WITHER.with(BASIC_TYPES, Object.class);
        WITHER.with(BASIC_TYPES, String.class, StringBuilder.class, StringBuffer.class, CharSequence.class);
        WITHER.with(BASIC_TYPES, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class);
        WITHER.with(BASIC_TYPES, void.class, char.class, boolean.class, Character.class, Boolean.class, Number.class);
    }

    public static boolean isBasicType(Class<?> type) { return isBasicType(getName(type)); }

    public static boolean isBasicType(String type) { return BASIC_TYPES.contains(type) || isPrimitive(type); }

    public static boolean isPrimitive(Class<?> type) { return isPrimitive(getName(type)); }

    public static boolean isPrimitive(String type) {
        return isPrimitiveNumber(type) || isPrimitiveBool(type) || isPrimitiveChar(type);
    }

    public static boolean isPrimitiveNumber(Class<?> type) { return isPrimitiveNumber(getName(type)); }

    public static boolean isPrimitiveNumber(String type) { return PRIMITIVE_NUMS.contains(type); }

    public static boolean isPrimitiveBool(Class<?> type) { return isPrimitiveBool(getName(type)); }

    public static boolean isPrimitiveBool(String type) { return "boolean".contains(type); }

    public static boolean isPrimitiveChar(Class<?> type) { return isPrimitiveChar(getName(type)); }

    public static boolean isPrimitiveChar(String type) { return "char".contains(type); }

    private static String getName(Class<?> cls) { return cls.getCanonicalName(); }
}
