package com.moon.processing.holder;

import com.moon.accessor.annotation.Accessor;
import com.moon.processing.decl.AccessorDeclared;
import com.moon.processing.decl.TableDeclared;
import com.moon.processing.decl.TypeDeclared;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.Environment2;

import javax.lang.model.element.TypeElement;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 数据库访问对象
 *
 * @author benshaoye
 */
public class AccessorHolder {

    private final TypeHolder typeHolder;

    private final TableHolder tableHolder;

    private final Map<String, AccessorDeclared> accessorDeclaredMap = new LinkedHashMap<>();

    public AccessorHolder(TypeHolder typeHolder, TableHolder tableHolder) {
        this.typeHolder = typeHolder;
        this.tableHolder = tableHolder;
    }

    public AccessorDeclared with(TypeElement element, Accessor accessor) {
        String classname = Element2.getQualifiedName(element);
        AccessorDeclared accessorDeclared = accessorDeclaredMap.get(classname);
        if (accessorDeclared != null) {
            return accessorDeclared;
        }
        String pojoClass = Element2.getClassname(accessor, Accessor::value);
        TypeElement pojoElement = Environment2.getUtils().getTypeElement(pojoClass);
        // 注解 Accessor 的类
        TypeDeclared typeDeclared = typeHolder.with(element);
        // 数据表定义
        TableDeclared tableDeclared = tableHolder.with(pojoElement);
        // 实体定义
        TypeDeclared pojoDeclared = typeHolder.with(pojoElement);
        accessorDeclared = new AccessorDeclared(typeDeclared, tableDeclared, pojoDeclared);
        accessorDeclaredMap.put(classname, accessorDeclared);
        return accessorDeclared;
    }
}
