package com.moon.processing.decl;

import com.moon.processor.model.DeclareGeneric;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.Environment2;
import com.moon.processor.utils.Test2;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.*;

/**
 * @author benshaoye
 */
public enum Generic2 {
    ;

    /**
     * 完整的映射实际类型
     *
     * @param generics           所有泛型
     * @param enclosingClassname 声明类
     * @param declareType        声明类型（声明类型可能是泛型声明，也可能是实际类，如果是实际类，这里也能正确返回完整的实际类名）
     *
     * @return
     */
    public static String mapToActual(
        Map<String, GenericDeclared> generics, String enclosingClassname, String declareType
    ) {
        StringBuilder actual = new StringBuilder();
        StringBuilder tempBuilder = new StringBuilder();
        char[] chars = declareType.toCharArray();
        for (char ch : chars) {
            switch (ch) {
                case ' ':
                    break;
                case '<':
                case '>':
                case ',':
                    actual.append(toActual(generics, enclosingClassname, tempBuilder)).append(ch);
                    tempBuilder.setLength(0);
                    break;
                default:
                    tempBuilder.append(ch);
                    break;
            }
        }
        actual.append(toActual(generics, enclosingClassname, tempBuilder));
        return actual.toString().trim();
    }

    private static String toActual(
        Map<String, GenericDeclared> generics, String enclosingClassname, StringBuilder declareBuilder
    ) {
        String declared = declareBuilder.toString().trim();
        String key = toFullKey(enclosingClassname, declared);
        GenericDeclared generic = generics.get(key);
        return generic == null ? declared : generic.getEffectType();
    }

    /**
     * 解析应用于特定方法上的泛型声明
     * <p>
     * 这个通常用于解析静态方法，因为静态方法不会依赖所在类的泛型声明
     *
     * @param method
     *
     * @return
     */
    public static Map<String, GenericDeclared> from(ExecutableElement method) {
        return from(method, Collections.emptyMap());
    }

    /**
     * 解析应用于特定方法上的泛型声明
     * <p>
     * 通常用于解析成员发方法，成员方法可能会依赖所在类的泛型声明
     *
     * @param method
     * @param declaredGenericMap 这个参数用于传递所在类的泛型声明，如果方法上存在，不能返回本身
     *                           如果不存在，应该返回本身，但是使用方不应该将是否返回本身用作特殊条件判断
     *
     * @return 方法上的泛型声明，相同的泛型声明，方法上的实际类型会覆盖所在类上的泛型声明，所以这里如果方法存在泛型声明
     * 不能返回{@code declaredGenericMap}
     */
    public static Map<String, GenericDeclared> from(
        ExecutableElement method, Map<String, GenericDeclared> declaredGenericMap
    ) {
        return declaredGenericMap;
    }

    public static String typeSimplify(String classname) {
        if (classname == null) {
            return null;
        }
        int index = classname.indexOf('<');
        return index < 0 ? classname : classname.substring(0, index);
    }

    /**
     * 解析类/接口上的泛型
     * <p>
     * 返回所有泛型声明对应的实际类型:
     * <p>
     * classname#T : java.lang.String
     *
     * @param element
     *
     * @return
     */
    public static Map<String, GenericDeclared> from(TypeElement element) {
        return parse(element, new LinkedHashMap<>());
    }

    private static Map<String, GenericDeclared> parse(
        TypeElement element, Map<String, GenericDeclared> thisGenericMap
    ) {
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
        Map<String, GenericDeclared> genericMap, List<? extends TypeMirror> elements, TypeElement implElement
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
        Map<String, GenericDeclared> genericMap, TypeMirror elementTyped, TypeElement element, TypeElement subClass
    ) {
        if (element == null || elementTyped == null) {
            return;
        }
        List<String> actualAll = splitSuperclass(elementTyped.toString());
        String declareClassname = Element2.getQualifiedName(element);
        String subClassname = subClass == null ? "" : Element2.getQualifiedName(subClass);
        Elements utils = Environment2.getUtils();
        int index = 0;
        for (TypeParameterElement param : element.getTypeParameters()) {
            String actual = index < actualAll.size() ? actualAll.get(index++) : null;
            // 存在多级继承时追溯实际类
            String fullKey = toFullKey(subClassname, actual);
            GenericDeclared subGenericModel = genericMap.get(fullKey);
            if (subGenericModel != null && utils.getTypeElement(actual) == null) {
                actual = subGenericModel.getActual();
            }
            // 泛型边界只取第一个，多边界的情况不考虑
            String bound = param.getBounds().get(0).toString();
            String declare = getDeclareType(param);
            GenericDeclared model = new GenericDeclared(declare, actual, bound);
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
            if (ch == ' ') {
                continue;
            }
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
