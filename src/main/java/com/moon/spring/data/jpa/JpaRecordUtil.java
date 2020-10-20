package com.moon.spring.data.jpa;

import com.moon.data.Record;
import com.moon.data.registry.LayerEnum;
import com.moon.spring.data.jpa.factory.CacheNamespace;

import java.util.Optional;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class JpaRecordUtil extends CacheNamespace {

    private JpaRecordUtil() { noInstanceError(); }

    /**
     * 返回{@code JpaRecord}实体缓存命名空间
     *
     * @param record 实体
     *
     * @return 命名空间，如果不存在就返回{@code null}
     */
    public static String getCacheNamespace(Record record) { return obtainCacheNamespace(record); }

    /**
     * 暂时保留
     *
     * @param domainClass 实体类
     * @param id          对应 ID
     * @param <ID>        ID 数据类型
     * @param <E>         实体数据类型
     *
     * @return ID 对应的实体
     */
    public static <ID, E extends JpaRecord<ID>> Optional<E> findById(Class<E> domainClass, ID id) {
        return LayerEnum.REPOSITORY.get(domainClass).findById(id);
    }
}
