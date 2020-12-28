package com.moon.processor.utils;

import com.moon.processor.model.DeclareGeneric;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author benshaoye
 */
public enum Generic2 {
    ;

    public static String findActual(Map<String, DeclareGeneric> generics, String declareClassname, String declareType) {
        DeclareGeneric model = generics.get(toFullKey(declareClassname, declareType));
        return model == null ? null : model.getSimpleEffectType();
    }

    public static String findActualOrDeclared(
        Map<String, DeclareGeneric> generics, String declareClassname, String declareType
    ) {
        DeclareGeneric model = generics.get(toFullKey(declareClassname, declareType));
        return model == null ? declareType : model.getEffectType();
    }

    /*
     * static methods
     */

    public static Map<String, DeclareGeneric> from(TypeElement element) {
        return parse(element, new LinkedHashMap<>());
    }

    private static Map<String, DeclareGeneric> parse(TypeElement element, Map<String, DeclareGeneric> thisGenericMap) {
        if (element == null) {
            return thisGenericMap;
        }
        parse(thisGenericMap, element.asType(), element, null);
        Types types = Environment2.getTypes();
        do {
            parseInterfaces(thisGenericMap, element.getInterfaces(), element);
            TypeMirror superclass = element.getSuperclass();
            if (Test2.isTypeof(superclass.toString(), Object.class)) {
                return thisGenericMap;
            }
            TypeElement superElem = (TypeElement) types.asElement(superclass);
            if (superElem == null) {
                return thisGenericMap;
            }
            parse(thisGenericMap, superclass, superElem, element);
            element = superElem;
        } while (true);
    }

    private static void parseInterfaces(
        Map<String, DeclareGeneric> genericMap, List<? extends TypeMirror> elements, TypeElement implElement
    ) {
        if (elements == null || elements.isEmpty()) {
            return;
        }
        Types types = Environment2.getTypes();
        for (TypeMirror mirror : elements) {
            TypeElement element = (TypeElement) types.asElement(mirror);
            parse(genericMap, mirror, element, implElement);
            parseInterfaces(genericMap, element.getInterfaces(), element);
        }
    }

    /**
     * 解析泛型
     * public UsernameGetter&lt;ID&gt; implements Supplier&lt;String&gt; {}
     *
     * @param genericMap   所有泛型 com.name.UsernameGetter#ID == String
     * @param elementTyped 等于 element
     * @param element      等于 elementTyped
     * @param subClass     element 的直接子类
     */
    private static void parse(
        Map<String, DeclareGeneric> genericMap, TypeMirror elementTyped, TypeElement element, TypeElement subClass
    ) {
        if (element == null || elementTyped == null || subClass == null) {
            return;
        }
        List<String> actualAll = splitSuperclass(elementTyped.toString());
        String declareClassname = Element2.getQualifiedName(element);
        String subClassname = Element2.getQualifiedName(subClass);
        Elements utils = Environment2.getUtils();
        int index = 0;
        for (TypeParameterElement param : element.getTypeParameters()) {
            String actual = index < actualAll.size() ? actualAll.get(index++) : null;
            // 追溯实际类
            String fullKey = toFullKey(subClassname, actual);
            DeclareGeneric subGenericModel = genericMap.get(fullKey);
            if (subGenericModel != null && utils.getTypeElement(actual) == null) {
                actual = subGenericModel.getActual();
            }
            String bound = param.getBounds().toString();
            String declare = getDeclareType(param);
            DeclareGeneric model = new DeclareGeneric(declare, actual, bound);
            String key = toFullKey(declareClassname, model.getDeclare());
            genericMap.putIfAbsent(key, model);
        }
    }

    private static String toFullKey(String declareClassname, String declareType) {
        return declareClassname + '#' + declareType;
    }

    private static String getDeclareType(TypeParameterElement parameterElement) {
        return parameterElement.toString();
    }

    private static List<String> splitSuperclass(String fullType) {
        return hasGeneric(fullType) ? split(extractWrapped(fullType)) : new ArrayList<>();
    }

    private static List<String> split(String angleWrapped) {
        char[] chars = unBracketAngle(angleWrapped).toCharArray();
        List<String> spliced = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        int bracket = 0;
        for (char ch : chars) {
            if (isLeft(ch)) {
                builder.append(ch);
                bracket++;
                continue;
            }
            if (isRight(ch)) {
                builder.append(ch);
                bracket--;
                continue;
            }
            if (isComma(ch)) {
                if (bracket > 0) {
                    builder.append(ch);
                    continue;
                }
                spliced.add(builder.toString());
                builder.setLength(0);
            } else {
                builder.append(ch);
            }
        }
        if (builder.length() > 0) {
            spliced.add(builder.toString());
        }
        return spliced;
    }

    private static boolean hasGeneric(String fullActual) {
        return fullActual != null && fullActual.contains("<");
    }

    private static String extractWrapped(String str) {
        return str.substring(str.indexOf('<'), str.lastIndexOf('>') + 1);
    }

    private static String unBracketAngle(String str) { return str.substring(1, str.length() - 1); }

    private static boolean isLeft(int ch) { return ch == '<'; }

    private static boolean isRight(int ch) { return ch == '>'; }

    private static boolean isComma(int ch) { return ch == ','; }
}
