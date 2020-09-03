package com.moon.spring;

import com.moon.data.accessor.RecordUtil;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * configuration
 *
 * @author moonsky
 */
@Configuration
@Import(MoonUtilConfiguration.ConfigurationImportSelector.class)
public class MoonUtilConfiguration {

    @Bean
    public SpringUtil moonSpringUtil() { return new SpringUtil(); }

    public static class ConfigurationImportSelector implements ImportSelector {

        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            List<String> classes = new ArrayList<>();
            try {
                new RecordableApplicationRunner();
                classes.add(RecordableApplicationRunner.class.getName());
            } catch (Throwable t) {
                // ignore
            }
            return classes.toArray(new String[classes.size()]);
        }
    }

    public final static class RecordableApplicationRunner implements ApplicationRunner {

        @Override
        public void run(ApplicationArguments args) throws Exception {
            RecordUtil.runningTakeAll();
        }
    }
}