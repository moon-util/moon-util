package com.moon.spring.data.redis;

import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author moonsky
 */
public class StringRedisService extends RedisService<String, Object> {

    public StringRedisService(RedisTemplate<String, Object> redisTemplate) { super(redisTemplate); }

    public StringRedisService(
        RedisTemplate<String, Object> redisTemplate, ExceptionStrategy exceptionStrategy
    ) { super(redisTemplate, exceptionStrategy); }

    public StringRedisService(
        RedisTemplate<String, Object> redisTemplate, ExceptionHandler exceptionStrategy
    ) { super(redisTemplate, exceptionStrategy); }
}
