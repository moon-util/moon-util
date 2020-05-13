/**
 * @author benshaoye
 */
package com.moon.redis;
/*
 * 在 spring-boot 中使用时，需要手动写一个 bean：
 *
 * @Bean
 * public ContextUtil redisUtil(RedisTemplate<String, Object> redisTemplate){
 *     return new RedisUtil(redisTemplate);
 * }
 */