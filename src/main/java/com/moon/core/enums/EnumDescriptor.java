package com.moon.core.enums;

import com.moon.more.model.getter.NameGetter;

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
     * @see com.moon.more.model.IdName
     * @see com.moon.more.model.KeyValue
     * @see com.moon.core.net.enums.StatusCode
     * @see Enum#name()
     */
    @Override
    default String getName() {
        return name();
    }
}
