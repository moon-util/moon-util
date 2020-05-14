package com.moon.redis;

import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author benshaoye
 */
public class RedisStringAccessor extends RedisAccessor<String, Object> {

    public RedisStringAccessor(RedisTemplate<String, Object> redisTemplate) { super(redisTemplate); }

    public RedisStringAccessor(
        RedisTemplate<String, Object> redisTemplate, ExceptionStrategy exceptionStrategy
    ) { super(redisTemplate, exceptionStrategy); }

    public RedisStringAccessor(
        RedisTemplate<String, Object> redisTemplate, ExceptionHandler exceptionStrategy
    ) { super(redisTemplate, exceptionStrategy); }
}
