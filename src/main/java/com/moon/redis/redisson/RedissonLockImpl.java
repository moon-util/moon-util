package com.moon.redis.redisson;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * 基于 Redisson 的分布式锁实现
 * <p>
 * redis 加锁细节：
 *
 * 一、原子命令加锁：set key random_value NX PX 3000
 * 1. 早期的加锁是基于：set + set expire，因为早期的 redis 不支持原子命令加锁
 * 2. key: 我怎么解释呢？
 * 3. random_value: 之所以设置成随机值，是为了释放锁的时候确保是自己释放的，更安全的释放锁；
 * 4. NX: 只有在 key 值不存在的时候才能 set 成功，保证只有第一个请求 key 的客户端才能获得锁；
 * 5. PX 3000: 自动过期时间
 *
 * @author moonsky
 */
public class RedissonLockImpl {

    private final RedissonClient client;
    private final TimeUnit defaultTimeUnit;

    public RedissonLockImpl(RedissonClient client) { this(client, TimeUnit.SECONDS); }

    public RedissonLockImpl(RedissonClient client, TimeUnit unit) {
        this.defaultTimeUnit = unit;
        this.client = client;
    }

    private RLock getLock(String lockKey) {
        return client.getLock(lockKey);
    }

    public RLock lock(String key) {
        RLock lock = getLock(key);
        lock.lock();
        return lock;
    }

    public RLock lock(String key, long timeout) {
        return this.lock(key, timeout, defaultTimeUnit);
    }

    public RLock lock(String key, long timeout, TimeUnit unit) {
        RLock lock = getLock(key);
        lock.lock(timeout, unit);
        return lock;
    }

    public boolean tryLock(String key) {
        RLock lock = getLock(key);
        return lock.tryLock();
    }

    public boolean tryLock(String key, long waitTime, long timeout, TimeUnit unit) {
        RLock lock = getLock(key);
        try {
            return lock.tryLock(waitTime, timeout, unit);
        } catch (InterruptedException e) {
            return false;
        }
    }

    public void unlock(String key) {
        unlock(getLock(key));
    }

    public void unlock(RLock lock) {
        lock.unlock();
    }
}
