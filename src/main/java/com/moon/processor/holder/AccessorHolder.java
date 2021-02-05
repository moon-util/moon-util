package com.moon.processor.holder;

import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.def.DefAccessor;
import com.moon.processor.def.DefTableModel;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.Test2;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class AccessorHolder implements JavaFileWriteable {

    private final CopierHolder copierHolder;
    private final ModelHolder modelHolder;
    private final PojoHolder pojoHolder;
    private final NameHolder nameHolder;

    private final Map<String, DefAccessor> accessorHashMap = new HashMap<>();

    public AccessorHolder(
        CopierHolder copierHolder, PojoHolder pojoHolder, ModelHolder modelHolder, NameHolder nameHolder
    ) {
        this.copierHolder = copierHolder;
        this.modelHolder = modelHolder;
        this.pojoHolder = pojoHolder;
        this.nameHolder = nameHolder;
    }

    public void with(TypeElement accessorElem, TypeElement modelElem) {
        assertAccessorDecl(accessorElem);
        String classname = Element2.getQualifiedName(accessorElem);
        DefTableModel model = modelHolder.with(modelElem, classname);
        accessorHashMap.computeIfAbsent(classname,
            k -> new DefAccessor(copierHolder, modelHolder, pojoHolder, nameHolder, accessorElem, model));
    }

    private static void throwException(String message, TypeElement element) {
        String classname = Element2.getQualifiedName(element);
        throw new IllegalStateException(message + classname);
    }

    private static void assertAccessorDecl(TypeElement element) {
        if (Test2.isAny(element, Modifier.FINAL)) {
            throwException("Accessor cannot be modified by 'final': ", element);
        }
        ElementKind kind = element.getKind();
        if (kind == ElementKind.ENUM) {
            throwException("Accessor cannot be an 'enum': ", element);
        }
        if (kind == ElementKind.ANNOTATION_TYPE) {
            throwException("Accessor cannot be an 'annotation': ", element);
        }
        // for future: record
        if ("record".equals(kind.name())) {
            throwException("Accessor cannot be a 'record': ", element);
        }
    }

    @Override
    public void writeJavaFile(JavaWriter writer) {
        accessorHashMap.forEach((k, accessor) -> accessor.writeJavaFile(writer));
    }
}
