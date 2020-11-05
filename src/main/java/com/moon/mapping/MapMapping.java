package com.moon.mapping;

import java.util.HashMap;
import java.util.Map;

/**
 * @author moonsky
 */
public interface MapMapping<THIS> extends ObjectMapping<THIS> {

    /**
     * 从{@code propertiesMap}复制属性到{@code thisObject}
     *
     * @param thisObject    属性覆盖目标对象
     * @param propertiesMap 包含{@code thisObject}属性和值的{@link Map}，
     *                      按字段名，有多少字段就获取和设置多少字段
     *
     * @return 属性覆盖目标对象 thisObject
     */
    THIS fromMap(THIS thisObject, Map<String, ?> propertiesMap);

    /**
     * 将{@code thisObject}的所有属性映射到指定{@code targetMap}中
     *
     * @param thisObject 数据源对象
     * @param targetMap  目标容器
     * @param <M>        Map
     *
     * @return java.util.Map、{@code targetMap}
     */
    <M extends Map<String, ?>> M toMap(THIS thisObject, M targetMap);

    /**
     * 将{@code thisObject}的所有属性映射到{@link HashMap}中
     *
     * @param thisObject 数据源对象
     *
     * @return java.util.HashMap
     */
    default HashMap<String, ?> toHashMap(THIS thisObject) {
        return toMap(thisObject, new HashMap<>(16));
    }
}
