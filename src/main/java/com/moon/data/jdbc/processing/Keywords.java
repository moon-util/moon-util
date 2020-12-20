package com.moon.data.jdbc.processing;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author benshaoye
 */
final class Keywords {

    /**
     * 插入
     */
    private final static String INSERT = "insert";
    /**
     * 删除
     */
    private final static String DELETE = "delete";
    /**
     * 更新
     */
    private final static String UPDATE = "update";
    /**
     * 特殊更新，将指定字段设置为 null
     */
    private final static String CLEAR = "clear";
    /**
     * 特殊查询，返回值类型只能是单条数据对象
     */
    private final static String GET = "get";
    /**
     * 特殊查询，返回值类型只能是集合类: List、Set、Collection、Queue、Stream、Iterable、Iterator、数组
     */
    private final static String LIST = "list";
    /**
     * 特殊查询，只能返回数字类型数据
     */
    private final static String COUNT = "count";
    /**
     * 特殊查询，只能返回 boolean/Boolean 类型数据
     */
    private final static String EXISTS = "exists";
    /**
     * 普通查询
     */
    private final static String SELECTS = "select|fetch|query|find|read";

    public boolean isInsert(String methodName) { return is(methodName, INSERT); }

    public boolean isDelete(String methodName) { return is(methodName, DELETE); }

    public boolean isUpdate(String methodName) { return is(methodName, UPDATE); }

    public boolean isUpdateNull(String methodName) { return is(methodName, CLEAR); }

    public boolean isSelectExists(String methodName) { return is(methodName, EXISTS); }

    public boolean isSelectCount(String methodName) { return is(methodName, COUNT); }

    public boolean isSelectList(String methodName) { return is(methodName, LIST); }

    public boolean isSelectGet(String methodName) { return is(methodName, GET); }

    public boolean isSelect(String methodName) { return SELECTS.contains(key(methodName)); }

    private static boolean is(String str, String start) { return Objects.equals(key(str), start); }

    private static String key(String str) {
        StringBuilder sb = new StringBuilder(8);
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char ch = str.charAt(i);
            if (Character.isLowerCase(ch)) {
                sb.append(ch);
            } else {
                break;
            }
        }
        return sb.toString();
    }
}
