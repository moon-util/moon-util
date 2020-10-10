package com.moon.spring.boot;

import com.moon.core.util.logger.Logger;
import com.moon.core.util.logger.LoggerUtil;
import com.moon.data.accessor.AccessorRegistration;
import com.moon.spring.data.redis.ExceptionHandler;
import com.moon.spring.data.redis.RedisAccessor;
import com.moon.spring.data.redis.StringRedisAccessor;
import com.moon.spring.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.Order;
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
@Import({SpringUtil.class})
public class MoonUtilConfiguration implements ImportSelector {

    private final static Logger logger = LoggerUtil.getLogger();

    @Override
    @SuppressWarnings("all")
    public String[] selectImports(AnnotationMetadata metadata) {
        List<String> classes = new ArrayList<>();
        try {
            ApplicationRunner.class.toString();
            classes.add(RecordableApplicationRunner.class.getName());
        } catch (Throwable t) {
            if (logger.isInfoEnabled()) {
                logger.info("可能存在未注册服务影响使用");
            }
        }
        try {
            RedisTemplate.class.toString();
            classes.add(RedisConfiguration.class.getName());
        } catch (Throwable t) {
            // ignore
        }
        return classes.toArray(new String[classes.size()]);
    }

    @ConditionalOnBean(name = "redisTemplate")
    @ConditionalOnClass({RedisTemplate.class})
    public static class RedisConfiguration {

        @Autowired(required = false)
        private ExceptionHandler exceptionHandler;

        @Bean
        public RedisAccessor redisAccessor(RedisTemplate redisTemplate) {
            return new RedisAccessor(redisTemplate, exceptionHandler);
        }

        @Bean
        public StringRedisAccessor stringRedisAccessor(RedisTemplate redisTemplate) {
            return new StringRedisAccessor(redisTemplate, exceptionHandler);
        }

        @Bean
        @ConditionalOnMissingBean
        public RedisCacheManager redisCacheManager(RedisConnectionFactory factory) {
            return RedisCacheManager.create(factory);
        }
    }

    @Order(Integer.MAX_VALUE - 1)
    public final static class RecordableApplicationRunner implements ApplicationRunner {

        @Override
        public void run(ApplicationArguments args) {
            AccessorRegistration.runningTakeAll();
        }
    }
}