package com.moon.mapping.processing;

import com.moon.mapping.BeanMapping;
import com.moon.mapping.annotation.MappingFor;

import javax.lang.model.element.TypeElement;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import static javax.lang.model.element.Modifier.ABSTRACT;

/**
 * @author benshaoye
 */
abstract class BaseDefinition<M extends BaseMethod, P extends BaseProperty<M>> extends LinkedHashMap<String, P>
    implements Completable {

    protected final static String IMPL_SUFFIX = "ImplGeneratedByMoonUtil";

    private final MappingFactory factory = new MappingFactory();

    /**
     * 声明注解{@link com.moon.mapping.annotation.MappingFor}的类
     */
    private final TypeElement thisElement;

    public BaseDefinition(TypeElement enclosingElement) { this.thisElement = enclosingElement; }

    public final TypeElement getThisElement() { return thisElement; }

    public final boolean isInterface() { return getThisElement().getKind().isInterface(); }

    public final boolean isAbstract() { return DetectUtils.isAny(getThisElement(), ABSTRACT); }

    public final MappingFactory getFactory() { return factory; }

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
    public final String getSimpleName() { return ElementUtils.getSimpleName(getThisElement()); }

    /**
     * {@link #getThisElement()}的完整合法名称
     *
     * @return
     */
    public final String getQualifiedName() { return ElementUtils.getQualifiedName(getThisElement()); }

    public String getFactThisImplName() { return getQualifiedName(); }

    public String getInterfaceImplSimpleName() {
        if (isInterface()) {
            return getSimpleName() + IMPL_SUFFIX;
        }
        return null;
    }

    /**
     * 实现自定义接口，如果{@link #thisElement}是接口的话
     *
     * @return
     */
    public StringAdder implGeneratedMethods() { return null; }

    @Override
    public void onCompleted() { forEach((name, prop) -> prop.onCompleted()); }

    public final <T> T reduce(BiFunction<T, P, T> converter, T totalValue) {
        for (Map.Entry<String, P> pEntry : entrySet()) {
            totalValue = converter.apply(totalValue, pEntry.getValue());
        }
        return totalValue;
    }

    public final <E> List<E> reduceList(BiFunction<List<E>, P, List<E>> converter) {
        return reduce(converter, new ArrayList<>());
    }

    public final <E> List<E> reduceFor(Function<P, E> converter) {
        return reduce(((list, p) -> {
            list.add(converter.apply(p));
            return list;
        }), new ArrayList<>());
    }

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
        String thatClassname = thatDef.getQualifiedName();
        adder.add("TO_" + StringUtils.underscore(thatClassname)).add(" {");
        build$safeWithThat(adder, thatDef);
        adder.add(getFactory().copyForwardMethod(getQualifiedName(), thatClassname));
        adder.add(getFactory().copyBackwardMethod(getQualifiedName(), thatClassname));
        adder.add(getFactory().newThatOnEmptyConstructor(getQualifiedName(), thatClassname));
        adder.add(getFactory().newThisOnEmptyConstructor(getQualifiedName(), thatClassname));
        adder.add("},");
    }

    private void build$safeWithThat(StringAdder adder, BaseDefinition thatDef) {
        final String thatClassname = thatDef.getQualifiedName();
        final String thisClassname = getQualifiedName();
        {
            Collection<String> fields = reducing(thisProp -> {
                Mappable thatProp = (Mappable) thatDef.get(thisProp.getName());
                if (isUsable(thisProp, thatProp)) {
                    return getFactory().copyForwardField(thisProp, thatProp);
                }
                return null;
            });
            adder.add(getFactory().safeCopyForwardMethod(thisClassname, thatClassname, fields));
        }
        {
            Collection<String> fields = reducing(thisProp -> {
                Mappable thatProp = (Mappable) thatDef.get(thisProp.getName());
                if (isUsable(thatProp, thisProp)) {
                    return getFactory().copyBackwardField(thatProp, thisProp);
                }
                return null;
            });
            adder.add(getFactory().safeCopyBackwardMethod(thisClassname, thatClassname, fields));
        }
    }

    private void addMapMapping(final StringAdder adder) {
        {
            // fromMap(Object,Map)
            Collection<String> fields = reducing(getFactory()::fromMapField);
            adder.add(getFactory().fromMapMethod(getFactThisImplName(), fields));
        }
        {
            // toMap(Object,Map)
            Collection<String> fields = reducing(getFactory()::toMapField);
            adder.add(getFactory().toMapMethod(getQualifiedName(), fields));
        }
        {
            // newThis(Map)
            adder.add(getFactory().newThisAsMapMethod(getFactThisImplName()));
        }
    }

    private void addObjectMapping(final StringAdder adder) {
        {
            // clone(Object)
            Collection<String> fields = reducing(getFactory()::cloneField);
            adder.add(getFactory().cloneMethod(getQualifiedName(), getFactThisImplName(), fields));
        }
        {
            // toString(Object)
            @SuppressWarnings("all") Collection<String> fields = reducing((list, property) ->//
                getFactory().toStringField(property, list.isEmpty()));
            adder.add(getFactory().toStringMethod(getFactThisImplName(), fields));
        }
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
                if (isNotBlank(field)) {
                    parsedFields.put(key, field);
                }
            }
        }
        return CollectUtils.reduce(entrySet(), (parsed, entry) -> {
            if (sortKeys.contains(entry.getKey())) {
                return parsed;
            }
            String field = serializer.apply(parsed, entry.getValue());
            if (isNotBlank(field)) {
                parsed.put(entry.getKey(), field);
            }
            return parsed;
        }, parsedFields).values();
    }

    private static boolean isNotBlank(String str) {
        if (str == null) {
            return false;
        }
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private Set<String> getSortedKeys() {
        Set<String> sorts = new LinkedHashSet<>();
        sorts.add("id");
        return sorts;
    }

    private static boolean isUsable(Mappable from, Mappable to) {
        boolean hasGetter = from != null && from.hasGetterMethod();
        boolean hasSetter = to != null && to.hasSetterMethod();
        return hasGetter && hasSetter;
    }
}
