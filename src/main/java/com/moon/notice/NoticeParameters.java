package com.moon.notice;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author benshaoye
 */
public interface NoticeParameters {

    /**
     * 返回所有参数
     *
     * @return
     */
    NoticeParameter[] getParameters();

    /**
     * 添加
     *
     * @param parameter
     *
     * @return
     */
    NoticeParameters add(NoticeParameter parameter);

    /**
     * 添加所有
     *
     * @param parameters
     *
     * @return
     */
    NoticeParameters addAll(NoticeParameter... parameters);

    /**
     * 删除第一个
     *
     * @param parameter
     *
     * @return
     */
    NoticeParameters remove(NoticeParameter parameter);

    /**
     * 删除所有匹配的
     *
     * @param tester
     *
     * @return
     */
    NoticeParameters removeAll(Predicate<? super NoticeParameter> tester);

    /**
     * 命名参数
     *
     * @return
     */
    Map toMap();

    /**
     * 索引参数
     *
     * @return
     */
    String[] toArray();

    /**
     * 删除所有
     *
     * @param parameter
     *
     * @return
     */
    default NoticeParameters removeAll(NoticeParameter parameter) {
        return removeAll(param -> Objects.equals(param, parameter));
    }

    /**
     * 删除指定名称参数
     *
     * @param name
     *
     * @return
     */
    default NoticeParameters removeAll(String name) {
        return removeAll(param -> Objects.equals(param.name(), name));
    }

    /**
     * 命名参数
     *
     * @return
     */
    default Map toObject() { return toMap(); }

    /**
     * 索引参数
     *
     * @return
     */
    default List toList() { return Arrays.asList(toArray()); }
}
