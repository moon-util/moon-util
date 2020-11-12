package com.moon.mapping.processing;

import com.moon.mapping.BeanMapping;
import com.moon.mapping.annotation.MappingFor;

import javax.lang.model.element.TypeElement;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.moon.mapping.processing.DetectUtils.isUsable;

/**
 * @author benshaoye
 */
abstract class BaseDefinition<M extends BaseMethod, P extends BaseProperty<M>> extends LinkedHashMap<String, P>
    implements Completable, CanonicalNameable {

    protected final static String IMPL_SUFFIX = "ImplGeneratedByMoonUtil";

    private final MapMethodFactory factory = new MapMethodFactory();

    /**
     * 声明注解{@link com.moon.mapping.annotation.MappingFor}的类
     */
    private final TypeElement thisElement;

    public BaseDefinition(TypeElement enclosingElement) { this.thisElement = enclosingElement; }

    public final TypeElement getThisElement() { return thisElement; }

    public final boolean isInterface() { return getThisElement().getKind().isInterface(); }

    public final MapMethodFactory getFactory() { return factory; }

    @Override
    public final String getCanonicalName() { return ElementUtils.getQualifiedName(getThisElement()); }

    /**
     * 声明{@link MappingFor}的类{@link #getThisElement()}所在包的完整名
     *
     * @return 包名
     */
    public final String getPackageName() { return ElementUtils.getPackageName(getThisElement()); }

    /**
     * {@link #getThisElement()}的类名，如果是内部类，也只返回最后一部分
     *
     * @return
     */
    @Override
    public final String getSimpleName() { return ElementUtils.getSimpleName(getThisElement()); }

    /**
     * {@link #getThisElement()}的完整合法名称
     *
     * @return
     */
    public final String getQualifiedName() { return ElementUtils.getQualifiedName(getThisElement()); }

    @Override
    public void onCompleted() { forEach((name, prop) -> prop.onCompleted()); }

    /**
     * 实现{@link BeanMapping}的公共方法
     */
    public StringAdder implMappingSharedMethods() {
        final StringAdder adder = new StringAdder();
        addObjectMapping(adder);
        addMapMapping(adder);
        return adder;
    }

    final void addBeanMapping(StringAdder adder, BaseDefinition thatDef) {
        adder.add("TO_" + StringUtils.underscore(thatDef.getCanonicalName())).add(" {");
        final boolean emptyForward = safeDoForward(adder, thatDef);
        final boolean emptyBackward = safeDoBackward(adder, thatDef);
        final String thisCls = getSimpleName();
        final String thatCls = thatDef.getSimpleName();
        adder.add(getFactory().copyForwardMethod(thisCls, thatCls, emptyForward));
        adder.add(getFactory().copyBackwardMethod(thisCls, thatCls, emptyBackward));
        adder.add(getFactory().newThatOnEmptyConstructor(thisCls, thatCls, emptyForward));
        adder.add(getFactory().newThisOnEmptyConstructor(thisCls, thatCls, emptyBackward));
        adder.add("},");
    }

    private boolean safeDoForward(StringAdder adder, BaseDefinition thatDef) {
        final String thatClass = thatDef.getSimpleName();
        final String thisClass = getSimpleName();
        Collection<String> fields = reducing(thisProp -> {
            Mappable thatProp = (Mappable) thatDef.get(thisProp.getName());
            if (isUsable(thisProp, thatProp)) {
                return factory.copyForwardField(thisProp, thatProp);
            }
            return null;
        });
        if (fields.isEmpty()) {
            return true;
        }
        adder.add(factory.safeCopyForwardMethod(thisClass, thatClass, fields));
        return false;
    }

    private boolean safeDoBackward(StringAdder adder, BaseDefinition thatDef) {
        Collection<String> fields = reducing(thisProp -> {
            Mappable thatProp = (Mappable) thatDef.get(thisProp.getName());
            if (isUsable(thatProp, thisProp)) {
                return factory.copyBackwardField(thatProp, thisProp);
            }
            return null;
        });
        if (fields.isEmpty()) {
            return true;
        }
        adder.add(factory.safeCopyBackwardMethod(getSimpleName(), thatDef.getSimpleName(), fields));
        return false;
    }

    private void addMapMapping(final StringAdder adder) {
        final String name = getSimpleName();
        final MapMethodFactory f = getFactory();
        // fromMap(Object,Map)
        adder.add(f.fromMapMethod(name, reducing(f::fromMapField)));
        // toMap(Object,Map)
        adder.add(f.toMapMethod(name, reducing(f::toMapField)));
        // newThis(Map)
        adder.add(f.newThisAsMapMethod(name));
    }

    private void addObjectMapping(final StringAdder adder) {
        final String name = getSimpleName();
        final MapMethodFactory f = getFactory();
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

    private Set<String> getSortedKeys() {
        Set<String> sorts = new LinkedHashSet<>();
        sorts.add("id");
        return sorts;
    }
}
