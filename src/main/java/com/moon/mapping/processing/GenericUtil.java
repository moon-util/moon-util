package com.moon.mapping.processing;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author moonsky
 */
final class GenericUtil {

    private GenericUtil() {}

    @SuppressWarnings("all")
    private static List<GenericModel> parse(TypeMirror superclass, TypeElement typedSuperclass, Types types) {
        // 解析泛型声明，不需要这段，但还是留着
        List<? extends TypeParameterElement> typeParameters = typedSuperclass.getTypeParameters();
        TypeMirror[] typeMirrors = typeParameters.stream().map(elem -> elem.asType()).toArray(TypeMirror[]::new);
        DeclaredType declaredType = types.getDeclaredType(typedSuperclass, typeMirrors);
        String fullDeclaredType = Extract.extractWrapped(declaredType.toString());
        List<String> declareSpliced = Extract.split(fullDeclaredType);

        // 解析泛型实际使用类型
        String fullActualType = Extract.extractWrapped(superclass.toString());
        List<String> actualSpliced = Extract.split(fullActualType);
        List<GenericModel> genericModels = new ArrayList<>();

        // 执行映射
        int index = 0;
        for (TypeParameterElement param : typedSuperclass.getTypeParameters()) {
            String declare = param.toString();
            String bound = param.toString();
            genericModels.add(new GenericModel(declare, actualSpliced.get(index), bound));
        }
        return genericModels;
    }

    /**
     * 还有问题
     *
     * @param superclass
     * @param typedSuperclass
     *
     * @return
     */
    @SuppressWarnings("all")
    static Map<String, GenericModel> parse(
        TypeMirror superclass, TypeElement typedSuperclass, ProcessingEnvironment env
    ) {
        Map<String, GenericModel> genericMap = new HashMap<>();
        List<String> actuals = Extract.splitSuperclass(superclass.toString());
        int index = 0;
        for (TypeParameterElement param : typedSuperclass.getTypeParameters()) {
            String actual = index < actuals.size() ? actuals.get(index++) : null;
            GenericModel model = new GenericModel(param, actual);
            genericMap.put(model.getDeclare(), model);
        }
        return genericMap;
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
