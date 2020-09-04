package com.moon.core.lang;

import org.springframework.cache.CacheManager;

/**
 * @author moonsky
 */
public final class MoonKey {

    public final static class Data {

        public final static class Jpa {

            public final static String IDENTIFIER = "moon.data.jpa.identifier";
            /**
             * @see CacheManager 的实现类
             */
            // public final static String CACHE_MANAGER_TYPE = "moon.data.jpa.cache-manager-type";
        }
    }
}
