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

    private final String typeClassname;
    /**
     * 所有泛型声明，如声明在 java.util.List&lt;T> 上的 T 在实际应用中如果是：
     * <p>
     * List&lt;String> 那么会这样包含:
     * <p>
     * java.util.List#T  ==  java.lang.String
     * java.util.Collection#T  ==  java.lang.String
     * java.util.Iterable#T  ==  java.lang.String
     * ...(所有父类、继承的接口都有其对应的声明和使用时的实际类)
     */
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
    private final Map<String, ConstructorDeclared> constructorsMap = new LinkedHashMap<>();
    /**
     * 除了 properties 中所有方法外的其他所有方法(自身声明+继承的+接口包含的)
     */
    private final List<MethodDeclared> methods = new ArrayList<>();

    private TypeDeclared(TypeElement typeElement, Map<String, GenericDeclared> genericDeclaredMap) {
        this.typeElement = typeElement;
        this.typeClassname = typeElement.getQualifiedName().toString();
        this.genericDeclaredMap = Collections.unmodifiableMap(genericDeclaredMap);
    }

    public static TypeDeclared from(TypeElement typeElement) {
        Map<String, GenericDeclared> thisGenericMap = Generic2.from(typeElement);
        TypeParser parser = new TypeParser(new TypeDeclared(typeElement, thisGenericMap));
        return parser.doParseTypeDeclared();
    }

    public String getTypeClassname() { return typeClassname; }

    public TypeElement getTypeElement() { return typeElement; }

    public Map<String, GenericDeclared> getGenericDeclaredMap() { return genericDeclaredMap; }

    public Map<String, PropertyDeclared> getCopiedProperties() { return new LinkedHashMap<>(properties); }

    public Map<String, FieldDeclared> getStaticFieldsMap() {
        return new LinkedHashMap<>(staticFieldsMap);
    }

    void setProperties(Map<String, PropertyDeclared> properties) {
        if (properties == null) {
            return;
        }
        this.properties.clear();
        this.properties.putAll(properties);
    }

    public void setStaticFieldsMap(Map<String, FieldDeclared> staticFieldsMap) {
        this.staticFieldsMap.clear();
        this.staticFieldsMap.putAll(staticFieldsMap);
    }

    public Set<String> getAllPropertiesName() { return new LinkedHashSet<>(properties.keySet()); }
}
