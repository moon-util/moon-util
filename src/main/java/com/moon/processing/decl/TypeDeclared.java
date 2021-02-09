package com.moon.processing.decl;

import javax.lang.model.element.TypeElement;
import java.util.*;

/**
 * 接口/普通类
 *
 * @author benshaoye
 */
public class TypeDeclared {

    private final TypeElement typeElement;

    private final Map<String, GenericDeclared> genericDeclaredMap;
    /**
     * 所有 properties
     * <p>
     * 只包括所有成员字段(自身声明+继承的+接口包含的)及其对应的 getter/setter 方法
     */
    private final Map<String, PropertyDeclared> properties = new LinkedHashMap<>();
    /**
     * 所有静态字段(自身声明+继承的+接口包含的)
     */
    private final Map<String, FieldDeclared> staticFieldsMap = new LinkedHashMap<>();
    /**
     * 所有构造器
     */
    private final List<ConstructorDeclared> constructors = new ArrayList<>();
    /**
     * 除了 properties 中所有方法外的其他所有方法(自身声明+继承的+接口包含的)
     */
    private final List<MethodDeclared> methods = new ArrayList<>();

    public TypeDeclared(TypeElement typeElement) {
        this.typeElement = typeElement;
        Map<String, GenericDeclared> thisGenericMap = Generic2.from(typeElement);
        this.genericDeclaredMap = Collections.unmodifiableMap(thisGenericMap);
    }

    public Map<String, PropertyDeclared> getCopiedProperties() { return new LinkedHashMap<>(properties); }
}
