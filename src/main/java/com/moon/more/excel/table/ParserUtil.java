package com.moon.more.excel.table;

import com.moon.more.excel.annotation.FieldTransformer;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;

/**
 * @author benshaoye
 */
final class ParserUtil {

    static final <T> T obtainOrNull(Marked marked, Function<Marked, T> getter) {
        return marked == null ? null : getter.apply(marked);
    }

    static boolean isExpectCached(Class type) {
        return !type.isMemberClass() || Modifier.isStatic(type.getModifiers());
    }

    /**
     * 检测必须是普通可实例化的类，不能是接口抽象类
     *
     * @param type {@link FieldTransformer}的实现类
     */
    static void checkValidImplClass(Class type, Class expectSuperClass) {
        int modifiers = type.getModifiers();
        if (!Modifier.isPublic(modifiers)) {
            throw new IllegalStateException("指定类「" + type + "」应该是普通公共类（public）");
        }
        if (Modifier.isInterface(modifiers) || Modifier.isAbstract(modifiers)) {
            throw new IllegalStateException("指定类「" + type + "」不能是接口或抽象类");
        }
        if (!FieldTransformer.class.isAssignableFrom(type)) {
            throw new IllegalStateException("指定类「" + type + "」应该是「" + expectSuperClass + "」的实现类");
        }
    }

    /**
     * 转换 Attribute 为具体执行类
     *
     * @param attributeMap {@link #merge2Attr(Map, Map)}
     * @param targetClass  被解析的类
     * @param transformer  like name
     *
     * @return Renderer，最终用于渲染 Table 的类型
     */
    static TableRenderer mapAttrs(
        Class targetClass, Map<String, Attribute> attributeMap, Function<AttrConfig, TableCol> transformer
    ) {
        Collection<Attribute> attributes = attributeMap.values();
        TableCol[] columns = new TableCol[attributes.size()];
        List<Attribute> list = new ArrayList<>(attributes);
        Collections.sort(list, Attribute::compareTo);
        AttrConfig config = new AttrConfig(targetClass);

        Map styleMap = StyleUtil.toStyleMap(targetClass);

        for (int i = 0, size = list.size(); i < size; i++) {
            Attribute attr = list.get(i);
            styleMap.putAll(StyleUtil.toStyleMap(targetClass, attr));
            config.setAttribute(list.get(i), i);
            columns[i] = transformer.apply(config);
        }

        return new TableRenderer(targetClass, styleMap, columns);
    }

    static void putMarked(Marked marked, Map annotated, Map unAnnotated) {
        Map group = marked.isAnnotated() ? annotated : unAnnotated;
        group.put(marked.getName(), marked);
    }

    /**
     * 合并{@link Marked}为{@link Attribute}
     * <p>
     * 相同字段的注解合并到一个{@link Attribute}上
     * <p>
     * 顺序以字段声明顺序优先
     *
     * @param atMethod 于方法上的注解
     * @param atField  于字段上的注解
     * @param <T>      {@link Marked}的具体实现类型
     *
     * @return 合并后的 attributes，key 是字段名
     */
    static <T extends Marked> Map<String, Attribute> merge2Attr(
        Map<String, T> atMethod, Map<String, T> atField
    ) {
        Map<String, Attribute> annotatedMap = new LinkedHashMap<>();
        for (Map.Entry<String, T> entry : atField.entrySet()) {
            String name = entry.getKey();
            Marked field = entry.getValue();
            Marked method = atMethod.remove(name);
            annotatedMap.put(name, new Attribute(method, field));
        }
        for (Map.Entry<String, T> entry : atMethod.entrySet()) {
            annotatedMap.put(entry.getKey(), new Attribute(entry.getValue(), null));
        }
        return annotatedMap;
    }
}
