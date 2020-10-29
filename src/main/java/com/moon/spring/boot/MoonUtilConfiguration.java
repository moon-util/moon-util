package com.moon.spring.boot;

import com.moon.core.util.logger.Logger;
import com.moon.core.util.logger.LoggerUtil;
import com.moon.spring.SpringUtil;
import com.moon.spring.data.redis.ExceptionHandler;
import com.moon.spring.data.redis.RedisService;
import com.moon.spring.data.redis.StringRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * configuration
 *
 * @author moonsky
 */
@Configuration
@ConditionalOnMissingBean({MoonUtilConfiguration.class})
public class MoonUtilConfiguration implements ImportSelector {

    private final static Logger logger = LoggerUtil.getLogger();

    @Override
    @SuppressWarnings("all")
    public String[] selectImports(AnnotationMetadata metadata) {
        List<String> classes = new ArrayList<>();
        classes.add(SpringUtil.class.getName());
        try {
            RedisTemplate.class.toString();
            classes.add(RedisConfiguration.class.getName());
        } catch (Throwable ignored) {
        }
        return classes.toArray(new String[classes.size()]);
    }

    @ConditionalOnMissingBean(value = {RedisConfiguration.class})
    @ConditionalOnBean(name = "redisTemplate")
    public static class RedisConfiguration {

        @Autowired(required = false)
        private ExceptionHandler exceptionHandler;

        @Bean
        @ConditionalOnMissingBean(value = {RedisService.class}, name = "redisService")
        public RedisService redisService(RedisTemplate redisTemplate) {
            return new RedisService(redisTemplate, exceptionHandler);
        }

        @Bean
        @ConditionalOnMissingBean(value = {StringRedisService.class}, name = "stringRedisService")
        public StringRedisService stringRedisService(RedisTemplate redisTemplate) {
            return new StringRedisService(redisTemplate, exceptionHandler);
        }

        @Bean
        @ConditionalOnMissingBean(value = {RedisCacheManager.class}, name = "redisCacheManager")
        public RedisCacheManager redisCacheManager(RedisConnectionFactory factory) {
            return RedisCacheManager.create(factory);
        }
    }
}