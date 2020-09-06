package com.moon.core.model;

import com.moon.core.lang.ArrayUtil;
import com.moon.core.model.getter.*;

import java.util.*;
import java.util.function.Function;

/**
 * 树形元素模板
 *
 * @author moonsky
 */
public class TreeElement<T> implements IdGetter, NameGetter {

    private String id;

    private String name;

    private List<TreeElement<T>> children;
    /**
     * 携带的扩展数据
     */
    private T data;

    public TreeElement() { }

    public TreeElement(String id, String name) {
        this.name = name;
        this.id = id;
    }

    public TreeElement(String id, String name, List<TreeElement<T>> children) {
        this.children = children;
        this.name = name;
        this.id = id;
    }

    public static <T extends KeyValueGetter> List<TreeElement<T>> fromKeyValueList(
        Iterable<T> data, Function<T, String> grouper, Function<String, String>... reGroupers
    ) { return fromList(data, grouper, KeyGetter::getKey, ValueGetter::getValue, reGroupers); }

    public static <T extends IdNameGetter> List<TreeElement<T>> fromIdNameList(
        Iterable<T> data, Function<T, String> grouper, Function<String, String>... reGroupers
    ) { return fromList(data, grouper, IdGetter::getId, NameGetter::getName, reGroupers); }

    /**
     * 从数据列表中解析树形结构，注意参数{@code grouper}的返回值说明
     *
     * @param data
     * @param grouper    分组函数；
     *                   返回值为父节点的 id；
     *                   若返回值为 null 代表没有父节点，可用来标记顶级节点
     * @param idGetter   当前节点的 id，{@link #id}
     * @param nameGetter 当前节点的 name，{@link #name}
     * @param <T>        数据类型
     *
     * @return
     */
    public static <T> List<TreeElement<T>> fromList(
        Iterable<T> data, Function<T, String> grouper,

        Function<T, String> idGetter, Function<T, String> nameGetter
    ) { return fromList(data, grouper, idGetter, nameGetter, null); }

    /**
     * 从数据列表中解析树形结构，注意参数{@code grouper}的返回值，null 值是有特殊意义的
     *
     * @param data
     * @param grouper    分组函数；
     *                   返回值为父节点的 id；
     *                   若返回值为 null 代表没有父节点，可用来标记顶级节点
     * @param idGetter   当前节点的 id，{@link #id}
     * @param nameGetter 当前节点的 name，{@link #name}
     * @param reGroupers 再分组函数（第一次分组以及树形化之后，可能还剩余有数据，可以重新分组）
     * @param <T>        数据类型
     *
     * @return
     */
    public static <T> List<TreeElement<T>> fromList(
        Iterable<T> data,
        Function<T, String> grouper,
        Function<T, String> idGetter,
        Function<T, String> nameGetter,
        Function<String, String>... reGroupers
    ) {
        List<TreeElement<T>> topParents = new ArrayList<>();
        Map<String, List<TreeElement<T>>> grouped = new HashMap<>();
        for (T datum : data) {
            String key = grouper.apply(datum);
            if (key == null) {
                TreeElement<T> element = new TreeElement<>();
                element.setName(nameGetter.apply(datum));
                element.setId(idGetter.apply(datum));
                element.setData(datum);
                topParents.add(element);
            } else {
                List<TreeElement<T>> list = grouped.get(key);
                if (list == null) {
                    list = new ArrayList<>();
                    grouped.put(key, list);
                }
                TreeElement<T> element = new TreeElement<>();
                element.setName(nameGetter.apply(datum));
                element.setId(idGetter.apply(datum));
                element.setData(datum);
                list.add(element);
            }
        }
        doGroup(topParents, grouped);
        // 再分组
        if (!grouped.isEmpty() && ArrayUtil.isNotEmpty(reGroupers)) {
            Map<String, List<TreeElement<T>>> restGrouped = grouped;
            for (int i = 0, len = reGroupers.length; i < len; i++) {
                Function<String, String> groupFn = reGroupers[i];
                Map<String, List<TreeElement<T>>> reGrouped = new HashMap<>();
                for (Map.Entry<String, List<TreeElement<T>>> listEntry : restGrouped.entrySet()) {
                    String reGroupKey = groupFn.apply(listEntry.getKey());
                    List<TreeElement<T>> reGroupArr = reGrouped.get(reGroupKey);
                    if (reGroupArr == null) {
                        reGrouped.put(reGroupKey, listEntry.getValue());
                    } else {
                        reGroupArr.addAll(listEntry.getValue());
                    }
                }
                redoGroup(topParents, reGrouped);
                if (reGrouped.isEmpty()) {
                    break;
                } else {
                    restGrouped = reGrouped;
                }
            }
        }
        return topParents;
    }

    @Override
    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    @Override
    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public List<TreeElement<T>> getChildren() { return children; }

    public void setChildren(List<TreeElement<T>> children) {
        this.children = children;
    }

    public T getData() { return data; }

    public void setData(T data) { this.data = data; }

    public void clearExpandData() {
        List<TreeElement<T>> children = getChildren();
        if (children != null) {
            for (TreeElement<T> child : children) {
                if (child != null) {
                    child.clearExpandData();
                }
            }
        }
        setData(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        TreeElement<?> that = (TreeElement<?>) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TreeElement{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", children=").append(children);
        sb.append(", data=").append(data);
        sb.append('}');
        return sb.toString();
    }

    /*
    supports
     */

    private static <T> void redoGroup(List<TreeElement<T>> parents, Map<String, List<TreeElement<T>>> grouped) {
        for (TreeElement<T> parent : parents) {
            String parentId = parent.getId();
            List list = grouped.remove(parentId);
            if (list != null) {
                List<TreeElement<T>> children = parent.getChildren();
                if (children == null) {
                    parent.setChildren(list);
                } else {
                    children.addAll(list);
                }
                redoGroup(list, grouped);
            }
        }
    }

    private static <T> void doGroup(List<TreeElement<T>> parents, Map<String, List<TreeElement<T>>> grouped) {
        for (TreeElement<T> parent : parents) {
            String parentId = parent.getId();
            List list = grouped.remove(parentId);
            if (list != null) {
                parent.setChildren(list);
                doGroup(list, grouped);
            }
        }
    }
}
