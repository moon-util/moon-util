package com.moon.more.excel.annotation;

/**
 * @author benshaoye
 */
public enum MergeStrategy {
    /**
     * 不合并，只填充第一行数据
     */
    FILL_FIRST,
    /**
     * 不合并，只填充最后一行数据
     */
    FILL_LAST,
    /**
     * 不合并，填充每一行数据
     */
    FILL_EVERY,
    /**
     * 合并，垂直顶对齐
     */
    MERGE_ALIGN_TOP,
    /**
     * 合并，垂直居中对齐
     */
    MERGE_ALIGN_MIDDLE,
    /**
     * 合并，垂直底部对齐
     */
    MERGE_ALIGN_BOTTOM,
}
