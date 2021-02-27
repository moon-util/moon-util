package com.moon.processing.decl;

import com.moon.accessor.annotation.domain.AutoInsertable;
import com.moon.accessor.annotation.domain.AutoUpdatable;
import com.moon.accessor.annotation.domain.TableId;
import com.moon.processing.util.Element2;
import com.moon.processing.util.String2;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 专门描述 setter/getter 方法
 *
 * @author benshaoye
 */
public class PropertyMethodDeclared extends MethodDeclared {

    private final String propertyName;
    private final boolean lombokGenerated;
    private final TableId tableId;
    private final AutoInsertable autoInsert;
    private final AutoUpdatable autoUpdate;

    public PropertyMethodDeclared(
        TypeElement thisElement,
        TypeElement enclosingElement,
        ExecutableElement method,
        String propertyName,
        TypeDeclared thisTypeDeclared,
        Map<String, GenericDeclared> thisGenericMap
    ) {
        this(thisElement, enclosingElement, method,propertyName, thisTypeDeclared, thisGenericMap, false);
    }

    public PropertyMethodDeclared(
        TypeElement thisElement,
        TypeElement enclosingElement,
        ExecutableElement method,
        String propertyName,
        TypeDeclared thisTypeDeclared,
        Map<String, GenericDeclared> thisGenericMap,
        boolean lombokGenerated
    ) {
        super(thisElement, enclosingElement, method, thisTypeDeclared, thisGenericMap);
        this.autoInsert = method.getAnnotation(AutoInsertable.class);
        this.autoUpdate = method.getAnnotation(AutoUpdatable.class);
        this.tableId = method.getAnnotation(TableId.class);
        this.lombokGenerated = lombokGenerated;
        this.propertyName = propertyName;
    }

    protected PropertyMethodDeclared(
        TypeElement thisElement,
        TypeElement enclosingElement,
        VariableElement field,
        TypeDeclared thisTypeDeclared,
        String methodName,
        String returnDeclaredType,
        Map<String, GenericDeclared> thisGenericMap,
        Map<String, String> parametersMap,
        boolean lombokGenerated
    ) {
        super(thisElement,
            enclosingElement,
            thisTypeDeclared,
            methodName,
            returnDeclaredType,
            thisGenericMap,
            parametersMap);
        this.lombokGenerated = lombokGenerated;
        this.propertyName = Element2.getSimpleName(field);
        this.autoInsert = null;
        this.autoUpdate = null;
        this.tableId = null;
    }

    public boolean isLombokGenerated() { return lombokGenerated; }

    public String getPropertyName() { return propertyName; }

    public TableId getTableId() { return tableId; }

    public boolean isTableId() { return getTableId() != null; }

    public static PropertyMethodDeclared ofLombokGetterGenerated(
        TypeElement thisElement,
        VariableElement field,
        TypeDeclared thisTypeDeclared,
        Map<String, GenericDeclared> thisGenericMap
    ) {
        String type = field.asType().toString();
        String getterName = String2.toGetterName(Element2.getSimpleName(field), type);
        return new PropertyMethodDeclared(thisElement,
            (TypeElement) field.getEnclosingElement(),
            field,
            thisTypeDeclared,
            getterName,
            type,
            thisGenericMap,
            Collections.emptyMap(),
            true);
    }

    public static PropertyMethodDeclared ofLombokSetterGenerated(
        TypeElement thisElement,
        VariableElement field,
        TypeDeclared thisTypeDeclared,
        Map<String, GenericDeclared> thisGenericMap
    ) {
        String type = field.asType().toString();
        String name = Element2.getSimpleName(field);
        Map<String, String> parametersMap = new LinkedHashMap<>();
        parametersMap.put(name, type);
        String getterName = String2.toSetterName(Element2.getSimpleName(field));
        return new PropertyMethodDeclared(thisElement,
            (TypeElement) field.getEnclosingElement(),
            field,
            thisTypeDeclared,
            getterName,
            "void",
            thisGenericMap,
            parametersMap,
            true);
    }
}
