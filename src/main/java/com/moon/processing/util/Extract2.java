package com.moon.processing.util;

import com.moon.accessor.annotation.Accessor;
import com.moon.mapper.annotation.MapperFor;
import com.moon.processor.utils.Collect2;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.String2;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author benshaoye
 */
public enum Extract2 {
    ;
    private final static char COMMA = ',';
    private final static String CLASS_SUFFIX = ".class";

    public static Collection<TypeElement> getAccessorValues(TypeElement accessorAnnotated) {
        return getValueClasses(accessorAnnotated, Accessor.class, "value");
    }

    public static Collection<TypeElement> getMapperForValues(TypeElement mapperForAnnotated) {
        return getValueClasses(mapperForAnnotated, MapperFor.class, "value");
    }

    public static <T extends Annotation> Collection<TypeElement> getValueClasses(
        TypeElement annotatedElement, Class<T> annotationClass, String... supportedMethods
    ) {
        Elements elements = Processing2.getUtils();
        TypeMirror annotationTypeMirror = elements.getTypeElement(annotationClass.getCanonicalName()).asType();
        Set<String> supportedMethodsSet = Collect2.set(supportedMethods);
        Collection<String> classes = extractClasses(annotatedElement, annotationTypeMirror, supportedMethodsSet);
        return classes.stream().map(elements::getTypeElement).collect(Collectors.toList());
    }

    private static Collection<String> extractClasses(
        TypeElement element, TypeMirror supportedAnnotationTypeMirror, Set<String> supportedMethods
    ) {
        Types types = Processing2.getTypes();
        Collection<String> classes = new LinkedHashSet<>();
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            DeclaredType declaredType = annotationMirror.getAnnotationType();
            if (!types.isSameType(declaredType, supportedAnnotationTypeMirror)) {
                continue;
            }
            Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValuesMap//
                = annotationMirror.getElementValues();
            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationValuesMap
                .entrySet()) {
                if (!supportedMethods.contains(Element2.getSimpleName(entry.getKey()))) {
                    continue;
                }
                String values = entry.getValue().getValue().toString();
                for (String stringify : String2.split(values, COMMA)) {
                    String classname = stringify.trim();
                    if (classname.endsWith(CLASS_SUFFIX)) {
                        classname = classname.substring(0, classname.length() - 6);
                    }
                    classes.add(classname);
                }
            }
        }
        return classes;
    }
}
