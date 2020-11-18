package com.moon.mapping.processing;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author benshaoye
 */
@Configuration
@ConditionalOnMissingBean
abstract class DetectUtils {

    final static boolean IMPORTED_LOMBOK;
    final static boolean IMPORTED_CONFIGURATION;
    final static boolean IMPORTED_MISSING_BEAN;
    final static boolean IMPORTED_BEAN;
    final static boolean IMPORTED_JODA_TIME;

    private final static String GET = "get", SET = "set", IS = "is", GET_CLASS = "getClass";

    static {
        boolean isImportedLombok;
        try {
            Data.class.toString();
            Getter.class.toString();
            Setter.class.toString();
            isImportedLombok = true;
        } catch (Throwable t) {
            isImportedLombok = false;
        }
        IMPORTED_LOMBOK = isImportedLombok;
        boolean isImportedConfig;
        try {
            Configuration.class.toString();
            isImportedConfig = true;
        } catch (Throwable t) {
            isImportedConfig = false;
        }
        IMPORTED_CONFIGURATION = isImportedConfig;
        boolean isImportedMissingBean;
        try {
            ConditionalOnMissingBean.class.toString();
            isImportedMissingBean = true;
        } catch (Throwable t) {
            isImportedMissingBean = false;
        }
        IMPORTED_MISSING_BEAN = isImportedMissingBean;
        boolean isImportedBean;
        try {
            Bean.class.toString();
            isImportedBean = true;
        } catch (Throwable t) {
            isImportedBean = false;
        }
        IMPORTED_BEAN = isImportedBean;
        boolean importedJodaTime;
        try {
            DateTime.now().toString();
            importedJodaTime = true;
        } catch (Throwable t) {
            importedJodaTime = false;
        }
        IMPORTED_JODA_TIME = importedJodaTime;
    }

    private DetectUtils() { }

    static void assertRootElement(TypeElement rootElement) {
        final Element parentElement = rootElement.getEnclosingElement();
        // 如果是类文件，要求必须是 public
        if (parentElement.getKind() == ElementKind.PACKAGE) {
            if (!DetectUtils.isPublic(rootElement)) {
                String thisClassname = rootElement.getQualifiedName().toString();
                throw new IllegalStateException("类 " + thisClassname + " 必须是被 public 修饰的公共类。");
            }
        }
    }

    static boolean hasLombokSetter(VariableElement field) {
        if (isImportedLombok()) {
            if (field == null) {
                return false;
            }
            Setter setter = field.getAnnotation(Setter.class);
            if (setter != null) {
                return setter.value() == AccessLevel.PUBLIC;
            } else {
                Element element = field.getEnclosingElement();
                return element.getAnnotation(Data.class) != null;
            }
        }
        return false;
    }

    static boolean hasLombokGetter(VariableElement field) {
        if (isImportedLombok()) {
            if (field == null) {
                return false;
            }
            Getter getter = field.getAnnotation(Getter.class);
            if (getter != null) {
                return getter.value() == AccessLevel.PUBLIC;
            } else {
                Element element = field.getEnclosingElement();
                return element.getAnnotation(Data.class) != null;
            }
        }
        return false;
    }


    static boolean isImportedLombok() { return IMPORTED_LOMBOK; }

    static boolean isPublic(Element elem) {
        return elem != null && elem.getModifiers().contains(Modifier.PUBLIC);
    }

    static boolean isMember(Element elem) {
        return elem != null && !elem.getModifiers().contains(Modifier.STATIC);
    }

    static boolean isMethod(Element elem) {
        return elem instanceof ExecutableElement && elem.getKind() == ElementKind.METHOD;
    }

    static boolean isEnum(Element elem) {
        return elem != null && elem.getKind() == ElementKind.ENUM;
    }

    static boolean isField(Element elem) {
        return elem instanceof VariableElement && elem.getKind() == ElementKind.FIELD;
    }

    static boolean isDigit(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    static boolean isVar(String str){
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        char value = str.trim().charAt(0);
        return Character.isLetter(value) || value == '_' || value == '$';
    }

    static boolean isAny(Element elem, Modifier modifier, Modifier... modifiers) {
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

    static boolean isNotAny(Element elem, Modifier modifier, Modifier... modifiers) {
        return !isAny(elem, modifier, modifiers);
    }

    static boolean isAnyNull(Object... values) {
        for (Object value : values) {
            if (value == null) {
                return true;
            }
        }
        return false;
    }

    static boolean isPackage(Element elem) {
        return elem.getKind() == ElementKind.PACKAGE;
    }

    static boolean isMemberField(Element elem) {
        return isField(elem) && isMember(elem);
    }

    static boolean isSetterMethod(Element elem) {
        if (isMethod(elem) && isMember(elem) && isPublic(elem)) {
            ExecutableElement exe = (ExecutableElement) elem;
            String name = exe.getSimpleName().toString();
            boolean maybeSet = name.length() > 3 && name.startsWith(SET);
            maybeSet = maybeSet && exe.getParameters().size() == 1;
            maybeSet = maybeSet && exe.getReturnType().getKind() == TypeKind.VOID;
            return maybeSet;
        }
        return false;
    }

    static boolean isGetterMethod(Element elem) {
        if (isMethod(elem) && isMember(elem) && isPublic(elem)) {
            ExecutableElement exe = (ExecutableElement) elem;
            String name = exe.getSimpleName().toString();
            boolean maybeGet = exe.getParameters().isEmpty();
            if (name.startsWith(GET)) {
                return maybeGet && name.length() > 3 && !name.equals(GET_CLASS);
            } else if (name.startsWith(IS)) {
                return maybeGet && name.length() > 2 && exe.getReturnType().getKind() == TypeKind.BOOLEAN;
            }
        }
        return false;
    }

    static boolean isConstructor(Element elem) {
        return elem != null && elem.getKind() == ElementKind.CONSTRUCTOR;
    }
}
