package com.moon.more.excel.table;

import com.moon.more.excel.annotation.FieldTransformer;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * @author benshaoye
 */
final class ParserUtil {

    /**
     * 检测必须是普通可实例化的类，不能是接口抽象类
     *
     * @param type {@link FieldTransformer}的实现类
     */
    static void checkValidImplClass(Class type) {
        int modifiers = type.getModifiers();
        if (!Modifier.isPublic(modifiers)) {
            throw new IllegalStateException("指定类「" + type + "」应该是公共类（public）");
        }
        if (Modifier.isInterface(modifiers) || Modifier.isAbstract(modifiers)) {
            throw new IllegalStateException("指定类「" + type + "」不能使接口或抽象类");
        }
        if (!FieldTransformer.class.isAssignableFrom(type)) {
            throw new IllegalStateException("指定类「" + type + "」应该是「" + FieldTransformer.class + "」的实现类");
        }
    }

    /**
     * 转换 Attribute 为具体执行类
     *
     * @param attributeMap {@link #mergeAttr(Map, Map)}
     * @param transformer  like name
     *
     * @return Renderer，最终用于渲染 Table 的类型
     */
    static TableRenderer mapAttrs(
        Map<String, Attribute> attributeMap, Function<Attribute, TableCol> transformer
    ) {
        Set<Map.Entry<String, Attribute>> attrEntrySet = attributeMap.entrySet();
        TableCol[] columns = new TableCol[attrEntrySet.size()];

        int index = 0;
        for (Map.Entry<String, Attribute> attrEntry : attrEntrySet) {
            columns[index++] = transformer.apply(attrEntry.getValue());
        }
        Arrays.sort(columns, TableCol::compareTo);
        return new TableRenderer(columns);
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
    static <T extends Marked> Map<String, Attribute> mergeAttr(
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
