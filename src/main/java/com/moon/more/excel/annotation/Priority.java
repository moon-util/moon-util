package com.moon.more.excel.annotation;

/**
 * @author benshaoye
 */
public enum Priority {
    /**
     * 优先按层级结构分组，分组后同组内根据{@code order}值进行排序
     * <p>
     * 同级别的顺序按相同级别的最小序号为依据，同组内相同序号按名称排序
     */
    GROUP,
    /**
     * 优先按照{@code order}和名称排序，
     * <p>
     * 排序后按先后顺序有相邻的表头名称就合并，否则各自独立显示
     */
    ORDER,
    ;
}
