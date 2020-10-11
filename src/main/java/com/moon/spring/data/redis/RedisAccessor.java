package com.moon.spring.data.redis;

import com.moon.core.util.ListUtil;
import org.springframework.data.redis.core.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author moonsky
 */
public class RedisAccessor<K, V> {

    private final RedisTemplate<K, V> template;
    private final ExceptionHandler strategy;

    public RedisAccessor(RedisTemplate<K, V> redisTemplate) { this(redisTemplate, null); }

    public RedisAccessor(RedisTemplate<K, V> redisTemplate, ExceptionStrategy exceptionStrategy) {
        this(redisTemplate, (ExceptionHandler) exceptionStrategy);
    }

    public RedisAccessor(RedisTemplate<K, V> redisTemplate, ExceptionHandler exceptionStrategy) {
        this.strategy = exceptionStrategy == null ? ExceptionStrategy.LOGGER_INFO : exceptionStrategy;
        this.template = redisTemplate;
    }

    private void onException(Exception ex) {
        strategy.onException(ex);
    }

    private boolean onExceptionThenFalse(Exception ex) {
        onException(ex);
        return false;
    }

    private long onExceptionThenZero(Exception ex) {
        onException(ex);
        return 0;
    }

    private <T> T onExceptionThen(Exception ex, T result) {
        onException(ex);
        return result;
    }

    // ============================= ops ===============================

    public ValueOperations<K, V> value() { return template.opsForValue(); }

    public ClusterOperations<K, V> cluster() { return template.opsForCluster(); }

    public GeoOperations<K, V> geo() { return template.opsForGeo(); }

    public HashOperations<K, Object, Object> hash() { return template.opsForHash(); }

    public ListOperations<K, V> list() { return template.opsForList(); }

    public SetOperations<K, V> set() { return template.opsForSet(); }

    public StreamOperations<K, Object, Object> stream() { return template.opsForStream(); }

    public ZSetOperations<K, V> zset() { return template.opsForZSet(); }

    public BoundGeoOperations<K, V> geo(K key) { return template.boundGeoOps(key); }

    public BoundHashOperations<K, Object, Object> hash(K key) { return template.boundHashOps(key); }

    public BoundListOperations<K, V> list(K key) { return template.boundListOps(key); }

    public BoundSetOperations<K, V> collect(K key) { return template.boundSetOps(key); }

    public BoundStreamOperations<K, Object, Object> stream(K key) { return template.boundStreamOps(key); }

    public BoundValueOperations<K, V> value(K key) { return template.boundValueOps(key); }

    public BoundZSetOperations<K, V> zset(K key) { return template.boundZSetOps(key); }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     *
     * @return 返回是否设置成功
     */
    public boolean expire(K key, long time) {
        try {
            return time > 0 && falseIfNull(template.expire(key, time, TimeUnit.SECONDS));
        } catch (Exception e) {
            return onExceptionThenFalse(e);
        }
    }

    /**
     * 根据 key 获取过期时间
     *
     * @param key 键 不能为null
     *
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(K key) { return zeroIfNull(template.getExpire(key, TimeUnit.SECONDS)); }

    /**
     * 判断key是否存在
     *
     * @param key 键
     *
     * @return true 存在 false不存在
     */
    public boolean hasKey(K key) {
        try {
            return falseIfNull(template.hasKey(key));
        } catch (Exception e) {
            return onExceptionThenFalse(e);
        }
    }

    /**
     * 删除缓存
     *
     * @param key 指定 key
     *
     * @return 返回缓存中是否存在 key 对应的值
     */
    public boolean delete(K key) { return key == null || falseIfNull(template.delete(key)); }

    /**
     * 删除缓存
     *
     * @param keys 可以传一个值 或多个
     */
    @SafeVarargs
    public final long delete(K... keys) {
        if (keys != null && keys.length > 0) {
            if (keys.length == 1) {
                return delete(keys[0]) ? 1 : 0;
            } else {
                return zeroIfNull(template.delete(ListUtil.toList(keys)));
            }
        }
        return 0;
    }

    /*
     * ***********************************************************************************************************
     * * value ***************************************************************************************************
     * ***********************************************************************************************************
     */

    /**
     * 普通缓存获取
     *
     * @param key 键
     *
     * @return 值
     */
    public V get(K key) { return key == null ? null : value().get(key); }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     *
     * @return true 成功 false失败
     */
    public boolean set(K key, V value) {
        try {
            value().set(key, value);
            return true;
        } catch (Exception e) {
            return onExceptionThenFalse(e);
        }
    }

    /**
     * 从缓存中获取，获取为 null 则加载，并缓存
     *
     * @param key    键
     * @param puller 加载器
     *
     * @return 值
     */
    public V getOrPull(K key, Supplier<V> puller) {
        V cached = get(key);
        if (cached == null) {
            cached = puller.get();
            set(key, cached);
        }
        return cached;
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key             键
     * @param value           值
     * @param expireOfSeconds 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     *
     * @return true成功 false 失败
     */
    public boolean set(K key, V value, long expireOfSeconds) {
        try {
            if (expireOfSeconds > 0) {
                value().set(key, value, expireOfSeconds, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            return onExceptionThenFalse(e);
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key      键
     * @param value    值
     * @param time     时间
     * @param timeUnit 时间单位
     *
     * @return true成功 false 失败
     */
    public boolean set(K key, V value, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                value().set(key, value, time, timeUnit);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            return onExceptionThenFalse(e);
        }
    }

    /**
     * 从缓存中获取，获取为 null 则加载，并指定缓存时间缓存
     *
     * @param key             键
     * @param puller          加载器
     * @param expireOfSeconds 时间
     *
     * @return 值
     */
    public V getOrPull(K key, Supplier<V> puller, long expireOfSeconds) {
        V cached = get(key);
        if (cached == null) {
            cached = puller.get();
            set(key, cached, expireOfSeconds);
        }
        return cached;
    }

    /**
     * 从缓存中获取，获取为 null 则加载，并指定缓存时间缓存
     *
     * @param key      键
     * @param puller   加载器
     * @param time     时间
     * @param timeUnit 时间单位
     *
     * @return 值
     */
    public V getOrPull(K key, Supplier<V> puller, long time, TimeUnit timeUnit) {
        V cached = get(key);
        if (cached == null) {
            cached = puller.get();
            set(key, cached, time, timeUnit);
        }
        return cached;
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 增量(大于0)
     *
     * @return long
     */
    public long increment(K key, long delta) { return zeroIfNull(value().increment(key, delta)); }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 增量(小于0)
     *
     * @return long
     */
    public long decrement(K key, long delta) { return zeroIfNull(value().increment(key, -delta)); }

    /*
     * ***********************************************************************************************************
     * * hash ****************************************************************************************************
     * ***********************************************************************************************************
     */

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     *
     * @return 值
     */
    public Object hashGet(K key, String item) { return hash().get(key, item); }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     *
     * @return 对应的多个键值
     */
    public Map<Object, Object> hashEntries(K key) { return hash().entries(key); }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     *
     * @return true 成功 false 失败
     */
    public boolean hashPutAll(K key, Map<K, V> map) {
        try {
            hash().putAll(key, map);
            return true;
        } catch (Exception e) {
            return onExceptionThenFalse(e);
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     *
     * @return true成功 false失败
     */
    public boolean hashPutAll(K key, Map<K, V> map, long time) {
        try {
            hash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            return onExceptionThenFalse(e);
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     *
     * @return true 成功 false失败
     */
    public boolean hashPut(K key, Object item, Object value) {
        try {
            hash().put(key, item, value);
            return true;
        } catch (Exception e) {
            return onExceptionThenFalse(e);
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒)  注意:如果已存在的hash表有时间,这里将会替换原有的时间
     *
     * @return true 成功 false失败
     */
    public boolean hashPut(K key, Object item, Object value, long time) {
        try {
            hash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            return onExceptionThenFalse(e);
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public long hashDelete(K key, Object... item) { return zeroIfNull(hash().delete(key, item)); }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     *
     * @return true 存在 false不存在
     */
    public boolean hashHasKey(K key, Object item) { return hash().hasKey(key, item); }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key   键
     * @param item  项
     * @param delta 增量(大于0)
     *
     * @return double
     */
    public double hashIncrement(K key, Object item, double delta) {
        return hash().increment(key, item, delta);
    }

    /**
     * hash递减
     *
     * @param key   键
     * @param item  项
     * @param delta 增量(小于0)
     *
     * @return double
     */
    public double hashDecrement(K key, Object item, double delta) {
        return hash().increment(key, item, -delta);
    }

    /*
     * ***********************************************************************************************************
     * * set *****************************************************************************************************
     * ***********************************************************************************************************
     */

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     *
     * @return Set
     */
    public Set<V> collectGet(K key) {
        try {
            return set().members(key);
        } catch (Exception e) {
            return onExceptionThen(e, Collections.emptySet());
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     *
     * @return true 存在 false不存在
     */
    public boolean collectHasKey(K key, Object value) {
        try {
            return falseIfNull(set().isMember(key, value));
        } catch (Exception e) {
            return onExceptionThenFalse(e);
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     *
     * @return 成功个数
     */
    @SafeVarargs
    public final long collectAdd(K key, V... values) {
        try {
            return zeroIfNull(set().add(key, values));
        } catch (Exception e) {
            return onExceptionThenZero(e);
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     *
     * @return 成功个数
     */
    @SafeVarargs
    public final long collectAdd(K key, long time, V... values) {
        try {
            Long count = set().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return zeroIfNull(count);
        } catch (Exception e) {
            return onExceptionThenZero(e);
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     *
     * @return long
     */
    public long collectSize(K key) {
        try {
            return zeroIfNull(set().size(key));
        } catch (Exception e) {
            return onExceptionThenZero(e);
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     *
     * @return 移除的个数
     */
    public long collectRemove(K key, Object... values) {
        try {
            return zeroIfNull(set().remove(key, values));
        } catch (Exception e) {
            return onExceptionThenZero(e);
        }
    }

    /*
     * ***********************************************************************************************************
     * * list ****************************************************************************************************
     * ***********************************************************************************************************
     */

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束  0 到 -1代表所有值
     *
     * @return List
     */
    public List<V> listGet(K key, long start, long end) {
        try {
            return list().range(key, start, end);
        } catch (Exception e) {
            return onExceptionThen(e, Collections.emptyList());
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     *
     * @return long
     */
    public long listSize(K key) {
        try {
            return zeroIfNull(list().size(key));
        } catch (Exception e) {
            return onExceptionThenZero(e);
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引
     *
     * @return Object
     */
    public Object listGet(K key, long index) {
        try {
            return list().index(key, index);
        } catch (Exception e) {
            return onExceptionThen(e, null);
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     *
     * @return boolean
     */
    public boolean listAdd(K key, V value) {
        try {
            list().rightPush(key, value);
            return true;
        } catch (Exception e) {
            return onExceptionThenFalse(e);
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     *
     * @return boolean
     */
    public boolean listAdd(K key, V value, long time) {
        try {
            list().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            return onExceptionThenFalse(e);
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     *
     * @return boolean
     */
    public boolean listAddAll(K key, List<V> value) {
        try {
            list().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            return onExceptionThenFalse(e);
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     *
     * @return boolean
     */
    public boolean listAddAll(K key, List<V> value, long time) {
        try {
            list().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            return onExceptionThenFalse(e);
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     *
     * @return boolean
     */
    public boolean listSet(K key, long index, V value) {
        try {
            list().set(key, index, value);
            return true;
        } catch (Exception e) {
            return onExceptionThenFalse(e);
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     *
     * @return 移除的个数
     */
    public long listRemove(K key, long count, Object value) {
        try {
            return zeroIfNull(list().remove(key, count, value));
        } catch (Exception e) {
            return onExceptionThenZero(e);
        }
    }

    private static long zeroIfNull(Long value) { return value == null ? 0 : value; }

    private static boolean falseIfNull(Boolean value) { return value != null && value; }
}
