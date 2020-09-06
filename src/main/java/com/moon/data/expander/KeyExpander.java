package com.moon.data.expander;

import com.moon.core.lang.StringUtil;

/**
 * 当 ID 为数字类型时，如果过长传给前端可能出现精度问题
 * <p>
 * JavaScript 只能精确显示 17 位以下的数字，超过 16 位建议用 string 显示
 *
 * @author benshaoye
 */
public interface KeyExpander<ID extends Number> {

    /**
     * ID
     *
     * @return ID
     */
    ID getId();

    /**
     * stringify id
     *
     * @return stringify id
     */
    default String getKey() { return StringUtil.stringify(getId()); }
}
