package com.moon.redis;

import com.moon.core.util.ListUtil;
import org.springframework.data.redis.core.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author benshaoye
 */
public class RedisUtil {

    private final RedisTemplate<String, Object> template;
    private final ExceptionHandler strategy;

    public RedisUtil(RedisTemplate<String, Object> redisTemplate) { this(redisTemplate, ExceptionStrategy.IGNORE); }

    public RedisUtil(RedisTemplate<String, Object> redisTemplate, ExceptionStrategy exceptionStrategy) {
        this(redisTemplate, (ExceptionHandler) exceptionStrategy);
    }

    public RedisUtil(RedisTemplate<String, Object> redisTemplate, ExceptionHandler exceptionStrategy) {
        this.strategy = exceptionStrategy == null ? ExceptionStrategy.IGNORE : exceptionStrategy;
        this.template = redisTemplate;
    }

    private final void onCanIgnoreException(Exception ex) { strategy.onException(ex); }

    @FunctionalInterface
    public interface ExceptionHandler extends Consumer<Exception> {

        void onException(Exception ex);

        @Override
        default void accept(Exception e) { onException(e);}
    }

    public enum ExceptionStrategy implements ExceptionHandler {
        IGNORE,
        MESSAGE {
            @Override
            public void onException(Exception ex) { System.err.println(ex.getMessage()); }
        },
        PRINT {
            @Override
            public void onException(Exception ex) { ex.printStackTrace(); }
        },
        THROW {
            @Override
            public void onException(Exception ex) { throw new RuntimeException(ex); }
        };

        @Override
        public void onException(Exception ex) {}
    }

    // ============================= ops ===============================

    public ValueOperations value() { return template.opsForValue(); }

    public ClusterOperations cluster() { return template.opsForCluster(); }

    public GeoOperations geo() { return template.opsForGeo(); }

    public HashOperations hash() { return template.opsForHash(); }

    public ListOperations list() { return template.opsForList(); }

    public SetOperations set() { return template.opsForSet(); }

    public StreamOperations stream() { return template.opsForStream(); }

    public ZSetOperations zset() { return template.opsForZSet(); }

    public BoundGeoOperations geo(String key) { return template.boundGeoOps(key); }

    public BoundHashOperations hash(String key) { return template.boundHashOps(key); }

    public BoundListOperations list(String key) { return template.boundListOps(key); }

    public BoundSetOperations collect(String key) { return template.boundSetOps(key); }

    public BoundStreamOperations stream(String key) { return template.boundStreamOps(key); }

    public BoundValueOperations value(String key) { return template.boundValueOps(key); }

    public BoundZSetOperations zset(String key) { return template.boundZSetOps(key); }

    // ============================= common ============================

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     *
     * @return boolean
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                template.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            onCanIgnoreException(e);
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     *
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) { return template.getExpire(key, TimeUnit.SECONDS); }

    /**
     * 判断key是否存在
     *
     * @param key 键
     *
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return template.hasKey(key);
        } catch (Exception e) {
            onCanIgnoreException(e);
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 指定 key
     */
    public void delete(String key) {
        if (key != null) {
            template.delete(key);
        }
    }

    /**
     * 删除缓存
     *
     * @param keys 可以传一个值 或多个
     */
    public void delete(String... keys) {
        if (keys != null && keys.length > 0) {
            if (keys.length == 1) {
                delete(keys[0]);
            } else {
                template.delete(ListUtil.toList(keys));
            }
        }
    }

    //============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     *
     * @return 值
     */
    public Object get(String key) { return key == null ? null : template.opsForValue().get(key); }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     *
     * @return true 成功 false失败
     */
    public boolean set(String key, Object value) {
        try {
            template.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            onCanIgnoreException(e);
            return false;
        }
    }

    /**
     * 从缓存中获取，获取为 null 则加载，并缓存
     *
     * @param key    键
     * @param puller 加载器
     * @param <T>    类型
     *
     * @return 值
     */
    public <T> T getOrPull(String key, Supplier<T> puller) {
        T cached = (T) get(key);
        if (cached == null) {
            cached = puller.get();
            set(key, cached);
        }
        return cached;
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     *
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                template.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            onCanIgnoreException(e);
            return false;
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
    public boolean set(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                template.opsForValue().set(key, value, time, timeUnit);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            onCanIgnoreException(e);
            return false;
        }
    }

    /**
     * 从缓存中获取，获取为 null 则加载，并指定缓存时间缓存
     *
     * @param key    键
     * @param puller 加载器
     * @param <T>    类型
     * @param time   时间
     *
     * @return 值
     */
    public <T> T getOrPull(String key, Supplier<T> puller, long time) {
        T cached = (T) get(key);
        if (cached == null) {
            cached = puller.get();
            set(key, cached, time);
        }
        return cached;
    }

    /**
     * 从缓存中获取，获取为 null 则加载，并指定缓存时间缓存
     *
     * @param key      键
     * @param puller   加载器
     * @param <T>      类型
     * @param time     时间
     * @param timeUnit 时间单位
     *
     * @return 值
     */
    public <T> T getOrPull(String key, Supplier<T> puller, long time, TimeUnit timeUnit) {
        T cached = (T) get(key);
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
    public long increment(String key, long delta) { return template.opsForValue().increment(key, delta); }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 增量(小于0)
     *
     * @return long
     */
    public long decrement(String key, long delta) { return template.opsForValue().increment(key, -delta); }

    //================================Map=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     *
     * @return 值
     */
    public Object hashGet(String key, String item) { return template.opsForHash().get(key, item); }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     *
     * @return 对应的多个键值
     */
    public Map<Object, Object> hashEntries(String key) { return template.opsForHash().entries(key); }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     *
     * @return true 成功 false 失败
     */
    public boolean hashPutAll(String key, Map<String, Object> map) {
        try {
            template.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
    public boolean hashPutAll(String key, Map<String, Object> map, long time) {
        try {
            template.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            onCanIgnoreException(e);
            return false;
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
    public boolean hashPut(String key, String item, Object value) {
        try {
            template.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            onCanIgnoreException(e);
            return false;
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
    public boolean hashPut(String key, String item, Object value, long time) {
        try {
            template.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            onCanIgnoreException(e);
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hashDelete(String key, Object... item) { template.opsForHash().delete(key, item); }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     *
     * @return true 存在 false不存在
     */
    public boolean hashHasKey(String key, String item) { return template.opsForHash().hasKey(key, item); }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key   键
     * @param item  项
     * @param delta 增量(大于0)
     *
     * @return double
     */
    public double hashIncrement(String key, String item, double delta) {
        return template.opsForHash().increment(key, item, delta);
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
    public double hashDecrement(String key, String item, double delta) {
        return template.opsForHash().increment(key, item, -delta);
    }

    // ============================ set =============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     *
     * @return Set
     */
    public Set<Object> collectGet(String key) {
        try {
            return template.opsForSet().members(key);
        } catch (Exception e) {
            onCanIgnoreException(e);
            return Collections.emptySet();
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
    public boolean collectHasKey(String key, Object value) {
        try {
            return template.opsForSet().isMember(key, value);
        } catch (Exception e) {
            onCanIgnoreException(e);
            return false;
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
    public long collectAdd(String key, Object... values) {
        try {
            return template.opsForSet().add(key, values);
        } catch (Exception e) {
            onCanIgnoreException(e);
            return 0;
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
    public long collectAdd(String key, long time, Object... values) {
        try {
            Long count = template.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            onCanIgnoreException(e);
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     *
     * @return long
     */
    public long collectSize(String key) {
        try {
            return template.opsForSet().size(key);
        } catch (Exception e) {
            onCanIgnoreException(e);
            return 0;
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
    public long collectRemove(String key, Object... values) {
        try {
            Long count = template.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            onCanIgnoreException(e);
            return 0;
        }
    }

    //===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束  0 到 -1代表所有值
     *
     * @return List
     */
    public List<Object> listGet(String key, long start, long end) {
        try {
            return template.opsForList().range(key, start, end);
        } catch (Exception e) {
            onCanIgnoreException(e);
            return Collections.emptyList();
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     *
     * @return long
     */
    public long listSize(String key) {
        try {
            return template.opsForList().size(key);
        } catch (Exception e) {
            onCanIgnoreException(e);
            return 0;
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
    public Object listGet(String key, long index) {
        try {
            return template.opsForList().index(key, index);
        } catch (Exception e) {
            onCanIgnoreException(e);
            return null;
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
    public boolean listAdd(String key, Object value) {
        try {
            template.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            onCanIgnoreException(e);
            return false;
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
    public boolean listAdd(String key, Object value, long time) {
        try {
            template.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            onCanIgnoreException(e);
            return false;
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
    public boolean listAddAll(String key, List<Object> value) {
        try {
            template.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            onCanIgnoreException(e);
            return false;
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
    public boolean listAddAll(String key, List<Object> value, long time) {
        try {
            template.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            onCanIgnoreException(e);
            return false;
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
    public boolean listSet(String key, long index, Object value) {
        try {
            template.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            onCanIgnoreException(e);
            return false;
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
    public long listRemove(String key, long count, Object value) {
        try {
            Long remove = template.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            onCanIgnoreException(e);
            return 0;
        }
    }
}
