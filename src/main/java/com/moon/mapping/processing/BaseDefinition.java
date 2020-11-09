package com.moon.mapping.processing;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author benshaoye
 */
abstract class BaseDefinition<M extends BaseMethod, P extends BaseProperty<M>> extends LinkedHashMap<String, P>
    implements JavaFileWritable, Completable {

    /**
     * 声明注解{@link com.moon.mapping.annotation.MappingFor}的类
     */
    private final TypeElement thisElement;

    public BaseDefinition(TypeElement enclosingElement) { this.thisElement = enclosingElement; }

    public final TypeElement getThisElement() { return thisElement; }

    public final String getPackageName() {
        return ElementUtils.getPackageName(getThisElement());
    }

    public final String getSimpleName() {
        return ElementUtils.getSimpleName(getThisElement());
    }

    public final String getQualifiedName() {
        return ElementUtils.getQualifiedName(getThisElement());
    }

    public final boolean isInterface() { return getThisElement().getKind().isInterface(); }

    public final boolean isAbstract() {
        return DetectUtils.isAny(getThisElement(), Modifier.ABSTRACT);
    }

    @Override
    public void onCompleted() {
        forEach((name, prop) -> prop.onCompleted());
    }

    @Override
    public void writeJavaFile() throws IOException { }

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
}
