package com.moon.processing.accessor;

import com.moon.accessor.annotation.Provided;
import com.moon.processing.decl.MethodDeclared;
import com.moon.processing.file.*;
import com.moon.processing.util.Assert2;
import com.moon.processing.util.Element2;
import com.moon.processing.util.String2;
import com.moon.processing.util.Test2;

import javax.lang.model.element.ExecutableElement;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author benshaoye
 */
public abstract class BaseGenerator extends BaseImportable {

    protected static JavaParameter getFirstParameter(JavaMethod method) {
        return method.getParameters().getFirstParameter();
    }

    protected static boolean isSamePropertyType(String actualType, String fieldClass) {
        String generalizableType = String2.toGeneralizableType(actualType);
        if (Objects.equals(generalizableType, fieldClass)) {
            return true;
        }
        return Test2.isSubtypeOf(generalizableType, fieldClass);
    }

    protected static String dotClass(String classname) { return classname + ".class"; }

    protected final void onProvidedAnnotated(MethodDeclared method, Provided provided) {
        final JavaMethod setter;
        ExecutableElement element = method.getMethod();
        BaseImplementation impl = getImplementation();
        Assert2.assertProvidedMethodParameters(element);
        String fieldName, actualType, paramName;
        if (Test2.isGetterMethod(element)) {
            fieldName = Element2.toPropertyName(element);
            actualType = method.getReturnActualType();
            JavaField field = impl.privateField(fieldName, actualType);
            field.withGetterMethod().returning(fieldName).override();
            setter = field.useSetterMethod();
        } else {
            fieldName = impl.nextVar();
            actualType = method.getReturnActualType();
            String parameterName = Element2.getSimpleName(element);
            JavaField field = impl.privateField(fieldName, actualType);
            field.publicMethod(parameterName).override().typeOf(actualType).returning(fieldName);
            setter = field.useMethod(String2.toSetterName(parameterName), params -> {
                params.add(parameterName, actualType);
            });
        }
        paramName = getFirstParameter(setter).getName();
        // TODO 注解的属性值应该考虑优先值问题
        setter.annotationAutowired(provided.required());
        setter.annotationQualifierIfNotBlank(provided.value());
        setter.nextFormatted("this.{} = {}", fieldName, paramName, LineScripter::withUnsorted);
    }

    /*
     #################################################################################################
     */

    public BaseGenerator(Importer importer) { super(importer); }

    /**
     * 文件实现
     *
     * @return 文件实现
     */
    protected abstract BaseImplementation getImplementation();
}
