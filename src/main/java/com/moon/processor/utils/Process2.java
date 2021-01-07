package com.moon.processor.utils;

import com.moon.mapper.annotation.MapperFor;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * @author benshaoye
 */
public enum Process2 {
    ;

    private final static String MAPPER_FOR_CLASSNAME = MapperFor.class.getCanonicalName();
    private final static String CLASS_SUFFIX = ".class";

    public static Collection<TypeElement> getMapperForClasses(TypeElement element) {
        Types types = Environment2.getTypes();
        Elements elements = Environment2.getUtils();
        TypeMirror supportedType = elements.getTypeElement(MAPPER_FOR_CLASSNAME).asType();
        Collection<String> classes = new HashSet<>();
        for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
            DeclaredType declaredType = mirror.getAnnotationType();
            if (!types.isSameType(supportedType, declaredType)) {
                continue;
            }
            for (AnnotationValue value : mirror.getElementValues().values()) {
                String[] classnames = value.getValue().toString().split(",");
                for (String classname : classnames) {
                    if (classname.endsWith(CLASS_SUFFIX)) {
                        classname = classname.substring(0, classname.length() - 6);
                    }
                    classes.add(classname);
                }
            }
        }
        return classes.stream().map(elements::getTypeElement).collect(Collectors.toList());
    }
}
