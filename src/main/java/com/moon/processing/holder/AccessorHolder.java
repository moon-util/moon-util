package com.moon.processing.holder;

import com.moon.accessor.annotation.Accessor;
import com.moon.processing.JavaDeclarable;
import com.moon.processing.JavaFiler;
import com.moon.processing.JavaWritable;
import com.moon.processing.decl.AccessorDeclared;
import com.moon.processing.decl.TableDeclared;
import com.moon.processing.decl.TypeDeclared;
import com.moon.processing.util.Processing2;
import com.moon.processor.utils.Element2;

import javax.lang.model.element.TypeElement;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 数据库访问对象
 *
 * @author benshaoye
 */
public class AccessorHolder extends BaseHolder {

    private final TypeHolder typeHolder;

    private final TableHolder tableHolder;

    private final Map<String, AccessorDeclared> accessorDeclaredMap = new LinkedHashMap<>();

    public AccessorHolder(TypeHolder typeHolder, TableHolder tableHolder) {
        this.typeHolder = typeHolder;
        this.tableHolder = tableHolder;
    }

    public AccessorDeclared with(TypeElement accessorElement, TypeElement modelElement) {
        String classname = Element2.getQualifiedName(accessorElement);
        AccessorDeclared accessorDeclared = accessorDeclaredMap.get(classname);
        return accessorDeclared == null ? newAccessorDeclared(classname, accessorElement, modelElement)
            : accessorDeclared;
    }

    public AccessorDeclared with(TypeElement accessorElement) {
        String classname = Element2.getQualifiedName(accessorElement);
        AccessorDeclared accessorDeclared = accessorDeclaredMap.get(classname);
        if (accessorDeclared != null) {
            return accessorDeclared;
        }
        Accessor accessor = accessorElement.getAnnotation(Accessor.class);
        String pojoClass = Element2.getClassname(accessor, Accessor::value);
        TypeElement pojoElement = Processing2.getUtils().getTypeElement(pojoClass);
        return newAccessorDeclared(classname, accessorElement, pojoElement);
    }

    private AccessorDeclared newAccessorDeclared(
        String accessorClassname, TypeElement accessorElement, TypeElement modelElement
    ) {
        // 注解 Accessor 的类
        TypeDeclared typeDeclared = typeHolder.with(accessorElement);
        // 数据表定义
        TableDeclared tableDeclared = tableHolder.with(modelElement);
        // 实体定义
        TypeDeclared pojoDeclared = typeHolder.with(modelElement);
        AccessorDeclared accessorDeclared = new AccessorDeclared(typeDeclared, tableDeclared, pojoDeclared);
        accessorDeclaredMap.put(accessorClassname, accessorDeclared);
        return accessorDeclared;
    }

    @Override
    protected Collection<? extends JavaDeclarable> getJavaDeclares() {
        return accessorDeclaredMap.values();
    }
}
