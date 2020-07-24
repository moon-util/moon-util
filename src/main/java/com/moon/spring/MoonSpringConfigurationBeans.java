package com.moon.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author moonsky
 */
@Configuration
public class MoonSpringConfigurationBeans {

    @Bean
    public SpringUtil moonSpringUtil(){
        return new SpringUtil();
    }
}
