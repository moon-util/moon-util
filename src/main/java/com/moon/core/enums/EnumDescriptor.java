package com.moon.core.enums;

/**
 * @author benshaoye
 */
public interface EnumDescriptor {

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
     * @see Enum#name()
     */
    String name();

    /**
     * 枚举名字
     * <p>
     * 与{@link #name()}相同
     *
     * @return
     * @see com.moon.core.models.IdName
     * @see com.moon.core.models.KeyValue
     * @see com.moon.core.net.enums.StatusCode
     * @see Enum#name()
     */
    default String getName() {
        return name();
    }
}
