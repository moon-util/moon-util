package com.moon.office.excel.core;

import com.moon.core.beans.BeanInfoUtil;
import com.moon.core.beans.FieldDescriptor;
import com.moon.core.enums.ArrayOperator;
import com.moon.core.enums.ArraysEnum;
import com.moon.core.util.runner.RunnerUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author benshaoye
 */
class VarSetterIn implements VarSetter {
    private final static int NO_SIZE = -1;
    private final String[] keys;
    private final String expression;

    public VarSetterIn(String[] keys, String expression) {
        this.keys = keys;
        this.expression = expression;
    }

    @Override
    public boolean isIn() {
        return true;
    }

    /**
     * 工厂模式、 迭代器模式
     *
     * @param centerMap
     * @param target
     * @return
     */
    @Override
    public WorkCenterMap setVarAndRender(WorkCenterMap centerMap, CenterRenderer target) {
        Object data = RunnerUtil.run(expression, centerMap);
        if (data instanceof Collection) {
            renderCollect(centerMap, target, keys, data);
        } else if (data instanceof Map) {
            renderMap(centerMap, target, keys, data);
        } else if (data == null) {
            return centerMap;
        } else if (data.getClass().isArray()) {
            renderArray(centerMap, target, keys, data);
        } else if (data instanceof Iterator) {
            renderIterator(centerMap, target, keys, data);
        } else if (data instanceof Iterable) {
            renderIterable(centerMap, target, keys, data);
        } else if (data instanceof Number) {
            renderNumber(centerMap, target, keys, data);
        } else if (data instanceof ResultSet) {
            renderResultSet(centerMap, target, keys, data);
        } else if (data instanceof CharSequence) {
            renderSequence(centerMap, target, keys, data);
        } else {
            renderBean(centerMap, target, keys, data);
        }
        return centerMap;
    }

    private final static void renderSequence(
        WorkCenterMap center, CenterRenderer target, String[] keys, Object data
    ) {
        char[] chars = data.toString().toCharArray();
        CenterRenderer[] children = target.getChildren();
        final int size = chars.length, length = children.length;
        for (int i = 0; i < size; i++) {
            setVars(center, keys, chars[i], i, i, size, i == 0, i + 1 == size);
            renderWhen(center, target, children, length);
        }
    }

    private final static void renderResultSet(
        WorkCenterMap center, CenterRenderer target, String[] keys, Object data
    ) {
        try {
            ResultSet set = (ResultSet) data;
            CenterRenderer[] children = target.getChildren();
            for (int i = 0, length = children.length, size = NO_SIZE; set.next(); i++) {
                setVars(center, keys, set, i, i, size, set.isFirst(), set.isLast());
                renderWhen(center, target, children, length);
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private final static void renderBean(
        WorkCenterMap center, CenterRenderer target, String[] keys, Object data
    ) {
        Map<String, FieldDescriptor> descriptorMap = BeanInfoUtil.getFieldDescriptorsMap(data.getClass());
        CenterRenderer[] children = target.getChildren();
        final int size = descriptorMap.size(), length = children.length;
        Set<Map.Entry<String, FieldDescriptor>> entries = descriptorMap.entrySet();
        int outerIndex = 0;
        for (Map.Entry<String, FieldDescriptor> entry : entries) {
            setVars(center, keys, entry.getValue().getValueIfPresent(data, true),
                entry.getKey(), outerIndex, size, outerIndex == 0, outerIndex + 1 == size);
            renderWhen(center, target, children, length);
            outerIndex++;
        }
    }

    private final static void renderNumber(
        WorkCenterMap center, CenterRenderer target, String[] keys, Object data
    ) {
        CenterRenderer[] children = target.getChildren();
        final int length = children.length, max = ((Number) data).intValue();
        for (int index = 0; index < max; index++) {
            setVars(center, keys, index, index, index, max, index == 0, false);
            renderWhen(center, target, children, length);
        }
    }

    /**
     * size 不可预测，总是为 0, last 总是为 false
     *
     * @param center
     * @param target
     * @param keys
     * @param data
     */
    private final static void renderIterator(
        WorkCenterMap center, CenterRenderer target, String[] keys, Object data
    ) {
        Iterator iterator = (Iterator) data;
        CenterRenderer[] children = target.getChildren();
        final int length = children.length;
        for (int index = 0, size = NO_SIZE; iterator.hasNext(); index++) {
            setVars(center, keys, iterator.next(), index, index, size, index == 0, false);
            renderWhen(center, target, children, length);
        }
    }

    /**
     * size 不可预测，总是为 0, last 总是为 false
     *
     * @param center
     * @param target
     * @param keys
     * @param data
     */
    private final static void renderIterable(
        WorkCenterMap center, CenterRenderer target, String[] keys, Object data
    ) {
        Iterable iterable = (Iterable) data;
        CenterRenderer[] children = target.getChildren();
        final int length = children.length, size = NO_SIZE;
        int outerIndex = 0;
        for (Object item : iterable) {
            setVars(center, keys, item, outerIndex, outerIndex, size, outerIndex == 0, false);
            renderWhen(center, target, children, length);
        }
    }

    private final static void renderArray(
        WorkCenterMap centerMap, CenterRenderer target, String[] keys, Object data
    ) {
        ArrayOperator arrayType = ArraysEnum.getOrObjects(data);
        CenterRenderer[] children = target.getChildren();
        final int size = arrayType.length(data), length = children.length;
        int index = 0;
        for (Object item; index < size; index++) {
            item = arrayType.get(data, index);
            setVars(centerMap, keys, item, index, index, size, index == 0, index + 1 == size);
            renderWhen(centerMap, target, children, length);
        }
    }

    private final static void renderCollect(
        WorkCenterMap centerMap, CenterRenderer target, String[] keys, Object data
    ) {
        Collection collect = (Collection) data;
        CenterRenderer[] children = target.getChildren();
        final int size = collect.size(), length = children.length;
        int index = 0;
        for (Object item : collect) {
            setVars(centerMap, keys, item, index, index, size, index == 0, index + 1 == size);
            renderWhen(centerMap, target, children, length);
            index++;
        }
    }

    private final static void renderMap(
        WorkCenterMap centerMap, CenterRenderer target, String[] keys, Object data
    ) {
        Map map = (Map) data;
        int outerIndex = 0;
        CenterRenderer[] children = target.getChildren();
        final int size = map.size(), length = children.length;
        Set<Map.Entry> entries = map.entrySet();
        for (Map.Entry entry : entries) {
            setVars(centerMap, keys, entry.getValue(), entry.getKey(),
                outerIndex, size, outerIndex == 0, outerIndex + 1 == size);
            renderWhen(centerMap, target, children, length);
            outerIndex++;
        }
    }

    private final static void renderWhen(
        WorkCenterMap centerMap, CenterRenderer target,
        CenterRenderer[] children, int length
    ) {
        if (target.isWhen(centerMap)) {
            target.beforeRender(centerMap);
            for (int innerIndex = 0; innerIndex < length; innerIndex++) {
                children[innerIndex].render(centerMap);
            }
            target.afterRender(centerMap);
        }
    }

    /**
     * @param centerMap
     * @param keys
     * @param values    value, key, index, size, first, last
     */
    private final static void setVars(WorkCenterMap centerMap, String[] keys, Object... values) {
        for (int i = 0, len = Math.min(keys.length, values.length); i < len; i++) {
            centerMap.put(keys[i], values[i]);
        }
    }
}
