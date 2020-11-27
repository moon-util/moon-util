package com.moon.mapping.processing;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author moonsky
 */
final class GenericUtils {

    private GenericUtils() {}

    static Map<String, GenericModel> parse(TypeElement element) {
        Map<String, GenericModel> thisGenericMap = new HashMap<>();
        parse(thisGenericMap,element.asType(), element);
        do {
            TypeMirror superclass = element.getSuperclass();
            if (DetectUtils.isTypeof(superclass.toString(), Object.class)) {
                return thisGenericMap;
            }
            element = (TypeElement) EnvUtils.getTypes().asElement(superclass);
            parse(thisGenericMap, superclass, element);
        } while (true);
    }

    static void parse(Map<String, GenericModel> genericMap,TypeMirror elementTyped, TypeElement element) {
        List<String> actuals = Extract.splitSuperclass(elementTyped.toString());
        int index = 0;
        for (TypeParameterElement param : element.getTypeParameters()) {
            String actual = index < actuals.size() ? actuals.get(index++) : null;
            GenericModel model = new GenericModel(param, actual);
            genericMap.putIfAbsent(model.getDeclareType(), model);
        }
    }

    static String findActualType(Map<String, GenericModel> generics, String declareType) {
        GenericModel model = generics.get(declareType);
        return model == null ? null : model.getSimpleFinalType();
    }

    private static class Extract {

        static List<String> splitSuperclass(String fullType) {
            return hasGeneric(fullType) ? split(extractWrapped(fullType)) : new ArrayList<>();
        }

        static List<String> split(String angleWrapped) {
            char[] chars = unbracketAngle(angleWrapped).toCharArray();
            List<String> spliced = new ArrayList<>();
            StringBuilder builder = new StringBuilder();
            int bracket = 0;
            for (char ch : chars) {
                if (Bound.isLeft(ch)) {
                    builder.append(ch);
                    bracket++;
                    continue;
                }
                if (Bound.isRight(ch)) {
                    builder.append(ch);
                    bracket--;
                    continue;
                }
                if (Bound.isComma(ch)) {
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

        private static String unbracketAngle(String str) {
            return str.substring(1, str.length() - 1);
        }
    }

    private final static class Bound {

        private static boolean isLeft(int ch) { return ch == '<'; }

        private static boolean isRight(int ch) { return ch == '>'; }

        private static boolean isComma(int ch) { return ch == ','; }
    }
}
