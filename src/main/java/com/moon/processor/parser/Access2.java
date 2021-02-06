package com.moon.processor.parser;

import com.moon.accessor.annotation.Provided;
import com.moon.processor.def.DefTableModel;
import com.moon.processor.file.*;
import com.moon.processor.model.DeclareGeneric;
import com.moon.processor.utils.*;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author benshaoye
 */
enum Access2 {
    ;

    static void assertParametersSameWithTableModel(DefTableModel tableModel, List<? extends VariableElement> elements) {

    }

    static void onPropertyProvided(
        DeclJavaFile impl,
        ExecutableElement method,
        String accessorClass,
        Map<String, DeclareGeneric> genericMap,
        Provided provided,
        AtomicInteger indexer
    ) {
        DeclField field;
        String setterParameterName = null;
        Assert2.assertProvidedMethodParameters(method);
        if (Test2.isGetterMethod(method)) {
            String prop = Element2.toPropertyName(method);
            String declaredType = Element2.getGetterDeclareType(method);
            String actualType = Generic2.mappingToActual(genericMap, accessorClass, declaredType);
            field = impl.privateField(prop, actualType).withGetterMethod(DeclMethod::override);
        } else {
            String simpleName = Element2.getSimpleName(method);
            String returnType = method.getReturnType().toString();
            String actualClass = Generic2.mappingToActual(genericMap, accessorClass, returnType);
            String fieldName = toFieldName(impl, simpleName, indexer);
            impl.publicMethod(simpleName, DeclParams.of()).override().returnTypeof(actualClass).returning(fieldName);
            field = impl.privateField(fieldName, actualClass);
            setterParameterName = simpleName;
        }
        field.withSetterMethod(setterParameterName, setter -> setter.markedOf(importer -> {
            DeclMarked marked = DeclMarked.ofAutowired(importer);
            marked.booleanOf("required", provided.required());
            return marked;
        }).markedOf(importer -> {
            String qualifier = provided.value();
            if (String2.isNotBlank(qualifier)) {
                DeclMarked marked = DeclMarked.ofQualifier(importer);
                return marked.stringOf("value", qualifier);
            }
            return null;
        }));
    }

    private static String toFieldName(DeclJavaFile impl, String basicName, AtomicInteger indexer) {
        if (basicName == null) {
            basicName = "value";
        }
        while (impl.hasField(basicName)) {
            basicName = basicName + indexer.getAndIncrement();
        }
        return basicName;
    }
}
