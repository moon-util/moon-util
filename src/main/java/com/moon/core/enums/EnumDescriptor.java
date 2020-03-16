package com.moon.core.enums;

import com.moon.core.getter.NameGetter;

/**
 * @author benshaoye
 */
public interface EnumDescriptor extends NameGetter {

    /**
     * 枚举信息
     *
     * @return
     */
    String getText();

    /**
     * 枚举名字
     *
     * @return
     *
     * @see Enum#name()
     */
    String name();

    /**
     * 枚举名字
     * <p>
     * 与{@link #name()}相同
     *
     * @return
     *
     * @see com.moon.core.model.IdName
     * @see com.moon.core.model.KeyValue
     * @see com.moon.core.net.enums.StatusCode
     * @see Enum#name()
     */
    @Override
    default String getName() {
        return name();
    }
}
