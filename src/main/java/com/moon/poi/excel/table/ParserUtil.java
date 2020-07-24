package com.moon.poi.excel.table;

import com.moon.core.util.ListUtil;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;

/**
 * @author moonsky
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
     */
    static void checkValidImplClass(Class type, Class expectSuperClass) {
        int modifiers = type.getModifiers();
        if (!Modifier.isPublic(modifiers)) {
            throw new IllegalStateException("指定类「" + type + "」应该是普通公共类（public）");
        }
        if (Modifier.isInterface(modifiers) || Modifier.isAbstract(modifiers)) {
            throw new IllegalStateException("指定类「" + type + "」不能是接口或抽象类");
        }
        // if (!FieldTransformer.class.isAssignableFrom(type)) {
        //     throw new IllegalStateException("指定类「" + type + "」应该是「" + expectSuperClass + "」的实现类");
        // }
    }


    static TableRenderer mapAttrsIfUnAnnotated(
        Class targetClass, Map<String, Attribute> unAnnotatedMap, Function<AttrConfig, TableCol> transformer
    ) { return doMapAttrs(targetClass, ListUtil.newList(unAnnotatedMap.values()), transformer); }

    /**
     * 转换 Attribute 为具体执行类
     *
     * @param annotatedMap {@link #merge2Attr(Map, Map)}
     * @param targetClass  被解析的类
     * @param transformer  like name
     *
     * @return Renderer，最终用于渲染 Table 的类型
     */
    static TableRenderer mapAttrs(
        Class targetClass, Map<String, Attribute> annotatedMap, Function<AttrConfig, TableCol> transformer
    ) { return doMapAttrs(targetClass, new ArrayList<>(annotatedMap.values()), transformer); }

    /**
     * 转换 Attribute
     *
     * @param targetClass 被解析的类
     * @param list        所有 Attribute 项
     * @param transformer 转换器
     *
     * @return Renderer，最终用于渲染 Table 的类型
     */
    private static TableRenderer doMapAttrs(
        Class targetClass, List<Attribute> list, Function<AttrConfig, TableCol> transformer
    ) {
        TableCol[] columns = new TableCol[list.size()];
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
    ) { return merge2Attr(atMethod, atField, new HashMap<>(0), new HashMap<>(0)); }

    /**
     * 合并{@link Marked}为{@link Attribute}
     * <p>
     * 相同字段的注解合并到一个{@link Attribute}上
     * <p>
     * 顺序以字段声明顺序优先
     *
     * @param primaryAtMethod    于方法上的注解
     * @param primaryAtField     于字段上的注解
     * @param supplementAtMethod 补充，primary 上的内容可能不全，这里用作补充
     * @param supplementAtField  补充
     * @param <T>                {@link Marked}的具体实现类型
     *
     * @return 合并后的 attributes，key 是字段名
     */
    static <T extends Marked> Map<String, Attribute> merge2Attr(
        Map<String, T> primaryAtMethod,
        Map<String, T> primaryAtField,
        Map<String, T> supplementAtMethod,
        Map<String, T> supplementAtField
    ) {
        Map<String, Attribute> attrMap = new LinkedHashMap<>();
        for (Map.Entry<String, T> entry : primaryAtField.entrySet()) {
            String name = entry.getKey();
            Marked field = entry.getValue();
            Marked method = primaryAtMethod.remove(name);
            if (method == null) {
                method = supplementAtMethod.remove(name);
            }
            attrMap.put(name, new Attribute(method, field));
        }
        for (Map.Entry<String, T> entry : primaryAtMethod.entrySet()) {
            String name = entry.getKey();
            Marked method = entry.getValue();
            Marked field = supplementAtField.remove(name);
            attrMap.put(name, new Attribute(method, field));
        }
        return attrMap;
    }
}
