package com.moon.accessor.session;

import com.moon.accessor.config.Configuration;

/**
 * @author benshaoye
 */
public interface DSLSessionBuilder {

    /**
     * 用配置信息构建 DSLSession
     *
     * @param configuration 配置信息
     *
     * @return DSLSession
     */
    DSLSession build(Configuration configuration);
}
