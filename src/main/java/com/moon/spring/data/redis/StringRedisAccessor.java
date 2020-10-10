package com.moon.spring.data.redis;

import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author moonsky
 */
public class StringRedisAccessor extends RedisAccessor<String, Object> {

    public StringRedisAccessor(RedisTemplate<String, Object> redisTemplate) { super(redisTemplate); }

    public StringRedisAccessor(
        RedisTemplate<String, Object> redisTemplate, ExceptionStrategy exceptionStrategy
    ) { super(redisTemplate, exceptionStrategy); }

    public StringRedisAccessor(
        RedisTemplate<String, Object> redisTemplate, ExceptionHandler exceptionStrategy
    ) { super(redisTemplate, exceptionStrategy); }
}
