package com.moon.mapping.processing;

import com.moon.mapping.BeanMapping;
import com.moon.mapping.annotation.MappingFor;

import javax.lang.model.element.TypeElement;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.moon.mapping.processing.PropertyAttr.DFT;

/**
 * @author benshaoye
 */
abstract class BaseDefinition<M extends BaseMethod, P extends BaseProperty<M>> extends LinkedHashMap<String, P>
    implements Completable, CanonicalNameable {

    private final MapMethodFactory methodFactory = new MapMethodFactory();

    private final MapFieldFactory fieldFactory = new MapFieldFactory();

    private final Set<String> sorts = new LinkedHashSet<>();

    { sorts.add("id"); }

    private final Map<String, Map<String, PropertyAttr>> fieldAttrMap = new HashMap<>();
    private final Map<String, Map<String, PropertyAttr>> setterAttrMap = new HashMap<>();
    private final Map<String, Map<String, PropertyAttr>> getterAttrMap = new HashMap<>();

    /**
     * 声明注解{@link com.moon.mapping.annotation.MappingFor}的类
     */
    private final TypeElement thisElement;

    public BaseDefinition(TypeElement enclosingElement) { this.thisElement = enclosingElement; }

    public final TypeElement getThisElement() { return thisElement; }

    public final boolean isInterface() { return getThisElement().getKind().isInterface(); }

    public final MapMethodFactory getMethodFactory() { return methodFactory; }

    public final MapFieldFactory getFieldFactory() { return fieldFactory; }

    @Override
    public final String getCanonicalName() { return ElemUtils.getQualifiedName(getThisElement()); }

    final void addFieldAttr(String name, PropertyAttr attr) {
        getFieldAttrMap().computeIfAbsent(attr.getTargetCls(), k -> new HashMap<>(4)).put(name, attr);
    }

    final void addSetterAttr(String name, PropertyAttr attr) {
        getSetterAttrMap().computeIfAbsent(attr.getTargetCls(), k -> new HashMap<>(4)).put(name, attr);
    }

    final void addGetterAttr(String name, PropertyAttr attr) {
        getGetterAttrMap().computeIfAbsent(attr.getTargetCls(), k -> new HashMap<>(4)).put(name, attr);
    }

    public Map<String, Map<String, PropertyAttr>> getFieldAttrMap() { return fieldAttrMap; }

    public Map<String, Map<String, PropertyAttr>> getGetterAttrMap() { return getterAttrMap; }

    public Map<String, Map<String, PropertyAttr>> getSetterAttrMap() { return setterAttrMap; }

    /**
     * 声明{@link MappingFor}的类{@link #getThisElement()}所在包的完整名
     *
     * @return 包名
     */
    public final String getPackageName() { return ElemUtils.getPackageName(getThisElement()); }

    /**
     * {@link #getThisElement()}的类名，如果是内部类，也只返回最后一部分
     *
     * @return
     */
    @Override
    public final String getSimpleName() { return ElemUtils.getSimpleName(getThisElement()); }

    /**
     * {@link #getThisElement()}的完整合法名称
     *
     * @return
     */
    public final String getQualifiedName() { return ElemUtils.getQualifiedName(getThisElement()); }

    @Override
    public void onCompleted() { forEach((name, prop) -> prop.onCompleted()); }

    /**
     * 实现{@link BeanMapping}的公共方法
     */
    public StringAdder implMappingSharedMethods(final Manager manager) {
        final StringAdder adder = new StringAdder();
        addMapMapping(adder, manager);
        addObjectMapping(adder);
        return adder;
    }

    final void addBeanMapping(StringAdder adder, BaseDefinition thatDef, Manager manager) {
        adder.add("TO_" + StringUtils.underscore(thatDef.getCanonicalName())).add(" {");
        final String thisCls = manager.onImported(getCanonicalName());
        final String thatCls = manager.onImported(thatDef.getCanonicalName());
        final boolean emptyForward = unsafeConvert(adder, thatDef, manager, ConvertStrategy.FORWARD);
        final boolean emptyBackward = unsafeConvert(adder, thatDef, manager, ConvertStrategy.BACKWARD);
        adder.add(getMethodFactory().newThatOnEmptyConstructor(thisCls, thatCls, emptyForward));
        adder.add(getMethodFactory().newThisOnEmptyConstructor(thisCls, thatCls, emptyBackward));
        adder.add("},");
    }

    private static PropertyAttr findPropertyAttr(
        BaseDefinition thatDef, Mappable thisProp, Map<String, Map<String, PropertyAttr>> propertiesMap
    ) {
        final String targetClass = thatDef.getCanonicalName();
        Map<String, PropertyAttr> attrMap = propertiesMap.get(targetClass);
        PropertyAttr attr;
        if (attrMap == null) {
            attrMap = propertiesMap.getOrDefault("void", Collections.emptyMap());
            attr = attrMap.get(thisProp.getName());
        } else {
            attr = attrMap.get(thisProp.getName());
            if (attr == null) {
                attrMap = propertiesMap.getOrDefault("void", Collections.emptyMap());
                attr = attrMap.get(thisProp.getName());
            }
        }
        return attr;
    }

    private PropertyAttr getPropertyAttr(BaseDefinition thatDef, Mappable thisProp, ConvertStrategy strategy) {
        Map propertiesMap = strategy.getMethodAttrMap(this);
        PropertyAttr attr = findPropertyAttr(thatDef, thisProp, propertiesMap);
        if (attr == null) {
            attr = findPropertyAttr(thatDef, thisProp, getFieldAttrMap());
        }
        return attr == null ? DFT : attr;
    }

    private boolean unsafeConvert(
        StringAdder adder, final BaseDefinition thatDef, Manager manager, ConvertStrategy strategy
    ) {
        Collection<String> fields = reducing(thisProp -> {
            PropertyAttr attr = getPropertyAttr(thatDef, thisProp, strategy);
            if (strategy.isIgnore(attr)) {
                return null;
            }
            String targetName = attr.getField(thisProp.getName());
            Mappable thatProp = (Mappable) thatDef.get(targetName);
            if (manager.isModelUsable(thisProp, thatProp, attr, strategy)) {
                return getFieldFactory().doConvertField(manager);
            }
            return null;
        });
        return forMethod(adder, fields, this, thatDef, manager, strategy);
    }

    private static boolean forMethod(
        StringAdder adder,
        Collection<String> fields,
        BaseDefinition thisDef,
        BaseDefinition thatDef,
        Manager manager,
        ConvertStrategy strategy
    ) {
        if (fields.isEmpty()) {
            return true;
        } else {
            String thisClass = manager.onImported(thisDef.getCanonicalName());
            String thatClass = manager.onImported(thatDef.getCanonicalName());
            MapMethodFactory factory = thisDef.getMethodFactory();
            adder.add(strategy.forMethod(factory, thisClass, thatClass, fields));
            return false;
        }
    }

    private void addMapMapping(final StringAdder adder, final Manager manager) {
        final String name = getSimpleName();
        final MapMethodFactory f = getMethodFactory();
        // fromMap(Object,Map)
        adder.add(f.fromMapMethod(name, reducing(mappable -> f.fromMapField(mappable, manager))));
        // toMap(Object,Map)
        adder.add(f.toMapMethod(name, reducing(f::toMapField)));
        // newThis(Map)
        adder.add(f.newThisAsMapMethod(name));
    }

    private void addObjectMapping(final StringAdder adder) {
        final String name = getSimpleName();
        final MapMethodFactory f = getMethodFactory();
        // clone(Object)
        adder.add(f.cloneMethod(name, name, reducing(f::cloneField)));
        // toString(Object)
        adder.add(f.toStringMethod(name, reducing((list, property) ->//
            f.toStringField(property, list.isEmpty()))));
    }

    private Collection<String> reducing(Function<Mappable, String> serializer) {
        return reducing((m, prop) -> serializer.apply(prop));
    }

    private Collection<String> reducing(BiFunction<Map<String, String>, Mappable, String> serializer) {
        final Map<String, String> parsedFields = new LinkedHashMap<>();
        final Set<String> sortKeys = getSortedKeys();
        for (String key : sortKeys) {
            Mappable property = get(key);
            if (property != null) {
                String field = serializer.apply(parsedFields, property);
                if (StringUtils.isNotBlank(field)) {
                    parsedFields.put(key, field);
                }
            }
        }
        return CollectUtils.reduce(entrySet(), (parsed, entry) -> {
            if (sortKeys.contains(entry.getKey())) {
                return parsed;
            }
            String field = serializer.apply(parsed, entry.getValue());
            if (StringUtils.isNotBlank(field)) {
                parsed.put(entry.getKey(), field);
            }
            return parsed;
        }, parsedFields).values();
    }

    private Set<String> getSortedKeys() { return sorts; }
}
