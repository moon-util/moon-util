/**
 * @author benshaoye
 */
package com.moon.redis;
/*
 * 在 spring-boot 中使用时，需要手动写一个 bean：
 *
 * @Bean
 * public RedisAccessor redisUtil(RedisTemplate<String, Object> redisTemplate){
 *     return new RedisAccessor(redisTemplate);
 *     // 或 return new StringRedisAccessor(redisTemplate);
 * }
 */